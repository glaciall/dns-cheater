package cn.org.hentai.dns.protocol.entity;

/**
 * Created by matrixy on 2019/5/6.
 */
public class CachedItem<T>
{
    public long expireTime;
    public T entity;
    public CachedItem(T entity, long expireTime)
    {
        this.entity = entity;
        this.expireTime = expireTime;
    }

    public int getTTL()
    {
        return (int)((this.expireTime - System.currentTimeMillis()) / 1000);
    }

    public boolean expired()
    {
        return System.currentTimeMillis() > expireTime;
    }
}
