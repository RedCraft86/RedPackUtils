package com.redcraft86.redpackutils.systems;

import com.redcraft86.redpackutils.ModClass;
import com.redcraft86.redpackutils.config.ClientConfig;

import com.mojang.logging.LogUtils;
import org.slf4j.Logger;

import java.util.Map;
import java.util.List;
import java.util.Random;
import java.util.ArrayList;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;

import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = ModClass.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class StartupSound {
    private static boolean hasPlayed = false;
    private static final Random RANDOM = new Random();
    private static final Logger LOGGER = LogUtils.getLogger();

    @SubscribeEvent
    void onScreenOpen(ScreenEvent.Opening event) {
        if (!hasPlayed && event.getNewScreen() instanceof TitleScreen) {
            hasPlayed = true;

            // Pick random
            List<Map.Entry<ResourceLocation, Float>> list = new ArrayList<>(ClientConfig.startupSounds.entrySet());
            Map.Entry<ResourceLocation, Float> entry = list.get(RANDOM.nextInt(list.size()));

            // Try playing
            SoundEvent soundEvent = ForgeRegistries.SOUND_EVENTS.getValue(entry.getKey());
            if (soundEvent != null) {
                Minecraft.getInstance().getSoundManager().playDelayed(
                        SimpleSoundInstance.forUI(soundEvent, 1, entry.getValue()), 50);
            } else {
                LOGGER.error("[RedPackUtils: Startup Sound] Unknown sound: {}", entry.getKey());
            }
        }
    }
}
