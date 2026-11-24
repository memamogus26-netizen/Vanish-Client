#!/usr/bin/env python3
"""Generates the hand shader post effects (assets/vanishclient/post_effect/*.json).
Animated shaders are baked into PHASES variants; keep PHASES equal to HandShader.PHASES in Java.
Run from the project root: python3 tools/gen_hand_effects.py
"""
import json, os

OUT = "src/main/resources/assets/vanishclient/post_effect"
PHASES = 12

PRESETS = {
    "pink": (0.90, 0.12, 0.50), "red": (0.86, 0.12, 0.12), "orange": (1.00, 0.55, 0.08),
    "yellow": (1.00, 0.86, 0.20), "green": (0.16, 0.78, 0.31), "cyan": (0.16, 0.78, 0.90),
    "blue": (0.20, 0.35, 0.94), "purple": (0.59, 0.24, 0.90), "white": (0.94, 0.94, 0.94),
}

MODES = {"solid": 1, "chroma": 2, "balatro": 3, "smoke": 4, "stripes": 5,
         "glow": 6, "glass": 7, "invert": 8, "snow": 9, "fire": 10}


def effect(mode, phase, a, b):
    return {
        "targets": {"swap": {}},
        "passes": [
            {
                "vertex_shader": "minecraft:core/screenquad",
                "fragment_shader": "vanishclient:post/hand_shader",
                "inputs": [
                    {"sampler_name": "In", "target": "minecraft:main"},
                    {"sampler_name": "Depth", "target": "minecraft:main", "use_depth_buffer": True},
                ],
                "output": "swap",
                "uniforms": {
                    "HandConfig": [
                        {"name": "Params", "type": "vec4", "value": [float(mode), phase, 0.0, 0.0]},
                        {"name": "ColorA", "type": "vec4", "value": [a[0], a[1], a[2], 1.0]},
                        {"name": "ColorB", "type": "vec4", "value": [b[0], b[1], b[2], 1.0]},
                    ]
                },
            },
            {
                "vertex_shader": "minecraft:core/screenquad",
                "fragment_shader": "vanishclient:post/hand_blit",
                "inputs": [{"sampler_name": "In", "target": "swap"}],
                "output": "minecraft:main",
            },
        ],
    }


def write(name, data):
    with open(os.path.join(OUT, name + ".json"), "w") as f:
        json.dump(data, f, indent=2)


os.makedirs(OUT, exist_ok=True)
for fn in os.listdir(OUT):
    if fn.startswith("hand_"):
        os.remove(os.path.join(OUT, fn))

dark = (0.15, 0.0, 0.05)
for shader in ("solid", "stripes", "glow", "glass"):
    for cname, col in PRESETS.items():
        write(f"hand_{shader}_{cname}", effect(MODES[shader], 0.0, col, dark))

write("hand_invert", effect(MODES["invert"], 0.0, (1, 1, 1), dark))

for shader in ("chroma", "balatro", "smoke", "snow", "fire"):
    a = (0.90, 0.10, 0.10) if shader == "balatro" else (1.0, 1.0, 1.0)
    for i in range(PHASES):
        write(f"hand_{shader}_{i:02d}", effect(MODES[shader], i / PHASES, a, dark))

print("wrote", len(os.listdir(OUT)), "effects")
