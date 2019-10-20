package com.luo.kill.server.config;

import com.luo.kill.model.entity.User;
import com.luo.kill.model.mapper.UserMapper;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Objects;

public class CustomRealm extends AuthorizingRealm {

    @Autowired
    private UserMapper userMapper;
    private static final int SESSION_TIMEOUT = 200;

    //授权
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {

        return null;
    }

    //认证
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {
        UsernamePasswordToken token = (UsernamePasswordToken) authenticationToken;
        String username = token.getUsername();
        String password = String.valueOf(token.getPassword());
        User user = userMapper.selectByUserName(username);
        if (user == null) {
            throw new UnknownAccountException("user 不存在");
        }
        if (!Objects.equals(1, user.getIsActive().intValue())) {
            throw new DisabledAccountException("user disable");
        }
        if (!user.getPassword().equals(password)) {
            throw new IncorrectCredentialsException("");
        }
        SimpleAuthenticationInfo simpleAuthenticationInfo = new SimpleAuthenticationInfo(user.getUserName(), password, getName());

        setSession("uid", user.getId());
        return null;
    }

    private void setSession(String key, Object value) {
        Session session = SecurityUtils.getSubject().getSession();
        if (session != null) {
            session.setAttribute(key, value);
            session.setTimeout(SESSION_TIMEOUT);
        }


    }
}
