package com.redcraft86.redpackutils.events;

import com.mojang.logging.LogUtils;
import org.slf4j.Logger;

import com.redcraft86.redpackutils.ModClass;
import com.redcraft86.redpackutils.config.CommonConfig;
import com.redcraft86.redpackutils.systems.StructureSpawn;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.Level;

import net.minecraftforge.event.level.LevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = ModClass.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class LevelEvents {
    private static final Logger LOGGER = LogUtils.getLogger();

    // For Structure Spawnpoint
    @SubscribeEvent(receiveCanceled = true)
    public static void onWorldCreate(LevelEvent.CreateSpawnPosition e) {
        String structure = CommonConfig.structureSpawnPoint.trim();
        if (structure.isEmpty()) {
            LOGGER.info("[RedPackUtils: Structure Spawn Point] Feature is disabled, spawning in normally...");
            return;
        } else if (!structure.contains(":")) {
            LOGGER.error("[RedPackUtils: Structure Spawn Point] ID/Tag is invalid, spawning in normally...");
            return;
        }

        LevelAccessor levelAccessor = e.getLevel();
        if (!levelAccessor.isClientSide() && levelAccessor instanceof Level level) {
            if (StructureSpawn.trySetSpawnPoint((ServerLevel)level, structure)) {
                e.setCanceled(true);
            }
        }
    }
}
