package sunnn.sunsite.config;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import sunnn.sunsite.dao.MeDao;

import java.util.LinkedHashMap;
import java.util.Map;

@Configuration
public class ShiroConfiguration {

    @Bean
    public ShiroFilterFactoryBean shirFilter(org.apache.shiro.mgt.SecurityManager securityManager) {
        ShiroFilterFactoryBean shiroFilterFactoryBean = new ShiroFilterFactoryBean();
        shiroFilterFactoryBean.setSecurityManager(securityManager);

        Map<String, String> filterChainDefinitionMap = new LinkedHashMap<>();
        /*
            顺序拦截
         */
        filterChainDefinitionMap.put("/hello", "anon");
        filterChainDefinitionMap.put("/verify", "anon");
        filterChainDefinitionMap.put("/error", "anon");
        filterChainDefinitionMap.put("/favicon.ico", "anon");   //我能怎么办也很绝望啊
        filterChainDefinitionMap.put("/**", "authc");
//        filterChainDefinitionMap.put("/index", "logout");

        shiroFilterFactoryBean.setLoginUrl("/index");
        shiroFilterFactoryBean.setSuccessUrl("/home");
        shiroFilterFactoryBean.setUnauthorizedUrl("/error");

        shiroFilterFactoryBean.setFilterChainDefinitionMap(filterChainDefinitionMap);

        return shiroFilterFactoryBean;
    }

    @Bean
    public org.apache.shiro.mgt.SecurityManager securityManager(SunRealm realm) {
        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
        securityManager.setRealm(realm);
        return securityManager;
    }

    @Bean
    public SunRealm myShiroRealm() {
        return new SunRealm();
    }
}

class SunRealm extends AuthorizingRealm {

    @Autowired
    private MeDao meDao;

    @Override
    public String getName() {
        return "SunRealm";
    }

    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
        return null;
    }

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {
        String passCode = meDao.getCorrectMe().getPassCode();
        return new SimpleAuthenticationInfo(
                "", //个人系统验证不需要用户名
                passCode,
                getName());
    }
}
