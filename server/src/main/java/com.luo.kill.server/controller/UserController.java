package com.luo.kill.server.controller;

import com.luo.kill.model.dto.KillDto;
import com.luo.kill.server.service.impl.KillService;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class UserController {
    @Autowired
    private KillService killService;

    @RequestMapping(value = {"to/login", "/unauth"}, method = RequestMethod.GET)
    public String detail(KillDto killDto, ModelMap modelMap) {
        try {
            killService.kill(killDto.getKillId(), killDto.getUserId());
        } catch (Exception e) {
            return "redirect:base/error";
        }
        return "login";
    }

    @RequestMapping(value = "login", method = RequestMethod.GET)
    public String login(String username, String password, ModelMap modelMap) {
        String errMsg = "";
        try {
            if (SecurityUtils.getSubject().isAuthenticated()) {
                UsernamePasswordToken token = new UsernamePasswordToken(username, password);
                SecurityUtils.getSubject().login(token);
            }

        } catch (Exception e) {
            errMsg = "登陆异常";
        }
        if (StringUtils.isNotBlank(errMsg)) {
            return "redirect:base/error";
        }
        return "login";
    }

}
