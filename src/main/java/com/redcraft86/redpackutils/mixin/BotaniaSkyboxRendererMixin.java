package com.redcraft86.redpackutils.mixin;

import org.joml.Matrix4f;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexBuffer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import vazkii.botania.client.render.world.SkyblockSkyRenderer;
import com.redcraft86.redpackutils.config.ClientConfig;

@Mixin(value = SkyblockSkyRenderer.class, remap = false)
public class BotaniaSkyboxRendererMixin {
    // We need this because Botania's Garden of Glass skybox makes the stars really bright. (and I prefer the skybox without that)
    @Inject(method = "renderStars", at = @At("HEAD"), cancellable = true, require = 0)
    private void stopRenderStars(VertexBuffer starVBO, PoseStack ms, Matrix4f projMat,
                                        float partialTicks, Runnable resetFog, CallbackInfo ci) {
        if (ClientConfig.disableGoGStars) {
            ci.cancel();
        }
    }
}
