package cn.org.hentai.dns.dns.entity;

import cn.org.hentai.dns.util.Packet;

import java.net.SocketAddress;

/**
 * Created by matrixy on 2019/4/25.
 */
public class Response
{
    public SocketAddress remoteAddress;
    public byte[] packet;

    public Response(SocketAddress remoteAddress, byte[] packet)
    {
        this.packet = packet;
        this.remoteAddress = remoteAddress;
    }
}
