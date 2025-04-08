package com.redcraft86.redpackutils.mixin;

import org.joml.Matrix4f;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexBuffer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import vazkii.botania.client.render.world.SkyblockSkyRenderer;
import com.redcraft86.redpackutils.RedPackUtilsConfig;

@Mixin(value = SkyblockSkyRenderer.class, remap = false)
public class BotaniaSkyboxMixin {
    // Botania's Garden of Glass skybox makes the stars really bright.
    // So we hijack botania's method that does that and stop it from doing anything.
    @Inject(method = "renderStars", at = @At("HEAD"), cancellable = true, require = 0)
    private static void StopRenderStars(VertexBuffer starVBO, PoseStack ms, Matrix4f projMat,
                                        float partialTicks, Runnable resetFog, CallbackInfo ci) {
        if (RedPackUtilsConfig.disableGoGStars) {
            ci.cancel();
        }
    }
}
