package cn.org.hentai.dns.dns;

import cn.org.hentai.dns.cache.CacheManager;
import cn.org.hentai.dns.dns.coder.SimpleMessageDecoder;
import cn.org.hentai.dns.dns.coder.SimpleMessageEncoder;
import cn.org.hentai.dns.dns.entity.*;
import cn.org.hentai.dns.entity.Address;
import cn.org.hentai.dns.stat.StatManager;
import cn.org.hentai.dns.util.ByteUtils;
import cn.org.hentai.dns.util.IPUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.net.Inet4Address;
import java.net.InetSocketAddress;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by matrixy on 2019/4/19.
 */
public class NameResolveWorker extends Thread
{
    static Logger logger = LoggerFactory.getLogger(NameResolveWorker.class);

    NameServer nameServer;
    ArrayList<Question> questions = new ArrayList(100);
    ArrayList answers = new ArrayList();
    SimpleDateFormat dateFormat = new SimpleDateFormat("HHmmss");

    public NameResolveWorker(NameServer nameServer)
    {
        this.nameServer = nameServer;
    }

    private void getAndResolve() throws Exception
    {
        Request request = this.nameServer.takeRequest();
        if (request == null) return;

        // 消息包解码
        Message msg = SimpleMessageDecoder.decode(request.packet);
        logger.info("decode: TransactionId: {}, Flags: {}, Questions: {}, AnswerRRs: {}, AuthorityRRs: {}, AdditionalRRs: {}", msg.transactionId, Integer.toBinaryString(msg.flags), msg.questions, msg.answerRRs, msg.authorityRRs, msg.additionalRRs);
        logger.info("decode flags: QR: {}, OP: {}, AA: {}, TC: {}, RD: {}, RA: {}, RCode: {}", msg.isQuestion(), msg.getOperateType(), msg.isAuthorityAnswer(), msg.isTruncateable(), msg.isRecursiveExpected(), msg.isRecursively(), msg.getReturnCode());

        if (msg.isQuestion() == false)
        {
            logger.info("skip current question: " + ByteUtils.toString(request.packet.nextBytes()));
            return;
        }

        // 遍历每一个要查询的域名
        request.packet.seek(12);
        int len = 0;
        questions.clear();
        for (int i = 0; i < msg.questions; i++)
        {
            StringBuilder name = new StringBuilder(64);
            while ((len = request.packet.nextByte() & 0xff) > 0)
            {
                name.append(new String(request.packet.nextBytes(len)));
                name.append('.');
            }
            if (name.charAt(name.length() - 1) == '.') name.deleteCharAt(name.length() - 1);
            int queryType = request.packet.nextShort() & 0xffff;
            int queryClass = request.packet.nextShort() & 0xffff;
            questions.add(new Question(name.toString(), queryType));
        }

        // 依次处理，一般来说，都是单个查询的吧，只有自己写程序才有可能会有批量查询的情况
        if (questions.size() > 1) throw new RuntimeException("multiple name resolve unsupported");
        CacheManager cacheManager = CacheManager.getInstance();
        int now = Integer.parseInt(dateFormat.format(new Date()));
        for (Question question : questions)
        {
            logger.info("resolve: name = {}, type = {}", question.name, question.type);
            long remoteAddr = ByteUtils.getLong(((InetSocketAddress) request.remoteAddress).getAddress().getAddress(), 0, 4);

            // 计数
            StatManager.getInstance().log(remoteAddr, now, question.name);

            Address answer = RuleManager.getInstance().matches(now, remoteAddr, question.name);
            if (answer == null)
            {
                logger.info("no matched rules for: " + question.name);
                // 如果有缓存并且缓存未过期，则返回缓存内容，否则交给递归解析线程去上游服务器解析
                CachedItem<ResourceRecord[]> cache = cacheManager.get(question.name);
                if (cache != null)
                {
                    int ttl = cache.getTTL();
                    ResourceRecord[] records = cache.entity;
                    logger.info("resolved from cache: name = {}, answers = {}", question.name, records);
                    byte[] resp = SimpleMessageEncoder.encode((short)(msg.transactionId & 0xffff), ttl, question.name, records);
                    this.nameServer.putResponse(new Response(request.remoteAddress, resp));
                }
                else
                {
                    StatManager.getInstance().addQueryUpstreamCount();
                    RecursiveResolver.getInstance().putRequest(request);
                }
            }
            else
            {
                // 返回结果
                ResourceRecord[] records = new ResourceRecord[] { new ResourceRecord(question.name, Message.TYPE_A, 180, IPUtils.toInteger(answer.getAddress())) };
                logger.info("resolved: name = {}, answer = {}", question.name, answer.getAddress());
                byte[] resp = SimpleMessageEncoder.encode(msg, question, records);
                this.nameServer.putResponse(new Response(request.remoteAddress, resp));
            }
        }
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
