package com.redcraft86.redpackutils.mixin;

import java.util.Iterator;

import com.redcraft86.redpackutils.config.ClientConfig;

import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(CreativeModeInventoryScreen.class)
public class CreativeInventoryMixin {
    @Redirect(method = "getTooltipFromContainerItem", at = @At(value = "INVOKE", target = "Ljava/util/Iterator;hasNext()Z"), require = 0)
    private boolean redirectHasNext(Iterator<CreativeModeTab> iterator) {
        return !ClientConfig.noCreativeTabTips && iterator.hasNext();
    }
}
