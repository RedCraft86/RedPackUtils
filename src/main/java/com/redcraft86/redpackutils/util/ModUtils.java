package com.redcraft86.redpackutils.util;

import org.slf4j.Logger;
import com.mojang.logging.LogUtils;
import com.redcraft86.redpackutils.ModClass;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.level.Level;
import net.minecraft.core.BlockPos;
import javax.annotation.Nonnull;
//import java.lang.reflect.Field;

public class ModUtils {
    private static final Logger LOGGER = LogUtils.getLogger();

    @Nonnull
    public static MutableComponent makeTooltip(String key) {
        return Component.translatable(String.format("tooltip.%s.%s", ModClass.MOD_ID, key));
    }

//    public static Object getPrivateField(Object target, String fieldName) {
//        if (target == null) {
//            return null;
//        }
//        try {
//            Field field = target.getClass().getDeclaredField(fieldName);
//            field.setAccessible(true);
//            return field.get(target);
//        } catch (NoSuchFieldException | IllegalAccessException e) {
//            LOGGER.warn("[RedPackUtils] Failed to get private field '{}' in '{}' because {}",
//                    fieldName, target.toString(), e.toString());
//            return null;
//        }
//    }
//
//    public static <T> void setPrivateField(Object target, String fieldName, T value) {
//        if (target == null) {
//            return;
//        }
//        try {
//            Field field = target.getClass().getDeclaredField(fieldName);
//            field.setAccessible(true);
//            field.set(target, value);
//        } catch (NoSuchFieldException | IllegalAccessException e) {
//            LOGGER.warn("[RedPackUtils] Failed to set private field '{}' in '{}' because {}",
//                    fieldName, target.toString(), e.toString());
//        }
//    }
}
