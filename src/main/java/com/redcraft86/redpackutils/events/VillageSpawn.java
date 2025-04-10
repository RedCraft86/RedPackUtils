package com.redcraft86.redpackutils.events;

import com.redcraft86.redpackutils.ModClass;
import com.redcraft86.redpackutils.config.CommonConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.StructureTags;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraftforge.event.level.LevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import com.mojang.datafixers.util.Pair;
import com.mojang.logging.LogUtils;
import java.util.Optional;
import org.slf4j.Logger;

@Mod.EventBusSubscriber(modid = ModClass.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class VillageSpawn {
    private static final Logger LOGGER = LogUtils.getLogger();

    @SubscribeEvent(receiveCanceled = true)
    public static void onWorldCreate(LevelEvent.CreateSpawnPosition e) {
        if (!CommonConfig.spawnInVillage) {
            LOGGER.error("[RedPackUtils: Village Spawn] Setting is disabled, spawning in normally...");
            return;
        }

        LevelAccessor levelAccessor = e.getLevel();
        if (!levelAccessor.isClientSide() && levelAccessor instanceof Level level) {
            if (trySetVillageSpawnpoint((ServerLevel)level)) {
                e.setCanceled(true);
            }
        }
    }

    public static boolean trySetVillageSpawnpoint(ServerLevel level) {
        if (level == null) {
            LOGGER.error("[RedPackUtils: Village Spawn] Server Level is null!");
            return false;
        }

        if (!level.getServer().getWorldData().worldGenOptions().generateStructures()) {
            LOGGER.warn("[RedPackUtils: Village Spawn] World does not allow any structures, skipping village locator.");
            return false;
        }

        Optional<HolderSet.Named<Structure>> villageTag = level.registryAccess()
                .registryOrThrow(Registries.STRUCTURE).getTag(StructureTags.VILLAGE);

        if (villageTag.isEmpty()) {
            LOGGER.error("[RedPackUtils: Village Spawn] Village tag was empty for whatever reason...");
            return false;
        }

        boolean villageFound = false;
        BlockPos spawnpoint = level.getSharedSpawnPos();
        var blacklist = CommonConfig.spawnVillageBlacklist;
        LOGGER.info("[RedPackUtils: Village Spawn] Looking for villages, please wait...");
        for (Holder<Structure> holder : villageTag.get()) {
            Optional<ResourceKey<Structure>> key = holder.unwrapKey();
            if (key.isEmpty()) {
                continue;
            }

            ResourceLocation id = key.get().location();
            if (blacklist.contains(id)) {
                continue;
            }

            try {
                Pair<BlockPos, Holder<Structure>> result = level.getChunkSource().getGenerator().findNearestMapStructure(
                        level, HolderSet.direct(holder), spawnpoint, 128, false);
                if (result != null) {
                    villageFound = true;
                    spawnpoint = result.getFirst();
                    LOGGER.info("[RedPackUtils: Village Spawn] Village '{}' found at {}", id, spawnpoint.toString());
                    break;
                }

            } catch(Exception e) {
                LOGGER.error("[RedPackUtils: Village Spawn] Skipping village '{}' because of exception {}", id, e);
            }
        }

        if (villageFound) { level.setDefaultSpawnPos(spawnpoint, 1.0f); }
        LOGGER.info("[RedPackUtils: Village Spawn] {}", (villageFound ? "Village found! Continuing world generation."
                : "Could not find a village within a 128 chunk radius, proceeding as normal."));

        return villageFound;
    }
}
