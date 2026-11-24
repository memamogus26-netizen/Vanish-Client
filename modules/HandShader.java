package com.vanishclient.modules;

import com.vanishclient.VanishClientAddon;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.modules.Module;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;

/**
 * Hands Shader. The effects themselves are post effects in assets/vanishclient (post_effect + shaders/post),
 * drawn right after the hand by HeldItemRendererMixin -> HandShaderRenderer.
 * Animated shaders are pre-baked into PHASES variants, and this module picks the variant for the current time.
 */
public class HandShader extends Module {
    public enum Shader { None, Solid, Chroma, Balatro, Smoke, Stripes, Glow, Glass, Invert, Snow, Fire }
    public enum ColorPreset { Pink, Red, Orange, Yellow, Green, Cyan, Blue, Purple, White }

    /** Must match PHASES in tools/gen_hand_effects.py. */
    private static final int PHASES = 12;

    private final SettingGroup sgMode = settings.createGroup("Mode");

    private final Setting<Shader> mainShader = sgMode.add(new EnumSetting.Builder<Shader>()
        .name("main-shader").description("Primary hand shader effect.")
        .defaultValue(Shader.Fire).build());

    private final Setting<Boolean> dual = sgMode.add(new BoolSetting.Builder()
        .name("dual-shader-mode").description("Combine two effects.")
        .defaultValue(false).build());

    private final Setting<Shader> secondShader = sgMode.add(new EnumSetting.Builder<Shader>()
        .name("second-shader").description("Second effect used in dual mode.")
        .defaultValue(Shader.Glow).visible(dual::get).build());

    private final Setting<ColorPreset> color = sgMode.add(new EnumSetting.Builder<ColorPreset>()
        .name("color").description("Colour for Solid, Glow, Glass and Stripes.")
        .defaultValue(ColorPreset.Pink).build());

    private final Setting<Double> speed = sgMode.add(new DoubleSetting.Builder()
        .name("speed").description("Animation speed for Chroma, Balatro, Smoke, Snow and Fire.")
        .defaultValue(1).min(0.1).sliderMax(5).build());

    public HandShader() {
        super(VanishClientAddon.CATEGORY, "hands-shader", "Shader effects for your hand and held item.");
    }

    /** Post effect ids to run this frame, in order. */
    public List<Identifier> effectIds() {
        List<Identifier> out = new ArrayList<>(2);
        add(out, mainShader.get());
        if (dual.get()) add(out, secondShader.get());
        return out;
    }

    private void add(List<Identifier> out, Shader s) {
        String name = switch (s) {
            case None -> null;
            case Invert -> "hand_invert";
            case Solid, Glow, Glass, Stripes -> "hand_" + s.name().toLowerCase() + "_" + color.get().name().toLowerCase();
            default -> "hand_" + s.name().toLowerCase() + "_" + String.format("%02d", phase());
        };
        if (name != null) out.add(Identifier.of("vanishclient", name));
    }

    /** One full animation loop takes 2 seconds at speed 1. */
    private int phase() {
        double cycle = System.currentTimeMillis() / 1000.0 * speed.get() / 2.0;
        return (int) ((cycle % 1.0) * PHASES) % PHASES;
    }
}
