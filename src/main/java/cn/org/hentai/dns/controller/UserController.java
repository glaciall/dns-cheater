package cn.org.hentai.dns.controller;

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

    @RequestMapping("/user/passwd/reset")
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
}
