/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 *  javax.annotation.Nullable
 */
package net.minecraft.client.gui;

import com.google.common.collect.Maps;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Matrix4f;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;
import java.util.Collection;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraft.world.level.saveddata.maps.MapDecoration;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;

public class MapRenderer
implements AutoCloseable {
    private static final ResourceLocation MAP_ICONS_LOCATION = new ResourceLocation("textures/map/map_icons.png");
    private static final RenderType MAP_ICONS = RenderType.text(MAP_ICONS_LOCATION);
    private final TextureManager textureManager;
    private final Map<String, MapInstance> maps = Maps.newHashMap();

    public MapRenderer(TextureManager textureManager) {
        this.textureManager = textureManager;
    }

    public void update(MapItemSavedData mapItemSavedData) {
        this.getMapInstance(mapItemSavedData).updateTexture();
    }

    public void render(PoseStack poseStack, MultiBufferSource multiBufferSource, MapItemSavedData mapItemSavedData, boolean bl, int n) {
        this.getMapInstance(mapItemSavedData).draw(poseStack, multiBufferSource, bl, n);
    }

    private MapInstance getMapInstance(MapItemSavedData mapItemSavedData) {
        MapInstance mapInstance = this.maps.get(mapItemSavedData.getId());
        if (mapInstance == null) {
            mapInstance = new MapInstance(mapItemSavedData);
            this.maps.put(mapItemSavedData.getId(), mapInstance);
        }
        return mapInstance;
    }

    @Nullable
    public MapInstance getMapInstanceIfExists(String string) {
        return this.maps.get(string);
    }

    public void resetData() {
        for (MapInstance mapInstance : this.maps.values()) {
            mapInstance.close();
        }
        this.maps.clear();
    }

    @Nullable
    public MapItemSavedData getData(@Nullable MapInstance mapInstance) {
        if (mapInstance != null) {
            return mapInstance.data;
        }
        return null;
    }

    @Override
    public void close() {
        this.resetData();
    }

    class MapInstance
    implements AutoCloseable {
        private final MapItemSavedData data;
        private final DynamicTexture texture;
        private final RenderType renderType;

        private MapInstance(MapItemSavedData mapItemSavedData) {
            this.data = mapItemSavedData;
            this.texture = new DynamicTexture(128, 128, true);
            ResourceLocation resourceLocation = MapRenderer.this.textureManager.register("map/" + mapItemSavedData.getId(), this.texture);
            this.renderType = RenderType.text(resourceLocation);
        }

        private void updateTexture() {
            for (int i = 0; i < 128; ++i) {
                for (int j = 0; j < 128; ++j) {
                    int n = j + i * 128;
                    int n2 = this.data.colors[n] & 0xFF;
                    if (n2 / 4 == 0) {
                        this.texture.getPixels().setPixelRGBA(j, i, 0);
                        continue;
                    }
                    this.texture.getPixels().setPixelRGBA(j, i, MaterialColor.MATERIAL_COLORS[n2 / 4].calculateRGBColor(n2 & 3));
                }
            }
            this.texture.upload();
        }

        private void draw(PoseStack poseStack, MultiBufferSource multiBufferSource, boolean bl, int n) {
            boolean bl2 = false;
            boolean bl3 = false;
            float f = 0.0f;
            Matrix4f matrix4f = poseStack.last().pose();
            VertexConsumer vertexConsumer = multiBufferSource.getBuffer(this.renderType);
            vertexConsumer.vertex(matrix4f, 0.0f, 128.0f, -0.01f).color(255, 255, 255, 255).uv(0.0f, 1.0f).uv2(n).endVertex();
            vertexConsumer.vertex(matrix4f, 128.0f, 128.0f, -0.01f).color(255, 255, 255, 255).uv(1.0f, 1.0f).uv2(n).endVertex();
            vertexConsumer.vertex(matrix4f, 128.0f, 0.0f, -0.01f).color(255, 255, 255, 255).uv(1.0f, 0.0f).uv2(n).endVertex();
            vertexConsumer.vertex(matrix4f, 0.0f, 0.0f, -0.01f).color(255, 255, 255, 255).uv(0.0f, 0.0f).uv2(n).endVertex();
            int n2 = 0;
            for (MapDecoration mapDecoration : this.data.decorations.values()) {
                if (bl && !mapDecoration.renderOnFrame()) continue;
                poseStack.pushPose();
                poseStack.translate(0.0f + (float)mapDecoration.getX() / 2.0f + 64.0f, 0.0f + (float)mapDecoration.getY() / 2.0f + 64.0f, -0.019999999552965164);
                poseStack.mulPose(Vector3f.ZP.rotationDegrees((float)(mapDecoration.getRot() * 360) / 16.0f));
                poseStack.scale(4.0f, 4.0f, 3.0f);
                poseStack.translate(-0.125, 0.125, 0.0);
                byte by = mapDecoration.getImage();
                float f2 = (float)(by % 16 + 0) / 16.0f;
                float f3 = (float)(by / 16 + 0) / 16.0f;
                float f4 = (float)(by % 16 + 1) / 16.0f;
                float f5 = (float)(by / 16 + 1) / 16.0f;
                Matrix4f matrix4f2 = poseStack.last().pose();
                float f6 = -0.001f;
                VertexConsumer vertexConsumer2 = multiBufferSource.getBuffer(MAP_ICONS);
                vertexConsumer2.vertex(matrix4f2, -1.0f, 1.0f, (float)n2 * -0.001f).color(255, 255, 255, 255).uv(f2, f3).uv2(n).endVertex();
                vertexConsumer2.vertex(matrix4f2, 1.0f, 1.0f, (float)n2 * -0.001f).color(255, 255, 255, 255).uv(f4, f3).uv2(n).endVertex();
                vertexConsumer2.vertex(matrix4f2, 1.0f, -1.0f, (float)n2 * -0.001f).color(255, 255, 255, 255).uv(f4, f5).uv2(n).endVertex();
                vertexConsumer2.vertex(matrix4f2, -1.0f, -1.0f, (float)n2 * -0.001f).color(255, 255, 255, 255).uv(f2, f5).uv2(n).endVertex();
                poseStack.popPose();
                if (mapDecoration.getName() != null) {
                    Font font = Minecraft.getInstance().font;
                    Component component = mapDecoration.getName();
                    float f7 = font.width(component);
                    font.getClass();
                    float f8 = Mth.clamp(25.0f / f7, 0.0f, 6.0f / 9.0f);
                    poseStack.pushPose();
                    poseStack.translate(0.0f + (float)mapDecoration.getX() / 2.0f + 64.0f - f7 * f8 / 2.0f, 0.0f + (float)mapDecoration.getY() / 2.0f + 64.0f + 4.0f, -0.02500000037252903);
                    poseStack.scale(f8, f8, 1.0f);
                    poseStack.translate(0.0, 0.0, -0.10000000149011612);
                    font.drawInBatch(component, 0.0f, 0.0f, -1, false, poseStack.last().pose(), multiBufferSource, false, Integer.MIN_VALUE, n);
                    poseStack.popPose();
                }
                ++n2;
            }
        }

        @Override
        public void close() {
            this.texture.close();
        }
    }

}

