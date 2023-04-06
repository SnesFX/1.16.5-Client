/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 *  org.lwjgl.system.MemoryUtil
 */
package com.mojang.blaze3d.shaders;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.shaders.AbstractUniform;
import com.mojang.blaze3d.shaders.Effect;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.math.Matrix4f;
import java.nio.Buffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.system.MemoryUtil;

public class Uniform
extends AbstractUniform
implements AutoCloseable {
    private static final Logger LOGGER = LogManager.getLogger();
    private int location;
    private final int count;
    private final int type;
    private final IntBuffer intValues;
    private final FloatBuffer floatValues;
    private final String name;
    private boolean dirty;
    private final Effect parent;

    public Uniform(String string, int n, int n2, Effect effect) {
        this.name = string;
        this.count = n2;
        this.type = n;
        this.parent = effect;
        if (n <= 3) {
            this.intValues = MemoryUtil.memAllocInt((int)n2);
            this.floatValues = null;
        } else {
            this.intValues = null;
            this.floatValues = MemoryUtil.memAllocFloat((int)n2);
        }
        this.location = -1;
        this.markDirty();
    }

    public static int glGetUniformLocation(int n, CharSequence charSequence) {
        return GlStateManager._glGetUniformLocation(n, charSequence);
    }

    public static void uploadInteger(int n, int n2) {
        RenderSystem.glUniform1i(n, n2);
    }

    public static int glGetAttribLocation(int n, CharSequence charSequence) {
        return GlStateManager._glGetAttribLocation(n, charSequence);
    }

    @Override
    public void close() {
        if (this.intValues != null) {
            MemoryUtil.memFree((Buffer)this.intValues);
        }
        if (this.floatValues != null) {
            MemoryUtil.memFree((Buffer)this.floatValues);
        }
    }

    private void markDirty() {
        this.dirty = true;
        if (this.parent != null) {
            this.parent.markDirty();
        }
    }

    public static int getTypeFromString(String string) {
        int n = -1;
        if ("int".equals(string)) {
            n = 0;
        } else if ("float".equals(string)) {
            n = 4;
        } else if (string.startsWith("matrix")) {
            if (string.endsWith("2x2")) {
                n = 8;
            } else if (string.endsWith("3x3")) {
                n = 9;
            } else if (string.endsWith("4x4")) {
                n = 10;
            }
        }
        return n;
    }

    public void setLocation(int n) {
        this.location = n;
    }

    public String getName() {
        return this.name;
    }

    @Override
    public void set(float f) {
        this.floatValues.position(0);
        this.floatValues.put(0, f);
        this.markDirty();
    }

    @Override
    public void set(float f, float f2) {
        this.floatValues.position(0);
        this.floatValues.put(0, f);
        this.floatValues.put(1, f2);
        this.markDirty();
    }

    @Override
    public void set(float f, float f2, float f3) {
        this.floatValues.position(0);
        this.floatValues.put(0, f);
        this.floatValues.put(1, f2);
        this.floatValues.put(2, f3);
        this.markDirty();
    }

    @Override
    public void set(float f, float f2, float f3, float f4) {
        this.floatValues.position(0);
        this.floatValues.put(f);
        this.floatValues.put(f2);
        this.floatValues.put(f3);
        this.floatValues.put(f4);
        this.floatValues.flip();
        this.markDirty();
    }

    @Override
    public void setSafe(float f, float f2, float f3, float f4) {
        this.floatValues.position(0);
        if (this.type >= 4) {
            this.floatValues.put(0, f);
        }
        if (this.type >= 5) {
            this.floatValues.put(1, f2);
        }
        if (this.type >= 6) {
            this.floatValues.put(2, f3);
        }
        if (this.type >= 7) {
            this.floatValues.put(3, f4);
        }
        this.markDirty();
    }

    @Override
    public void setSafe(int n, int n2, int n3, int n4) {
        this.intValues.position(0);
        if (this.type >= 0) {
            this.intValues.put(0, n);
        }
        if (this.type >= 1) {
            this.intValues.put(1, n2);
        }
        if (this.type >= 2) {
            this.intValues.put(2, n3);
        }
        if (this.type >= 3) {
            this.intValues.put(3, n4);
        }
        this.markDirty();
    }

    @Override
    public void set(float[] arrf) {
        if (arrf.length < this.count) {
            LOGGER.warn("Uniform.set called with a too-small value array (expected {}, got {}). Ignoring.", (Object)this.count, (Object)arrf.length);
            return;
        }
        this.floatValues.position(0);
        this.floatValues.put(arrf);
        this.floatValues.position(0);
        this.markDirty();
    }

    @Override
    public void set(Matrix4f matrix4f) {
        this.floatValues.position(0);
        matrix4f.store(this.floatValues);
        this.markDirty();
    }

    public void upload() {
        if (!this.dirty) {
            // empty if block
        }
        this.dirty = false;
        if (this.type <= 3) {
            this.uploadAsInteger();
        } else if (this.type <= 7) {
            this.uploadAsFloat();
        } else if (this.type <= 10) {
            this.uploadAsMatrix();
        } else {
            LOGGER.warn("Uniform.upload called, but type value ({}) is not a valid type. Ignoring.", (Object)this.type);
            return;
        }
    }

    private void uploadAsInteger() {
        this.floatValues.clear();
        switch (this.type) {
            case 0: {
                RenderSystem.glUniform1(this.location, this.intValues);
                break;
            }
            case 1: {
                RenderSystem.glUniform2(this.location, this.intValues);
                break;
            }
            case 2: {
                RenderSystem.glUniform3(this.location, this.intValues);
                break;
            }
            case 3: {
                RenderSystem.glUniform4(this.location, this.intValues);
                break;
            }
            default: {
                LOGGER.warn("Uniform.upload called, but count value ({}) is  not in the range of 1 to 4. Ignoring.", (Object)this.count);
            }
        }
    }

    private void uploadAsFloat() {
        this.floatValues.clear();
        switch (this.type) {
            case 4: {
                RenderSystem.glUniform1(this.location, this.floatValues);
                break;
            }
            case 5: {
                RenderSystem.glUniform2(this.location, this.floatValues);
                break;
            }
            case 6: {
                RenderSystem.glUniform3(this.location, this.floatValues);
                break;
            }
            case 7: {
                RenderSystem.glUniform4(this.location, this.floatValues);
                break;
            }
            default: {
                LOGGER.warn("Uniform.upload called, but count value ({}) is not in the range of 1 to 4. Ignoring.", (Object)this.count);
            }
        }
    }

    private void uploadAsMatrix() {
        this.floatValues.clear();
        switch (this.type) {
            case 8: {
                RenderSystem.glUniformMatrix2(this.location, false, this.floatValues);
                break;
            }
            case 9: {
                RenderSystem.glUniformMatrix3(this.location, false, this.floatValues);
                break;
            }
            case 10: {
                RenderSystem.glUniformMatrix4(this.location, false, this.floatValues);
            }
        }
    }
}

