package com.redcraft86.redpackutils.blocks;

import java.util.List;
import net.minecraft.world.level.Level;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.Nullable;

import static com.redcraft86.redpackutils.util.ModUtils.processTooltipKeys;

public class ModBlockItem extends BlockItem {

    private final List<String> tooltipKeys;

    public ModBlockItem(Block block, Properties properties) {
        super(block, properties);
        this.tooltipKeys = processTooltipKeys(block instanceof ModBlock modBlock
                ? modBlock.getTooltipKeys() : List.of());
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltipComponents, TooltipFlag isAdvanced) {
        super.appendHoverText(stack, level, tooltipComponents, isAdvanced);
        for (String tooltip : tooltipKeys) {
            tooltipComponents.add(Component.translatable(tooltip));
        }
    }
}