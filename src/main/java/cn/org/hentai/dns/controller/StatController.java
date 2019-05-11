package cn.org.hentai.dns.controller;

import cn.org.hentai.dns.cache.CacheManager;
import cn.org.hentai.dns.entity.Result;
import cn.org.hentai.dns.stat.StatManager;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by matrixy on 2019/5/10.
 */
@Controller
@RequestMapping("/manage/stat")
public class StatController extends BaseController
{
    @RequestMapping("/")
    public String index()
    {
        return "/stat/index";
    }

    // 几个计数项：总查询次数、总应答次数、总回源查询次数、当前缓存量
    @RequestMapping("/summary")
    @ResponseBody
    public Result summary()
    {
        Result result = new Result();
        try
        {
            StatManager mgr = StatManager.getInstance();
            result.setData(Result.values("totalQueryCount", mgr.getTotalQueryCount(),
                        "totalAnswerCount", mgr.getTotalAnswerCount(),
                        "totalQueryUpstreamCount", mgr.getTotalQueryUpstreamCount(),
                        "totalCachedCount", CacheManager.getInstance().getCachedCount(),
                        "totalQueryNames", mgr.getTotalQueryNames()
                    ));
        }
        catch(Exception ex)
        {
            result.setError(ex);
        }
        return result;
    }

    // 当天每分钟的查询量
    @RequestMapping("/everyMinuteQueries")
    @ResponseBody
    public Result everyMinuteQueries()
    {
        Result result = new Result();
        try
        {
            result.setData(StatManager.getInstance().getEveryMinuteQueryCount());
        }
        catch(Exception ex)
        {
            result.setError(ex);
        }
        return result;
    }

    // 当天查询来源IP TOP N
    @RequestMapping("/topQueryClients")
    @ResponseBody
    public Result topQueryClients(@RequestParam(defaultValue = "10") int count)
    {
        Result result = new Result();
        try
        {
            result.setData(StatManager.getInstance().findTopClients(count));
        }
        catch(Exception ex)
        {
            result.setError(ex);
        }
        return result;
    }

    // 当天查询域名TOP N
    @RequestMapping("/topQueryNames")
    @ResponseBody
    public Result topQueryNames(@RequestParam(defaultValue = "10") int count)
    {
        Result result = new Result();
        try
        {
            result.setData(StatManager.getInstance().findTopNames(count));
        }
        catch(Exception ex)
        {
            result.setError(ex);
        }
        return result;
    }
}
