package cn.org.hentai.dns.controller;

import cn.org.hentai.dns.entity.User;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by matrixy on 2019/4/30.
 */

public class BaseController
{
    @Autowired
    HttpServletRequest request;

    @Autowired
    HttpServletResponse response;

    public User getLoginUser()
    {
        return (User) request.getAttribute("loginUser");
    }

    protected final String getIP()
    {
        String addr = request.getHeader("X-Forwarded-For");
        if (null == addr) return request.getRemoteAddr();
        if (addr.indexOf(',') == -1) return addr;
        else return addr.substring(0, addr.indexOf(',')).trim();
    }
}
