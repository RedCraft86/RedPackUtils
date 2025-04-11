package com.redcraft86.redpackutils.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import net.minecraft.world.entity.ai.goal.TemptGoal;
import com.redcraft86.redpackutils.config.CommonConfig;

@Mixin(TemptGoal.class)
public class TemptGoalMixin {
    @Shadow private int calmDown;

    // When you 'tempt' an animal with a food item and either break line of sight or hide it,
    // the animal will no longer get tempted for a set cooldown even if you bring the item back.
    // This mixin hooks into the Goal class that does tha and reset the cooldown.
    @Inject(method = "stop()V", at = @At(value = "TAIL"))
    public void resetTemptCooldown(CallbackInfo ci) {
        if (CommonConfig.noTemptCooldown) {
            calmDown = 0;
        }
    }
}
