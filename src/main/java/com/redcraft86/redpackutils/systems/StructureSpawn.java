package com.redcraft86.redpackutils.systems;

import com.redcraft86.redpackutils.ModClass;
import com.redcraft86.redpackutils.config.CommonConfig;

import java.util.Set;
import java.util.Optional;
import com.mojang.logging.LogUtils;
import com.mojang.datafixers.util.Pair;
import net.minecraftforge.event.level.LevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.slf4j.Logger;

import net.minecraft.tags.TagKey;
import net.minecraft.core.Holder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.HolderSet;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.resources.ResourceKey;

@Mod.EventBusSubscriber(modid = ModClass.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class StructureSpawn {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static boolean searching = false;

    public static boolean isSearching() { return searching; }

    @SubscribeEvent(receiveCanceled = true)
    static void onWorldCreate(LevelEvent.CreateSpawnPosition e) {
        String structure = CommonConfig.structureSpawnPoint.trim();
        if (structure.isEmpty()) {
            LOGGER.info("[RedPackUtils: Structure Spawn Point] Feature is disabled, spawning in normally...");
            return;
        } else if (!structure.contains(":")) {
            LOGGER.error("[RedPackUtils: Structure Spawn Point] ID/Tag is invalid, spawning in normally...");
            return;
        }

        LevelAccessor levelAccessor = e.getLevel();
        if (!levelAccessor.isClientSide() && levelAccessor instanceof Level level) {
            if (StructureSpawn.trySetSpawnPoint((ServerLevel) level, structure)) {
                e.setCanceled(true);
            }
        }
    }

    private static boolean trySetSpawnPoint(ServerLevel level, String structure) {
        if (level == null) {
            LOGGER.error("[RedPackUtils: Structure Spawn Point] Server Level is null!");
            return false;
        }

        if (!level.getServer().getWorldData().worldGenOptions().generateStructures()) {
            LOGGER.warn("[RedPackUtils: Structure Spawn Point] World does not allow any structures, skipping structure locator.");
            return false;
        }

        searching = true;
        Pair<BlockPos, Holder<Structure>> result = null;
        LOGGER.info("[RedPackUtils: Structure Spawn Point] Attempting to locate structure {}...", structure);
        if (structure.startsWith("#")) {
            result = findStructureByTag(level, structure.substring(1));
        } else {
            result = findStructureByID(level, structure);
        }

        searching = false;
        if (result == null) {
            LOGGER.warn("[RedPackUtils: Structure Spawn Point] Could not find structure, proceeding as normal.");
            return false;
        } else {
            level.setDefaultSpawnPos(result.getFirst(), 1.0f);
            LOGGER.info("[RedPackUtils: Structure Spawn Point] Spawn Point set! Continuing world generation.");
            return true;
        }
    }

    private static Pair<BlockPos, Holder<Structure>> findStructureByTag(ServerLevel level, String tag) {
        ResourceLocation tagLocation = ResourceLocation.parse(tag);
        Registry<Structure> registry = level.registryAccess().registryOrThrow(Registries.STRUCTURE);
        Optional<HolderSet.Named<Structure>> structures = registry.getTag(TagKey.create(Registries.STRUCTURE, tagLocation));
        if (structures.isEmpty()) {
            LOGGER.error("[RedPackUtils: Spawn Structure] Structure tag was empty for whatever reason...");
            return null;
        }

        Set<ResourceLocation> blacklist = CommonConfig.spawnPointBlacklist;
        for (Holder<Structure> holder : structures.get()) {
            Optional<ResourceKey<Structure>> key = holder.unwrapKey();
            if (key.isEmpty()) {
                continue;
            }

            ResourceLocation id = key.get().location();
            if (blacklist.contains(id)) {
                continue;
            }

            return getStructure(level, holder);
        }

        return null;
    }

    private static Pair<BlockPos, Holder<Structure>> findStructureByID(ServerLevel level, String id) {
        ResourceLocation structureID = ResourceLocation.parse(id);
        Registry<Structure> registry = level.registryAccess().registryOrThrow(Registries.STRUCTURE);
        Optional<Holder.Reference<Structure>> structure = registry.getHolder(ResourceKey.create(Registries.STRUCTURE, structureID));
        return structure.map(structureRef -> getStructure(level, structureRef)).orElse(null);
    }

    private static Pair<BlockPos, Holder<Structure>> getStructure(ServerLevel level, Holder<Structure> holder) {
        Optional<ResourceKey<Structure>> key = holder.unwrapKey();
        if (key.isEmpty()) {
            return null;
        }

        ResourceLocation id = key.get().location();
        try {
            Pair<BlockPos, Holder<Structure>> result = level.getChunkSource().getGenerator().findNearestMapStructure(
                    level, HolderSet.direct(holder), BlockPos.ZERO, CommonConfig.structureSearchRadius, false);
            if (result != null) {
                BlockPos Pos = result.getFirst();
                LOGGER.info("[RedPackUtils: Structure Spawn Point] Structure '{}' found at {}, {}", id, Pos.getX(), Pos.getZ());
                return result;
            }

        } catch(Exception e) {
            LOGGER.error("[RedPackUtils: Structure Spawn Point] Skipping structure '{}' because of exception {}", id, e);
            return null;
        }

        return null;
    }
}
