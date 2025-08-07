package com.redcraft86.redpackutils.mixin;

import com.redcraft86.redpackutils.config.CommonConfig;

import net.minecraft.world.entity.player.Player;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

// This mixin removes the damage strength cooldown
@Mixin(Player.class)
public class PlayerMixin {
    @Inject(method = "resetAttackStrengthTicker",at = @At("HEAD"), cancellable = true)
    public void cancelStrengthTicker(CallbackInfo ci) {
        if (CommonConfig.noAtkCooldown) {
            ci.cancel();
        }
    }

    @Inject(method = "getAttackStrengthScale",at = @At("HEAD"), cancellable = true)
    public void getMaxAtkStrength(float adjustTicks, CallbackInfoReturnable<Float> cir) {
        if (CommonConfig.noAtkCooldown) {
            cir.setReturnValue(1.0f);
        }
    }
}