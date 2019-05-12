package cn.org.hentai.dns.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Created by matrixy on 2019/5/1.
 */
@Controller
@RequestMapping("/manage")
public class PortalController
{
    @RequestMapping("/")
    public String index()
    {
        return "redirect:/manage/stat/";
    }
}
