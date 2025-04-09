package com.redcraft86.redpackutils.registries.blocks;

import java.util.Map;
import java.util.TreeMap;
import net.minecraft.world.level.block.Block;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import com.redcraft86.redpackutils.util.ModUtils;

public class ModBlock extends Block {
    public Map<String, Component> tooltips = new TreeMap<>();

    public ModBlock(Properties properties) {
        super(properties);
    }

    public MutableComponent addTooltip(String key) {
        MutableComponent newTooltip = ModUtils.makeTooltip(key);
        tooltips.put(key, newTooltip);
        return newTooltip;
    }
}
