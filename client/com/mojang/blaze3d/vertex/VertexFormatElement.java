/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package com.mojang.blaze3d.vertex;

import com.mojang.blaze3d.platform.GlStateManager;
import java.util.function.IntConsumer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class VertexFormatElement {
    private static final Logger LOGGER = LogManager.getLogger();
    private final Type type;
    private final Usage usage;
    private final int index;
    private final int count;
    private final int byteSize;

    public VertexFormatElement(int n, Type type, Usage usage, int n2) {
        if (this.supportsUsage(n, usage)) {
            this.usage = usage;
        } else {
            LOGGER.warn("Multiple vertex elements of the same type other than UVs are not supported. Forcing type to UV.");
            this.usage = Usage.UV;
        }
        this.type = type;
        this.index = n;
        this.count = n2;
        this.byteSize = type.getSize() * this.count;
    }

    private boolean supportsUsage(int n, Usage usage) {
        return n == 0 || usage == Usage.UV;
    }

    public final Type getType() {
        return this.type;
    }

    public final Usage getUsage() {
        return this.usage;
    }

    public final int getIndex() {
        return this.index;
    }

    public String toString() {
        return this.count + "," + this.usage.getName() + "," + this.type.getName();
    }

    public final int getByteSize() {
        return this.byteSize;
    }

    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object == null || this.getClass() != object.getClass()) {
            return false;
        }
        VertexFormatElement vertexFormatElement = (VertexFormatElement)object;
        if (this.count != vertexFormatElement.count) {
            return false;
        }
        if (this.index != vertexFormatElement.index) {
            return false;
        }
        if (this.type != vertexFormatElement.type) {
            return false;
        }
        return this.usage == vertexFormatElement.usage;
    }

    public int hashCode() {
        int n = this.type.hashCode();
        n = 31 * n + this.usage.hashCode();
        n = 31 * n + this.index;
        n = 31 * n + this.count;
        return n;
    }

    public void setupBufferState(long l, int n) {
        this.usage.setupBufferState(this.count, this.type.getGlType(), n, l, this.index);
    }

    public void clearBufferState() {
        this.usage.clearBufferState(this.index);
    }

    public static enum Type {
        FLOAT(4, "Float", 5126),
        UBYTE(1, "Unsigned Byte", 5121),
        BYTE(1, "Byte", 5120),
        USHORT(2, "Unsigned Short", 5123),
        SHORT(2, "Short", 5122),
        UINT(4, "Unsigned Int", 5125),
        INT(4, "Int", 5124);
        
        private final int size;
        private final String name;
        private final int glType;

        private Type(int n2, String string2, int n3) {
            this.size = n2;
            this.name = string2;
            this.glType = n3;
        }

        public int getSize() {
            return this.size;
        }

        public String getName() {
            return this.name;
        }

        public int getGlType() {
            return this.glType;
        }
    }

    public static enum Usage {
        POSITION("Position", (n, n2, n3, l, n4) -> {
            GlStateManager._vertexPointer(n, n2, n3, l);
            GlStateManager._enableClientState(32884);
        }, n -> GlStateManager._disableClientState(32884)),
        NORMAL("Normal", (n, n2, n3, l, n4) -> {
            GlStateManager._normalPointer(n2, n3, l);
            GlStateManager._enableClientState(32885);
        }, n -> GlStateManager._disableClientState(32885)),
        COLOR("Vertex Color", (n, n2, n3, l, n4) -> {
            GlStateManager._colorPointer(n, n2, n3, l);
            GlStateManager._enableClientState(32886);
        }, n -> {
            GlStateManager._disableClientState(32886);
            GlStateManager._clearCurrentColor();
        }),
        UV("UV", (n, n2, n3, l, n4) -> {
            GlStateManager._glClientActiveTexture(33984 + n4);
            GlStateManager._texCoordPointer(n, n2, n3, l);
            GlStateManager._enableClientState(32888);
            GlStateManager._glClientActiveTexture(33984);
        }, n -> {
            GlStateManager._glClientActiveTexture(33984 + n);
            GlStateManager._disableClientState(32888);
            GlStateManager._glClientActiveTexture(33984);
        }),
        PADDING("Padding", (n, n2, n3, l, n4) -> {}, n -> {}),
        GENERIC("Generic", (n, n2, n3, l, n4) -> {
            GlStateManager._enableVertexAttribArray(n4);
            GlStateManager._vertexAttribPointer(n4, n, n2, false, n3, l);
        }, GlStateManager::_disableVertexAttribArray);
        
        private final String name;
        private final SetupState setupState;
        private final IntConsumer clearState;

        private Usage(String string2, SetupState setupState, IntConsumer intConsumer) {
            this.name = string2;
            this.setupState = setupState;
            this.clearState = intConsumer;
        }

        private void setupBufferState(int n, int n2, int n3, long l, int n4) {
            this.setupState.setupBufferState(n, n2, n3, l, n4);
        }

        public void clearBufferState(int n) {
            this.clearState.accept(n);
        }

        public String getName() {
            return this.name;
        }

        static interface SetupState {
            public void setupBufferState(int var1, int var2, int var3, long var4, int var6);
        }

    }

}

