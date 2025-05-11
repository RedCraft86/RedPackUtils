package com.redcraft86.redpackutils.util;

import org.slf4j.Logger;
import com.mojang.logging.LogUtils;
import com.redcraft86.redpackutils.ModClass;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import javax.annotation.Nonnull;

public class ModUtils {
    private static final Logger LOGGER = LogUtils.getLogger();

    @Nonnull
    public static MutableComponent makeTooltip(String key) {
        return Component.translatable(String.format("tooltip.%s.%s", ModClass.MOD_ID, key));
    }

    @Nonnull
    public static MutableComponent makeMessage(String key) {
        return Component.translatable(String.format("message.%s.%s", ModClass.MOD_ID, key));
    }
}
