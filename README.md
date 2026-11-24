# Vanish Client (Meteor addon, Minecraft 1.21.11)

Adds a **Vanish Client** category to Meteor with:

| Module | What it does |
|---|---|
| `human-aim-assist` | Aim assist with micro rotations (your original module). |
| `trails` | Ribbon trail behind players. Colors / Render / Style / Dimensions. "Only F5" (on by default) hides your own trail in first person. |
| `kill-effect` | Death (entity dissolves into rising particles + shockwave ring), Particles, Thunder or Blood whenever any entity dies. |
| `target-esp` | Circle, Ghosts, Crystals or Lightning on your current target. Every mode has its own settings group. Separate friend settings. |
| `hands-shader` | Real shaders on your hand and held item: Solid, Chroma, Balatro, Smoke, Stripes, Glow, Glass, Invert, Snow, Fire. Dual mode runs two at once. |

## Build
JDK 21, then `./gradlew build` (jar in `build/libs`). Pushing to GitHub also builds it via the workflow.
If a version "does not exist", update `yarn`, `fabric-loader` and `loom` in `gradle/libs.versions.toml`.

## Known limits
- **Hands Shader** is a post effect drawn right after the hand (mixin on `HeldItemRenderer.renderItem`), masked with the depth buffer. It was written without being able to run it. If the hand is not shaded, send `latest.log`. Animated shaders are pre-baked into 12 frames (`tools/gen_hand_effects.py`), and Solid/Glow/Glass/Stripes use a colour preset list. Fire has fixed flame colours.
- Kill Effect triggers when an entity's health reaches 0 on your client. Servers that remove entities instantly on death may not trigger it.
- Target ESP "target" = the entity you last hit (attack key + crosshair), kept for `follow-time` seconds.
- Ghost "texture" is approximated with glow layers (Soft / Bloom / Smoke), not real textures.
- Not compiled by the author. If the build fails, send the errors.
