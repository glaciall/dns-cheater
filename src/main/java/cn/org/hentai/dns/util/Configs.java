package cn.org.hentai.dns.util;

import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Created by matrixy on 2017/8/14.
 */
public final class Configs
{
    static Environment env;

    public static void init(ApplicationContext context)
    {
        env = context.getEnvironment();
    }

    public static String get(String key)
    {
        Object val = env.getProperty(key);
        if (null == val) return null;
        else return String.valueOf(val).trim();
    }

    public static String get(String key, String defaultVal)
    {
        return env.getProperty(key, defaultVal);
    }

    public static int getInt(String key, int defaultVal)
    {
        String val = get(key, String.valueOf(defaultVal));
        return Integer.parseInt(val);
    }
}
