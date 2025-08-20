package com.redcraft86.redpackutils.events;

import com.redcraft86.redpackutils.ModClass;
import com.redcraft86.redpackutils.systems.StructureSpawn;

import net.minecraftforge.event.level.LevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = ModClass.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class LevelEvents {
    @SubscribeEvent(receiveCanceled = true)
    public static void onWorldCreate(LevelEvent.CreateSpawnPosition e) {
        if (StructureSpawn.onWorldCreate(e.getLevel())) {
            e.setCanceled(true);
        }
    }
}
