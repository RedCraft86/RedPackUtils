package com.redcraft86.redpackutils.items;

import java.util.List;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;

import static com.redcraft86.redpackutils.util.ModUtils.processTooltipKeys;

public class ModItem extends Item {

    private final List<String> tooltipKeys;

    public ModItem(Properties properties, List<String> tooltipKeys) {
        super(properties);
        this.tooltipKeys = processTooltipKeys(tooltipKeys);
    }

    public ModItem(Properties properties) {
        this(properties, List.of());
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltipComponents, TooltipFlag isAdvanced) {
        super.appendHoverText(stack, level, tooltipComponents, isAdvanced);
        for (String tooltip : tooltipKeys) {
            tooltipComponents.add(Component.translatable(tooltip));
        }
    }
}
