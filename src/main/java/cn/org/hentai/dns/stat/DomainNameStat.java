package cn.org.hentai.dns.stat;

/**
 * Created by matrixy on 2019/5/8.
 */
public class DomainNameStat
{
    public int id;
    public String name;
    public int queryCount;
    public boolean success;

    public DomainNameStat(int id, String name)
    {
        this.id = id;
        this.name = name;
        this.queryCount = 0;
    }
}
