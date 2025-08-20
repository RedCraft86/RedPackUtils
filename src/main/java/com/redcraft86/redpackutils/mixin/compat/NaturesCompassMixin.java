package com.redcraft86.redpackutils.mixin.compat;

import com.chaosthedude.naturescompass.client.ClientEventHandler;

import net.minecraftforge.client.event.RenderGuiOverlayEvent;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

// Fix double text overlay
@Mixin(value = ClientEventHandler.class, remap = false)
public class NaturesCompassMixin {
    @Inject(method = "onRenderTick", at = @At("HEAD"), cancellable = true)
    private void renderTick(RenderGuiOverlayEvent.Post event, CallbackInfo ci) {
        if (!event.getOverlay().id().toString().equals("minecraft:crosshair")) {
            ci.cancel();
        }
    }
}