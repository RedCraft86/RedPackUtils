package com.redcraft86.redpackutils.events;

import com.redcraft86.redpackutils.ModClass;
import com.redcraft86.redpackutils.systems.StructureSpawn;

import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;

import net.minecraftforge.event.level.LevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = ModClass.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class LevelEvents {
    @SubscribeEvent
    public static void onWorldCreate(LevelEvent.CreateSpawnPosition e) {
        LevelAccessor levelAccessor = e.getLevel();
        if (levelAccessor instanceof Level level) {
            StructureSpawn.onWorldCreate(level);
        }
    }
}
