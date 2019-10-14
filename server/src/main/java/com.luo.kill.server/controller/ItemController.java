package com.luo.kill.server.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@Slf4j
public class ItemController {

    private static final String prefix = "item";

    //获取待秒杀item
    @RequestMapping(value = {"/", "/index", prefix + "list", prefix + "/index.html"})
    public String list() {
        try {

        } catch (Exception e) {

            return "redirect:base/error";
        }
        return "list";
    }
}
