package com.luo.kill.server.controller;

import com.luo.kill.model.entity.ItemKill;
import com.luo.kill.server.service.IItemService;
import com.luo.kill.server.service.impl.ItemService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;

@Controller
@Slf4j
public class ItemController {

    private static final String prefix = "item";

    @Autowired
    IItemService itemService;
    //获取待秒杀item
    @RequestMapping(value = {"/", "/index", prefix + "list", prefix + "/index.html"}, method = RequestMethod.GET)
    public String list(ModelMap modelMap) {
        try {
            List<ItemKill> killItems = itemService.getKillItems();
            modelMap.put("list", killItems);
            // log.info("list:{}",killItems);
        } catch (Exception e) {
            return "redirect:base/error";
        }
        return "list";
    }

    @RequestMapping(value = prefix + "detail/{id}", method = RequestMethod.GET)
    public String detail(@PathVariable Integer id) {
        try {
            List<ItemKill> killItems = itemService.getKillItems();
            // modelMap.put("list", killItems);
            // log.info("list:{}",killItems);
        } catch (Exception e) {
            return "redirect:base/error";
        }
        return "info";
    }

}
