package cn.org.hentai.dns.dns.entity;

import cn.org.hentai.dns.util.ByteUtils;

import java.net.Inet6Address;

/**
 * Created by matrixy on 2019/4/23.
 * 响应消息的资源记录，一般为域名所对应的IP或CNAME等
 */
public class ResourceRecord
{
    public String name;
    public int type;
    // public int class;            // 固定值就不声明了
    public int ttl;
    public int dlen;
    public int ipv4;                // 如果type == TYPE_A，则使用IP作为data，省得转来转去
    public Inet6Address ipv6;       // 同上
    public byte[] data;

    public ResourceRecord(String name, int type, int ttl, int ipv4)
    {
        this.name = name;
        this.type = type;
        this.ttl = ttl;
        this.ipv4 = ipv4;
        this.dlen = 4;
    }

    public ResourceRecord(String name, int type, int ttl, Inet6Address ipv6)
    {
        this.name = name;
        this.type = type;
        this.ttl = ttl;
        this.ipv6 = ipv6;
        this.dlen = 16;
    }

    public ResourceRecord(String name, int type, int ttl, byte[] data)
    {
        this.name = name;
        this.type = type;
        this.ttl = ttl;
        this.data = data;
        this.dlen = data.length;
    }

    public String getAnswerString()
    {
        if (type == Message.TYPE_A)
        {
            int a = (ipv4 >> 24) & 0xff,
                    b = (ipv4 >> 16) & 0xff,
                    c = (ipv4 >> 8) & 0xff,
                    d = ipv4 & 0xff;
            return a + "." + b + "." + c + "." + d;
        }
        else if (type == Message.TYPE_AAAA)
        {
            return ipv6.toString();
        }
        else
        {
            return ByteUtils.toString(data);
        }
    }
}
