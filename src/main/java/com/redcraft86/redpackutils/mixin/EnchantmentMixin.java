package com.redcraft86.redpackutils.mixin;

import com.redcraft86.redpackutils.config.CommonConfig;
import net.minecraft.world.item.enchantment.Enchantment;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Enchantment.class)
public class EnchantmentMixin {
    @Inject(method = "isCompatibleWith", at = @At("HEAD"), cancellable = true)
    private void onCompatWith(Enchantment other, CallbackInfoReturnable<Boolean> cir) {
        if (CommonConfig.mixEnchants) {
            cir.setReturnValue(true);
        }
    }
}
