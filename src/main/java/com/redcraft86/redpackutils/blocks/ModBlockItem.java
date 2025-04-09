package com.redcraft86.redpackutils.blocks;

import java.util.List;
import org.jetbrains.annotations.Nullable;
import net.minecraft.world.level.Level;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.block.Block;
import net.minecraft.network.chat.Component;
import com.redcraft86.redpackutils.util.ModUtils;

public class ModBlockItem extends BlockItem {
    protected final String blockID;
    protected final boolean bTooltip;

    public ModBlockItem(Block block, Properties properties, String ID, boolean bHasTooltip) {
        super(block, properties);
        blockID = ID;
        bTooltip = bHasTooltip;
    }

    public ModBlockItem(Block block, Properties properties, String itemID) {
        this(block, properties, itemID, false);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltipComponents, TooltipFlag isAdvanced) {
        super.appendHoverText(stack, level, tooltipComponents, isAdvanced);
        if (bTooltip && !blockID.isEmpty()) {
            tooltipComponents.add(ModUtils.getTooltip(blockID));
        }
    }
}
