package com.redcraft86.redpackutils.mixin;

import java.util.Iterator;

import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import com.redcraft86.redpackutils.config.ClientConfig;

// Mainly for creative mode but we need this if there's a mod that adds a mod name tooltip to the items.
// Say there's a mod called 'RandomItems' and it adds a creative mode tab with the same name.
// If you put any of those items into your creative inventory, the creative tab name (RandomItems) will show up.
// If there's a mod that adds mod name tool tips, that will stack with the tab name. So you end up with the mod name displayed twice.
// This mixin stops minecraft from adding the tab name.
@Mixin(CreativeModeInventoryScreen.class)
public class CreativeInventoryScreenMixin {

    @Redirect(method = "getTooltipFromContainerItem", at = @At(value = "INVOKE", target = "Ljava/util/Iterator;hasNext()Z"), require = 0)
    private boolean redirectHasNext(Iterator<CreativeModeTab> iterator) {
        return !ClientConfig.disableCreativeTabTips && iterator.hasNext();
    }
}
