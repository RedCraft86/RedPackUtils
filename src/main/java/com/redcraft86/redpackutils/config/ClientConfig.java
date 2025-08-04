package com.redcraft86.redpackutils.config;

import com.mojang.logging.LogUtils;
import org.slf4j.Logger;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.resources.ResourceLocation;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;

import com.redcraft86.redpackutils.ModClass;

@Mod.EventBusSubscriber(modid = ModClass.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ClientConfig {
    private static final Logger LOGGER = LogUtils.getLogger();

    private static final ForgeConfigSpec.BooleanValue DISABLE_CREATIVE_TAB_TIPS;
    private static final ForgeConfigSpec.ConfigValue<List<? extends String>> STARTUP_SOUNDS;

    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    static {
        DISABLE_CREATIVE_TAB_TIPS = BUILDER.comment("Disables the item tooltip labels in Creative Mode such as \"Functional Blocks,\" \"Natural Blocks,\" etc.")
            .define("disableCreativeTabTips", true);

        STARTUP_SOUNDS = BUILDER.comment("Picks a random sound from this list to play on startup.\nLeave empty to disable. Format is: \"sound_id volume\"")
            .defineListAllowEmpty("startupSounds", List.of("minecraft:entity.experience_orb.pickup 0.7", "minecraft:entity.player.levelup 0.3"),
                obj -> obj instanceof String name && ResourceLocation.isValidResourceLocation(name.split(" ", 2)[0]));
    }
    public static final ForgeConfigSpec SPEC = BUILDER.build();

    public static boolean disableCreativeTabTips = true;
    public static Map<ResourceLocation, Float> startupSounds = new HashMap<>();

    @SubscribeEvent
    static void onLoad(final ModConfigEvent event)
    {
        if (event.getConfig().getSpec() != SPEC) {
            return;
        }

        processStartupSounds();
        disableCreativeTabTips = DISABLE_CREATIVE_TAB_TIPS.get();
    }

    private static void processStartupSounds() {

        // Only need this to process during startup.
        // We don't really care if this needs updating after it's relevancy is gone.
        if (!startupSounds.isEmpty()) {
            return;
        }

        List<? extends String> soundList = STARTUP_SOUNDS.get();
        for (String entry : soundList) {

            // Expect format: "sound_id volume" -> then convert to [sound id, volume]
            String[] parts = entry.split(" ", 2);
            if (parts.length != 2){
                LOGGER.warn("[RedPackUtils: Startup Sound] Invalid Entry: {}", entry);
                continue;
            }

            // Validate ResourceLocation
            String[] id = parts[0].split(":", 2);
            ResourceLocation sound = ResourceLocation.tryBuild(id[0].trim(), id[1].trim());
            if (sound == null) {
                LOGGER.error("[RedPackUtils: Startup Sound] Invalid Sound ID: {}", entry);
                continue;
            }

            // Parse and clamp volume
            String vol = parts[1].trim();
            float volume = 0.0f;
            try {
                volume = Math.max(0, Float.parseFloat(vol));
            } catch (NumberFormatException e) {
                LOGGER.error("[RedPackUtils: Startup Sound] Invalid Volume: {}", entry);
            }

            if (volume > 0.0f) {
                startupSounds.put(sound, volume);
            }
        }
    }
}
