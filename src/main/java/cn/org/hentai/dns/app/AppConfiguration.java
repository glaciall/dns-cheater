package cn.org.hentai.dns.app;

import cn.org.hentai.dns.interceptor.CommonInterceptor;
import cn.org.hentai.dns.interceptor.UserInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/**
 * Created by matrixy on 2017/12/13.
 */
@Configuration
public class AppConfiguration extends WebMvcConfigurerAdapter
{
    @Override
    public void addInterceptors(InterceptorRegistry registry)
    {
        registry.addInterceptor(new CommonInterceptor()).addPathPatterns("/**");
        registry.addInterceptor(new UserInterceptor()).addPathPatterns("/manage/**").addPathPatterns("/user/logout").addPathPatterns("/user/passwd/reset");
        super.addInterceptors(registry);
    }
}
