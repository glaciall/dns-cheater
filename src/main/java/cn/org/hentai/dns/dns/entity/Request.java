package cn.org.hentai.dns.dns.entity;

import cn.org.hentai.dns.util.Packet;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.SocketAddress;

/**
 * Created by matrixy on 2019/4/24.
 */
public class Request
{
    public Packet packet;
    public SocketAddress remoteAddress;

    public Request(SocketAddress remoteAddress, Packet packet)
    {
        this.packet = packet;
        this.remoteAddress = remoteAddress;
    }
}
