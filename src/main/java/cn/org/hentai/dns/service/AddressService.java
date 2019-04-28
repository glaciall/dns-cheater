package cn.org.hentai.dns.service;

import cn.org.hentai.dns.dao.AddressMapper;
import cn.org.hentai.dns.entity.Address;
import cn.org.hentai.dns.entity.AddressExample;
import cn.org.hentai.dns.entity.Rule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by matrixy on 2019/4/28.
 */
@Service
public class AddressService
{
    @Autowired
    AddressMapper addrMapper;

    public Long create(Address addr)
    {
        addrMapper.insert(addr);
        return addr.getId();
    }

    public int update(Address addr)
    {
        return addrMapper.updateByPrimaryKey(addr);
    }

    public int remove(Address addr)
    {
        return addrMapper.deleteByPrimaryKey(addr.getId());
    }

    public int remove(Rule rule)
    {
        return addrMapper.deleteByExample(new AddressExample().createCriteria().andRuleIdEqualTo(rule.getId()).example());
    }

    public List<Address> find(Long ruleId)
    {
        return addrMapper.selectByExample(new AddressExample().createCriteria().andRuleIdEqualTo(ruleId).example());
    }
}
