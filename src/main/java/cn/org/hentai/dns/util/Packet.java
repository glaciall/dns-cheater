package cn.org.hentai.dns.util;

/**
 * Created by matrixy on 2018/4/14.
 */
public class Packet
{
    int size = 0;
    int offset = 0;
    int maxSize = 0;
    byte[] data;

    private Packet()
    {
        // do nothing here..
    }

    public static Packet create(int length)
    {
        Packet p = new Packet();
        p.data = new byte[length];
        p.size = 0;
        p.maxSize = length;
        return p;
    }

    public static Packet create(byte[] data)
    {
        Packet p = new Packet();
        p.data = data;
        p.size = data.length;
        p.maxSize = data.length;
        return p;
    }

    public int size()
    {
        return this.size;
    }

    public Packet addByte(byte b)
    {
        this.data[size++] = b;
        return this;
    }

    public Packet addShort(short s)
    {
        this.data[size++] = (byte)((s >> 8) & 0xff);
        this.data[size++] = (byte)(s & 0xff);
        return this;
    }

    public Packet setShort(short s)
    {
        this.data[offset++] = (byte)((s >> 8) & 0xff);
        this.data[offset++] = (byte)(s & 0xff);
        return this;
    }

    public Packet addInt(int i)
    {
        this.data[size++] = (byte)((i >> 24) & 0xff);
        this.data[size++] = (byte)((i >> 16) & 0xff);
        this.data[size++] = (byte)((i >> 8) & 0xff);
        this.data[size++] = (byte)(i & 0xff);
        return this;
    }

    public Packet setInt(int i)
    {
        this.data[offset++] = (byte)((i >> 24) & 0xff);
        this.data[offset++] = (byte)((i >> 16) & 0xff);
        this.data[offset++] = (byte)((i >> 8) & 0xff);
        this.data[offset++] = (byte)(i & 0xff);
        return this;
    }

    public Packet addLong(long l)
    {
        this.data[size++] = (byte)((l >> 56) & 0xff);
        this.data[size++] = (byte)((l >> 48) & 0xff);
        this.data[size++] = (byte)((l >> 40) & 0xff);
        this.data[size++] = (byte)((l >> 32) & 0xff);
        this.data[size++] = (byte)((l >> 24) & 0xff);
        this.data[size++] = (byte)((l >> 16) & 0xff);
        this.data[size++] = (byte)((l >> 8) & 0xff);
        this.data[size++] = (byte)(l & 0xff);
        return this;
    }

    public Packet addBytes(byte[] b)
    {
        System.arraycopy(b, 0, this.data, size, b.length);
        size += b.length;
        return this;
    }

    public Packet addBytes(byte[] b, int offset, int length)
    {
        System.arraycopy(b, offset, this.data, size, length);
        size += length;
        return this;
    }

    public Packet reset()
    {
        this.offset = 0;
        this.size = 0;
        return this;
    }

    public Packet rewind()
    {
        this.offset = 0;
        return this;
    }

    public int offset()
    {
        return this.offset;
    }

    public byte nextByte()
    {
        return this.data[offset++];
    }

    public short nextShort()
    {
        return (short)(((this.data[offset++] & 0xff) << 8) | (this.data[offset++] & 0xff));
    }

    public int nextInt()
    {
        return (this.data[offset++] & 0xff) << 24 | (this.data[offset++] & 0xff) << 16 | (this.data[offset++] & 0xff) << 8 | (this.data[offset++] & 0xff);
    }

    public long nextLong()
    {
        return ((long)this.data[offset++] & 0xff) << 56
                | ((long)this.data[offset++] & 0xff) << 48
                | ((long)this.data[offset++] & 0xff) << 40
                | ((long)this.data[offset++] & 0xff) << 32
                | ((long)this.data[offset++] & 0xff) << 24
                | ((long)this.data[offset++] & 0xff) << 16
                | ((long)this.data[offset++] & 0xff) << 8
                | ((long)this.data[offset++] & 0xff);
    }

    public byte[] nextBytes(int length)
    {
        byte[] buf = new byte[length];
        System.arraycopy(this.data, offset, buf, 0, length);
        offset += length;
        return buf;
    }

    public byte[] nextBytes()
    {
        byte[] buf = new byte[this.size - this.offset];
        System.arraycopy(this.data, offset, buf, 0, buf.length);
        offset += buf.length;
        return buf;
    }

    public byte get(int position)
    {
        return this.data[position];
    }

    public int getInt(int offset)
    {
        return (this.data[offset++] & 0xff) << 24 | (this.data[offset++] & 0xff) << 16 | (this.data[offset++] & 0xff) << 8 | (this.data[offset++] & 0xff);
    }

    public byte[] get(int offset, int length)
    {
        byte[] buf = new byte[length];
        System.arraycopy(this.data, offset, buf, 0, length);
        return buf;
    }

    public Packet skip(int offset)
    {
        this.offset += offset;
        return this;
    }

    public Packet seek(int index)
    {
        this.offset = index;
        return this;
    }

    public boolean hasMoreBytes()
    {
        return this.offset < this.size;
    }

    public byte[] getBytes()
    {
        if (size == maxSize) return this.data;
        else
        {
            byte[] buff = new byte[size];
            System.arraycopy(this.data, 0, buff, 0, size);
            return buff;
        }
    }

    /**
     * 复制len个字节，到dest的offset位置处
     * @param dest
     * @param offset
     * @param len
     */
    public void copyBytes(byte[] dest, int offset, int len)
    {
        System.arraycopy(this.data, this.offset, dest, offset, len);
        this.offset += len;
    }

    public boolean hasEnoughSpace(int count)
    {
        return (this.maxSize - this.offset) > count;
    }
}
