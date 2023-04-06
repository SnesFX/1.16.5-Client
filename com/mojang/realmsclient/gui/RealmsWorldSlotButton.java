/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.util.Pair
 *  javax.annotation.Nullable
 */
package com.mojang.realmsclient.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.datafixers.util.Pair;
import com.mojang.realmsclient.dto.RealmsServer;
import com.mojang.realmsclient.dto.RealmsWorldOptions;
import com.mojang.realmsclient.util.RealmsTextureManager;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.TickableWidget;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class RealmsWorldSlotButton
extends Button
implements TickableWidget {
    public static final ResourceLocation SLOT_FRAME_LOCATION = new ResourceLocation("realms", "textures/gui/realms/slot_frame.png");
    public static final ResourceLocation EMPTY_SLOT_LOCATION = new ResourceLocation("realms", "textures/gui/realms/empty_frame.png");
    public static final ResourceLocation DEFAULT_WORLD_SLOT_1 = new ResourceLocation("minecraft", "textures/gui/title/background/panorama_0.png");
    public static final ResourceLocation DEFAULT_WORLD_SLOT_2 = new ResourceLocation("minecraft", "textures/gui/title/background/panorama_2.png");
    public static final ResourceLocation DEFAULT_WORLD_SLOT_3 = new ResourceLocation("minecraft", "textures/gui/title/background/panorama_3.png");
    private static final Component SLOT_ACTIVE_TOOLTIP = new TranslatableComponent("mco.configure.world.slot.tooltip.active");
    private static final Component SWITCH_TO_MINIGAME_SLOT_TOOLTIP = new TranslatableComponent("mco.configure.world.slot.tooltip.minigame");
    private static final Component SWITCH_TO_WORLD_SLOT_TOOLTIP = new TranslatableComponent("mco.configure.world.slot.tooltip");
    private final Supplier<RealmsServer> serverDataProvider;
    private final Consumer<Component> toolTipSetter;
    private final int slotIndex;
    private int animTick;
    @Nullable
    private State state;

    public RealmsWorldSlotButton(int n, int n2, int n3, int n4, Supplier<RealmsServer> supplier, Consumer<Component> consumer, int n5, Button.OnPress onPress) {
        super(n, n2, n3, n4, TextComponent.EMPTY, onPress);
        this.serverDataProvider = supplier;
        this.slotIndex = n5;
        this.toolTipSetter = consumer;
    }

    @Nullable
    public State getState() {
        return this.state;
    }

    @Override
    public void tick() {
        long l;
        boolean bl;
        String string;
        boolean bl2;
        String string2;
        boolean bl3;
        ++this.animTick;
        RealmsServer realmsServer = this.serverDataProvider.get();
        if (realmsServer == null) {
            return;
        }
        RealmsWorldOptions realmsWorldOptions = realmsServer.slots.get(this.slotIndex);
        boolean bl4 = bl3 = this.slotIndex == 4;
        if (bl3) {
            bl2 = realmsServer.worldType == RealmsServer.WorldType.MINIGAME;
            string = "Minigame";
            l = realmsServer.minigameId;
            string2 = realmsServer.minigameImage;
            bl = realmsServer.minigameId == -1;
        } else {
            bl2 = realmsServer.activeSlot == this.slotIndex && realmsServer.worldType != RealmsServer.WorldType.MINIGAME;
            string = realmsWorldOptions.getSlotName(this.slotIndex);
            l = realmsWorldOptions.templateId;
            string2 = realmsWorldOptions.templateImage;
            bl = realmsWorldOptions.empty;
        }
        Action action = RealmsWorldSlotButton.getAction(realmsServer, bl2, bl3);
        Pair<Component, Component> pair = this.getTooltipAndNarration(realmsServer, string, bl, bl3, action);
        this.state = new State(bl2, string, l, string2, bl, bl3, action, (Component)pair.getFirst());
        this.setMessage((Component)pair.getSecond());
    }

    private static Action getAction(RealmsServer realmsServer, boolean bl, boolean bl2) {
        if (bl) {
            if (!realmsServer.expired && realmsServer.state != RealmsServer.State.UNINITIALIZED) {
                return Action.JOIN;
            }
        } else if (bl2) {
            if (!realmsServer.expired) {
                return Action.SWITCH_SLOT;
            }
        } else {
            return Action.SWITCH_SLOT;
        }
        return Action.NOTHING;
    }

    private Pair<Component, Component> getTooltipAndNarration(RealmsServer realmsServer, String string, boolean bl, boolean bl2, Action action) {
        if (action == Action.NOTHING) {
            return Pair.of(null, (Object)new TextComponent(string));
        }
        Component component = bl2 ? (bl ? TextComponent.EMPTY : new TextComponent(" ").append(string).append(" ").append(realmsServer.minigameName)) : new TextComponent(" ").append(string);
        Component component2 = action == Action.JOIN ? SLOT_ACTIVE_TOOLTIP : (bl2 ? SWITCH_TO_MINIGAME_SLOT_TOOLTIP : SWITCH_TO_WORLD_SLOT_TOOLTIP);
        MutableComponent mutableComponent = component2.copy().append(component);
        return Pair.of((Object)component2, (Object)mutableComponent);
    }

    @Override
    public void renderButton(PoseStack poseStack, int n, int n2, float f) {
        if (this.state == null) {
            return;
        }
        this.drawSlotFrame(poseStack, this.x, this.y, n, n2, this.state.isCurrentlyActiveSlot, this.state.slotName, this.slotIndex, this.state.imageId, this.state.image, this.state.empty, this.state.minigame, this.state.action, this.state.actionPrompt);
    }

    private void drawSlotFrame(PoseStack poseStack, int n, int n2, int n3, int n4, boolean bl, String string, int n5, long l, @Nullable String string2, boolean bl2, boolean bl3, Action action, @Nullable Component component) {
        boolean bl4;
        boolean bl5 = this.isHovered();
        if (this.isMouseOver(n3, n4) && component != null) {
            this.toolTipSetter.accept(component);
        }
        Minecraft minecraft = Minecraft.getInstance();
        TextureManager textureManager = minecraft.getTextureManager();
        if (bl3) {
            RealmsTextureManager.bindWorldTemplate(String.valueOf(l), string2);
        } else if (bl2) {
            textureManager.bind(EMPTY_SLOT_LOCATION);
        } else if (string2 != null && l != -1L) {
            RealmsTextureManager.bindWorldTemplate(String.valueOf(l), string2);
        } else if (n5 == 1) {
            textureManager.bind(DEFAULT_WORLD_SLOT_1);
        } else if (n5 == 2) {
            textureManager.bind(DEFAULT_WORLD_SLOT_2);
        } else if (n5 == 3) {
            textureManager.bind(DEFAULT_WORLD_SLOT_3);
        }
        if (bl) {
            float f = 0.85f + 0.15f * Mth.cos((float)this.animTick * 0.2f);
            RenderSystem.color4f(f, f, f, 1.0f);
        } else {
            RenderSystem.color4f(0.56f, 0.56f, 0.56f, 1.0f);
        }
        RealmsWorldSlotButton.blit(poseStack, n + 3, n2 + 3, 0.0f, 0.0f, 74, 74, 74, 74);
        textureManager.bind(SLOT_FRAME_LOCATION);
        boolean bl6 = bl4 = bl5 && action != Action.NOTHING;
        if (bl4) {
            RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        } else if (bl) {
            RenderSystem.color4f(0.8f, 0.8f, 0.8f, 1.0f);
        } else {
            RenderSystem.color4f(0.56f, 0.56f, 0.56f, 1.0f);
        }
        RealmsWorldSlotButton.blit(poseStack, n, n2, 0.0f, 0.0f, 80, 80, 80, 80);
        RealmsWorldSlotButton.drawCenteredString(poseStack, minecraft.font, string, n + 40, n2 + 66, 16777215);
    }

    public static class State {
        private final boolean isCurrentlyActiveSlot;
        private final String slotName;
        private final long imageId;
        private final String image;
        public final boolean empty;
        public final boolean minigame;
        public final Action action;
        @Nullable
        private final Component actionPrompt;

        State(boolean bl, String string, long l, @Nullable String string2, boolean bl2, boolean bl3, Action action, @Nullable Component component) {
            this.isCurrentlyActiveSlot = bl;
            this.slotName = string;
            this.imageId = l;
            this.image = string2;
            this.empty = bl2;
            this.minigame = bl3;
            this.action = action;
            this.actionPrompt = component;
        }
    }

    public static enum Action {
        NOTHING,
        SWITCH_SLOT,
        JOIN;
        
    }

}

