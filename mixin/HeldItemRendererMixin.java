package com.vanishclient.mixin;

import com.vanishclient.util.HandShaderRenderer;
import net.minecraft.client.render.item.HeldItemRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HeldItemRenderer.class)
public class HeldItemRendererMixin {
    /**
     * The game draws the hand and item and flushes them at the end of this method.
     * Right after that, only hand pixels have depth (the depth buffer was cleared before the hand),
     * so the shader can use the depth buffer as a hand mask.
     */
    @Inject(
        method = "renderItem(FLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/command/OrderedRenderCommandQueue;Lnet/minecraft/client/network/ClientPlayerEntity;I)V",
        at = @At("TAIL")
    )
    private void vanishclient$afterHand(CallbackInfo ci) {
        HandShaderRenderer.render();
    }
}
