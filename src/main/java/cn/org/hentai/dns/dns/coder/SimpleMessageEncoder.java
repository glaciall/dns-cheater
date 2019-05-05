package cn.org.hentai.dns.dns.coder;

import cn.org.hentai.dns.dns.entity.Message;
import cn.org.hentai.dns.dns.entity.Question;
import cn.org.hentai.dns.dns.entity.ResourceRecord;
import cn.org.hentai.dns.util.Packet;
import static cn.org.hentai.dns.dns.entity.Message.TYPE_A;
import static cn.org.hentai.dns.dns.entity.Message.TYPE_AAAA;

/**
 * Created by matrixy on 2019/4/23.
 */
public final class SimpleMessageEncoder
{
    // 创建回应消息包
    public static byte[] encode(Message query, Question question, ResourceRecord[] answers)
    {
        return encode((short)(query.transactionId & 0xffff), 900, question.name, answers);
    }

    public static byte[] encode(short sequence, int ttl, String domainName, ResourceRecord[] answers)
    {
        Packet packet = Packet.create(1024);

        // 会话标识
        packet.addShort((short)(sequence & 0xffff));

        // 标志，始终为成功应答
        packet.addShort((short)0x8000);

        // 数量
        packet.addShort((short)0x01).addShort((short)answers.length).addShort((short)0x00).addShort((short)0x00);

        // Queries区域，这里只处理单域名查询的情况
        packet.addBytes(encodeName(domainName));
        packet.addByte((byte)0x00);
        packet.addInt(0x00010001);                      // 查询类型、查询类固定为0x01

        // Resource Record列表
        for (int i = 0; i < answers.length; i++)
        {
            ResourceRecord answer = answers[i];

            // 始终指向查询区的名称
            packet.addShort((short)0xc00c);
            packet.addShort((short)answer.type);
            packet.addShort((short)0x01);
            packet.addInt(answer.ttl);
            packet.addShort((short)answer.dlen);
            if (answer.type == TYPE_A)
            {
                packet.addInt((int)(answer.ipv4 & 0xffffffff));
            }
            else if (answer.type == TYPE_AAAA)
            {
                packet.addBytes(answer.ipv6.getAddress());
            }
            else
            {
                packet.addBytes(answer.data);
            }
        }

        return packet.getBytes();
    }

    private static byte[] encodeName(String queryName)
    {
        byte[] nameBytes = new byte[queryName.length() + 1];
        int lastHeadIndex = 0, s = 0;
        for (int i = 0, k = 1; i < queryName.length(); i++, k++)
        {
            char chr = queryName.charAt(i);
            if (chr == '.')
            {
                nameBytes[lastHeadIndex] = (byte)s;
                s = 0;
                lastHeadIndex = i + 1;
                continue;
            }
            nameBytes[k] = (byte)chr;
            s += 1;
        }
        nameBytes[lastHeadIndex] = (byte)s;
        return nameBytes;
    }
}
