package com.redcraft86.redpackutils.blocks;

import java.util.function.Supplier;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.eventbus.api.IEventBus;

import com.redcraft86.redpackutils.RedPackUtils;
import com.redcraft86.redpackutils.items.ItemRegistry;

public class BlockRegistry {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, RedPackUtils.MOD_ID);

    public static final RegistryObject<Block> RAINBOW_BEACON_BLOCK = registerBlock("rainbow_beacon",
            () -> new RainbowBeaconBlock(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK)), true);

    private static <T extends Block> RegistryObject<T> registerBlock(String ID, Supplier<T> block, boolean bTooltip) {
        RegistryObject<T> toReturn = BLOCKS.register(ID, block);
        registerBlockItem(ID, toReturn, bTooltip);
        return toReturn;
    }

    private static <T extends Block> void registerBlockItem(String ID, RegistryObject<T> block, boolean bTooltip) {
        ItemRegistry.ITEMS.register(ID, () -> new ModBlockItem(block.get(), new Item.Properties(), ID, bTooltip));
    }

    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
    }
}
