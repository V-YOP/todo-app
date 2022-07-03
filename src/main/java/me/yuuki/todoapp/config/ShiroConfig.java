package me.yuuki.todoapp.config;

import me.yuuki.todoapp.service.realm.ShiroRealm;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.realm.Realm;
import org.apache.shiro.spring.web.config.DefaultShiroFilterChainDefinition;
import org.apache.shiro.spring.web.config.ShiroFilterChainDefinition;
import org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

/**
 * 对 Shiro 的配置，应尽量简单，不使用filter去直接拦截/login，/logout的请求，
 * 而是在控制器中直接依赖SecurityUtils去进行业务，使其尽量通俗易懂
 */
@Configuration
public class ShiroConfig {

    @Bean
    Realm realm() {
        return new ShiroRealm();
    }

    /**
     * 默认为允许匿名访问，实际需要权限通过注解给定
     */
    @Bean
    public ShiroFilterChainDefinition shiroFilterChainDefinition() {
        DefaultShiroFilterChainDefinition chainDefinition = new DefaultShiroFilterChainDefinition();
        chainDefinition.addPathDefinition("/**", "anon"); // all paths are managed via annotations
        return chainDefinition;
    }

    /**
     * 解决使用shiro注解时相应接口返回404的奇怪问题
     */
    @Bean
    public DefaultAdvisorAutoProxyCreator getDefaultAdvisorAutoProxyCreator(){

        DefaultAdvisorAutoProxyCreator defaultAdvisorAutoProxyCreator=new DefaultAdvisorAutoProxyCreator();
        defaultAdvisorAutoProxyCreator.setUsePrefix(true); // 解决使用shiro注解时相应接口返回404的奇怪问题
        defaultAdvisorAutoProxyCreator.setProxyTargetClass(true);
        return defaultAdvisorAutoProxyCreator;
    }

}
