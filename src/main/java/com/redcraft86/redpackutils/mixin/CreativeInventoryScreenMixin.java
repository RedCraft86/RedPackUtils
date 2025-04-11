package com.redcraft86.redpackutils.mixin;

import java.util.Iterator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import com.redcraft86.redpackutils.config.ClientConfig;

@Mixin(CreativeModeInventoryScreen.class)
public class CreativeInventoryScreenMixin {

    // Mainly for creative mode but we need this if there's a mod that adds a mod name tooltip to the items.
    // Say there's a mod called 'RandomItems' and it adds a creative mode tab with the same name.
    // If you put any of those items into your creative inventory, the creative tab name (RandomItems) will show up.
    // If there's a mod that adds mod name tool tips, which is necessary for things like JEI in survival,
    // That will stack with the tab name. So you end up with the mod name displayed twice.
    // That is why this mixin exist.
    @Redirect(method = "getTooltipFromContainerItem", at = @At(value = "INVOKE", target = "Ljava/util/Iterator;hasNext()Z"), require = 0)
    private boolean redirectHasNext(Iterator<CreativeModeTab> iterator) {
        return !ClientConfig.disableCreativeTabTips && iterator.hasNext();
    }
}
