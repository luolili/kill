package com.luo.kill.server.service;

import com.luo.kill.model.entity.ItemKill;

import java.util.List;

public interface IItemService {
    List<ItemKill> getKillItems();

    ItemKill getKillDetail(Integer id) throws Exception;
}
