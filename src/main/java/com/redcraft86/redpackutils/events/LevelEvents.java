package com.redcraft86.redpackutils.events;

import com.redcraft86.redpackutils.ModClass;

import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = ModClass.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class LevelEvents {
    @SubscribeEvent
    public static void onServerStart(ServerStartedEvent event) {
    }
}
