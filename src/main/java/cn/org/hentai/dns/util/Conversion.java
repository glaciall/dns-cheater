package cn.org.hentai.dns.util;

/**
 * Created by huangzhian on 2019/10/21.
 */
public class Conversion {

    /**
     * 将配置的缓存空间单位KMG转换为byte,
     * 因为int类型的限制，将最大缓存空间设为1G
     */
    public static int toByte(String memory) {
        int count = 0;
        String mem;
        if (memory == null) {
            return 0;
        }
        mem = memory.toUpperCase();
        if (mem.matches("[0-9]+[KMG]{1}")) {
            String submem = memory.substring(0, memory.length() - 1);
            count = Integer.parseInt(submem);
        } else if (mem.matches("[0-9]+")) {
            count = Integer.parseInt(memory);
        }
        if (mem.endsWith("M")) {
            return (count > 1024 ? 1024 : count) << 20;
        } else if (mem.endsWith("G")) {
            return (count > 1 ? 1 : count) << 30;
        } else if (mem.endsWith("K")) {
            return count << 10;
        } else {
            return count;
        }
    }
}
