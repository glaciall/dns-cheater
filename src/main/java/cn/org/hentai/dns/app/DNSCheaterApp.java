package cn.org.hentai.dns.app;

import cn.org.hentai.dns.util.BeanUtils;
import cn.org.hentai.dns.util.Configs;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.env.Environment;
import org.sqlite.SQLiteDataSource;

import javax.sql.DataSource;

/**
 * Created by matrixy on 2019/4/19.
 */
@ComponentScan(value = {"cn.org.hentai"})
@EnableAutoConfiguration
@SpringBootApplication
@MapperScan("cn.org.hentai.dns")
public class DNSCheaterApp
{
    @Autowired
    private Environment env;

    public static void main(String[] args) throws Exception
    {
        ApplicationContext context = SpringApplication.run(DNSCheaterApp.class, args);
        BeanUtils.init(context);
        Configs.init("/application.properties");
    }
}
