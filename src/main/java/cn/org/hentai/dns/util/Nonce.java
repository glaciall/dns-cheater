package cn.org.hentai.dns.util;

/**
 * Created by Expect on 2017/12/25.
 */
public final class Nonce
{
    private static final String characters = "1234567890abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
    public static String generate()
    {
        return generate(16);
    }
    public static String generate(int length)
    {
        char[] nonce = new char[length];
        for (int i = 0; i < nonce.length; i++)
        {
            nonce[i] = characters.charAt((int)(Math.random() * characters.length()));
        }
        return new String(nonce);
    }
}
