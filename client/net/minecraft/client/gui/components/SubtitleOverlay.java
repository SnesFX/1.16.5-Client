/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 */
package net.minecraft.client.gui.components;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.Iterator;
import java.util.List;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.SoundEventListener;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.client.sounds.WeighedSoundEvents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

public class SubtitleOverlay
extends GuiComponent
implements SoundEventListener {
    private final Minecraft minecraft;
    private final List<Subtitle> subtitles = Lists.newArrayList();
    private boolean isListening;

    public SubtitleOverlay(Minecraft minecraft) {
        this.minecraft = minecraft;
    }

    public void render(PoseStack poseStack) {
        if (!this.isListening && this.minecraft.options.showSubtitles) {
            this.minecraft.getSoundManager().addListener(this);
            this.isListening = true;
        } else if (this.isListening && !this.minecraft.options.showSubtitles) {
            this.minecraft.getSoundManager().removeListener(this);
            this.isListening = false;
        }
        if (!this.isListening || this.subtitles.isEmpty()) {
            return;
        }
        RenderSystem.pushMatrix();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        Vec3 vec3 = new Vec3(this.minecraft.player.getX(), this.minecraft.player.getEyeY(), this.minecraft.player.getZ());
        Vec3 vec32 = new Vec3(0.0, 0.0, -1.0).xRot(-this.minecraft.player.xRot * 0.017453292f).yRot(-this.minecraft.player.yRot * 0.017453292f);
        Vec3 vec33 = new Vec3(0.0, 1.0, 0.0).xRot(-this.minecraft.player.xRot * 0.017453292f).yRot(-this.minecraft.player.yRot * 0.017453292f);
        Vec3 vec34 = vec32.cross(vec33);
        int n = 0;
        int n2 = 0;
        Iterator<Subtitle> iterator = this.subtitles.iterator();
        while (iterator.hasNext()) {
            Subtitle subtitle = iterator.next();
            if (subtitle.getTime() + 3000L <= Util.getMillis()) {
                iterator.remove();
                continue;
            }
            n2 = Math.max(n2, this.minecraft.font.width(subtitle.getText()));
        }
        n2 += this.minecraft.font.width("<") + this.minecraft.font.width(" ") + this.minecraft.font.width(">") + this.minecraft.font.width(" ");
        for (Subtitle subtitle : this.subtitles) {
            int n3 = 255;
            Component component = subtitle.getText();
            Vec3 vec35 = subtitle.getLocation().subtract(vec3).normalize();
            double d = -vec34.dot(vec35);
            double d2 = -vec32.dot(vec35);
            boolean bl = d2 > 0.5;
            int n4 = n2 / 2;
            this.minecraft.font.getClass();
            int n5 = 9;
            int n6 = n5 / 2;
            float f = 1.0f;
            int n7 = this.minecraft.font.width(component);
            int n8 = Mth.floor(Mth.clampedLerp(255.0, 75.0, (float)(Util.getMillis() - subtitle.getTime()) / 3000.0f));
            int n9 = n8 << 16 | n8 << 8 | n8;
            RenderSystem.pushMatrix();
            RenderSystem.translatef((float)this.minecraft.getWindow().getGuiScaledWidth() - (float)n4 * 1.0f - 2.0f, (float)(this.minecraft.getWindow().getGuiScaledHeight() - 30) - (float)(n * (n5 + 1)) * 1.0f, 0.0f);
            RenderSystem.scalef(1.0f, 1.0f, 1.0f);
            SubtitleOverlay.fill(poseStack, -n4 - 1, -n6 - 1, n4 + 1, n6 + 1, this.minecraft.options.getBackgroundColor(0.8f));
            RenderSystem.enableBlend();
            if (!bl) {
                if (d > 0.0) {
                    this.minecraft.font.draw(poseStack, ">", (float)(n4 - this.minecraft.font.width(">")), (float)(-n6), n9 + -16777216);
                } else if (d < 0.0) {
                    this.minecraft.font.draw(poseStack, "<", (float)(-n4), (float)(-n6), n9 + -16777216);
                }
            }
            this.minecraft.font.draw(poseStack, component, (float)(-n7 / 2), (float)(-n6), n9 + -16777216);
            RenderSystem.popMatrix();
            ++n;
        }
        RenderSystem.disableBlend();
        RenderSystem.popMatrix();
    }

    @Override
    public void onPlaySound(SoundInstance soundInstance, WeighedSoundEvents weighedSoundEvents) {
        if (weighedSoundEvents.getSubtitle() == null) {
            return;
        }
        Component component = weighedSoundEvents.getSubtitle();
        if (!this.subtitles.isEmpty()) {
            for (Subtitle subtitle : this.subtitles) {
                if (!subtitle.getText().equals(component)) continue;
                subtitle.refresh(new Vec3(soundInstance.getX(), soundInstance.getY(), soundInstance.getZ()));
                return;
            }
        }
        this.subtitles.add(new Subtitle(component, new Vec3(soundInstance.getX(), soundInstance.getY(), soundInstance.getZ())));
    }

    public class Subtitle {
        private final Component text;
        private long time;
        private Vec3 location;

        public Subtitle(Component component, Vec3 vec3) {
            this.text = component;
            this.location = vec3;
            this.time = Util.getMillis();
        }

        public Component getText() {
            return this.text;
        }

        public long getTime() {
            return this.time;
        }

        public Vec3 getLocation() {
            return this.location;
        }

        public void refresh(Vec3 vec3) {
            this.location = vec3;
            this.time = Util.getMillis();
        }
    }

}

