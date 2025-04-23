package com.redcraft86.redpackutils.events;

import java.util.Collections;
import javax.annotation.Nonnull;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.chunk.ChunkStatus;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import com.redcraft86.redpackutils.ModClass;
import com.redcraft86.redpackutils.config.CommonConfig;
import com.redcraft86.redpackutils.util.ModUtils;

@Mod.EventBusSubscriber(modid = ModClass.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class CampfireSpawn {
    private static final String TAG_SPAWN_DATA = "CampfireSpawnData";

    @SubscribeEvent
    public static void onCampfireTooltip(ItemTooltipEvent event) {
        if (event.getItemStack().getItem() == Items.CAMPFIRE && CommonConfig.campfireSpawnPoint) {
            event.getToolTip().add(ModUtils.makeTooltip("campfire_spawnpoint").withStyle(ChatFormatting.GRAY));
        }
    }

    @SubscribeEvent
    public static void onCampfireInteract(PlayerInteractEvent.RightClickBlock e) {
        if (!e.getLevel().isClientSide() && CommonConfig.campfireSpawnPoint) {
            Player player = e.getEntity();
            Level level = e.getLevel();
            BlockPos pos = e.getPos();
            BlockState state = level.getBlockState(pos);
            if (player.isShiftKeyDown() && state.is(Blocks.CAMPFIRE)) {
                if (state.getValue(BlockStateProperties.LIT)) {
                    setSpawnData(player, formatSpawnData(level, pos));
                    player.displayClientMessage(Component.literal("Temporary spawnpoint set!"), true);
                } else {
                    player.displayClientMessage(Component.literal("Campfire must be lit to set a temporary spawnpoint!"), true);
                }
            }
        }
    }

    @SubscribeEvent
    public static void onCampfireBroken(BlockEvent.BreakEvent e) {
        BlockState state = e.getState();
        LevelAccessor levelAccessor = e.getLevel();
        if (levelAccessor.isClientSide() || !state.is(Blocks.CAMPFIRE) || !CommonConfig.campfireSpawnPoint) return;
        if (levelAccessor instanceof Level level) {
            BlockPos pos = e.getPos();
            Player player = e.getPlayer();
            if (getSpawnData(player).equals(formatSpawnData(level, pos))) {
                setSpawnData(player, "none");
                player.displayClientMessage(Component.literal("Temporary spawnpoint removed!"), true);
                /* We intentionally don't notify everyone else who might have set their spawnpoint. Very evil, I know.

                 Also, as much as I'd like to, we unfortunately don't have a way to automatically remove the spawnpoint
                 like this when the campfire gets extinguished since forge don't have hooks for it. So unless you
                 break it to remove the temporary spawnpoint, you'll get the "... became unlit!" message. */
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent e) {
        Player player = e.getEntity();
        Level level = player.level();
        if (!level.isClientSide() && CommonConfig.campfireSpawnPoint) {
            String value = getSpawnData(player);
            if (value.equals("none") || !value.contains("|")) return;
            String[] parts = value.split("\\|", 4);
            if (parts.length != 4) return;

            ResourceLocation dimId = new ResourceLocation(parts[0]);
            ResourceKey<Level> resKey = ResourceKey.create(Registries.DIMENSION, dimId);
            ServerLevel campfireLevel = player.getServer().getLevel(resKey);
            if (campfireLevel == null) {
                setSpawnData(player, "none");
                player.displayClientMessage(Component.literal("Failed to find temporary spawnpoint!"), true);
                return;
            }

            try {
                int x = Integer.parseInt(parts[1]);
                int y = Integer.parseInt(parts[2]);
                int z = Integer.parseInt(parts[3]);
                BlockPos campfirePos = new BlockPos(x, y, z);

                // Ensure the chunk is loaded
                int chunkX = campfirePos.getX() >> 4, chunkZ = campfirePos.getZ() >> 4;
                if (!campfireLevel.getChunkSource().hasChunk(chunkX, chunkZ)) {
                    campfireLevel.getChunkSource().getChunk(chunkX, chunkZ, ChunkStatus.FULL, true);
                }

                BlockState state = campfireLevel.getBlockState(campfirePos);
                if (state.is(Blocks.CAMPFIRE) && state.getValue(BlockStateProperties.LIT)) {
                    BlockPos above = campfirePos.above();
                    player.teleportTo(campfireLevel, above.getX() + 0.5, above.getY(), above.getZ() + 0.5, Collections.emptySet(), player.getYRot(), player.getXRot());
                    player.displayClientMessage(Component.literal("Respawned at temporary spawnpoint!"), true);
                } else {
                    setSpawnData(player, "none");
                    player.displayClientMessage(Component.literal("Temporary spawnpoint was destroyed or became unlit!"), true);
                }
            } catch (NumberFormatException ignored) {
                setSpawnData(player, "none");
                player.displayClientMessage(Component.literal("Failed to retrieve temporary spawnpoint data!"), true);
            }
        }
    }

    @Nonnull
    public static String formatSpawnData(Level level, BlockPos pos) {
        return level.dimension().location() + "|" + pos.getX() + "|" + pos.getY() + "|" + pos.getZ();
    }

    @Nonnull
    public static void setSpawnData(Player player, String data) {
        CompoundTag playerData = player.getPersistentData();
        CompoundTag persistedData = playerData.getCompound(Player.PERSISTED_NBT_TAG);
        persistedData.putString(TAG_SPAWN_DATA, data);

        // Not sure if there's a chance of persisted data not existing so this is a sanity check lol
        if (!playerData.contains(Player.PERSISTED_NBT_TAG, CompoundTag.TAG_COMPOUND)) {
            playerData.put(Player.PERSISTED_NBT_TAG, persistedData);
        }
    }

    @Nonnull
    public static String getSpawnData(Player player) {
        return player.getPersistentData().getCompound(Player.PERSISTED_NBT_TAG).getString(TAG_SPAWN_DATA);
    }
}
