package com.redcraft86.redpackutils.mixin;

import com.redcraft86.redpackutils.ModGameRules;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Player.class)
public class PlayerMixin {

    // These two mixins remove the damage strength cooldown
    @Inject(method = "resetAttackStrengthTicker", at = @At("HEAD"), cancellable = true)
    public void cancelStrengthTicker(CallbackInfo ci) {
        if (((Player)(Object)this).level().getGameRules().getBoolean(ModGameRules.NO_ATTACK_COOLDOWN)) {
            ci.cancel();
        }
    }

    @Inject(method = "getAttackStrengthScale", at = @At("HEAD"), cancellable = true)
    public void getMaxAtkStrength(float adjustTicks, CallbackInfoReturnable<Float> cir) {
        if (((Player)(Object)this).level().getGameRules().getBoolean(ModGameRules.NO_ATTACK_COOLDOWN)) {
            cir.setReturnValue(1.0f);
        }
    }

    // This is a tick check that prevents regen and poison from being applied at the same time
    @Inject(method = "tick", at = @At("TAIL"))
    public void onTick(CallbackInfo ci) {
        Player player = (Player)(Object)this;

        MobEffectInstance poison = player.getEffect(MobEffects.POISON);
        MobEffectInstance regen = player.getEffect(MobEffects.REGENERATION);

        if (poison != null && regen != null) {
            int durationDiff = poison.getDuration() - regen.getDuration();
            int amplifierDiff = Math.min(Math.abs(poison.getAmplifier() - regen.getAmplifier()), 255);

            player.removeEffect(MobEffects.POISON);
            player.removeEffect(MobEffects.REGENERATION);

            if (durationDiff != 0) {
                MobEffect effectToKeep = durationDiff > 0 ? MobEffects.POISON : MobEffects.REGENERATION;

                if (amplifierDiff > 0) {
                    player.addEffect(new MobEffectInstance(effectToKeep, Math.abs(durationDiff), amplifierDiff));
                }
            }
        }
    }
}