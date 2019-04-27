package cn.org.hentai.dns.rex;

import cn.org.hentai.dns.util.ByteUtils;

import java.io.Serializable;
import java.net.Inet4Address;

/**
 * Created by matrixy on 2019/4/25.
 */
public class Rule implements Serializable
{
    Long id;
    Long ipFrom;
    Long ipTo;
    Integer timeFrom;
    Integer timeTo;
    Integer netmask;
    String matchMode;
    String name;

    public boolean matches(int now, byte[] addr, String domainName)
    {
        // 时间段，07:34:11 -> 08:01:01
        // Inet4Address.getLoopbackAddress();
        long ip = addr[0] << 24 | addr[1] << 16 | addr[2] << 8 | addr[3];
        if (ipFrom != null && ip < ipFrom) return false;
        if (ipTo != null && ip > ipTo) return false;

        // 时间
        if (timeFrom != null && now < timeFrom) return false;
        if (timeTo != null && now > timeTo) return false;

        // 域名匹配
        if ("prefix".equals(matchMode)) return domainName.startsWith(name);
        else if ("suffix".equals(matchMode)) return domainName.endsWith(name);
        else return domainName.indexOf(name) > -1;
    }

    public String getMatchMode() {
        return matchMode;
    }

    public Rule setMatchMode(String matchMode) {
        this.matchMode = matchMode;
        return this;
    }

    public Long getId() {
        return id;
    }

    public Integer getNetmask() {
        return netmask;
    }

    public Rule setNetmask(Integer netmask) {
        this.netmask = netmask;
        return this;
    }

    public Rule setId(Long id) {
        this.id = id;
        return this;
    }

    public Long getIpFrom() {
        return ipFrom;
    }

    public Rule setIpFrom(Long ipFrom) {
        this.ipFrom = ipFrom;
        return this;
    }

    public Long getIpTo() {
        return ipTo;
    }

    public Rule setIpTo(Long ipTo) {
        this.ipTo = ipTo;
        return this;
    }

    public Integer getTimeFrom() {
        return timeFrom;
    }

    public Rule setTimeFrom(Integer timeFrom) {
        this.timeFrom = timeFrom;
        return this;
    }

    public Integer getTimeTo() {
        return timeTo;
    }

    public Rule setTimeTo(Integer timeTo) {
        this.timeTo = timeTo;
        return this;
    }

    public String getName() {
        return name;
    }

    public Rule setName(String name) {
        this.name = name;
        return this;
    }

    @Override
    public String toString() {
        return "Rule{" +
                "id=" + id +
                ", ipFrom=" + ipFrom +
                ", ipTo=" + ipTo +
                ", timeFrom=" + timeFrom +
                ", timeTo=" + timeTo +
                ", netmask=" + netmask +
                ", matchMode='" + matchMode + '\'' +
                ", name='" + name + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Rule)) return false;

        Rule rule = (Rule) o;

        if (getId() != null ? !getId().equals(rule.getId()) : rule.getId() != null) return false;
        if (getIpFrom() != null ? !getIpFrom().equals(rule.getIpFrom()) : rule.getIpFrom() != null) return false;
        if (getIpTo() != null ? !getIpTo().equals(rule.getIpTo()) : rule.getIpTo() != null) return false;
        if (getTimeFrom() != null ? !getTimeFrom().equals(rule.getTimeFrom()) : rule.getTimeFrom() != null)
            return false;
        if (getTimeTo() != null ? !getTimeTo().equals(rule.getTimeTo()) : rule.getTimeTo() != null) return false;
        if (getNetmask() != null ? !getNetmask().equals(rule.getNetmask()) : rule.getNetmask() != null) return false;
        if (getMatchMode() != null ? !getMatchMode().equals(rule.getMatchMode()) : rule.getMatchMode() != null)
            return false;
        if (getName() != null ? !getName().equals(rule.getName()) : rule.getName() != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = getId() != null ? getId().hashCode() : 0;
        result = 31 * result + (getIpFrom() != null ? getIpFrom().hashCode() : 0);
        result = 31 * result + (getIpTo() != null ? getIpTo().hashCode() : 0);
        result = 31 * result + (getTimeFrom() != null ? getTimeFrom().hashCode() : 0);
        result = 31 * result + (getTimeTo() != null ? getTimeTo().hashCode() : 0);
        result = 31 * result + (getNetmask() != null ? getNetmask().hashCode() : 0);
        result = 31 * result + (getMatchMode() != null ? getMatchMode().hashCode() : 0);
        result = 31 * result + (getName() != null ? getName().hashCode() : 0);
        return result;
    }
}
