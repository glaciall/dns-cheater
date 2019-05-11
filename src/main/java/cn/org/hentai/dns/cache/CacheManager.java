package cn.org.hentai.dns.cache;

import cn.org.hentai.dns.dns.entity.CachedItem;
import cn.org.hentai.dns.dns.entity.ResourceRecord;

/**
 * Created by matrixy on 2019/4/23.
 */
public final class CacheManager
{
    LRU<String, CachedItem> cachePool = null;

    public CachedItem get(String key)
    {
        CachedItem item = cachePool.get(key);
        if (item == null) return null;
        if (item.expired())
        {
            cachePool.remove(key);
            return null;
        }
        return item;
    }

    public void put(String key, ResourceRecord[] records, long expireTime)
    {
        cachePool.put(key, new CachedItem(records, expireTime));
    }

    public int getCachedCount()
    {
        return cachePool.usedSize();
    }

    static volatile CacheManager instance;
    private CacheManager()
    {
        cachePool = new LRU<String, CachedItem>(4096 * 100);
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
