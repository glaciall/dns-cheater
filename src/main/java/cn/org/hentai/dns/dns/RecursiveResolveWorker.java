package cn.org.hentai.dns.dns;

import cn.org.hentai.dns.cache.CacheManager;
import cn.org.hentai.dns.dns.coder.SimpleMessageDecoder;
import cn.org.hentai.dns.dns.coder.SimpleMessageEncoder;
import cn.org.hentai.dns.dns.entity.*;
import cn.org.hentai.dns.entity.Address;
import cn.org.hentai.dns.util.ByteUtils;
import cn.org.hentai.dns.util.IPUtils;
import cn.org.hentai.dns.util.Packet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by matrixy on 2019/4/19.
 */
public class RecursiveResolveWorker extends Thread
{
    static Logger logger = LoggerFactory.getLogger(RecursiveResolveWorker.class);

    ArrayList<Question> questions = new ArrayList(100);
    ArrayList answers = new ArrayList();
    SimpleDateFormat dateFormat = new SimpleDateFormat("HHmmss");

    public RecursiveResolveWorker()
    {

    }

    private void getAndResolve() throws Exception
    {
        Response response = RecursiveResolver.getInstance().takeResponse();
        if (response == null) return;

        // 消息包解码
        Packet packet = Packet.create(response.packet);
        Message msg = SimpleMessageDecoder.decode(packet);
        if (msg.isQuestion() == true)
        {
            logger.info("skip current question: " + ByteUtils.toString(packet.nextBytes()));
            return;
        }

        // 遍历每一个要查询的域名
        packet.seek(12);
        int len = 0;
        questions.clear();
        for (int i = 0; i < msg.questions; i++)
        {
            StringBuilder name = new StringBuilder(64);
            while ((len = packet.nextByte() & 0xff) > 0)
            {
                name.append(new String(packet.nextBytes(len)));
                name.append('.');
            }
            if (name.charAt(name.length() - 1) == '.') name.deleteCharAt(name.length() - 1);
            int queryType = packet.nextShort() & 0xffff;
            int queryClass = packet.nextShort() & 0xffff;
            questions.add(new Question(name.toString(), queryType));

            logger.info("question: {}, type: {}", name.toString(), queryType);
        }

        if (questions.size() == 0)
        {
            logger.error("no question block found...");
            return;
        }

        Question question = questions.get(0);
        if (msg.answerRRs == 0)
        {
            logger.error("no answer for: {}", question.name);
            return;
        }

        List<ResourceRecord> records = new ArrayList();
        CacheManager cacheManager = CacheManager.getInstance();
        long minTTL = Integer.MAX_VALUE;
        for (int i = 0, k = msg.answerRRs + msg.authorityRRs + msg.additionalRRs; i < k; i++)
        {
            short pointer = packet.nextShort();
            short type = packet.nextShort();
            // 如果应答的不是IPv4地址，则略过应答内容区
            // TYPE_AAAA应当留下来的，不过现在未普及，我也没用过就先算了
            if (type != Message.TYPE_A)
            {
                packet.skip(packet.skip(6).nextShort());
                continue;
            }

            packet.skip(2);
            int ttl = packet.nextInt();
            int dlen = packet.nextShort() & 0xffff;
            if (dlen != 4) throw new RuntimeException("invalid answer dlen");
            long answerIP = packet.nextInt() & 0xffffffffL;

            minTTL = Math.min(ttl, minTTL);
            records.add(new ResourceRecord(question.name, Message.TYPE_A, ttl, answerIP));
        }
        cacheManager.put(question.name, records.toArray(new ResourceRecord[0]), System.currentTimeMillis() + minTTL * 1000);
    }

    public void run()
    {
        while (!this.isInterrupted())
        {
            try
            {
                getAndResolve();
            }
            catch(Exception e)
            {
                logger.error("domain name resolve error", e);
            }
        }
    }
}
