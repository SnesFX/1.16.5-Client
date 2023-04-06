/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Maps
 *  com.google.gson.JsonArray
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  it.unimi.dsi.fastutil.ints.IntArrayList
 *  javax.annotation.Nullable
 *  org.apache.commons.io.IOUtils
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.client.renderer;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.shaders.AbstractUniform;
import com.mojang.blaze3d.shaders.BlendMode;
import com.mojang.blaze3d.shaders.Effect;
import com.mojang.blaze3d.shaders.Program;
import com.mojang.blaze3d.shaders.ProgramManager;
import com.mojang.blaze3d.shaders.Uniform;
import com.mojang.blaze3d.systems.RenderSystem;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.IntSupplier;
import javax.annotation.Nullable;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.ChainedJsonException;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.GsonHelper;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class EffectInstance
implements Effect,
AutoCloseable {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final AbstractUniform DUMMY_UNIFORM = new AbstractUniform();
    private static EffectInstance lastAppliedEffect;
    private static int lastProgramId;
    private final Map<String, IntSupplier> samplerMap = Maps.newHashMap();
    private final List<String> samplerNames = Lists.newArrayList();
    private final List<Integer> samplerLocations = Lists.newArrayList();
    private final List<Uniform> uniforms = Lists.newArrayList();
    private final List<Integer> uniformLocations = Lists.newArrayList();
    private final Map<String, Uniform> uniformMap = Maps.newHashMap();
    private final int programId;
    private final String name;
    private boolean dirty;
    private final BlendMode blend;
    private final List<Integer> attributes;
    private final List<String> attributeNames;
    private final Program vertexProgram;
    private final Program fragmentProgram;

    public EffectInstance(ResourceManager resourceManager, String string) throws IOException {
        ResourceLocation resourceLocation = new ResourceLocation("shaders/program/" + string + ".json");
        this.name = string;
        Resource resource = null;
        try {
            Object object;
            JsonArray jsonArray;
            JsonArray jsonArray2;
            resource = resourceManager.getResource(resourceLocation);
            JsonObject jsonObject = GsonHelper.parse(new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8));
            String string2 = GsonHelper.getAsString(jsonObject, "vertex");
            String string3 = GsonHelper.getAsString(jsonObject, "fragment");
            JsonArray jsonArray3 = GsonHelper.getAsJsonArray(jsonObject, "samplers", null);
            if (jsonArray3 != null) {
                int n = 0;
                for (Object object2 : jsonArray3) {
                    try {
                        this.parseSamplerNode((JsonElement)object2);
                    }
                    catch (Exception exception) {
                        object = ChainedJsonException.forException(exception);
                        object.prependJsonKey("samplers[" + n + "]");
                        throw object;
                    }
                    ++n;
                }
            }
            if ((jsonArray = GsonHelper.getAsJsonArray(jsonObject, "attributes", null)) != null) {
                int n = 0;
                this.attributes = Lists.newArrayListWithCapacity((int)jsonArray.size());
                this.attributeNames = Lists.newArrayListWithCapacity((int)jsonArray.size());
                for (JsonElement object3 : jsonArray) {
                    try {
                        this.attributeNames.add(GsonHelper.convertToString(object3, "attribute"));
                    }
                    catch (Exception exception) {
                        ChainedJsonException chainedJsonException = ChainedJsonException.forException(exception);
                        chainedJsonException.prependJsonKey("attributes[" + n + "]");
                        throw chainedJsonException;
                    }
                    ++n;
                }
            } else {
                this.attributes = null;
                this.attributeNames = null;
            }
            if ((jsonArray2 = GsonHelper.getAsJsonArray(jsonObject, "uniforms", null)) != null) {
                int n = 0;
                for (JsonElement jsonElement : jsonArray2) {
                    try {
                        this.parseUniformNode(jsonElement);
                    }
                    catch (Exception exception) {
                        ChainedJsonException chainedJsonException = ChainedJsonException.forException(exception);
                        chainedJsonException.prependJsonKey("uniforms[" + n + "]");
                        throw chainedJsonException;
                    }
                    ++n;
                }
            }
            this.blend = EffectInstance.parseBlendNode(GsonHelper.getAsJsonObject(jsonObject, "blend", null));
            this.vertexProgram = EffectInstance.getOrCreate(resourceManager, Program.Type.VERTEX, string2);
            this.fragmentProgram = EffectInstance.getOrCreate(resourceManager, Program.Type.FRAGMENT, string3);
            this.programId = ProgramManager.createProgram();
            ProgramManager.linkProgram(this);
            this.updateLocations();
            if (this.attributeNames != null) {
                for (String string4 : this.attributeNames) {
                    object = Uniform.glGetAttribLocation(this.programId, string4);
                    this.attributes.add((int)object);
                }
            }
        }
        catch (Exception exception) {
            String string4 = resource != null ? " (" + resource.getSourceName() + ")" : "";
            ChainedJsonException chainedJsonException = ChainedJsonException.forException(exception);
            chainedJsonException.setFilenameAndFlush(resourceLocation.getPath() + string4);
            throw chainedJsonException;
        }
        finally {
            IOUtils.closeQuietly((Closeable)resource);
        }
        this.markDirty();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static Program getOrCreate(ResourceManager resourceManager, Program.Type type, String string) throws IOException {
        Program program = type.getPrograms().get(string);
        if (program == null) {
            ResourceLocation resourceLocation = new ResourceLocation("shaders/program/" + string + type.getExtension());
            Resource resource = resourceManager.getResource(resourceLocation);
            try {
                program = Program.compileShader(type, string, resource.getInputStream(), resource.getSourceName());
            }
            finally {
                IOUtils.closeQuietly((Closeable)resource);
            }
        }
        return program;
    }

    public static BlendMode parseBlendNode(JsonObject jsonObject) {
        if (jsonObject == null) {
            return new BlendMode();
        }
        int n = 32774;
        int n2 = 1;
        int n3 = 0;
        int n4 = 1;
        int n5 = 0;
        boolean bl = true;
        boolean bl2 = false;
        if (GsonHelper.isStringValue(jsonObject, "func") && (n = BlendMode.stringToBlendFunc(jsonObject.get("func").getAsString())) != 32774) {
            bl = false;
        }
        if (GsonHelper.isStringValue(jsonObject, "srcrgb") && (n2 = BlendMode.stringToBlendFactor(jsonObject.get("srcrgb").getAsString())) != 1) {
            bl = false;
        }
        if (GsonHelper.isStringValue(jsonObject, "dstrgb") && (n3 = BlendMode.stringToBlendFactor(jsonObject.get("dstrgb").getAsString())) != 0) {
            bl = false;
        }
        if (GsonHelper.isStringValue(jsonObject, "srcalpha")) {
            n4 = BlendMode.stringToBlendFactor(jsonObject.get("srcalpha").getAsString());
            if (n4 != 1) {
                bl = false;
            }
            bl2 = true;
        }
        if (GsonHelper.isStringValue(jsonObject, "dstalpha")) {
            n5 = BlendMode.stringToBlendFactor(jsonObject.get("dstalpha").getAsString());
            if (n5 != 0) {
                bl = false;
            }
            bl2 = true;
        }
        if (bl) {
            return new BlendMode();
        }
        if (bl2) {
            return new BlendMode(n2, n3, n4, n5, n);
        }
        return new BlendMode(n2, n3, n);
    }

    @Override
    public void close() {
        for (Uniform uniform : this.uniforms) {
            uniform.close();
        }
        ProgramManager.releaseProgram(this);
    }

    public void clear() {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        ProgramManager.glUseProgram(0);
        lastProgramId = -1;
        lastAppliedEffect = null;
        for (int i = 0; i < this.samplerLocations.size(); ++i) {
            if (this.samplerMap.get(this.samplerNames.get(i)) == null) continue;
            GlStateManager._activeTexture(33984 + i);
            GlStateManager._disableTexture();
            GlStateManager._bindTexture(0);
        }
    }

    public void apply() {
        RenderSystem.assertThread(RenderSystem::isOnGameThread);
        this.dirty = false;
        lastAppliedEffect = this;
        this.blend.apply();
        if (this.programId != lastProgramId) {
            ProgramManager.glUseProgram(this.programId);
            lastProgramId = this.programId;
        }
        for (int i = 0; i < this.samplerLocations.size(); ++i) {
            String object = this.samplerNames.get(i);
            IntSupplier intSupplier = this.samplerMap.get(object);
            if (intSupplier == null) continue;
            RenderSystem.activeTexture(33984 + i);
            RenderSystem.enableTexture();
            int n = intSupplier.getAsInt();
            if (n == -1) continue;
            RenderSystem.bindTexture(n);
            Uniform.uploadInteger(this.samplerLocations.get(i), i);
        }
        for (Uniform uniform : this.uniforms) {
            uniform.upload();
        }
    }

    @Override
    public void markDirty() {
        this.dirty = true;
    }

    @Nullable
    public Uniform getUniform(String string) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        return this.uniformMap.get(string);
    }

    public AbstractUniform safeGetUniform(String string) {
        RenderSystem.assertThread(RenderSystem::isOnGameThread);
        Uniform uniform = this.getUniform(string);
        return uniform == null ? DUMMY_UNIFORM : uniform;
    }

    private void updateLocations() {
        int n;
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        IntArrayList intArrayList = new IntArrayList();
        for (n = 0; n < this.samplerNames.size(); ++n) {
            String object = this.samplerNames.get(n);
            int n2 = Uniform.glGetUniformLocation(this.programId, object);
            if (n2 == -1) {
                LOGGER.warn("Shader {} could not find sampler named {} in the specified shader program.", (Object)this.name, (Object)object);
                this.samplerMap.remove(object);
                intArrayList.add(n);
                continue;
            }
            this.samplerLocations.add(n2);
        }
        for (n = intArrayList.size() - 1; n >= 0; --n) {
            this.samplerNames.remove(intArrayList.getInt(n));
        }
        for (Uniform uniform : this.uniforms) {
            String string = uniform.getName();
            int n3 = Uniform.glGetUniformLocation(this.programId, string);
            if (n3 == -1) {
                LOGGER.warn("Could not find uniform named {} in the specified shader program.", (Object)string);
                continue;
            }
            this.uniformLocations.add(n3);
            uniform.setLocation(n3);
            this.uniformMap.put(string, uniform);
        }
    }

    private void parseSamplerNode(JsonElement jsonElement) {
        JsonObject jsonObject = GsonHelper.convertToJsonObject(jsonElement, "sampler");
        String string = GsonHelper.getAsString(jsonObject, "name");
        if (!GsonHelper.isStringValue(jsonObject, "file")) {
            this.samplerMap.put(string, null);
            this.samplerNames.add(string);
            return;
        }
        this.samplerNames.add(string);
    }

    public void setSampler(String string, IntSupplier intSupplier) {
        if (this.samplerMap.containsKey(string)) {
            this.samplerMap.remove(string);
        }
        this.samplerMap.put(string, intSupplier);
        this.markDirty();
    }

    private void parseUniformNode(JsonElement jsonElement) throws ChainedJsonException {
        Object object2;
        JsonObject jsonObject = GsonHelper.convertToJsonObject(jsonElement, "uniform");
        String string = GsonHelper.getAsString(jsonObject, "name");
        int n = Uniform.getTypeFromString(GsonHelper.getAsString(jsonObject, "type"));
        int n2 = GsonHelper.getAsInt(jsonObject, "count");
        float[] arrf = new float[Math.max(n2, 16)];
        JsonArray jsonArray = GsonHelper.getAsJsonArray(jsonObject, "values");
        if (jsonArray.size() != n2 && jsonArray.size() > 1) {
            throw new ChainedJsonException("Invalid amount of values specified (expected " + n2 + ", found " + jsonArray.size() + ")");
        }
        int n3 = 0;
        for (Object object2 : jsonArray) {
            try {
                arrf[n3] = GsonHelper.convertToFloat((JsonElement)object2, "value");
            }
            catch (Exception exception) {
                ChainedJsonException chainedJsonException = ChainedJsonException.forException(exception);
                chainedJsonException.prependJsonKey("values[" + n3 + "]");
                throw chainedJsonException;
            }
            ++n3;
        }
        if (n2 > 1 && jsonArray.size() == 1) {
            while (n3 < n2) {
                arrf[n3] = arrf[0];
                ++n3;
            }
        }
        int n4 = n2 > 1 && n2 <= 4 && n < 8 ? n2 - 1 : 0;
        object2 = new Uniform(string, n + n4, n2, this);
        if (n <= 3) {
            ((Uniform)object2).setSafe((int)arrf[0], (int)arrf[1], (int)arrf[2], (int)arrf[3]);
        } else if (n <= 7) {
            ((Uniform)object2).setSafe(arrf[0], arrf[1], arrf[2], arrf[3]);
        } else {
            ((Uniform)object2).set(arrf);
        }
        this.uniforms.add((Uniform)object2);
    }

    @Override
    public Program getVertexProgram() {
        return this.vertexProgram;
    }

    @Override
    public Program getFragmentProgram() {
        return this.fragmentProgram;
    }

    @Override
    public int getId() {
        return this.programId;
    }

    static {
        lastProgramId = -1;
    }
}

