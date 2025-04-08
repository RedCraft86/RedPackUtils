package com.redcraft86.redpackutils.mixin;

import java.util.Iterator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.world.item.CreativeModeTab;

import com.redcraft86.redpackutils.RedPackUtilsConfig;

@Mixin(value = CreativeModeInventoryScreen.class)
public class CreativeInventoryScreenMixin {

    // The creative inventory displays a tooltip on items stating the creative tab said item originates from
    // This is what handles things such as "Functional Blocks" or "Redstone Blocks" on the tooltips
    // However, it also causes issues with mods that add mod name tooltips. Since modded items exist under
    // a tab with the mod name, if there's a mod that displays mod name tooltips, the mod name can get stacked twice
    @Redirect(method = "getTooltipFromContainerItem", at = @At(value = "INVOKE", target = "Ljava/util/Iterator;hasNext()Z"), require = 0)
    private boolean redirectHasNext(Iterator<CreativeModeTab> iterator) {
        return !RedPackUtilsConfig.disableCreativeTabTips && iterator.hasNext();
    }
}
