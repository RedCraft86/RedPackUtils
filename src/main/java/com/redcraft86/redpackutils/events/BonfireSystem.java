package com.redcraft86.redpackutils.events;

import java.util.Collections;
import javax.annotation.Nonnull;
import com.mojang.logging.LogUtils;
import net.minecraft.world.InteractionResult;
import org.slf4j.Logger;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
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
import com.redcraft86.redpackutils.util.BonfireEffect;
import com.redcraft86.redpackutils.util.ModUtils;

@Mod.EventBusSubscriber(modid = ModClass.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class BonfireSystem {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final String TAG_SPAWN_DATA = "CampfireSpawnData";

    @SubscribeEvent
    public static void onTooltip(ItemTooltipEvent e) {
        if (e.getItemStack().getItem() == (CommonConfig.bonfireSoulfire ? Items.SOUL_CAMPFIRE : Items.CAMPFIRE)) {
            if (!CommonConfig.bonfireEffects.isEmpty()) {
                e.getToolTip().add(ModUtils.makeTooltip("bonfire.effects").withStyle(ChatFormatting.GRAY));
            }

            if (CommonConfig.bonfireSpawnPoint) {
                e.getToolTip().add(ModUtils.makeTooltip("bonfire.spawnpoint").withStyle(ChatFormatting.GRAY));
            }
        }
    }

    @SubscribeEvent
    public static void onInteract(PlayerInteractEvent.RightClickBlock e) {
        Level level = e.getLevel();
        BlockPos pos = e.getPos();
        Player player = e.getEntity();
        BlockState state = level.getBlockState(pos);
        if (level.isClientSide() || e.getHand() != InteractionHand.MAIN_HAND
                || !isBonfireBlock(state) || player.getMainHandItem().getItem() != Items.AIR) return;

        if (!state.getValue(BlockStateProperties.LIT)) {
            player.displayClientMessage(ModUtils.makeMessage("bonfire.unlit"), true);
            e.setCancellationResult(InteractionResult.SUCCESS);
            return;
        }

        player.swing(InteractionHand.MAIN_HAND, true);
        if (CommonConfig.bonfireSpawnPoint && player.isCrouching()) {
            setSpawnData(player, formatSpawnData(level, pos));
            player.displayClientMessage(ModUtils.makeMessage("bonfire.spawn_set"), true);
        } else {
            giveBlessings(player, true);
        }

        e.setCancellationResult(InteractionResult.SUCCESS);
    }

    @SubscribeEvent
    public static void onBroken(BlockEvent.BreakEvent e) {
        BlockState state = e.getState();
        LevelAccessor levelAccessor = e.getLevel();
        if (levelAccessor.isClientSide() || !CommonConfig.bonfireSpawnPoint || !isBonfireBlock(state)) return;
        if (levelAccessor instanceof Level level) {
            BlockPos pos = e.getPos();
            Player player = e.getPlayer();
            if (getSpawnData(player).equals(formatSpawnData(level, pos))) {
                setSpawnData(player, "none");
                player.displayClientMessage(ModUtils.makeMessage("bonfire.spawn_removed"), true);
                /* We intentionally don't notify everyone else who might have set their spawnpoint. Very evil, I know,
                 but it is also for performance reasons since we'd need to iterate through every player to check.

                 Also, as much as I'd like to, we unfortunately don't have a way to automatically remove the spawnpoint
                 like this when the campfire gets extinguished since forge don't have hooks for it. So unless you
                 break it to remove the temporary spawnpoint, you'll get the "... destroyed or became unlit!" message. */
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent e) {
        Player player = e.getEntity();
        Level level = player.level();
        if (level.isClientSide() || !CommonConfig.bonfireSpawnPoint) return;

        String value = getSpawnData(player);
        if (value.equals("none") || !value.contains("|")) return;
        String[] parts = value.split("\\|", 4);
        if (parts.length != 4) return;

        ServerLevel bonfireLevel = null;
        if (ResourceLocation.isValidResourceLocation(parts[0]))
        {
            bonfireLevel = player.getServer().getLevel(ResourceKey.create(
                    Registries.DIMENSION, new ResourceLocation(parts[0])));
        }
        if (bonfireLevel == null) {
            setSpawnData(player, "none");
            LOGGER.warn("[RedPackUtils: Bonfire Spawn Point] Failed to find bonfire because dimension '{}' is null", parts[0]);
            return;
        }

        try {
            BlockPos bonfirePos = new BlockPos(
                    Integer.parseInt(parts[1]),
                    Integer.parseInt(parts[2]),
                    Integer.parseInt(parts[3])
            );

            // Ensure the chunk is loaded
            int chunkX = bonfirePos.getX() >> 4, chunkZ = bonfirePos.getZ() >> 4;
            if (!bonfireLevel.getChunkSource().hasChunk(chunkX, chunkZ)) {
                bonfireLevel.getChunkSource().getChunk(chunkX, chunkZ, ChunkStatus.FULL, true);
            }

            BlockState state = bonfireLevel.getBlockState(bonfirePos);
            if (isBonfireBlock(state) && state.getValue(BlockStateProperties.LIT)) {
                giveBlessings(player, false);
                player.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 200,
                        0, false ,false, true));

                BlockPos above = bonfirePos.above();
                player.teleportTo(bonfireLevel, above.getX() + 0.5, above.getY(), above.getZ() + 0.5,
                        Collections.emptySet(), player.getYRot(), player.getXRot());

                player.displayClientMessage(ModUtils.makeMessage("bonfire.respawned"), true);
            } else {
                setSpawnData(player, "none");
                player.displayClientMessage(ModUtils.makeMessage("bonfire.spawn_invalid"), true);
            }
        } catch (Exception ex) {
            setSpawnData(player, "none");
            LOGGER.warn("[RedPackUtils: Bonfire Spawn Point] Failed parse bonfire data '{}' because {}", value, ex);
        }
    }

    @Nonnull
    private static void giveBlessings(Player player, boolean sendMsg) {
        boolean appliedAny = false;
        for (BonfireEffect bonfireEffect : CommonConfig.bonfireEffects) {
            if (bonfireEffect.effect != null) {
                appliedAny = true;
                player.addEffect(new MobEffectInstance(bonfireEffect.effect, bonfireEffect.duration,
                        bonfireEffect.amplifier, false, false, true));
            }
        }

        if (appliedAny && sendMsg) {
            player.displayClientMessage(ModUtils.makeMessage("bonfire.effects_applied"), true);
        }
    }

    @Nonnull
    private static boolean isBonfireBlock(BlockState state) {
        return state.is(CommonConfig.bonfireSoulfire ? Blocks.SOUL_CAMPFIRE : Blocks.CAMPFIRE);
    }

    @Nonnull
    private static String formatSpawnData(Level level, BlockPos pos) {
        return level.dimension().location() + "|"  + pos.getX() + "|" + pos.getY() + "|" + pos.getZ();
    }

    @Nonnull
    public static void setSpawnData(Player player, String data) {
        CompoundTag playerData = player.getPersistentData();
        CompoundTag persistedData = playerData.getCompound(Player.PERSISTED_NBT_TAG);
        persistedData.putString(TAG_SPAWN_DATA, data);

        // Sanity check in case persistent data never existed in the player
        if (!playerData.contains(Player.PERSISTED_NBT_TAG, CompoundTag.TAG_COMPOUND)) {
            playerData.put(Player.PERSISTED_NBT_TAG, persistedData);
        }
    }

    @Nonnull
    public static String getSpawnData(Player player) {
        return player.getPersistentData().getCompound(Player.PERSISTED_NBT_TAG).getString(TAG_SPAWN_DATA);
    }
}
