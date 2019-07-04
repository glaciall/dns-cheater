package cn.org.hentai.dns.protocol;

import cn.org.hentai.dns.protocol.entity.Request;
import cn.org.hentai.dns.protocol.entity.Response;
import cn.org.hentai.dns.stat.StatManager;
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
import java.util.Iterator;
import java.util.concurrent.ArrayBlockingQueue;

/**
 * Created by matrixy on 2019/4/19.
 */
public class NameServer extends Thread
{
    static Logger logger = LoggerFactory.getLogger(NameServer.class);

    NameResolveWorker[] resolveWorkers = null;
    ArrayBlockingQueue<Request> queries = null;
    ArrayBlockingQueue<Response> responses = null;

    public NameServer()
    {
        this.setName("nameserver-thread");
        this.resolveWorkers = new NameResolveWorker[Runtime.getRuntime().availableProcessors() * 2];
        this.queries = new ArrayBlockingQueue<Request>(65535);
        this.responses = new ArrayBlockingQueue<Response>(65535);
        for (int i = 0; i < this.resolveWorkers.length; i++)
        {
            this.resolveWorkers[i] = new NameResolveWorker(this);
            this.resolveWorkers[i].setName("name-resolve-worker-" + i);
            this.resolveWorkers[i].start();
        }
    }

    public void run()
    {
        DatagramChannel datagramChannel = null;
        try
        {
            StatManager statMgr = StatManager.getInstance();

            String bindIP = Configs.get("dns.server.addr", "0.0.0.0");
            int port = Configs.getInt("dns.server.port", 53);
            Selector selector = Selector.open();

            datagramChannel = DatagramChannel.open();
            datagramChannel.socket().bind(new InetSocketAddress(bindIP, port));
            datagramChannel.configureBlocking(false);

            new Sender(this, datagramChannel).start();

            logger.info("NameServer started at {}:{}", bindIP, port);

            datagramChannel.configureBlocking(false);
            ByteBuffer buffer = ByteBuffer.allocate(1024);

            datagramChannel.register(selector, SelectionKey.OP_READ);
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
                        logger.info("received: from = {}, length = {}, ", addr.toString(), message.length);
                        queries.put(new Request(addr, Packet.create(message)));
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

    public Request takeRequest()
    {
        try
        {
            return queries.take();
        }
        catch(Exception ex)
        {
            return null;
        }
    }

    public boolean putResponse(Response response)
    {
        try
        {
            responses.put(response);
            return true;
        }
        catch (InterruptedException e)
        {
            return false;
        }
    }

    public Response takeResponse() throws InterruptedException
    {
        return responses.take();
    }

    // 使用独立的线籔程去发送回应
    static class Sender extends Thread
    {
        NameServer nameServer;
        DatagramChannel datagramChannel;

        public Sender(NameServer nameServer, DatagramChannel datagramChannel)
        {
            this.nameServer = nameServer;
            this.datagramChannel = datagramChannel;

            this.setName("name-server-sender");
        }

        public void run()
        {
            ByteBuffer buffer = ByteBuffer.allocate(1024);
            StatManager statMgr = StatManager.getInstance();
            while (!this.isInterrupted())
            {
                try
                {
                    Response response = nameServer.takeResponse();
                    statMgr.addAnswerCount();
                    buffer.clear();
                    buffer.put(response.packet);
                    buffer.flip();
                    datagramChannel.send(buffer, response.remoteAddress);
                    logger.info("send: to = {}, length = {}", response.remoteAddress, response.packet.length);
                }
                catch(Exception e)
                {
                    if (e instanceof InterruptedException) break;
                    logger.error("send error", e);
                }
            }
        }
    }

    static NameServer instance = null;
    public void init()
    {
        instance.start();
    }

    public static synchronized NameServer getInstance()
    {
        if (null == instance) instance = new NameServer();
        return instance;
    }
}
