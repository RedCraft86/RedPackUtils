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

    private static final ForgeConfigSpec.BooleanValue NO_ATK_COOLDOWN;
    private static final ForgeConfigSpec.ConfigValue<List<? extends String>> GRIEF_BLACKLIST;

    private static final ForgeConfigSpec.IntValue CAMPFIRE_EFFECT_RANGE;
    private static final ForgeConfigSpec.ConfigValue<List<? extends String>> CAMPFIRE_EFFECTS;

    private static final ForgeConfigSpec.ConfigValue<? extends String> STRUCTURE_SPAWNPOINT;
    private static final ForgeConfigSpec.ConfigValue<List<? extends String>> SPAWN_POINT_BLACKLIST;

    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    static {

        NO_ATK_COOLDOWN = BUILDER.comment("Removes the attack cooldown introduced in the 1.9 Combat Update.")
            .define("noAtkCooldown", false);

        GRIEF_BLACKLIST = BUILDER.comment("List of entity IDs that cannot grief the world.")
            .defineListAllowEmpty("mobGriefBlacklist", List.of("minecraft:creeper", "minecraft:enderman", "minecraft:fireball", "minecraft:wither_skull"),
                obj -> obj instanceof String);

        BUILDER.push("Campfire Effects");

        CAMPFIRE_EFFECT_RANGE = BUILDER.comment("The range around the campfire in which players will receive effects. Set to 0 to disable feature.")
            .defineInRange("campfireRange", 3, 0, 10);

        CAMPFIRE_EFFECTS = BUILDER.comment("Effects to give when near campfires. Leave empty to disable.\nFormat is: \"effect_id level\" (Level Range: 1 ~ 256)")
            .defineListAllowEmpty("campfireEffects", List.of("minecraft:regeneration 2", "minecraft:saturation 2", "minecraft:resistance 2", "minecraft:strength 2"),
                obj -> obj instanceof final String name && ResourceLocation.isValidResourceLocation(name.split(" ", 2)[0]));

        BUILDER.pop();
        BUILDER.push("Spawn Structure");

        STRUCTURE_SPAWNPOINT = BUILDER.comment("Spawns the player in the nearest structure within a 128-chunk radius from [0, 0, 0]. (a single ID or a Tag, leave empty to disable)")
            .define("structureID", "#minecraft:village");

        SPAWN_POINT_BLACKLIST = BUILDER.comment("List of structure IDs to ignore when searching for the nearest valid structure spawn point. (Only used when spawnStructure is a Tag)")
            .defineListAllowEmpty("structureBlacklist", List.of("minecraft:village_snowy"),
                obj -> obj instanceof String);

        BUILDER.pop();
    }
    public static final ForgeConfigSpec SPEC = BUILDER.build();

    public static boolean noAtkCooldown = false;
    public static Set<ResourceLocation> griefBlacklist = new HashSet<>();

    public static int campfireEffRange = 3;
    public static Map<MobEffect, Integer> campfireEffects = new HashMap<>();

    public static String structureSpawnPoint = "#minecraft:village";
    public static Set<ResourceLocation> spawnPointBlacklist = new HashSet<>();

    @SubscribeEvent
    static void onLoad(final ModConfigEvent event)
    {
        if (event.getConfig().getSpec() != SPEC) {
            return;
        }

        noAtkCooldown = NO_ATK_COOLDOWN.get();
        processIdList(GRIEF_BLACKLIST.get(), griefBlacklist, "Grief Blacklist");

        campfireEffRange = CAMPFIRE_EFFECT_RANGE.get();
        if (campfireEffRange > 0) {
            processCampfireEffects(CAMPFIRE_EFFECTS.get());
        } else {
            campfireEffects.clear();
        }

        structureSpawnPoint = STRUCTURE_SPAWNPOINT.get();
        processIdList(SPAWN_POINT_BLACKLIST.get(), spawnPointBlacklist, "Structure Spawn Point (Blacklist)");
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

    private static void processIdList(List<? extends String> entries, Set<ResourceLocation> result, String logCategory) {
        result.clear();
        for (String entry : entries) {
            if (ResourceLocation.isValidResourceLocation(entry)){
                result.add(ResourceLocation.parse(entry));
            } else {
                LOGGER.error("[RedPackUtils: {}] Invalid ID: {}", logCategory, entry);
            }
        }
    }
}
