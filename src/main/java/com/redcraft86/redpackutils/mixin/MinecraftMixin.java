package com.redcraft86.redpackutils.mixin;

import com.redcraft86.redpackutils.config.ClientConfig;

import net.minecraft.client.Minecraft;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Minecraft.class)
public class MinecraftMixin {

    @Unique
    private static final int UPDATE_INTERVAL = 5;

    @Unique
    private static int tickCounter = 0;

    @Inject(method = "createTitle", at = @At("RETURN"), cancellable = true)
    private void onUpdateTitle(CallbackInfoReturnable<String> cir) {
        String result = cir.getReturnValue();
        if (ClientConfig.titleName != null) {
            result = ClientConfig.titleName;
        }
        if (ClientConfig.memUsage) {
            Runtime runtime = Runtime.getRuntime();
            long totalMem = runtime.totalMemory();
            long usedMem = (totalMem - runtime.freeMemory()) / (1024 * 1024);
            result += String.format(" | Memory: %,d MB / %,d MB (%,d MB Allocated)", usedMem,
                    runtime.maxMemory() / (1024 * 1024), totalMem / (1024 * 1024));
        }
        cir.setReturnValue(result);
    }

    @Inject(method = "tick", at = @At("TAIL"))
    private void onTick(CallbackInfo ci) {
        tickCounter++; // Slow tick
        if (tickCounter >= UPDATE_INTERVAL) {
            ((Minecraft)(Object)this).updateTitle();
            tickCounter = 0;
        }
    }
}
