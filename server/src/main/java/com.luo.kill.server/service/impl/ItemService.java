package com.luo.kill.server.service.impl;

import com.luo.kill.model.entity.ItemKill;
import com.luo.kill.model.mapper.ItemKillMapper;
import com.luo.kill.server.service.IItemService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class ItemService implements IItemService {
    @Autowired
    ItemKillMapper itemKillMapper;

    @Override
    public List<ItemKill> getKillItems() {
        return itemKillMapper.selectAll();
    }
}
