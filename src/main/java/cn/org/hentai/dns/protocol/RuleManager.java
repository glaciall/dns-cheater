package cn.org.hentai.dns.protocol;

import cn.org.hentai.dns.entity.Address;
import cn.org.hentai.dns.entity.Rule;
import cn.org.hentai.dns.service.AddressService;
import cn.org.hentai.dns.service.RuleService;
import cn.org.hentai.dns.util.BeanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Created by matrixy on 2019/5/3.
 * 域名解析规则管理器
 */
public final class RuleManager
{
    static Logger logger = LoggerFactory.getLogger(RuleManager.class);

    ConcurrentLinkedQueue<Rule> rules;

    private RuleManager()
    {
        this.rules = new ConcurrentLinkedQueue<Rule>();
    }

    // 匹配是否有已经设定的解析规则，如果有，则根据分发模式给出应答地址，否则返回null交由上游DNS服务器进行解答
    public Address matches(int now, long ip, String domainName)
    {
        for (Rule rule : rules)
        {
            if (rule.getEnabled() && rule.matches(now, ip, domainName))
            {
                return rule.dispatchAddress(ip);
            }
        }
        return null;
    }

    public void remove(Rule rule)
    {
        rules.remove(rule);
    }

    public void enable(Long ruleId)
    {
        for (Rule rule : rules)
        {
            if (rule.getId().equals(ruleId))
            {
                rule.setEnabled(true);
                break;
            }
        }
    }

    public void disable(Long ruleId)
    {
        for (Rule rule : rules)
        {
            if (rule.getId().equals(ruleId))
            {
                rule.setEnabled(false);
                break;
            }
        }
    }

    public void add(Rule rule)
    {
        this.rules.add(rule);
    }

    // 初始化，从数据库中加载全部设定的应答规则
    public void init()
    {
        RuleService ruleService = null;
        AddressService addrService = null;
        try
        {
            ruleService = BeanUtils.create(RuleService.class);
            addrService = BeanUtils.create(AddressService.class);

            List<Rule> ruleList = ruleService.find(1, Integer.MAX_VALUE).getList();
            for (int i = ruleList.size() - 1; i >= 0; i--)
            {
                Rule rule = ruleList.get(i);
                rule.setAddresses(addrService.find(rule.getId()));
                this.rules.add(rule);
            }

            logger.info("load {} rules from database...", rules.size());
        }
        catch(Exception ex)
        {
            throw new RuntimeException(ex);
        }
        finally
        {
            try { BeanUtils.destroy(ruleService); } catch(Exception e) { }
            try { BeanUtils.destroy(addrService); } catch(Exception e) { }
        }
    }

    static RuleManager instance = null;
    public static synchronized RuleManager getInstance()
    {
        if (null == instance) instance = new RuleManager();
        return instance;
    }
}
