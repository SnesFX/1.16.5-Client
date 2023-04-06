/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 *  org.apache.commons.lang3.StringUtils
 */
package com.mojang.blaze3d.shaders;

import com.google.common.collect.Maps;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.TextureUtil;
import com.mojang.blaze3d.shaders.Effect;
import com.mojang.blaze3d.systems.RenderSystem;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;

public class Program {
    private final Type type;
    private final String name;
    private final int id;
    private int references;

    private Program(Type type, int n, String string) {
        this.type = type;
        this.id = n;
        this.name = string;
    }

    public void attachToEffect(Effect effect) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        ++this.references;
        GlStateManager.glAttachShader(effect.getId(), this.id);
    }

    public void close() {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        --this.references;
        if (this.references <= 0) {
            GlStateManager.glDeleteShader(this.id);
            this.type.getPrograms().remove(this.name);
        }
    }

    public String getName() {
        return this.name;
    }

    public static Program compileShader(Type type, String string, InputStream inputStream, String string2) throws IOException {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        String string3 = TextureUtil.readResourceAsString(inputStream);
        if (string3 == null) {
            throw new IOException("Could not load program " + type.getName());
        }
        int n = GlStateManager.glCreateShader(type.getGlType());
        GlStateManager.glShaderSource(n, string3);
        GlStateManager.glCompileShader(n);
        if (GlStateManager.glGetShaderi(n, 35713) == 0) {
            String string4 = StringUtils.trim((String)GlStateManager.glGetShaderInfoLog(n, 32768));
            throw new IOException("Couldn't compile " + type.getName() + " program (" + string2 + ", " + string + ") : " + string4);
        }
        Program program = new Program(type, n, string);
        type.getPrograms().put(string, program);
        return program;
    }

    public static enum Type {
        VERTEX("vertex", ".vsh", 35633),
        FRAGMENT("fragment", ".fsh", 35632);
        
        private final String name;
        private final String extension;
        private final int glType;
        private final Map<String, Program> programs = Maps.newHashMap();

        private Type(String string2, String string3, int n2) {
            this.name = string2;
            this.extension = string3;
            this.glType = n2;
        }

        public String getName() {
            return this.name;
        }

        public String getExtension() {
            return this.extension;
        }

        private int getGlType() {
            return this.glType;
        }

        public Map<String, Program> getPrograms() {
            return this.programs;
        }
    }

}

