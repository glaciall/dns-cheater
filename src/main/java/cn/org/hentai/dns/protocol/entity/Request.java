package cn.org.hentai.dns.protocol.entity;

import cn.org.hentai.dns.util.Packet;

import java.net.SocketAddress;

/**
 * Created by matrixy on 2019/4/24.
 */
public class Request
{
    public short sequence;
    public Packet packet;
    public SocketAddress remoteAddress;

    public Request(SocketAddress remoteAddress, Packet packet)
    {
        this.packet = packet;
        this.remoteAddress = remoteAddress;
    }
}
