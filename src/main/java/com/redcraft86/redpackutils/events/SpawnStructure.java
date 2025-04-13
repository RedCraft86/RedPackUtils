package com.redcraft86.redpackutils.events;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.event.level.LevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import com.mojang.datafixers.util.Pair;
import com.mojang.logging.LogUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.levelgen.structure.Structure;
import com.redcraft86.redpackutils.config.CommonConfig;
import com.redcraft86.redpackutils.ModClass;
import org.slf4j.Logger;
import java.util.Optional;

@Mod.EventBusSubscriber(modid = ModClass.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class SpawnStructure {
    private static final Logger LOGGER = LogUtils.getLogger();

    @SubscribeEvent(receiveCanceled = true)
    public static void onWorldCreate(LevelEvent.CreateSpawnPosition e) {
        var structure = CommonConfig.spawnStructure.trim();
        if (structure.isEmpty()) {
            LOGGER.info("[RedPackUtils: Structure Spawn Point] Feature is disabled, spawning in normally...");
            return;
        } else if (!structure.contains(":")) {
            LOGGER.error("[RedPackUtils: Structure Spawn Point] ID/Tag is invalid, spawning in normally...");
            return;
        }

        LevelAccessor levelAccessor = e.getLevel();
        if (!levelAccessor.isClientSide() && levelAccessor instanceof Level level) {
            if (trySetStructureSpawnPoint((ServerLevel)level, structure)) {
                e.setCanceled(true);
            }
        }
    }

    private static boolean trySetStructureSpawnPoint(ServerLevel level, String structure) {
        if (level == null) {
            LOGGER.error("[RedPackUtils: Spawn Structure] Server Level is null!");
            return false;
        }

        if (!level.getServer().getWorldData().worldGenOptions().generateStructures()) {
            LOGGER.warn("[RedPackUtils: Spawn Structure] World does not allow any structures, skipping structure locator.");
            return false;
        }

        Pair<BlockPos, Holder<Structure>> result = null;
        LOGGER.info("[RedPackUtils: Spawn Structure] Attempting to locate structure {}...", structure);
        if (structure.startsWith("#")) {
            result = findStructureByTag(level, structure.substring(1));
        } else {
            result = findStructureByID(level, structure);
        }

        if (result == null) {
            LOGGER.warn("[RedPackUtils: Spawn Structure] Could not find requested structure, proceeding as normal.");
            return false;
        } else {
            level.setDefaultSpawnPos(result.getFirst(), 1.0f);
            LOGGER.info("[RedPackUtils: Spawn Structure] Spawn Point set! Continuing world generation.");
            return true;
        }
    }

    private static Pair<BlockPos, Holder<Structure>> findStructureByTag(ServerLevel level, String tag) {
        ResourceLocation tagLocation = new ResourceLocation(tag);
        Registry<Structure> registry = level.registryAccess().registryOrThrow(Registries.STRUCTURE);
        Optional<HolderSet.Named<Structure>> structures = registry.getTag(TagKey.create(Registries.STRUCTURE, tagLocation));
        if (structures.isEmpty()) {
            LOGGER.error("[RedPackUtils: Spawn Structure] Structure tag was empty for whatever reason...");
            return null;
        }

        var blacklist = CommonConfig.spawnStructureBlacklist;
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
        ResourceLocation structureID = new ResourceLocation(id);
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
                    level, HolderSet.direct(holder), BlockPos.ZERO, 128, false);
            if (result != null) {
                BlockPos Pos = result.getFirst();
                LOGGER.info("[RedPackUtils: Spawn Structure] Structure '{}' found at {}, {}", id, Pos.getX(), Pos.getZ());
                return result;
            }

        } catch(Exception e) {
            LOGGER.error("[RedPackUtils: Spawn Structure] Skipping structure '{}' because of exception {}", id, e);
        }

        return null;
    }
}
