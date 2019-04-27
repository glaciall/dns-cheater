package cn.org.hentai.dns.cache;

import cn.org.hentai.dns.dns.entity.ResourceRecord;

/**
 * Created by matrixy on 2019/4/23.
 * LRC+k缓存实现
 */
public final class CacheManager
{
    LRU<String, ResourceRecord[]> cachePool = null;

    public ResourceRecord[] get(String key)
    {
        return cachePool.get(key);
    }

    public void put(String key, ResourceRecord[] records)
    {
        cachePool.put(key, records);
    }

    static volatile CacheManager instance;
    private CacheManager()
    {
        cachePool = new LRU<String, ResourceRecord[]>(4096 * 100);
    }

    public static CacheManager getInstance()
    {
        if (instance == null)
        {
            synchronized (CacheManager.class)
            {
                if (instance == null)
                {
                    instance = new CacheManager();
                }
            }
        }
        return instance;
    }
}
