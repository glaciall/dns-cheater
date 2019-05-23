package cn.org.hentai.dns.app;

import cn.org.hentai.dns.dns.NameServer;
import cn.org.hentai.dns.dns.RecursiveResolver;
import cn.org.hentai.dns.dns.RuleManager;
import cn.org.hentai.dns.util.BeanUtils;
import cn.org.hentai.dns.util.Configs;
import org.mybatis.spring.annotation.MapperScan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.env.Environment;

/**
 * Created by matrixy on 2019/4/19.
 */
@ComponentScan(value = {"cn.org.hentai"})
@SpringBootApplication
@MapperScan("cn.org.hentai.dns")
public class DNSCheaterApp
{
    static Logger logger = LoggerFactory.getLogger(DNSCheaterApp.class);

    public static void main(String[] args) throws Exception
    {
        ApplicationContext context = SpringApplication.run(DNSCheaterApp.class, args);
        BeanUtils.init(context);
        Configs.init(context);

        RuleManager.getInstance().init();
        NameServer.getInstance().init();
        RecursiveResolver.getInstance().init();
    }
}
