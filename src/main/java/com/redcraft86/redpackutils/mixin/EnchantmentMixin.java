package com.redcraft86.redpackutils.mixin;

import com.redcraft86.redpackutils.config.CommonConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import net.minecraft.world.item.enchantment.Enchantment;

@Mixin(Enchantment.class)
public class EnchantmentMixin {
    @Inject(method = "canCombine", at = @At("HEAD"), cancellable = true)
    private void combineEnchant(Enchantment other, CallbackInfoReturnable<Boolean> ci){
        if (CommonConfig.combineAnyEnchant && other != (Object)this) {
            ci.cancel();
            ci.setReturnValue(true);
        }
    }
}
