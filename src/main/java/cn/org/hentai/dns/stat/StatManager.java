package cn.org.hentai.dns.stat;

import cn.org.hentai.dns.util.Configs;
import cn.org.hentai.dns.util.Packet;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by matrixy on 2019/5/8.
 */
public final class StatManager
{
    private Packet logs;
    private Map<String, DomainNameStat> domainNameMap = null;
    private static int sequence;

    private StatManager()
    {
        logs = Packet.create(1024 * 1024 * 200);
        domainNameMap = new HashMap();
        sequence = 1;

        // TODO: 启动定时器，在每日0:00:00时清空一次数据
    }

    // 记录查询日志，保存查询时的来源IP、时间（距当天0:00:00过去的秒数）、域名（通过map转为数字id，提高复用率）
    public synchronized void log(long ip, int time, String domainName)
    {
        // 如果配置为不记录查询日志，则直接跳出
        if ("on".equals(Configs.get("dns.stat-logger")) == false) return;

        // TODO: 空间不足时如何处理？重新申请更大的空间？还是放弃掉？
        if (logs.hasEnoughSpace(12) == false) return;

        DomainNameStat stat = domainNameMap.get(domainName);
        if (stat == null)
        {
            stat = new DomainNameStat(sequence++, domainName);
            domainNameMap.put(domainName, stat);
        }
        stat.queryCount += 1;

        logs.addInt((int)(ip & 0xffffffff));
        logs.addInt(time);
        logs.addInt(stat.id);

        // TODO: 通过来源IP进行计数
    }

    static StatManager instance = null;
    public static synchronized StatManager getInstance()
    {
        if (null == instance) instance = new StatManager();
        return instance;
    }
}
