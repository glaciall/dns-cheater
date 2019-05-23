package cn.org.hentai.dns.protocol.entity;

/**
 * Created by matrixy on 2019/4/19.
 */
public class Message
{
    public static final int QR_QUESTION = 0x00;                     // QR类型：问询
    public static final int QR_RESPONSE = 0x01;                     // QR类型：响应

    public static final int OP_STANDARD_QUERY = 0x00;               // OP类型：标准查询
    public static final int OP_REVERSE_QUERY = 0x01;                // OP类型：反向查询
    public static final int OP_STATUS_QUER = 0x02;                  // OP类型，服务器状态查询

    public static final int RCODE_SUCCESS = 0x00;                   // 返回类型：成功
    public static final int RCODE_INVALID_NAME = 0x01;              // 返回类型：错误的名字
    public static final int RCODE_SERVER_FAILURE = 0x02;            // 返回类型：服务器错误

    public static final int TYPE_A = 1;                             // 由域名获得IPv4地址
    public static final int TYPE_NS = 2;                            // 查询域名服务器
    public static final int TYPE_CNAME = 5;                         // 查询规范名称/别名
    public static final int TYPE_SOA = 6;                           // 开始授权
    public static final int TYPE_WKS = 11;                          // 熟知服务
    public static final int TYPE_PTR = 12;                          // 把IP地址转换成域名
    public static final int TYPE_HINFO = 13;                        // 主机信息
    public static final int TYPE_MX = 15;                           // 邮件交换
    public static final int TYPE_AAAA = 28;                         // 由域名获得IPv6地址
    public static final int TYPE_AXFR = 252;                        // 传送整个区的请求
    public static final int TYPE_ANY = 255;                         // 对所有记录的请求

    public int transactionId;           // 会话标识
    public int flags;                   // 标志
    public int questions;               // 问题数
    public int answerRRs;               // 回答资源记录数
    public int authorityRRs;            // 授权资源记录数
    public int additionalRRs;           // 附加资源记录数

    public Message()
    {

    }

    public int getReturnCode()
    {
        return flags & 0x0f;
    }

    public boolean isRecursively()
    {
        return ((flags >> 7) & 0x01) == 1;
    }

    public boolean isRecursiveExpected()
    {
        return ((flags >> 8) & 0x01) == 1;
    }

    public boolean isTruncateable()
    {
        return ((flags >> 9) & 0x01) == 1;
    }

    public boolean isAuthorityAnswer()
    {
        return ((flags >> 10) & 0x01) == 1;
    }

    public int getOperateType()
    {
        return (flags >> 11) & 0x0f;
    }

    public boolean isQuestion()
    {
        return (flags & 0x8000) == 0;
    }
}
