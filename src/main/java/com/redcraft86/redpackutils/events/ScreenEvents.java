package com.redcraft86.redpackutils.events;

import com.redcraft86.redpackutils.ModClass;
import com.redcraft86.redpackutils.systems.StartupSound;

import net.minecraft.client.gui.screens.TitleScreen;

import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = ModClass.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ScreenEvents {
    @SubscribeEvent
    public static void onScreenOpen(ScreenEvent.Opening event) {
        if (event.getNewScreen() instanceof TitleScreen) {
            StartupSound.Play();
        }
    }
}
