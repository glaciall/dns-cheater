package cn.org.hentai.dns.cache;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by matrixy on 2019/4/23.
 */
public class LRU<K, V>
{
    private static final float hashLoadFactory = 0.75f;
    private LinkedHashMap<K, V> map;
    private int cacheSize;

    public LRU(int cacheSize)
    {
        this.cacheSize = cacheSize;
        int capacity = (int) Math.ceil(cacheSize / hashLoadFactory) + 1;
        map = new LinkedHashMap<K, V>(capacity, hashLoadFactory, true)
        {
            @Override
            protected boolean removeEldestEntry(Map.Entry eldest)
            {
                return size() > LRU.this.cacheSize;
            }
        };
    }

    public synchronized V get(K key)
    {
        return map.get(key);
    }

    public synchronized V remove(K key)
    {
        return map.remove(key);
    }

    public synchronized void put(K key, V value)
    {
        map.put(key, value);
    }

    public synchronized void clear()
    {
        map.clear();
    }

    public synchronized int usedSize()
    {
        return map.size();
    }
}