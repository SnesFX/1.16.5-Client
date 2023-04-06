/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.client.gui.components.toasts;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.components.toasts.ToastComponent;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;

public interface Toast {
    public static final ResourceLocation TEXTURE = new ResourceLocation("textures/gui/toasts.png");
    public static final Object NO_TOKEN = new Object();

    public Visibility render(PoseStack var1, ToastComponent var2, long var3);

    default public Object getToken() {
        return NO_TOKEN;
    }

    default public int width() {
        return 160;
    }

    default public int height() {
        return 32;
    }

    public static enum Visibility {
        SHOW(SoundEvents.UI_TOAST_IN),
        HIDE(SoundEvents.UI_TOAST_OUT);
        
        private final SoundEvent soundEvent;

        private Visibility(SoundEvent soundEvent) {
            this.soundEvent = soundEvent;
        }

        public void playSound(SoundManager soundManager) {
            soundManager.play(SimpleSoundInstance.forUI(this.soundEvent, 1.0f, 1.0f));
        }
    }

}

