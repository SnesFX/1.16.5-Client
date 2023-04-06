/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  javax.annotation.Nullable
 */
package net.minecraft.client.gui.components.toasts;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.List;
import java.util.OptionalInt;
import java.util.function.ToIntFunction;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.toasts.Toast;
import net.minecraft.client.gui.components.toasts.ToastComponent;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;

public class SystemToast
implements Toast {
    private final SystemToastIds id;
    private Component title;
    private List<FormattedCharSequence> messageLines;
    private long lastChanged;
    private boolean changed;
    private final int width;

    public SystemToast(SystemToastIds systemToastIds, Component component, @Nullable Component component2) {
        this(systemToastIds, component, (List<FormattedCharSequence>)SystemToast.nullToEmpty(component2), 160);
    }

    public static SystemToast multiline(Minecraft minecraft, SystemToastIds systemToastIds, Component component, Component component2) {
        Font font = minecraft.font;
        List<FormattedCharSequence> list = font.split(component2, 200);
        int n = Math.max(200, list.stream().mapToInt(font::width).max().orElse(200));
        return new SystemToast(systemToastIds, component, list, n + 30);
    }

    private SystemToast(SystemToastIds systemToastIds, Component component, List<FormattedCharSequence> list, int n) {
        this.id = systemToastIds;
        this.title = component;
        this.messageLines = list;
        this.width = n;
    }

    private static ImmutableList<FormattedCharSequence> nullToEmpty(@Nullable Component component) {
        return component == null ? ImmutableList.of() : ImmutableList.of((Object)component.getVisualOrderText());
    }

    @Override
    public int width() {
        return this.width;
    }

    @Override
    public Toast.Visibility render(PoseStack poseStack, ToastComponent toastComponent, long l) {
        int n;
        if (this.changed) {
            this.lastChanged = l;
            this.changed = false;
        }
        toastComponent.getMinecraft().getTextureManager().bind(TEXTURE);
        RenderSystem.color3f(1.0f, 1.0f, 1.0f);
        int n2 = this.width();
        int n3 = 12;
        if (n2 == 160 && this.messageLines.size() <= 1) {
            toastComponent.blit(poseStack, 0, 0, 0, 64, n2, this.height());
        } else {
            n = this.height() + Math.max(0, this.messageLines.size() - 1) * 12;
            int n4 = 28;
            int n5 = Math.min(4, n - 28);
            this.renderBackgroundRow(poseStack, toastComponent, n2, 0, 0, 28);
            for (int i = 28; i < n - n5; i += 10) {
                this.renderBackgroundRow(poseStack, toastComponent, n2, 16, i, Math.min(16, n - i - n5));
            }
            this.renderBackgroundRow(poseStack, toastComponent, n2, 32 - n5, n - n5, n5);
        }
        if (this.messageLines == null) {
            toastComponent.getMinecraft().font.draw(poseStack, this.title, 18.0f, 12.0f, -256);
        } else {
            toastComponent.getMinecraft().font.draw(poseStack, this.title, 18.0f, 7.0f, -256);
            for (n = 0; n < this.messageLines.size(); ++n) {
                toastComponent.getMinecraft().font.draw(poseStack, this.messageLines.get(n), 18.0f, (float)(18 + n * 12), -1);
            }
        }
        return l - this.lastChanged < 5000L ? Toast.Visibility.SHOW : Toast.Visibility.HIDE;
    }

    private void renderBackgroundRow(PoseStack poseStack, ToastComponent toastComponent, int n, int n2, int n3, int n4) {
        int n5 = n2 == 0 ? 20 : 5;
        int n6 = Math.min(60, n - n5);
        toastComponent.blit(poseStack, 0, n3, 0, 64 + n2, n5, n4);
        for (int i = n5; i < n - n6; i += 64) {
            toastComponent.blit(poseStack, i, n3, 32, 64 + n2, Math.min(64, n - i - n6), n4);
        }
        toastComponent.blit(poseStack, n - n6, n3, 160 - n6, 64 + n2, n6, n4);
    }

    public void reset(Component component, @Nullable Component component2) {
        this.title = component;
        this.messageLines = SystemToast.nullToEmpty(component2);
        this.changed = true;
    }

    public SystemToastIds getToken() {
        return this.id;
    }

    public static void add(ToastComponent toastComponent, SystemToastIds systemToastIds, Component component, @Nullable Component component2) {
        toastComponent.addToast(new SystemToast(systemToastIds, component, component2));
    }

    public static void addOrUpdate(ToastComponent toastComponent, SystemToastIds systemToastIds, Component component, @Nullable Component component2) {
        SystemToast systemToast = toastComponent.getToast(SystemToast.class, (Object)systemToastIds);
        if (systemToast == null) {
            SystemToast.add(toastComponent, systemToastIds, component, component2);
        } else {
            systemToast.reset(component, component2);
        }
    }

    public static void onWorldAccessFailure(Minecraft minecraft, String string) {
        SystemToast.add(minecraft.getToasts(), SystemToastIds.WORLD_ACCESS_FAILURE, new TranslatableComponent("selectWorld.access_failure"), new TextComponent(string));
    }

    public static void onWorldDeleteFailure(Minecraft minecraft, String string) {
        SystemToast.add(minecraft.getToasts(), SystemToastIds.WORLD_ACCESS_FAILURE, new TranslatableComponent("selectWorld.delete_failure"), new TextComponent(string));
    }

    public static void onPackCopyFailure(Minecraft minecraft, String string) {
        SystemToast.add(minecraft.getToasts(), SystemToastIds.PACK_COPY_FAILURE, new TranslatableComponent("pack.copyFailure"), new TextComponent(string));
    }

    @Override
    public /* synthetic */ Object getToken() {
        return this.getToken();
    }

    public static enum SystemToastIds {
        TUTORIAL_HINT,
        NARRATOR_TOGGLE,
        WORLD_BACKUP,
        WORLD_GEN_SETTINGS_TRANSFER,
        PACK_LOAD_FAILURE,
        WORLD_ACCESS_FAILURE,
        PACK_COPY_FAILURE;
        
    }

}

