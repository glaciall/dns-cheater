package cn.org.hentai.dns.cache;

import cn.org.hentai.dns.dns.entity.ResourceRecord;

/**
 * Created by matrixy on 2019/4/23.
 * LRC+k缓存实现
 */
public final class CacheManager
{
    LRU<String, CachedItem> cachePool = null;

    public ResourceRecord[] get(String key)
    {
        CachedItem item = cachePool.get(key);
        if (item == null) return null;
        if (item.expired())
        {
            cachePool.remove(key);
            return null;
        }
        return (ResourceRecord[]) item.entity;
    }

    public void put(String key, ResourceRecord[] records, long expireTime)
    {
        cachePool.put(key, new CachedItem(records, expireTime));
    }

    // TODO: 需要定时清理已经过期的缓存项

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

    static class CachedItem<T>
    {
        public long expireTime;
        public T entity;
        public CachedItem(T entity, long expireTime)
        {
            this.entity = entity;
            this.expireTime = expireTime;
        }

        public boolean expired()
        {
            return System.currentTimeMillis() > expireTime;
        }
    }
}
