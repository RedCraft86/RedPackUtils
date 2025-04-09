package com.redcraft86.redpackutils.util;

import com.redcraft86.redpackutils.RedPackUtils;
import net.minecraft.network.chat.Component;

public class ModUtils {
    // While I can always do RedPackUtils.MOD_ID,
    // it's easier to have this if I ever reuse code since this is in a helper library.
    public static String getModID() { return RedPackUtils.MOD_ID; }

    public static Component getTooltip(String key) {
        return key.isEmpty() ? null : Component.translatable(String.format("tooltip.%s.%s", getModID(), key));
    }
}
