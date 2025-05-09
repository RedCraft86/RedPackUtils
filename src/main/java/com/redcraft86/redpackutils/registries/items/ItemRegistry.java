package com.redcraft86.redpackutils.registries.items;

import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraft.world.item.Item;
import com.redcraft86.redpackutils.ModClass;

public class ItemRegistry {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, ModClass.MOD_ID);

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
