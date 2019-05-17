package cn.org.hentai.dns.service;

import cn.org.hentai.dns.dao.UserMapper;
import cn.org.hentai.dns.entity.Page;
import cn.org.hentai.dns.entity.User;
import cn.org.hentai.dns.entity.UserExample;
import cn.org.hentai.dns.util.MD5;
import cn.org.hentai.dns.util.Nonce;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.List;

/**
 * Created by matrixy on 2019/4/30.
 */
@Service
public class UserService
{
    @Autowired
    UserMapper userMapper;

    private Long create(User user)
    {
        userMapper.insert(user);
        return user.getId();
    }

    public int update(User user)
    {
        return userMapper.updateByPrimaryKey(user);
    }

    public int remove(User user)
    {
        return remove(user.getId());
    }

    public int remove(Long id)
    {
        return userMapper.deleteByPrimaryKey(id);
    }

    public User getById(Long id)
    {
        return userMapper.selectByPrimaryKey(id);
    }

    public User getByName(String name)
    {
        List<User> userList = userMapper.selectByExample(new UserExample().createCriteria().andNameEqualTo(name).example());
        if (userList.size() > 0) return userList.get(0);
        else return null;
    }

    public User login(String name, String password, String ip)
    {
        User user = getByName(name);
        if (null == user) throw new RuntimeException("用户名或密码错误");
        if (user.getEnabled() == false) throw new RuntimeException("账号已被禁用");

        if (!user.getPassword().equals(encode(password, user.getSalt()))) throw new RuntimeException("用户名或密码错误");

        String nonce = Nonce.generate(16);
        String accesstoken = encode(ip, nonce);
        user.setAccesstoken(accesstoken);
        user.setNonce(nonce);
        user.setLastLoginIP(ip);
        user.setLastLoginTime(new Date());

        update(user);

        return user;
    }

    public User register(String name, String password)
    {
        User user = new User();
        user.setName(name);
        user.setSalt(Nonce.generate(16));
        user.setPassword(encode(password, user.getSalt()));
        user.setType("admin");
        user.setEnabled(true);

        create(user);

        return user;
    }

    public boolean checkPassword(User user, String password)
    {
        return encode(password, user.getSalt()).equals(user.getPassword());
    }

    public String encode(String text, String nonce)
    {
        return MD5.encode(text + ":::" + nonce);
    }

    public Page<User> find(String name, int pageIndex, int pageSize)
    {
        Page<User> page = new Page<User>(pageIndex, pageSize);
        UserExample.Criteria criteria = UserExample.newAndCreateCriteria();
        if (!StringUtils.isEmpty(name)) criteria.andNameLike(name);
        criteria.example().setOrderByClause(User.Column.id.desc());
        criteria.example().setPageInfo(pageIndex, pageSize);
        page.setList(userMapper.selectByExample(criteria.example()));
        page.setRecordCount(userMapper.countByExample(criteria.example()));

        return page;
    }
}
