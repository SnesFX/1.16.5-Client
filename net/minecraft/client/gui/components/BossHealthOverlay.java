/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 */
package net.minecraft.client.gui.components;

import com.google.common.collect.Maps;
import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.Collection;
import java.util.Map;
import java.util.UUID;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.components.LerpingBossEvent;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.protocol.game.ClientboundBossEventPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.BossEvent;

public class BossHealthOverlay
extends GuiComponent {
    private static final ResourceLocation GUI_BARS_LOCATION = new ResourceLocation("textures/gui/bars.png");
    private final Minecraft minecraft;
    private final Map<UUID, LerpingBossEvent> events = Maps.newLinkedHashMap();

    public BossHealthOverlay(Minecraft minecraft) {
        this.minecraft = minecraft;
    }

    public void render(PoseStack poseStack) {
        if (this.events.isEmpty()) {
            return;
        }
        int n = this.minecraft.getWindow().getGuiScaledWidth();
        int n2 = 12;
        for (LerpingBossEvent lerpingBossEvent : this.events.values()) {
            int n3 = n / 2 - 91;
            int n4 = n2;
            RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
            this.minecraft.getTextureManager().bind(GUI_BARS_LOCATION);
            this.drawBar(poseStack, n3, n4, lerpingBossEvent);
            Component component = lerpingBossEvent.getName();
            int n5 = this.minecraft.font.width(component);
            int n6 = n / 2 - n5 / 2;
            int n7 = n4 - 9;
            this.minecraft.font.drawShadow(poseStack, component, (float)n6, (float)n7, 16777215);
            this.minecraft.font.getClass();
            if ((n2 += 10 + 9) < this.minecraft.getWindow().getGuiScaledHeight() / 3) continue;
            break;
        }
    }

    private void drawBar(PoseStack poseStack, int n, int n2, BossEvent bossEvent) {
        int n3;
        this.blit(poseStack, n, n2, 0, bossEvent.getColor().ordinal() * 5 * 2, 182, 5);
        if (bossEvent.getOverlay() != BossEvent.BossBarOverlay.PROGRESS) {
            this.blit(poseStack, n, n2, 0, 80 + (bossEvent.getOverlay().ordinal() - 1) * 5 * 2, 182, 5);
        }
        if ((n3 = (int)(bossEvent.getPercent() * 183.0f)) > 0) {
            this.blit(poseStack, n, n2, 0, bossEvent.getColor().ordinal() * 5 * 2 + 5, n3, 5);
            if (bossEvent.getOverlay() != BossEvent.BossBarOverlay.PROGRESS) {
                this.blit(poseStack, n, n2, 0, 80 + (bossEvent.getOverlay().ordinal() - 1) * 5 * 2 + 5, n3, 5);
            }
        }
    }

    public void update(ClientboundBossEventPacket clientboundBossEventPacket) {
        if (clientboundBossEventPacket.getOperation() == ClientboundBossEventPacket.Operation.ADD) {
            this.events.put(clientboundBossEventPacket.getId(), new LerpingBossEvent(clientboundBossEventPacket));
        } else if (clientboundBossEventPacket.getOperation() == ClientboundBossEventPacket.Operation.REMOVE) {
            this.events.remove(clientboundBossEventPacket.getId());
        } else {
            this.events.get(clientboundBossEventPacket.getId()).update(clientboundBossEventPacket);
        }
    }

    public void reset() {
        this.events.clear();
    }

    public boolean shouldPlayMusic() {
        if (!this.events.isEmpty()) {
            for (BossEvent bossEvent : this.events.values()) {
                if (!bossEvent.shouldPlayBossMusic()) continue;
                return true;
            }
        }
        return false;
    }

    public boolean shouldDarkenScreen() {
        if (!this.events.isEmpty()) {
            for (BossEvent bossEvent : this.events.values()) {
                if (!bossEvent.shouldDarkenScreen()) continue;
                return true;
            }
        }
        return false;
    }

    public boolean shouldCreateWorldFog() {
        if (!this.events.isEmpty()) {
            for (BossEvent bossEvent : this.events.values()) {
                if (!bossEvent.shouldCreateWorldFog()) continue;
                return true;
            }
        }
        return false;
    }
}

