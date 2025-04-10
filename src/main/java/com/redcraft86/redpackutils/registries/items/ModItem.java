package com.redcraft86.redpackutils.registries.items;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import com.redcraft86.redpackutils.util.ModUtils;

public class ModItem extends Item {
    public Map<String, Component> tooltips = new TreeMap<>();

    public ModItem(Properties properties) {
        super(properties);
    }

    public MutableComponent addTooltip(String key) {
        MutableComponent newTooltip = ModUtils.makeTooltip(key);
        tooltips.put(key, newTooltip);
        return newTooltip;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltipComponents, TooltipFlag isAdvanced) {
        super.appendHoverText(stack, level, tooltipComponents, isAdvanced);
        tooltipComponents.addAll(tooltips.values());
    }
}
