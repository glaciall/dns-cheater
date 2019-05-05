package cn.org.hentai.dns.entity;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class Rule implements Serializable
{
    static Logger logger = LoggerFactory.getLogger(Rule.class);

    private Long id;
    private Long ipFrom;
    private Long ipTo;
    private Integer timeFrom;
    private Integer timeTo;
    private String matchMode;
    private String name;
    private Integer priority;
    private Boolean enabled;
    private String dispatchMode;

    private List<Address> addresses;
    private int sequence = 0;
    private Random random = new Random();

    private static final long serialVersionUID = 1L;

    public boolean matches(int now, long ip, String domainName)
    {
        // 时间段，07:34:11 -> 08:01:01
        // Inet4Address.getLoopbackAddress();
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

    // 根据设定的分发模式给出应答IP
    public Address dispatchAddress(long ip)
    {
        if ("random".equals(dispatchMode))
        {
            // 随机
            return this.addresses.get((random.nextInt() & 0x7fffffff) % this.addresses.size());
        }
        else if ("round-robin".equals(dispatchMode))
        {
            // 轮循
            return this.addresses.get(((sequence++) & 0x7fffffff) % this.addresses.size());
        }
        else if ("iphash".equals(dispatchMode))
        {
            // IP Hash
            return this.addresses.get((int)(ip % this.addresses.size()));
        }
        else return null;
    }

    public List<Address> getAddresses()
    {
        return addresses;
    }

    public Rule setAddresses(List<Address> addresses)
    {
        this.addresses = addresses;
        return this;
    }

    public Long getId() {
        return id;
    }

    public Rule withId(Long id) {
        this.setId(id);
        return this;
    }

    public String getDispatchMode() {
        return dispatchMode;
    }

    public Rule setDispatchMode(String dispatchMode) {
        this.dispatchMode = dispatchMode;
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getIpFrom() {
        return ipFrom;
    }

    public Rule withIpFrom(Long ipFrom) {
        this.setIpFrom(ipFrom);
        return this;
    }

    public void setIpFrom(Long ipFrom) {
        this.ipFrom = ipFrom;
    }

    public Long getIpTo() {
        return ipTo;
    }

    public Rule withIpTo(Long ipTo) {
        this.setIpTo(ipTo);
        return this;
    }

    public void setIpTo(Long ipTo) {
        this.ipTo = ipTo;
    }

    public Integer getTimeFrom() {
        return timeFrom;
    }

    public Rule withTimeFrom(Integer timeFrom) {
        this.setTimeFrom(timeFrom);
        return this;
    }

    public void setTimeFrom(Integer timeFrom) {
        this.timeFrom = timeFrom;
    }

    public Integer getTimeTo() {
        return timeTo;
    }

    public Rule withTimeto(Integer timeto) {
        this.setTimeTo(timeto);
        return this;
    }

    public void setTimeTo(Integer timeTo) {
        this.timeTo = timeTo;
    }

    public String getMatchMode() {
        return matchMode;
    }

    public Rule withMatchMode(String matchMode) {
        this.setMatchMode(matchMode);
        return this;
    }

    public void setMatchMode(String matchMode) {
        this.matchMode = matchMode == null ? null : matchMode.trim();
    }

    public String getName() {
        return name;
    }

    public Rule withName(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    public Integer getPriority() {
        return priority;
    }

    public Rule withPriority(Integer priority) {
        this.setPriority(priority);
        return this;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public Rule withEnabled(Boolean enabled) {
        this.setEnabled(enabled);
        return this;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", ipFrom=").append(ipFrom);
        sb.append(", ipTo=").append(ipTo);
        sb.append(", timeFrom=").append(timeFrom);
        sb.append(", timeTo=").append(timeTo);
        sb.append(", matchMode=").append(matchMode);
        sb.append(", name=").append(name);
        sb.append(", priority=").append(priority);
        sb.append(", enabled=").append(enabled);
        sb.append(", dispatchMode=").append(dispatchMode);
        sb.append("]");
        return sb.toString();
    }

    @Override
    public boolean equals(Object that) {
        if (this == that) {
            return true;
        }
        if (that == null) {
            return false;
        }
        if (getClass() != that.getClass()) {
            return false;
        }
        Rule other = (Rule) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
            && (this.getIpFrom() == null ? other.getIpFrom() == null : this.getIpFrom().equals(other.getIpFrom()))
            && (this.getIpTo() == null ? other.getIpTo() == null : this.getIpTo().equals(other.getIpTo()))
            && (this.getTimeFrom() == null ? other.getTimeFrom() == null : this.getTimeFrom().equals(other.getTimeFrom()))
            && (this.getTimeTo() == null ? other.getTimeTo() == null : this.getTimeTo().equals(other.getTimeTo()))
            && (this.getMatchMode() == null ? other.getMatchMode() == null : this.getMatchMode().equals(other.getMatchMode()))
            && (this.getName() == null ? other.getName() == null : this.getName().equals(other.getName()))
            && (this.getPriority() == null ? other.getPriority() == null : this.getPriority().equals(other.getPriority()))
            && (this.getDispatchMode() == null ? other.getDispatchMode() == null : this.getDispatchMode().equals(other.getDispatchMode()))
            && (this.getEnabled() == null ? other.getEnabled() == null : this.getEnabled().equals(other.getEnabled()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getIpFrom() == null) ? 0 : getIpFrom().hashCode());
        result = prime * result + ((getIpTo() == null) ? 0 : getIpTo().hashCode());
        result = prime * result + ((getTimeFrom() == null) ? 0 : getTimeFrom().hashCode());
        result = prime * result + ((getTimeTo() == null) ? 0 : getTimeTo().hashCode());
        result = prime * result + ((getMatchMode() == null) ? 0 : getMatchMode().hashCode());
        result = prime * result + ((getName() == null) ? 0 : getName().hashCode());
        result = prime * result + ((getPriority() == null) ? 0 : getPriority().hashCode());
        result = prime * result + ((getEnabled() == null) ? 0 : getEnabled().hashCode());
        result = prime * result + ((getDispatchMode() == null) ? 0 : getDispatchMode().hashCode());
        return result;
    }

    /**
     * This enum was generated by MyBatis Generator.
     * This enum corresponds to the database table dns-cheater..rule
     *
     * @mbg.generated
     * @project https://github.com/itfsw/mybatis-generator-plugin
     */
    public enum Column {
        id("id", "id", "BIGINT", false),
        ipFrom("ipFrom", "ipFrom", "BIGINT", false),
        ipTo("ipTo", "ipTo", "BIGINT", false),
        timeFrom("timeFrom", "timeFrom", "INTEGER", false),
        timeto("timeTo", "timeTo", "INTEGER", false),
        matchMode("matchMode", "matchMode", "VARCHAR", false),
        name("name", "name", "VARCHAR", true),
        priority("priority", "priority", "INTEGER", false),
        enabled("enabled", "enabled", "BIT", false),
        dispatchMode("dispatchMode", "dispatchMode", "VARCHAR", false);

        /**
         * This field was generated by MyBatis Generator.
         * This field corresponds to the database table dns-cheater..rule
         *
         * @mbg.generated
         * @project https://github.com/itfsw/mybatis-generator-plugin
         */
        private static final String BEGINNING_DELIMITER = "`";

        /**
         * This field was generated by MyBatis Generator.
         * This field corresponds to the database table dns-cheater..rule
         *
         * @mbg.generated
         * @project https://github.com/itfsw/mybatis-generator-plugin
         */
        private static final String ENDING_DELIMITER = "`";

        /**
         * This field was generated by MyBatis Generator.
         * This field corresponds to the database table dns-cheater..rule
         *
         * @mbg.generated
         * @project https://github.com/itfsw/mybatis-generator-plugin
         */
        private final String column;

        /**
         * This field was generated by MyBatis Generator.
         * This field corresponds to the database table dns-cheater..rule
         *
         * @mbg.generated
         * @project https://github.com/itfsw/mybatis-generator-plugin
         */
        private final boolean isColumnNameDelimited;

        /**
         * This field was generated by MyBatis Generator.
         * This field corresponds to the database table dns-cheater..rule
         *
         * @mbg.generated
         * @project https://github.com/itfsw/mybatis-generator-plugin
         */
        private final String javaProperty;

        /**
         * This field was generated by MyBatis Generator.
         * This field corresponds to the database table dns-cheater..rule
         *
         * @mbg.generated
         * @project https://github.com/itfsw/mybatis-generator-plugin
         */
        private final String jdbcType;

        /**
         * This method was generated by MyBatis Generator.
         * This method corresponds to the database table dns-cheater..rule
         *
         * @mbg.generated
         * @project https://github.com/itfsw/mybatis-generator-plugin
         */
        public String value() {
            return this.column;
        }

        /**
         * This method was generated by MyBatis Generator.
         * This method corresponds to the database table dns-cheater..rule
         *
         * @mbg.generated
         * @project https://github.com/itfsw/mybatis-generator-plugin
         */
        public String getValue() {
            return this.column;
        }

        /**
         * This method was generated by MyBatis Generator.
         * This method corresponds to the database table dns-cheater..rule
         *
         * @mbg.generated
         * @project https://github.com/itfsw/mybatis-generator-plugin
         */
        public String getJavaProperty() {
            return this.javaProperty;
        }

        /**
         * This method was generated by MyBatis Generator.
         * This method corresponds to the database table dns-cheater..rule
         *
         * @mbg.generated
         * @project https://github.com/itfsw/mybatis-generator-plugin
         */
        public String getJdbcType() {
            return this.jdbcType;
        }

        /**
         * This method was generated by MyBatis Generator.
         * This method corresponds to the database table dns-cheater..rule
         *
         * @mbg.generated
         * @project https://github.com/itfsw/mybatis-generator-plugin
         */
        Column(String column, String javaProperty, String jdbcType, boolean isColumnNameDelimited) {
            this.column = column;
            this.javaProperty = javaProperty;
            this.jdbcType = jdbcType;
            this.isColumnNameDelimited = isColumnNameDelimited;
        }

        /**
         * This method was generated by MyBatis Generator.
         * This method corresponds to the database table dns-cheater..rule
         *
         * @mbg.generated
         * @project https://github.com/itfsw/mybatis-generator-plugin
         */
        public String desc() {
            return this.getEscapedColumnName() + " DESC";
        }

        /**
         * This method was generated by MyBatis Generator.
         * This method corresponds to the database table dns-cheater..rule
         *
         * @mbg.generated
         * @project https://github.com/itfsw/mybatis-generator-plugin
         */
        public String asc() {
            return this.getEscapedColumnName() + " ASC";
        }

        /**
         * This method was generated by MyBatis Generator.
         * This method corresponds to the database table dns-cheater..rule
         *
         * @mbg.generated
         * @project https://github.com/itfsw/mybatis-generator-plugin
         */
        public static Column[] excludes(Column ... excludes) {
            ArrayList<Column> columns = new ArrayList<Column>(Arrays.asList(Column.values()));
            if (excludes != null && excludes.length > 0) {
                columns.removeAll(new ArrayList<Column>(Arrays.asList(excludes)));
            }
            return columns.toArray(new Column[]{});
        }

        /**
         * This method was generated by MyBatis Generator.
         * This method corresponds to the database table dns-cheater..rule
         *
         * @mbg.generated
         * @project https://github.com/itfsw/mybatis-generator-plugin
         */
        public String getEscapedColumnName() {
            if (this.isColumnNameDelimited) {
                return new StringBuilder().append(BEGINNING_DELIMITER).append(this.column).append(ENDING_DELIMITER).toString();
            } else {
                return this.column;
            }
        }
    }
}