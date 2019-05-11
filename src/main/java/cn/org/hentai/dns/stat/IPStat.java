package cn.org.hentai.dns.stat;

import cn.org.hentai.dns.util.IPUtils;

/**
 * Created by matrixy on 2019/5/9.
 */
public class IPStat
{
    public int ip;
    public int queryCount;

    public IPStat(int ip, int count)
    {
        this.ip = ip;
        this.queryCount = count;
    }

    public String getIP()
    {
        return IPUtils.fromInteger(ip);
    }

    public int getQueryCount()
    {
        return this.queryCount;
    }
}
