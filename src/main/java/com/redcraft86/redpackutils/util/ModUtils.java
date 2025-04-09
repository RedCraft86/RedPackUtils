package com.redcraft86.redpackutils.util;

import com.redcraft86.redpackutils.ModClass;
import net.minecraft.network.chat.Component;

public class ModUtils {
    public static Component getTooltip(String key) {
        return key.isEmpty() ? null : Component.translatable(String.format("tooltip.%s.%s", ModClass.MOD_ID, key));
    }
}
