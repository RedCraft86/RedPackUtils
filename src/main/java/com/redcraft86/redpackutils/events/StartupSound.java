package com.redcraft86.redpackutils.events;

import com.mojang.logging.LogUtils;
import com.redcraft86.redpackutils.ModClass;
import com.redcraft86.redpackutils.config.ClientConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;
import org.slf4j.Logger;

import java.util.List;
import java.util.Random;

@Mod.EventBusSubscriber(modid = ModClass.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class StartupSound {
    private static boolean hasPlayedStartup = false;
    private static final Random RANDOM = new Random();
    private static final Logger LOGGER = LogUtils.getLogger();

    @SubscribeEvent
    public static void onScreenOpen(ScreenEvent.Opening event) {
        if (event.getNewScreen() instanceof TitleScreen && !hasPlayedStartup) {
            hasPlayedStartup = true;
            List<? extends String> soundList = ClientConfig.startupSounds;
            if (soundList != null && !soundList.isEmpty()) {
                String entry = soundList.get(RANDOM.nextInt(0, soundList.size()));
                String[] parts = entry.split(" ", 2);
                if (parts.length != 2){
                    LOGGER.warn("[RedPackUtils: Startup Sound] Invalid entry: {}", entry);
                    return;
                }

                String id = parts[0].trim(), vol = parts[1].trim();
                try {
                    float volume = Math.max(0, Float.parseFloat(vol));
                    if (volume <= 0){
                        LOGGER.warn("[RedPackUtils: Startup Sound] Volume for {} is 0 or less", id);
                        return;
                    }

                    ResourceLocation sound = new ResourceLocation(id);
                    SoundEvent soundEvent = ForgeRegistries.SOUND_EVENTS.getValue(sound);
                    if (soundEvent != null) {
                        Minecraft.getInstance().getSoundManager().playDelayed(
                                SimpleSoundInstance.forUI(soundEvent, 1, volume), 50);
                    } else {
                        LOGGER.error("[RedPackUtils: Startup Sound] Unknown sound: {}", id);
                    }

                } catch (NumberFormatException e) {
                    LOGGER.error("[RedPackUtils: Startup Sound] Invalid volume number: {}", vol);
                }
            }
        }
    }
}
