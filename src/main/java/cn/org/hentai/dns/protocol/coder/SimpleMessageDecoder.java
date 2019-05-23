package cn.org.hentai.dns.protocol.coder;

import cn.org.hentai.dns.protocol.entity.Message;
import cn.org.hentai.dns.util.Packet;

/**
 * Created by matrixy on 2019/4/19.
 * 查询消息包解码器
 */
public final class SimpleMessageDecoder
{
    public static Message decode(Packet packet)
    {
        Message msg = new Message();
        msg.transactionId = packet.nextShort() & 0xffff;
        msg.flags = packet.nextShort() & 0xffff;
        msg.questions = packet.nextShort() & 0xffff;
        msg.answerRRs = packet.nextShort() & 0xffff;
        msg.authorityRRs = packet.nextShort() & 0xffff;
        msg.additionalRRs = packet.nextShort() & 0xffff;
        return msg;
    }
}
