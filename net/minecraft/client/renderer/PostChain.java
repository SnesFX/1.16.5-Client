/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Maps
 *  com.google.gson.JsonArray
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonSyntaxException
 *  org.apache.commons.io.IOUtils
 */
package net.minecraft.client.renderer;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.shaders.Uniform;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.math.Matrix4f;
import java.io.Closeable;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.IntSupplier;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.EffectInstance;
import net.minecraft.client.renderer.PostPass;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.ChainedJsonException;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.GsonHelper;
import org.apache.commons.io.IOUtils;

public class PostChain
implements AutoCloseable {
    private final RenderTarget screenTarget;
    private final ResourceManager resourceManager;
    private final String name;
    private final List<PostPass> passes = Lists.newArrayList();
    private final Map<String, RenderTarget> customRenderTargets = Maps.newHashMap();
    private final List<RenderTarget> fullSizedTargets = Lists.newArrayList();
    private Matrix4f shaderOrthoMatrix;
    private int screenWidth;
    private int screenHeight;
    private float time;
    private float lastStamp;

    public PostChain(TextureManager textureManager, ResourceManager resourceManager, RenderTarget renderTarget, ResourceLocation resourceLocation) throws IOException, JsonSyntaxException {
        this.resourceManager = resourceManager;
        this.screenTarget = renderTarget;
        this.time = 0.0f;
        this.lastStamp = 0.0f;
        this.screenWidth = renderTarget.viewWidth;
        this.screenHeight = renderTarget.viewHeight;
        this.name = resourceLocation.toString();
        this.updateOrthoMatrix();
        this.load(textureManager, resourceLocation);
    }

    private void load(TextureManager textureManager, ResourceLocation resourceLocation) throws IOException, JsonSyntaxException {
        Resource resource;
        block11 : {
            resource = null;
            try {
                JsonArray jsonArray;
                int n;
                resource = this.resourceManager.getResource(resourceLocation);
                JsonObject jsonObject = GsonHelper.parse(new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8));
                if (GsonHelper.isArrayNode(jsonObject, "targets")) {
                    jsonArray = jsonObject.getAsJsonArray("targets");
                    n = 0;
                    for (JsonElement jsonElement : jsonArray) {
                        try {
                            this.parseTargetNode(jsonElement);
                        }
                        catch (Exception exception) {
                            ChainedJsonException chainedJsonException = ChainedJsonException.forException(exception);
                            chainedJsonException.prependJsonKey("targets[" + n + "]");
                            throw chainedJsonException;
                        }
                        ++n;
                    }
                }
                if (!GsonHelper.isArrayNode(jsonObject, "passes")) break block11;
                jsonArray = jsonObject.getAsJsonArray("passes");
                n = 0;
                for (JsonElement jsonElement : jsonArray) {
                    try {
                        this.parsePassNode(textureManager, jsonElement);
                    }
                    catch (Exception exception) {
                        ChainedJsonException chainedJsonException = ChainedJsonException.forException(exception);
                        chainedJsonException.prependJsonKey("passes[" + n + "]");
                        throw chainedJsonException;
                    }
                    ++n;
                }
            }
            catch (Exception exception) {
                try {
                    String string = resource != null ? " (" + resource.getSourceName() + ")" : "";
                    ChainedJsonException chainedJsonException = ChainedJsonException.forException(exception);
                    chainedJsonException.setFilenameAndFlush(resourceLocation.getPath() + string);
                    throw chainedJsonException;
                }
                catch (Throwable throwable) {
                    IOUtils.closeQuietly(resource);
                    throw throwable;
                }
            }
        }
        IOUtils.closeQuietly((Closeable)resource);
    }

    private void parseTargetNode(JsonElement jsonElement) throws ChainedJsonException {
        if (GsonHelper.isStringValue(jsonElement)) {
            this.addTempTarget(jsonElement.getAsString(), this.screenWidth, this.screenHeight);
        } else {
            JsonObject jsonObject = GsonHelper.convertToJsonObject(jsonElement, "target");
            String string = GsonHelper.getAsString(jsonObject, "name");
            int n = GsonHelper.getAsInt(jsonObject, "width", this.screenWidth);
            int n2 = GsonHelper.getAsInt(jsonObject, "height", this.screenHeight);
            if (this.customRenderTargets.containsKey(string)) {
                throw new ChainedJsonException(string + " is already defined");
            }
            this.addTempTarget(string, n, n2);
        }
    }

    private void parsePassNode(TextureManager textureManager, JsonElement jsonElement) throws IOException {
        Object object;
        JsonArray jsonArray;
        JsonObject jsonObject;
        block21 : {
            jsonObject = GsonHelper.convertToJsonObject(jsonElement, "pass");
            String string = GsonHelper.getAsString(jsonObject, "name");
            String string2 = GsonHelper.getAsString(jsonObject, "intarget");
            String string3 = GsonHelper.getAsString(jsonObject, "outtarget");
            RenderTarget renderTarget = this.getRenderTarget(string2);
            RenderTarget renderTarget2 = this.getRenderTarget(string3);
            if (renderTarget == null) {
                throw new ChainedJsonException("Input target '" + string2 + "' does not exist");
            }
            if (renderTarget2 == null) {
                throw new ChainedJsonException("Output target '" + string3 + "' does not exist");
            }
            PostPass postPass = this.addPass(string, renderTarget, renderTarget2);
            JsonArray jsonArray2 = GsonHelper.getAsJsonArray(jsonObject, "auxtargets", null);
            if (jsonArray2 == null) break block21;
            int n = 0;
            for (Object object2 : jsonArray2) {
                block20 : {
                    Object object3;
                    try {
                        boolean bl;
                        RenderTarget renderTarget3;
                        block22 : {
                            Object object4;
                            JsonObject jsonObject2 = GsonHelper.convertToJsonObject((JsonElement)object2, "auxtarget");
                            object3 = GsonHelper.getAsString(jsonObject2, "name");
                            object = GsonHelper.getAsString(jsonObject2, "id");
                            if (((String)object).endsWith(":depth")) {
                                bl = true;
                                object4 = ((String)object).substring(0, ((String)object).lastIndexOf(58));
                            } else {
                                bl = false;
                                object4 = object;
                            }
                            renderTarget3 = this.getRenderTarget((String)object4);
                            if (renderTarget3 != null) break block22;
                            if (bl) {
                                throw new ChainedJsonException("Render target '" + (String)object4 + "' can't be used as depth buffer");
                            }
                            ResourceLocation resourceLocation = new ResourceLocation("textures/effect/" + (String)object4 + ".png");
                            Resource resource = null;
                            try {
                                resource = this.resourceManager.getResource(resourceLocation);
                            }
                            catch (FileNotFoundException fileNotFoundException) {
                                try {
                                    throw new ChainedJsonException("Render target or texture '" + (String)object4 + "' does not exist");
                                }
                                catch (Throwable throwable) {
                                    IOUtils.closeQuietly(resource);
                                    throw throwable;
                                }
                            }
                            IOUtils.closeQuietly((Closeable)resource);
                            textureManager.bind(resourceLocation);
                            AbstractTexture abstractTexture = textureManager.getTexture(resourceLocation);
                            int n2 = GsonHelper.getAsInt(jsonObject2, "width");
                            int n3 = GsonHelper.getAsInt(jsonObject2, "height");
                            boolean bl2 = GsonHelper.getAsBoolean(jsonObject2, "bilinear");
                            if (bl2) {
                                RenderSystem.texParameter(3553, 10241, 9729);
                                RenderSystem.texParameter(3553, 10240, 9729);
                            } else {
                                RenderSystem.texParameter(3553, 10241, 9728);
                                RenderSystem.texParameter(3553, 10240, 9728);
                            }
                            postPass.addAuxAsset((String)object3, abstractTexture::getId, n2, n3);
                            break block20;
                        }
                        if (bl) {
                            postPass.addAuxAsset((String)object3, renderTarget3::getDepthTextureId, renderTarget3.width, renderTarget3.height);
                        } else {
                            postPass.addAuxAsset((String)object3, renderTarget3::getColorTextureId, renderTarget3.width, renderTarget3.height);
                        }
                    }
                    catch (Exception exception) {
                        object3 = ChainedJsonException.forException(exception);
                        ((ChainedJsonException)object3).prependJsonKey("auxtargets[" + n + "]");
                        throw object3;
                    }
                }
                ++n;
            }
        }
        if ((jsonArray = GsonHelper.getAsJsonArray(jsonObject, "uniforms", null)) != null) {
            int n = 0;
            for (JsonObject jsonObject2 : jsonArray) {
                try {
                    this.parseUniformNode((JsonElement)jsonObject2);
                }
                catch (Exception exception) {
                    object = ChainedJsonException.forException(exception);
                    ((ChainedJsonException)object).prependJsonKey("uniforms[" + n + "]");
                    throw object;
                }
                ++n;
            }
        }
    }

    private void parseUniformNode(JsonElement jsonElement) throws ChainedJsonException {
        JsonObject jsonObject = GsonHelper.convertToJsonObject(jsonElement, "uniform");
        String string = GsonHelper.getAsString(jsonObject, "name");
        Uniform uniform = this.passes.get(this.passes.size() - 1).getEffect().getUniform(string);
        if (uniform == null) {
            throw new ChainedJsonException("Uniform '" + string + "' does not exist");
        }
        float[] arrf = new float[4];
        int n = 0;
        JsonArray jsonArray = GsonHelper.getAsJsonArray(jsonObject, "values");
        for (JsonElement jsonElement2 : jsonArray) {
            try {
                arrf[n] = GsonHelper.convertToFloat(jsonElement2, "value");
            }
            catch (Exception exception) {
                ChainedJsonException chainedJsonException = ChainedJsonException.forException(exception);
                chainedJsonException.prependJsonKey("values[" + n + "]");
                throw chainedJsonException;
            }
            ++n;
        }
        switch (n) {
            case 0: {
                break;
            }
            case 1: {
                uniform.set(arrf[0]);
                break;
            }
            case 2: {
                uniform.set(arrf[0], arrf[1]);
                break;
            }
            case 3: {
                uniform.set(arrf[0], arrf[1], arrf[2]);
                break;
            }
            case 4: {
                uniform.set(arrf[0], arrf[1], arrf[2], arrf[3]);
            }
        }
    }

    public RenderTarget getTempTarget(String string) {
        return this.customRenderTargets.get(string);
    }

    public void addTempTarget(String string, int n, int n2) {
        RenderTarget renderTarget = new RenderTarget(n, n2, true, Minecraft.ON_OSX);
        renderTarget.setClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        this.customRenderTargets.put(string, renderTarget);
        if (n == this.screenWidth && n2 == this.screenHeight) {
            this.fullSizedTargets.add(renderTarget);
        }
    }

    @Override
    public void close() {
        for (RenderTarget object : this.customRenderTargets.values()) {
            object.destroyBuffers();
        }
        for (PostPass postPass : this.passes) {
            postPass.close();
        }
        this.passes.clear();
    }

    public PostPass addPass(String string, RenderTarget renderTarget, RenderTarget renderTarget2) throws IOException {
        PostPass postPass = new PostPass(this.resourceManager, string, renderTarget, renderTarget2);
        this.passes.add(this.passes.size(), postPass);
        return postPass;
    }

    private void updateOrthoMatrix() {
        this.shaderOrthoMatrix = Matrix4f.orthographic(this.screenTarget.width, this.screenTarget.height, 0.1f, 1000.0f);
    }

    public void resize(int n, int n2) {
        this.screenWidth = this.screenTarget.width;
        this.screenHeight = this.screenTarget.height;
        this.updateOrthoMatrix();
        for (PostPass object : this.passes) {
            object.setOrthoMatrix(this.shaderOrthoMatrix);
        }
        for (RenderTarget renderTarget : this.fullSizedTargets) {
            renderTarget.resize(n, n2, Minecraft.ON_OSX);
        }
    }

    public void process(float f) {
        if (f < this.lastStamp) {
            this.time += 1.0f - this.lastStamp;
            this.time += f;
        } else {
            this.time += f - this.lastStamp;
        }
        this.lastStamp = f;
        while (this.time > 20.0f) {
            this.time -= 20.0f;
        }
        for (PostPass postPass : this.passes) {
            postPass.process(this.time / 20.0f);
        }
    }

    public final String getName() {
        return this.name;
    }

    private RenderTarget getRenderTarget(String string) {
        if (string == null) {
            return null;
        }
        if (string.equals("minecraft:main")) {
            return this.screenTarget;
        }
        return this.customRenderTargets.get(string);
    }
}

