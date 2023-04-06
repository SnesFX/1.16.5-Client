/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.client;

import com.mojang.blaze3d.platform.InputConstants;
import java.util.function.BooleanSupplier;
import net.minecraft.client.KeyMapping;

public class ToggleKeyMapping
extends KeyMapping {
    private final BooleanSupplier needsToggle;

    public ToggleKeyMapping(String string, int n, String string2, BooleanSupplier booleanSupplier) {
        super(string, InputConstants.Type.KEYSYM, n, string2);
        this.needsToggle = booleanSupplier;
    }

    @Override
    public void setDown(boolean bl) {
        if (this.needsToggle.getAsBoolean()) {
            if (bl) {
                super.setDown(!this.isDown());
            }
        } else {
            super.setDown(bl);
        }
    }
}

