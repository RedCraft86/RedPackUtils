package com.redcraft86.redpackutils.util;

import com.redcraft86.redpackutils.ModClass;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

import javax.annotation.Nonnull;

public class ModUtils {
    @Nonnull
    public static MutableComponent makeTooltip(String key) {
        return Component.translatable(String.format("tooltip.%s.%s", ModClass.MOD_ID, key));
    }
}
