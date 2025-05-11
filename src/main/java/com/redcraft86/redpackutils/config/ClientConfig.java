package com.redcraft86.redpackutils.config;

import com.redcraft86.redpackutils.ModClass;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Collections;
import java.util.List;

@Mod.EventBusSubscriber(modid = ModClass.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ClientConfig
{

    private static final ForgeConfigSpec.ConfigValue<List<? extends String>> STARTUP_SOUNDS;
    private static final ForgeConfigSpec.BooleanValue DISABLE_CREATIVE_TAB_TIPS;
    private static final ForgeConfigSpec.BooleanValue DISABLE_GOG_STARS;

    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    static {
        //BUILDER.push("Tweaks");

        STARTUP_SOUNDS = BUILDER.comment("Picks a random sound from this list to play on startup.\nLeave empty to disable. Format is: \"sound_id volume\"")
                .defineListAllowEmpty("startupSounds",
                        List.of("minecraft:entity.experience_orb.pickup 0.7", "minecraft:entity.player.levelup 0.3"),
                        obj -> obj instanceof final String name && ResourceLocation.isValidResourceLocation(name.split(" ", 2)[0]));

        DISABLE_CREATIVE_TAB_TIPS = BUILDER.comment("Disables the tooltip labels on Creative Mode tabs such as \"Functional Blocks,\" \"Natural Blocks,\" etc.")
                .define("disableCreativeTabTips", true);

        DISABLE_GOG_STARS = BUILDER.comment("Prevents Botania's Garden of Glass skybox from modifying the stars.")
                .define("disableGardenOfGlassStars", true);

        //BUILDER.pop();
    }
    public static final ForgeConfigSpec SPEC = BUILDER.build();


    public static List<? extends String> startupSounds = Collections.emptyList();
    public static boolean disableCreativeTabTips = true;
    public static boolean disableGoGStars = true;

    @SubscribeEvent
    static void onLoad(final ModConfigEvent event)
    {
        if (event.getConfig().getSpec() == SPEC) {
            startupSounds = STARTUP_SOUNDS.get();
            disableCreativeTabTips = DISABLE_CREATIVE_TAB_TIPS.get();
            disableGoGStars = DISABLE_GOG_STARS.get();
        }
    }
}
