package com.wk.oauth.config;
import com.wk.oauth.util.UserJwt;
import com.wk.user.feign.UserFeign;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/*****
 * 自定义授权认证类
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    ClientDetailsService clientDetailsService;

    @Autowired
    private UserFeign userFeign;

    /****
     * 自定义授权认证
     * @param username
     * @return
     * @throws UsernameNotFoundException
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        /*---------------------------------客户端信息认证开始，授权码认证--------------------------------------*/
        //取出身份，如果身份为空说明没有认证
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        //没有认证统一采用httpbasic认证，httpbasic中存储了client_id和client_secret，开始认证client_id和client_secret
        if(authentication==null){
            //查询数据库表oauth_client_details对应的客户端数据
            ClientDetails clientDetails = clientDetailsService.loadClientByClientId(username);
            if(clientDetails!=null){
                //秘钥
                String clientSecret = clientDetails.getClientSecret();
                //静态方式
                /*return new User(
                        username,       //客户端ID
                        new BCryptPasswordEncoder().encode(clientSecret),       //客户端秘钥 加密操作
                        AuthorityUtils.commaSeparatedStringToAuthorityList(""));    //对应权限*/
                //数据库查找方式
                return new User(
                        username,       //客户端ID
                        clientSecret,   //客户端秘钥 数据库中的数据已加密
                        AuthorityUtils.commaSeparatedStringToAuthorityList(""));    //对应权限
            }
        }

        /*---------------------------------客户端信息认证结束--------------------------------------*/

        /*---------------------------------用户账号密码信息认证开始 账号密码认证--------------------------------------*/
        if (StringUtils.isEmpty(username)) {
            return null;
        }
        /**
         * 从数据库中查询对应的用户信息
         * 此时用户未登录，没有令牌，需要生成令牌才能调用用户服务
         * 令牌需要封装到头文件中，在访问各个微服务时携带
         * 在feign调用之前操作以上步骤，使用拦截器RequestInterceptor，在feign调用之前拦截
         */
        com.wk.user.entity.User user = userFeign.findById(username).getData();

        if (user == null) {
            return null;
        }

        //客户端ID：changgou
        //客户端秘钥：changgou
        //普通用户账号：任意账号，密码：szitheima

        //根据用户名查询用户信息
        //String pwd = new BCryptPasswordEncoder().encode("szitheima");
        String pwd = user.getPassword();
        //指定用户角色信息
        String permissions = "user,manager";

        UserJwt userDetails = new UserJwt(username,pwd,AuthorityUtils.commaSeparatedStringToAuthorityList(permissions));

        /*---------------------------------用户账号密码信息认证结束--------------------------------------*/
        return userDetails;
    }
}
