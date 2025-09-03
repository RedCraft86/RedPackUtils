package com.redcraft86.redpackutils.mixin;

import com.mojang.logging.LogUtils;
import com.redcraft86.redpackutils.config.CommonConfig;

import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.entity.CampfireBlockEntity;

import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.Map;

// This mixin allows the campfire effects feature to work
@Mixin(CampfireBlockEntity.class)
public class CampfireMixin {
    private static final Logger LOGGER = LogUtils.getLogger();

    @Inject(
            method = "cookTick",
            at = @At("HEAD")
    )
    private static void onCookTick(Level level, BlockPos pos, BlockState state, CampfireBlockEntity blockEntity, CallbackInfo ci) {
        if (!level.isClientSide() && CommonConfig.campfireEffRange > 0 && !CommonConfig.campfireEffects.isEmpty()) {
            applyEffects(level, pos);
        }
    }

    private static void applyEffects(Level level, BlockPos pos) {
        AABB box = new AABB(pos).inflate(Math.max(1, CommonConfig.campfireEffRange));
        List<Player> players = level.getEntitiesOfClass(Player.class, box);
        for (Player player : players) {
            if (player == null || player.isSpectator()) {
                continue;
            }

            if (CommonConfig.campfireClearHarm)
            {
                for (MobEffectInstance effect : player.getActiveEffects()) {
                    if (effect != null && effect.getEffect().getCategory() == MobEffectCategory.HARMFUL) {
                        player.removeEffect(effect.getEffect());
                    }
                }
            }

            for (Map.Entry<MobEffect, Integer> entry : CommonConfig.campfireEffects.entrySet()) {
                if (entry != null && entry.getKey() != null) {
                    player.addEffect(new MobEffectInstance(entry.getKey(), 40,
                            entry.getValue(), false, false, true));
                }
            }
        }
    }
}
