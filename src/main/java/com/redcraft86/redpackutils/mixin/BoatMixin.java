package com.redcraft86.redpackutils.mixin;

import com.redcraft86.redpackutils.config.CommonConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Boat.class)
public class BoatMixin {
    @Inject(method = "checkFallDamage", at = @At("HEAD"), cancellable = true)
    private void removeFallDmg(double Y, boolean onGround, BlockState state, BlockPos pos, CallbackInfo ci) {
        if (CommonConfig.noBoatFallDmg && onGround) {
            ci.cancel();
        }
    }
}
