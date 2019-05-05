package cn.org.hentai.dns.controller;

import cn.org.hentai.dns.entity.Result;
import cn.org.hentai.dns.entity.User;
import cn.org.hentai.dns.service.UserService;
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
}
