package cn.org.hentai.dns.protocol;

import cn.org.hentai.dns.protocol.entity.Request;
import cn.org.hentai.dns.protocol.entity.Response;
import cn.org.hentai.dns.util.ByteUtils;
import cn.org.hentai.dns.util.Configs;
import cn.org.hentai.dns.util.Packet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;

/**
 * Created by matrixy on 2019/4/19.
 */
public class RecursiveResolver extends Thread
{
    static Logger logger = LoggerFactory.getLogger(RecursiveResolver.class);
    ArrayBlockingQueue<Request> queries = null;
    ArrayBlockingQueue<Response> responses = null;
    Map<Short, OriginalRequest> transactionMap = null;

    RecursiveResolveWorker[] resolveWorkers = null;

    short sequence = 1;

    public RecursiveResolver()
    {
        this.setName("recursive-resolver-thread");
        this.queries = new ArrayBlockingQueue<Request>(65535);
        this.responses = new ArrayBlockingQueue<Response>(65535);
        transactionMap = new HashMap(65535);

        resolveWorkers = new RecursiveResolveWorker[Runtime.getRuntime().availableProcessors()];
        for (int i = 0; i < resolveWorkers.length; i++)
        {
            resolveWorkers[i] = new RecursiveResolveWorker();
            resolveWorkers[i].setName("recursive-resolve-worker-" + i);
            resolveWorkers[i].start();
        }
    }

    public void run()
    {
        DatagramChannel datagramChannel = null;
        try
        {
            Selector selector = Selector.open();

            datagramChannel = DatagramChannel.open();
            datagramChannel.configureBlocking(false);
            datagramChannel.register(selector, SelectionKey.OP_READ | SelectionKey.OP_WRITE);

            logger.info("Recursive Resolver started...");

            datagramChannel.configureBlocking(false);
            ByteBuffer buffer = ByteBuffer.allocate(1024);

            InetSocketAddress upstreamNameServer = new InetSocketAddress(Configs.get("dns.upstream.server.address"), Configs.getInt("dns.upstream.server.port", 53));

            datagramChannel.register(selector, SelectionKey.OP_READ | SelectionKey.OP_WRITE);
            while (!this.isInterrupted())
            {
                selector.select();
                Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
                while (iterator.hasNext())
                {
                    SelectionKey selectionKey = (SelectionKey) iterator.next();
                    if (selectionKey.isReadable())
                    {
                        buffer.clear();
                        SocketAddress addr = datagramChannel.receive(buffer);
                        buffer.flip();
                        byte[] message = new byte[buffer.limit()];
                        buffer.get(message, 0, message.length);

                        logger.info("##############################################################################################");
                        logger.info("answer received: from = {}, length = {}, ", addr.toString(), message.length);
                        short seq = 0;
                        OriginalRequest req = transactionMap.remove(seq = (short)ByteUtils.getShort(message, 0, 2));
                        if (req != null) responses.add(new Response(req.sequence, req.remoteAddress, message));
                        else logger.info("no original request found for: " + seq);
                    }
                    while (selectionKey.isWritable())
                    {
                        if (queries.size() == 0)
                        {
                            if (selectionKey.isReadable() == false) Thread.sleep(1);
                            break;
                        }
                        Request request = queries.poll();
                        if (request != null)
                        {
                            Packet packet = request.packet;
                            short seq = sequence++;
                            request.sequence = seq;
                            transactionMap.put(seq, new OriginalRequest(packet.seek(0).nextShort(), request.remoteAddress));
                            packet.seek(0).setShort(seq);
                            buffer.clear();
                            buffer.put(packet.getBytes());
                            buffer.flip();
                            datagramChannel.send(buffer, upstreamNameServer);
                            logger.info("send request to upstream: to = {}, sequence = {}, length = {}", upstreamNameServer, seq, packet.size());
                        }
                    }
                }
            }
        }
        catch (Exception ex)
        {
            logger.error("nameserver receive error", ex);
        }
        finally
        {
            try { datagramChannel.close(); } catch(Exception e) { }
            logger.info("NameServer app exited...");
            System.exit(1);
        }
    }

    public void putRequest(Request request)
    {
        try
        {
            this.queries.add(request);
        }
        catch(Exception ex)
        {
            logger.error("put request error", ex);
        }
    }

    public Response takeResponse()
    {
        try
        {
            return responses.take();
        }
        catch (InterruptedException e)
        {
            return null;
        }
    }

    static class OriginalRequest
    {
        public short sequence;
        public SocketAddress remoteAddress;

        public OriginalRequest(short seq, SocketAddress addr)
        {
            this.sequence = seq;
            this.remoteAddress = addr;
        }
    }

    static RecursiveResolver instance = null;
    public void init()
    {
        instance.start();
    }
    public static synchronized RecursiveResolver getInstance()
    {
        if (null == instance) instance = new RecursiveResolver();
        return instance;
    }
}
