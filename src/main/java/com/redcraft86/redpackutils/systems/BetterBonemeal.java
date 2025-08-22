package com.redcraft86.redpackutils.systems;

import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

import com.redcraft86.redpackutils.ModClass;
import com.redcraft86.redpackutils.config.CommonConfig;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.server.TickTask;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.FlowerBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Block;

import net.minecraftforge.event.level.LevelEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.BonemealEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = ModClass.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class BetterBonemeal {
    private static final Random random = new Random();
    public static List<Block> flowers = new ArrayList<Block>();

    @SubscribeEvent
    static void onServerStart(LevelEvent.Load event) {
        LevelAccessor level = event.getLevel();
        if (level.isClientSide()) {
            return;
        }

        generateFlowerList((Level)level);
    }

    @SubscribeEvent // Dirt to grass, unaffected by sneakyGrassChance
    static void onRightClick(PlayerInteractEvent.RightClickBlock event) {
        Level level = event.getLevel();
        Player player = event.getEntity();
        if (level.isClientSide() || event.getHand() != InteractionHand.MAIN_HAND || !CommonConfig.bonemealDirtGrass) {
            return;
        }

        ItemStack item = event.getItemStack();
        if (!item.is(Items.BONE_MEAL)) {
            return;
        }

        BlockPos pos = event.getPos();
        if (level.getBlockState(pos).is(Blocks.DIRT)) {
            level.setBlockAndUpdate(pos, Blocks.GRASS_BLOCK.defaultBlockState());
            if (!player.isCreative()) {
                item.shrink(1);
            }
            player.swing(InteractionHand.MAIN_HAND, true);
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    static void OnBonemeal(BonemealEvent event) {
        Level level = event.getLevel();
        if (level.isClientSide() || CommonConfig.shortGrassChance >= 0.99f || CommonConfig.tallGrassChance >= 0.99f) {
            return;
        }

        Player player = event.getEntity();
        // isShiftKeyDown doesn't actually check if the Shift key is pressed
        // It checks if you're crouching, except it's also true if you're crouching in air
        if (CommonConfig.sneakyGrassChance && !player.isShiftKeyDown()) {
            return;
        }

        if (CommonConfig.regenFlowerList) {
            generateFlowerList(level);
        }

        BlockPos originPos = event.getPos();
        int x = originPos.getX();
        int y = originPos.getY();
        int z = originPos.getZ();

        level.getServer().tell(new TickTask(level.getServer().getTickCount(), () -> {
            Iterator<BlockPos> it = BlockPos.betweenClosedStream(x-6, y, z-6, x+6, y+1, z+6).iterator();
            while (it.hasNext()) {
                BlockPos pos = it.next();
                Block block = level.getBlockState(pos).getBlock();
                if (block == Blocks.GRASS && random.nextFloat() > CommonConfig.shortGrassChance) {
                    level.setBlockAndUpdate(pos, airOrRandomFlower().defaultBlockState());
                }
                if (block == Blocks.TALL_GRASS && random.nextFloat() > CommonConfig.tallGrassChance) {
                    // If tall grass, silently break the top block first to avoid breaking effect
                    level.setBlock(pos.above(), Blocks.AIR.defaultBlockState(), 2 | 16);
                    level.setBlockAndUpdate(pos, airOrRandomFlower().defaultBlockState());
                }
            }
        }));
    }

    private static Block airOrRandomFlower() {
        if (random.nextFloat() < CommonConfig.randomFlowerChance) {
            return flowers.get(random.nextInt(flowers.size()));
        } else {
            return Blocks.AIR;
        }
    }

    private static void generateFlowerList(Level level) {
        if (level == null) {
            return;
        }

        flowers.clear();
        CommonConfig.regenFlowerList = false;

        Registry<Block> blockRegistry = level.registryAccess().registryOrThrow(Registries.BLOCK);
        for (Block block : blockRegistry) {
            if (block instanceof FlowerBlock) {
                ResourceLocation id = blockRegistry.getKey(block);
                if (id == null || CommonConfig.flowerBlacklist.contains(id.toString())) {
                    continue;
                }

                flowers.add(block);
            }
        }
    }
}
