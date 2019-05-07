package cn.org.hentai.dns.controller;

import cn.org.hentai.dns.entity.Page;
import cn.org.hentai.dns.entity.Result;
import cn.org.hentai.dns.entity.User;
import cn.org.hentai.dns.service.UserService;
import cn.org.hentai.dns.util.Nonce;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Created by matrixy on 2019/4/30.
 */
@Controller
public class UserController extends BaseController
{
    @Autowired
    UserService userService;

    @RequestMapping("/")
    public String index()
    {
        return "/login";
    }

    @RequestMapping("/login")
    @ResponseBody
    public Result login(@RequestParam String name,
                        @RequestParam String password,
                        HttpSession session,
                        HttpServletResponse response)
    {
        Result result = new Result();
        try
        {
            if (StringUtils.isEmpty(name)) throw new RuntimeException("请输入用户名");
            if (StringUtils.isEmpty(password)) throw new RuntimeException("请输入登陆密码");

            User user = userService.login(name, password, getIP());

            session.setAttribute("loginUser", user);
        }
        catch(Exception ex)
        {
            result.setError(ex);
        }
        return result;
    }

    @RequestMapping("/logout")
    public String logout(HttpSession session)
    {
        session.removeAttribute("loginUser");
        return "redirect:/";
    }

    @RequestMapping("/manage/user/passwd/reset")
    @ResponseBody
    public Result resetPassword(@RequestParam String oldPwd, @RequestParam String password, @RequestParam String password2)
    {
        Result result = new Result();
        try
        {
            if (StringUtils.isEmpty(oldPwd)) throw new RuntimeException("请输入旧密码");
            if (StringUtils.isEmpty(password)) throw new RuntimeException("请输入新的登陆密码");
            if (!password.equals(password2)) throw new RuntimeException("两次输入的新密码不一致");

            if (password.length() < 6 || password.length() > 16) throw new RuntimeException("新的密码应该在6~16个字符");

            User user = this.getLoginUser();
            if (userService.checkPassword(user, oldPwd) == false)
                throw new RuntimeException("旧的登陆密码不正确");

            user.setSalt(Nonce.generate(16));
            user.setPassword(userService.encode(password, user.getSalt()));

            userService.update(user);
        }
        catch(Exception ex)
        {
            result.setError(ex);
        }
        return result;
    }

    @RequestMapping("/manage/user/")
    public String userList()
    {
        return "/user/index";
    }

    @RequestMapping("/manage/user/json")
    @ResponseBody
    public Result userListJson(@RequestParam(defaultValue = "1") int pageIndex)
    {
        Result result = new Result();
        try
        {
            Page<User> users = userService.find(null, pageIndex, 20);
            for (User user : users.getList())
            {
                user.secure();
            }
            result.setData(users);
        }
        catch(Exception ex)
        {
            result.setError(ex);
        }
        return result;
    }

    @RequestMapping("/manage/user/create")
    @ResponseBody
    public Result createUser(@RequestParam String name, @RequestParam String password, @RequestParam String password2)
    {
        Result result = new Result();
        try
        {
            User user = this.getLoginUser();
            if (!"sa".equalsIgnoreCase(user.getType())) throw new RuntimeException("你无权进行此操作");

            if (StringUtils.isEmpty(name)) throw new RuntimeException("请输入账号名称");
            if (name.length() < 4 || name.length() > 20) throw new RuntimeException("账号名称必须是4~20个字符之间");
            if (StringUtils.isEmpty(password)) throw new RuntimeException("请输入账号密码");
            if (password.length() < 6 || password.length() > 16) throw new RuntimeException("登陆密码必须是6~16个字符之间");
            if (!password.equals(password2)) throw new RuntimeException("两次输入的密码不一致");

            userService.register(name, password);
        }
        catch(Exception ex)
        {
            result.setError(ex);
        }
        return result;
    }

    // 启用/禁用
    @RequestMapping("/manage/user/setEnable")
    @ResponseBody
    public Result setEnable(@RequestParam Long userId, @RequestParam Boolean enabled)
    {
        Result result = new Result();
        try
        {
            User user = this.getLoginUser();
            if (!"sa".equalsIgnoreCase(user.getType())) throw new RuntimeException("你无权进行此操作");

            User account = userService.getById(userId);
            if (account == null) throw new RuntimeException("查无此账号");

            account.setEnabled(enabled);
            userService.update(account);
        }
        catch(Exception ex)
        {
            result.setError(ex);
        }
        return result;
    }

    // 重置密码
    @RequestMapping("/manage/user/resetPassword")
    @ResponseBody
    public Result resetPassword(@RequestParam Long userId)
    {
        Result result = new Result();
        try
        {
            User user = this.getLoginUser();
            if (!"sa".equalsIgnoreCase(user.getType())) throw new RuntimeException("你无权进行此操作");

            User account = userService.getById(userId);
            if (null == account) throw new RuntimeException("查无此账号");

            account.setSalt(Nonce.generate(16));
            String password = String.valueOf(100000 + (int)(Math.random() * 899999));
            account.setPassword(userService.encode(password, account.getSalt()));
            userService.update(account);

            result.setData(password);
        }
        catch(Exception ex)
        {
            result.setError(ex);
        }
        return result;
    }

    // 删除账号
    @RequestMapping("/manage/user/remove")
    @ResponseBody
    public Result remove(@RequestParam Long userId)
    {
        Result result = new Result();
        try
        {
            User user = this.getLoginUser();
            if (!"sa".equalsIgnoreCase(user.getType())) throw new RuntimeException("你无权进行此操作");

            userService.remove(userId);
        }
        catch(Exception ex)
        {
            result.setError(ex);
        }
        return result;
    }
}
