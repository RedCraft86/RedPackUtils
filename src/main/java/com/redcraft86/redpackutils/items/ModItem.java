package com.redcraft86.redpackutils.items;

import java.util.List;
import org.jetbrains.annotations.Nullable;
import net.minecraft.world.level.Level;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.network.chat.Component;
import com.redcraft86.redpackutils.util.ModUtils;


public class ModItem extends Item {
    protected final String itemID;
    protected final boolean bTooltip;

    public ModItem(Properties properties, String ID, boolean bHasTooltip) {
        super(properties);
        itemID = ID;
        bTooltip = bHasTooltip;
    }

    public ModItem(Properties properties, String itemID) {
        this(properties, itemID, false);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltipComponents, TooltipFlag isAdvanced) {
        super.appendHoverText(stack, level, tooltipComponents, isAdvanced);
        if (bTooltip && !itemID.isEmpty()) {
            tooltipComponents.add(ModUtils.getTooltip(itemID));
        }
    }
}
