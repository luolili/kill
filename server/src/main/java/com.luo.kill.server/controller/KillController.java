package com.luo.kill.server.controller;

import com.luo.kill.model.dto.KillDto;
import com.luo.kill.model.entity.ItemKill;
import com.luo.kill.server.service.impl.KillService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class KillController {
    @Autowired
    private KillService killService;

    @RequestMapping(value = "detail/{id}", method = RequestMethod.GET)
    public String detail(KillDto killDto, ModelMap modelMap) {
        try {
            killService.kill(killDto.getKillId(), killDto.getUserId());
            // log.info("list:{}",killItems);
        } catch (Exception e) {
            return "redirect:base/error";
        }
        return "info";
    }

}
