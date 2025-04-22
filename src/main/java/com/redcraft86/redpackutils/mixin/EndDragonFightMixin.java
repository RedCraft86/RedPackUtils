package com.redcraft86.redpackutils.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.level.dimension.end.EndDragonFight;
import com.redcraft86.redpackutils.config.CommonConfig;

@SuppressWarnings("FieldCanBeLocal")
@Mixin(EndDragonFight.class)
public class EndDragonFightMixin {
    @Shadow
    private boolean previouslyKilled;

    // Always make sure a Dragon Egg will spawn on the podium after a
    // fight with the Ender Dragon even if it is no longer the first fight
    @Inject(method = "setDragonKilled", at = @At("HEAD"), require = 0)
    private void alwaysSpawnDragonEgg(EnderDragon dragon, CallbackInfo ci) {
        if (CommonConfig.alwaysDragonEgg) {
            previouslyKilled = false;
        }
    }
}
