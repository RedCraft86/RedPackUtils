package com.redcraft86.redpackutils.config;

import com.redcraft86.redpackutils.ModClass;

import java.util.*;
import org.slf4j.Logger;
import com.mojang.logging.LogUtils;

import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.resources.ResourceLocation;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import net.minecraftforge.registries.ForgeRegistries;

@Mod.EventBusSubscriber(modid = ModClass.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class CommonConfig {
    private static final Logger LOGGER = LogUtils.getLogger();

    private static final ForgeConfigSpec.ConfigValue<List<? extends String>> GRIEF_BLACKLIST;

    private static final ForgeConfigSpec.IntValue CAMPFIRE_EFFECT_RANGE;
    private static final ForgeConfigSpec.BooleanValue CAMPFIRE_CLEAR_HARM;
    private static final ForgeConfigSpec.ConfigValue<List<? extends String>> CAMPFIRE_EFFECTS;

    private static final ForgeConfigSpec.BooleanValue BONEMEAL_DIRT_GRASS;
    private static final ForgeConfigSpec.IntValue SHORT_GRASS_CHANCE;
    private static final ForgeConfigSpec.IntValue TALL_GRASS_CHANCE;
    private static final ForgeConfigSpec.IntValue RANDOM_FLOWER_CHANCE;
    private static final ForgeConfigSpec.ConfigValue<List<? extends String>> FLOWER_BLACKLIST;

    private static final ForgeConfigSpec.ConfigValue<? extends String> STRUCTURE_SPAWNPOINT;
    private static final ForgeConfigSpec.ConfigValue<List<? extends String>> SPAWN_POINT_BLACKLIST;

    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    static {
        GRIEF_BLACKLIST = BUILDER.comment("List of entity IDs that cannot grief the world.")
            .defineListAllowEmpty("mobGriefBlacklist", List.of("minecraft:creeper", "minecraft:enderman", "minecraft:fireball", "minecraft:wither_skull"),
                obj -> obj instanceof String id && ResourceLocation.isValidResourceLocation(id));

        BUILDER.push("Campfire Effects");

        CAMPFIRE_EFFECT_RANGE = BUILDER.comment("The range around the campfire in which players will receive effects. Set to 0 to disable feature.")
            .defineInRange("campfireRange", 3, 0, 10);

        CAMPFIRE_CLEAR_HARM = BUILDER.comment("If the campfire should clear harmful effects.")
            .define("campfireNoHarm", true);

        CAMPFIRE_EFFECTS = BUILDER.comment("Effects to give when near campfires. Leave empty to disable.\nFormat is: \"effect_id level\" (Level Range: 1 ~ 256)")
            .defineListAllowEmpty("campfireEffects", List.of("minecraft:regeneration 1", "minecraft:saturation 1"),
                obj -> obj instanceof final String name && ResourceLocation.isValidResourceLocation(name.split(" ", 2)[0]));

        BUILDER.pop();
        BUILDER.push("Better Bonemeal");

        BONEMEAL_DIRT_GRASS = BUILDER.comment("Lets player bonemeal dirt blocks into grass.")
            .define("bonemealDirtToGrass", true);

        SHORT_GRASS_CHANCE = BUILDER.comment("Chance of short grass spawning from bonemeal.")
            .defineInRange("shortGrassChance", 30, 0, 100);

        TALL_GRASS_CHANCE = BUILDER.comment("Chance of tall grass spawning from bonemeal.")
            .defineInRange("tallGrassChance", 20, 0, 100);

        RANDOM_FLOWER_CHANCE = BUILDER.comment("Chance of spawning a random flower instead of air in place of grass.")
            .defineInRange("randomFlowerChance", 75, 0, 100);

        FLOWER_BLACKLIST = BUILDER.comment("List of flowers that bonemeal cannot spawn.")
            .defineListAllowEmpty("flowerBlacklist", List.of("minecraft:wither_rose"),
                obj -> obj instanceof String id && ResourceLocation.isValidResourceLocation(id));

        BUILDER.pop();
        BUILDER.push("Spawn Structure");

        STRUCTURE_SPAWNPOINT = BUILDER.comment("Spawns the player in the nearest structure within a 128-chunk radius from [0, 0, 0]. (a single ID or a Tag, leave empty to disable)")
            .define("structureID", "#minecraft:village");

        SPAWN_POINT_BLACKLIST = BUILDER.comment("List of structure IDs to ignore when searching for the nearest valid structure spawn point. (Only used when structureID is a Tag)")
            .defineListAllowEmpty("structureBlacklist", List.of("minecraft:village_snowy"),
                obj -> obj instanceof String id && ResourceLocation.isValidResourceLocation(id));

        BUILDER.pop();
    }
    public static final ForgeConfigSpec SPEC = BUILDER.build();

    public static Set<ResourceLocation> griefBlacklist = new HashSet<>();

    public static int campfireEffRange = 3;
    public static boolean campfireClearHarm = true;
    public static Map<MobEffect, Integer> campfireEffects = new HashMap<>();

    public static boolean regenFlowerList = true;
    public static boolean bonemealDirtGrass = true;
    public static float shortGrassChance = 0.3f;
    public static float tallGrassChance = 0.2f;
    public static float randomFlowerChance = 0.75f;
    public static Set<String> flowerBlacklist = new HashSet<>();

    public static String structureSpawnPoint = "#minecraft:village";
    public static Set<ResourceLocation> spawnPointBlacklist = new HashSet<>();

    @SubscribeEvent
    static void onLoad(final ModConfigEvent event)
    {
        if (event.getConfig().getSpec() != SPEC) {
            return;
        }

        processIdListRL(GRIEF_BLACKLIST.get(), griefBlacklist, "Grief Blacklist");

        campfireEffRange = CAMPFIRE_EFFECT_RANGE.get();
        if (campfireEffRange > 0) {
            campfireClearHarm = CAMPFIRE_CLEAR_HARM.get();
            processCampfireEffects(CAMPFIRE_EFFECTS.get());
        } else {
            campfireEffects.clear();
        }

        regenFlowerList = true;
        bonemealDirtGrass = BONEMEAL_DIRT_GRASS.get();
        shortGrassChance = Math.min(100.0f, (float)SHORT_GRASS_CHANCE.get() / 100.0f);
        tallGrassChance = Math.min(100.0f, (float)TALL_GRASS_CHANCE.get() / 100.0f);
        randomFlowerChance = Math.min(100.0f, (float)RANDOM_FLOWER_CHANCE.get() / 100.0f);
        processIdListStr(FLOWER_BLACKLIST.get(), flowerBlacklist, "Better Bonemeal (Blacklist)");

        structureSpawnPoint = STRUCTURE_SPAWNPOINT.get();
        if (ResourceLocation.isValidResourceLocation(structureSpawnPoint)) {
            processIdListRL(SPAWN_POINT_BLACKLIST.get(), spawnPointBlacklist, "Structure Spawn Point (Blacklist)");
        }
    }

    private static void processCampfireEffects(List<? extends String> entries) {
        campfireEffects.clear();
        for (String entry : entries) {
            String[] parts = entry.split(" ", 2);
            if (parts.length < 2) {
                LOGGER.error("[RedPackUtils: Campfire Effects] Failed to parse entry '{}'", entry);
                continue;
            }

            if (ResourceLocation.isValidResourceLocation(parts[0])){
                MobEffect effect = ForgeRegistries.MOB_EFFECTS.getValue(ResourceLocation.parse(parts[0]));
                if (effect == null) {
                    LOGGER.error("[RedPackUtils: Campfire Effects] Effect {} does not exist in registry", parts[0]);
                    continue;
                }

                int amplifier = 0;
                try {
                    amplifier = Mth.clamp(Integer.parseInt(parts[1]) - 1, 0, 255);
                } catch (NumberFormatException e) {
                    LOGGER.error("[RedPackUtils: Campfire Effects] Failed to parse level in '{}' because {}", entry, e);
                }
                finally {
                    if (amplifier >= 0) {
                        campfireEffects.put(effect, amplifier);
                    }
                }
            } else {
                LOGGER.error("[RedPackUtils: Campfire Effects] Invalid ID: {}", entry);
            }
        }
    }

    private static void processIdListRL(List<? extends String> entries, Set<ResourceLocation> result, String logCategory) {
        result.clear();
        for (String entry : entries) {
            if (ResourceLocation.isValidResourceLocation(entry)){
                result.add(ResourceLocation.parse(entry));
            } else {
                LOGGER.error("[RedPackUtils: {}] Invalid ID: {}", logCategory, entry);
            }
        }
    }

    private static void processIdListStr(List<? extends String> entries, Set<String> result, String logCategory) {
        result.clear();
        for (String entry : entries) {
            if (ResourceLocation.isValidResourceLocation(entry)){
                result.add(entry);
            } else {
                LOGGER.error("[RedPackUtils: {}] Invalid ID: {}", logCategory, entry);
            }
        }
    }
}
