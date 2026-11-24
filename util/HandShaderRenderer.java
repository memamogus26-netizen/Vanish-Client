package com.vanishclient.util;

import com.vanishclient.mixin.GameRendererAccessor;
import com.vanishclient.modules.HandShader;
import meteordevelopment.meteorclient.systems.modules.Modules;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.PostEffectProcessor;
import net.minecraft.client.render.DefaultFramebufferSet;
import net.minecraft.client.util.Pool;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Set;

/** Runs the hand shader post effects right after the first-person hand has been drawn. */
public final class HandShaderRenderer {
    private static final Logger LOG = LoggerFactory.getLogger("Vanish Client");
    private static final Set<Identifier> missing = new HashSet<>();
    private static boolean failed;

    private HandShaderRenderer() {}

    public static void render() {
        if (failed) return;

        HandShader module = Modules.get().get(HandShader.class);
        if (module == null || !module.isActive()) return;

        MinecraftClient mc = MinecraftClient.getInstance();
        try {
            Pool pool = ((GameRendererAccessor) mc.gameRenderer).vanishclient$getPool();
            for (Identifier id : module.effectIds()) {
                PostEffectProcessor processor = mc.getShaderLoader().loadPostEffect(id, DefaultFramebufferSet.MAIN_ONLY);
                if (processor == null) {
                    if (missing.add(id)) LOG.warn("Hand shader effect {} could not be loaded, check the log above for the reason.", id);
                    continue;
                }
                processor.render(mc.getFramebuffer(), pool);
            }
        } catch (Throwable t) {
            failed = true;
            LOG.error("Hand shader failed and is disabled until the game restarts.", t);
            module.error("Hand shader failed, it is disabled until restart. Send latest.log.");
        }
    }
}
