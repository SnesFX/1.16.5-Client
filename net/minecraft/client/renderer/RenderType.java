/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  it.unimi.dsi.fastutil.Hash
 *  it.unimi.dsi.fastutil.Hash$Strategy
 *  it.unimi.dsi.fastutil.objects.ObjectOpenCustomHashSet
 *  javax.annotation.Nullable
 */
package net.minecraft.client.renderer;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import it.unimi.dsi.fastutil.Hash;
import it.unimi.dsi.fastutil.objects.ObjectOpenCustomHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.function.Consumer;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.blockentity.TheEndPortalRenderer;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.resources.ResourceLocation;

public abstract class RenderType
extends RenderStateShard {
    private static final RenderType SOLID = RenderType.create("solid", DefaultVertexFormat.BLOCK, 7, 2097152, true, false, CompositeState.builder().setShadeModelState(SMOOTH_SHADE).setLightmapState(LIGHTMAP).setTextureState(BLOCK_SHEET_MIPPED).createCompositeState(true));
    private static final RenderType CUTOUT_MIPPED = RenderType.create("cutout_mipped", DefaultVertexFormat.BLOCK, 7, 131072, true, false, CompositeState.builder().setShadeModelState(SMOOTH_SHADE).setLightmapState(LIGHTMAP).setTextureState(BLOCK_SHEET_MIPPED).setAlphaState(MIDWAY_ALPHA).createCompositeState(true));
    private static final RenderType CUTOUT = RenderType.create("cutout", DefaultVertexFormat.BLOCK, 7, 131072, true, false, CompositeState.builder().setShadeModelState(SMOOTH_SHADE).setLightmapState(LIGHTMAP).setTextureState(BLOCK_SHEET).setAlphaState(MIDWAY_ALPHA).createCompositeState(true));
    private static final RenderType TRANSLUCENT = RenderType.create("translucent", DefaultVertexFormat.BLOCK, 7, 262144, true, true, RenderType.translucentState());
    private static final RenderType TRANSLUCENT_MOVING_BLOCK = RenderType.create("translucent_moving_block", DefaultVertexFormat.BLOCK, 7, 262144, false, true, RenderType.translucentMovingBlockState());
    private static final RenderType TRANSLUCENT_NO_CRUMBLING = RenderType.create("translucent_no_crumbling", DefaultVertexFormat.BLOCK, 7, 262144, false, true, RenderType.translucentState());
    private static final RenderType LEASH = RenderType.create("leash", DefaultVertexFormat.POSITION_COLOR_LIGHTMAP, 7, 256, CompositeState.builder().setTextureState(NO_TEXTURE).setCullState(NO_CULL).setLightmapState(LIGHTMAP).createCompositeState(false));
    private static final RenderType WATER_MASK = RenderType.create("water_mask", DefaultVertexFormat.POSITION, 7, 256, CompositeState.builder().setTextureState(NO_TEXTURE).setWriteMaskState(DEPTH_WRITE).createCompositeState(false));
    private static final RenderType ARMOR_GLINT = RenderType.create("armor_glint", DefaultVertexFormat.POSITION_TEX, 7, 256, CompositeState.builder().setTextureState(new RenderStateShard.TextureStateShard(ItemRenderer.ENCHANT_GLINT_LOCATION, true, false)).setWriteMaskState(COLOR_WRITE).setCullState(NO_CULL).setDepthTestState(EQUAL_DEPTH_TEST).setTransparencyState(GLINT_TRANSPARENCY).setTexturingState(GLINT_TEXTURING).setLayeringState(VIEW_OFFSET_Z_LAYERING).createCompositeState(false));
    private static final RenderType ARMOR_ENTITY_GLINT = RenderType.create("armor_entity_glint", DefaultVertexFormat.POSITION_TEX, 7, 256, CompositeState.builder().setTextureState(new RenderStateShard.TextureStateShard(ItemRenderer.ENCHANT_GLINT_LOCATION, true, false)).setWriteMaskState(COLOR_WRITE).setCullState(NO_CULL).setDepthTestState(EQUAL_DEPTH_TEST).setTransparencyState(GLINT_TRANSPARENCY).setTexturingState(ENTITY_GLINT_TEXTURING).setLayeringState(VIEW_OFFSET_Z_LAYERING).createCompositeState(false));
    private static final RenderType GLINT_TRANSLUCENT = RenderType.create("glint_translucent", DefaultVertexFormat.POSITION_TEX, 7, 256, CompositeState.builder().setTextureState(new RenderStateShard.TextureStateShard(ItemRenderer.ENCHANT_GLINT_LOCATION, true, false)).setWriteMaskState(COLOR_WRITE).setCullState(NO_CULL).setDepthTestState(EQUAL_DEPTH_TEST).setTransparencyState(GLINT_TRANSPARENCY).setTexturingState(GLINT_TEXTURING).setOutputState(ITEM_ENTITY_TARGET).createCompositeState(false));
    private static final RenderType GLINT = RenderType.create("glint", DefaultVertexFormat.POSITION_TEX, 7, 256, CompositeState.builder().setTextureState(new RenderStateShard.TextureStateShard(ItemRenderer.ENCHANT_GLINT_LOCATION, true, false)).setWriteMaskState(COLOR_WRITE).setCullState(NO_CULL).setDepthTestState(EQUAL_DEPTH_TEST).setTransparencyState(GLINT_TRANSPARENCY).setTexturingState(GLINT_TEXTURING).createCompositeState(false));
    private static final RenderType GLINT_DIRECT = RenderType.create("glint_direct", DefaultVertexFormat.POSITION_TEX, 7, 256, CompositeState.builder().setTextureState(new RenderStateShard.TextureStateShard(ItemRenderer.ENCHANT_GLINT_LOCATION, true, false)).setWriteMaskState(COLOR_WRITE).setCullState(NO_CULL).setDepthTestState(EQUAL_DEPTH_TEST).setTransparencyState(GLINT_TRANSPARENCY).setTexturingState(GLINT_TEXTURING).createCompositeState(false));
    private static final RenderType ENTITY_GLINT = RenderType.create("entity_glint", DefaultVertexFormat.POSITION_TEX, 7, 256, CompositeState.builder().setTextureState(new RenderStateShard.TextureStateShard(ItemRenderer.ENCHANT_GLINT_LOCATION, true, false)).setWriteMaskState(COLOR_WRITE).setCullState(NO_CULL).setDepthTestState(EQUAL_DEPTH_TEST).setTransparencyState(GLINT_TRANSPARENCY).setOutputState(ITEM_ENTITY_TARGET).setTexturingState(ENTITY_GLINT_TEXTURING).createCompositeState(false));
    private static final RenderType ENTITY_GLINT_DIRECT = RenderType.create("entity_glint_direct", DefaultVertexFormat.POSITION_TEX, 7, 256, CompositeState.builder().setTextureState(new RenderStateShard.TextureStateShard(ItemRenderer.ENCHANT_GLINT_LOCATION, true, false)).setWriteMaskState(COLOR_WRITE).setCullState(NO_CULL).setDepthTestState(EQUAL_DEPTH_TEST).setTransparencyState(GLINT_TRANSPARENCY).setTexturingState(ENTITY_GLINT_TEXTURING).createCompositeState(false));
    private static final RenderType LIGHTNING = RenderType.create("lightning", DefaultVertexFormat.POSITION_COLOR, 7, 256, false, true, CompositeState.builder().setWriteMaskState(COLOR_DEPTH_WRITE).setTransparencyState(LIGHTNING_TRANSPARENCY).setOutputState(WEATHER_TARGET).setShadeModelState(SMOOTH_SHADE).createCompositeState(false));
    private static final RenderType TRIPWIRE = RenderType.create("tripwire", DefaultVertexFormat.BLOCK, 7, 262144, true, true, RenderType.tripwireState());
    public static final CompositeRenderType LINES = RenderType.create("lines", DefaultVertexFormat.POSITION_COLOR, 1, 256, CompositeState.builder().setLineState(new RenderStateShard.LineStateShard(OptionalDouble.empty())).setLayeringState(VIEW_OFFSET_Z_LAYERING).setTransparencyState(TRANSLUCENT_TRANSPARENCY).setOutputState(ITEM_ENTITY_TARGET).setWriteMaskState(COLOR_DEPTH_WRITE).createCompositeState(false));
    private final VertexFormat format;
    private final int mode;
    private final int bufferSize;
    private final boolean affectsCrumbling;
    private final boolean sortOnUpload;
    private final Optional<RenderType> asOptional;

    public static RenderType solid() {
        return SOLID;
    }

    public static RenderType cutoutMipped() {
        return CUTOUT_MIPPED;
    }

    public static RenderType cutout() {
        return CUTOUT;
    }

    private static CompositeState translucentState() {
        return CompositeState.builder().setShadeModelState(SMOOTH_SHADE).setLightmapState(LIGHTMAP).setTextureState(BLOCK_SHEET_MIPPED).setTransparencyState(TRANSLUCENT_TRANSPARENCY).setOutputState(TRANSLUCENT_TARGET).createCompositeState(true);
    }

    public static RenderType translucent() {
        return TRANSLUCENT;
    }

    private static CompositeState translucentMovingBlockState() {
        return CompositeState.builder().setShadeModelState(SMOOTH_SHADE).setLightmapState(LIGHTMAP).setTextureState(BLOCK_SHEET_MIPPED).setTransparencyState(TRANSLUCENT_TRANSPARENCY).setOutputState(ITEM_ENTITY_TARGET).createCompositeState(true);
    }

    public static RenderType translucentMovingBlock() {
        return TRANSLUCENT_MOVING_BLOCK;
    }

    public static RenderType translucentNoCrumbling() {
        return TRANSLUCENT_NO_CRUMBLING;
    }

    public static RenderType armorCutoutNoCull(ResourceLocation resourceLocation) {
        CompositeState compositeState = CompositeState.builder().setTextureState(new RenderStateShard.TextureStateShard(resourceLocation, false, false)).setTransparencyState(NO_TRANSPARENCY).setDiffuseLightingState(DIFFUSE_LIGHTING).setAlphaState(DEFAULT_ALPHA).setCullState(NO_CULL).setLightmapState(LIGHTMAP).setOverlayState(OVERLAY).setLayeringState(VIEW_OFFSET_Z_LAYERING).createCompositeState(true);
        return RenderType.create("armor_cutout_no_cull", DefaultVertexFormat.NEW_ENTITY, 7, 256, true, false, compositeState);
    }

    public static RenderType entitySolid(ResourceLocation resourceLocation) {
        CompositeState compositeState = CompositeState.builder().setTextureState(new RenderStateShard.TextureStateShard(resourceLocation, false, false)).setTransparencyState(NO_TRANSPARENCY).setDiffuseLightingState(DIFFUSE_LIGHTING).setLightmapState(LIGHTMAP).setOverlayState(OVERLAY).createCompositeState(true);
        return RenderType.create("entity_solid", DefaultVertexFormat.NEW_ENTITY, 7, 256, true, false, compositeState);
    }

    public static RenderType entityCutout(ResourceLocation resourceLocation) {
        CompositeState compositeState = CompositeState.builder().setTextureState(new RenderStateShard.TextureStateShard(resourceLocation, false, false)).setTransparencyState(NO_TRANSPARENCY).setDiffuseLightingState(DIFFUSE_LIGHTING).setAlphaState(DEFAULT_ALPHA).setLightmapState(LIGHTMAP).setOverlayState(OVERLAY).createCompositeState(true);
        return RenderType.create("entity_cutout", DefaultVertexFormat.NEW_ENTITY, 7, 256, true, false, compositeState);
    }

    public static RenderType entityCutoutNoCull(ResourceLocation resourceLocation, boolean bl) {
        CompositeState compositeState = CompositeState.builder().setTextureState(new RenderStateShard.TextureStateShard(resourceLocation, false, false)).setTransparencyState(NO_TRANSPARENCY).setDiffuseLightingState(DIFFUSE_LIGHTING).setAlphaState(DEFAULT_ALPHA).setCullState(NO_CULL).setLightmapState(LIGHTMAP).setOverlayState(OVERLAY).createCompositeState(bl);
        return RenderType.create("entity_cutout_no_cull", DefaultVertexFormat.NEW_ENTITY, 7, 256, true, false, compositeState);
    }

    public static RenderType entityCutoutNoCull(ResourceLocation resourceLocation) {
        return RenderType.entityCutoutNoCull(resourceLocation, true);
    }

    public static RenderType entityCutoutNoCullZOffset(ResourceLocation resourceLocation, boolean bl) {
        CompositeState compositeState = CompositeState.builder().setTextureState(new RenderStateShard.TextureStateShard(resourceLocation, false, false)).setTransparencyState(NO_TRANSPARENCY).setDiffuseLightingState(DIFFUSE_LIGHTING).setAlphaState(DEFAULT_ALPHA).setCullState(NO_CULL).setLightmapState(LIGHTMAP).setOverlayState(OVERLAY).setLayeringState(VIEW_OFFSET_Z_LAYERING).createCompositeState(bl);
        return RenderType.create("entity_cutout_no_cull_z_offset", DefaultVertexFormat.NEW_ENTITY, 7, 256, true, false, compositeState);
    }

    public static RenderType entityCutoutNoCullZOffset(ResourceLocation resourceLocation) {
        return RenderType.entityCutoutNoCullZOffset(resourceLocation, true);
    }

    public static RenderType itemEntityTranslucentCull(ResourceLocation resourceLocation) {
        CompositeState compositeState = CompositeState.builder().setTextureState(new RenderStateShard.TextureStateShard(resourceLocation, false, false)).setTransparencyState(TRANSLUCENT_TRANSPARENCY).setOutputState(ITEM_ENTITY_TARGET).setDiffuseLightingState(DIFFUSE_LIGHTING).setAlphaState(DEFAULT_ALPHA).setLightmapState(LIGHTMAP).setOverlayState(OVERLAY).setWriteMaskState(RenderStateShard.COLOR_DEPTH_WRITE).createCompositeState(true);
        return RenderType.create("item_entity_translucent_cull", DefaultVertexFormat.NEW_ENTITY, 7, 256, true, true, compositeState);
    }

    public static RenderType entityTranslucentCull(ResourceLocation resourceLocation) {
        CompositeState compositeState = CompositeState.builder().setTextureState(new RenderStateShard.TextureStateShard(resourceLocation, false, false)).setTransparencyState(TRANSLUCENT_TRANSPARENCY).setDiffuseLightingState(DIFFUSE_LIGHTING).setAlphaState(DEFAULT_ALPHA).setLightmapState(LIGHTMAP).setOverlayState(OVERLAY).createCompositeState(true);
        return RenderType.create("entity_translucent_cull", DefaultVertexFormat.NEW_ENTITY, 7, 256, true, true, compositeState);
    }

    public static RenderType entityTranslucent(ResourceLocation resourceLocation, boolean bl) {
        CompositeState compositeState = CompositeState.builder().setTextureState(new RenderStateShard.TextureStateShard(resourceLocation, false, false)).setTransparencyState(TRANSLUCENT_TRANSPARENCY).setDiffuseLightingState(DIFFUSE_LIGHTING).setAlphaState(DEFAULT_ALPHA).setCullState(NO_CULL).setLightmapState(LIGHTMAP).setOverlayState(OVERLAY).createCompositeState(bl);
        return RenderType.create("entity_translucent", DefaultVertexFormat.NEW_ENTITY, 7, 256, true, true, compositeState);
    }

    public static RenderType entityTranslucent(ResourceLocation resourceLocation) {
        return RenderType.entityTranslucent(resourceLocation, true);
    }

    public static RenderType entitySmoothCutout(ResourceLocation resourceLocation) {
        CompositeState compositeState = CompositeState.builder().setTextureState(new RenderStateShard.TextureStateShard(resourceLocation, false, false)).setAlphaState(MIDWAY_ALPHA).setDiffuseLightingState(DIFFUSE_LIGHTING).setShadeModelState(SMOOTH_SHADE).setCullState(NO_CULL).setLightmapState(LIGHTMAP).createCompositeState(true);
        return RenderType.create("entity_smooth_cutout", DefaultVertexFormat.NEW_ENTITY, 7, 256, compositeState);
    }

    public static RenderType beaconBeam(ResourceLocation resourceLocation, boolean bl) {
        CompositeState compositeState = CompositeState.builder().setTextureState(new RenderStateShard.TextureStateShard(resourceLocation, false, false)).setTransparencyState(bl ? TRANSLUCENT_TRANSPARENCY : NO_TRANSPARENCY).setWriteMaskState(bl ? COLOR_WRITE : COLOR_DEPTH_WRITE).setFogState(NO_FOG).createCompositeState(false);
        return RenderType.create("beacon_beam", DefaultVertexFormat.BLOCK, 7, 256, false, true, compositeState);
    }

    public static RenderType entityDecal(ResourceLocation resourceLocation) {
        CompositeState compositeState = CompositeState.builder().setTextureState(new RenderStateShard.TextureStateShard(resourceLocation, false, false)).setDiffuseLightingState(DIFFUSE_LIGHTING).setAlphaState(DEFAULT_ALPHA).setDepthTestState(EQUAL_DEPTH_TEST).setCullState(NO_CULL).setLightmapState(LIGHTMAP).setOverlayState(OVERLAY).createCompositeState(false);
        return RenderType.create("entity_decal", DefaultVertexFormat.NEW_ENTITY, 7, 256, compositeState);
    }

    public static RenderType entityNoOutline(ResourceLocation resourceLocation) {
        CompositeState compositeState = CompositeState.builder().setTextureState(new RenderStateShard.TextureStateShard(resourceLocation, false, false)).setTransparencyState(TRANSLUCENT_TRANSPARENCY).setDiffuseLightingState(DIFFUSE_LIGHTING).setAlphaState(DEFAULT_ALPHA).setCullState(NO_CULL).setLightmapState(LIGHTMAP).setOverlayState(OVERLAY).setWriteMaskState(COLOR_WRITE).createCompositeState(false);
        return RenderType.create("entity_no_outline", DefaultVertexFormat.NEW_ENTITY, 7, 256, false, true, compositeState);
    }

    public static RenderType entityShadow(ResourceLocation resourceLocation) {
        CompositeState compositeState = CompositeState.builder().setTextureState(new RenderStateShard.TextureStateShard(resourceLocation, false, false)).setTransparencyState(TRANSLUCENT_TRANSPARENCY).setDiffuseLightingState(DIFFUSE_LIGHTING).setAlphaState(DEFAULT_ALPHA).setCullState(CULL).setLightmapState(LIGHTMAP).setOverlayState(OVERLAY).setWriteMaskState(COLOR_WRITE).setDepthTestState(LEQUAL_DEPTH_TEST).setLayeringState(VIEW_OFFSET_Z_LAYERING).createCompositeState(false);
        return RenderType.create("entity_shadow", DefaultVertexFormat.NEW_ENTITY, 7, 256, false, false, compositeState);
    }

    public static RenderType dragonExplosionAlpha(ResourceLocation resourceLocation, float f) {
        CompositeState compositeState = CompositeState.builder().setTextureState(new RenderStateShard.TextureStateShard(resourceLocation, false, false)).setAlphaState(new RenderStateShard.AlphaStateShard(f)).setCullState(NO_CULL).createCompositeState(true);
        return RenderType.create("entity_alpha", DefaultVertexFormat.NEW_ENTITY, 7, 256, compositeState);
    }

    public static RenderType eyes(ResourceLocation resourceLocation) {
        RenderStateShard.TextureStateShard textureStateShard = new RenderStateShard.TextureStateShard(resourceLocation, false, false);
        return RenderType.create("eyes", DefaultVertexFormat.NEW_ENTITY, 7, 256, false, true, CompositeState.builder().setTextureState(textureStateShard).setTransparencyState(ADDITIVE_TRANSPARENCY).setWriteMaskState(COLOR_WRITE).setFogState(BLACK_FOG).createCompositeState(false));
    }

    public static RenderType energySwirl(ResourceLocation resourceLocation, float f, float f2) {
        return RenderType.create("energy_swirl", DefaultVertexFormat.NEW_ENTITY, 7, 256, false, true, CompositeState.builder().setTextureState(new RenderStateShard.TextureStateShard(resourceLocation, false, false)).setTexturingState(new RenderStateShard.OffsetTexturingStateShard(f, f2)).setFogState(BLACK_FOG).setTransparencyState(ADDITIVE_TRANSPARENCY).setDiffuseLightingState(DIFFUSE_LIGHTING).setAlphaState(DEFAULT_ALPHA).setCullState(NO_CULL).setLightmapState(LIGHTMAP).setOverlayState(OVERLAY).createCompositeState(false));
    }

    public static RenderType leash() {
        return LEASH;
    }

    public static RenderType waterMask() {
        return WATER_MASK;
    }

    public static RenderType outline(ResourceLocation resourceLocation) {
        return RenderType.outline(resourceLocation, NO_CULL);
    }

    public static RenderType outline(ResourceLocation resourceLocation, RenderStateShard.CullStateShard cullStateShard) {
        return RenderType.create("outline", DefaultVertexFormat.POSITION_COLOR_TEX, 7, 256, CompositeState.builder().setTextureState(new RenderStateShard.TextureStateShard(resourceLocation, false, false)).setCullState(cullStateShard).setDepthTestState(NO_DEPTH_TEST).setAlphaState(DEFAULT_ALPHA).setTexturingState(OUTLINE_TEXTURING).setFogState(NO_FOG).setOutputState(OUTLINE_TARGET).createCompositeState(OutlineProperty.IS_OUTLINE));
    }

    public static RenderType armorGlint() {
        return ARMOR_GLINT;
    }

    public static RenderType armorEntityGlint() {
        return ARMOR_ENTITY_GLINT;
    }

    public static RenderType glintTranslucent() {
        return GLINT_TRANSLUCENT;
    }

    public static RenderType glint() {
        return GLINT;
    }

    public static RenderType glintDirect() {
        return GLINT_DIRECT;
    }

    public static RenderType entityGlint() {
        return ENTITY_GLINT;
    }

    public static RenderType entityGlintDirect() {
        return ENTITY_GLINT_DIRECT;
    }

    public static RenderType crumbling(ResourceLocation resourceLocation) {
        RenderStateShard.TextureStateShard textureStateShard = new RenderStateShard.TextureStateShard(resourceLocation, false, false);
        return RenderType.create("crumbling", DefaultVertexFormat.BLOCK, 7, 256, false, true, CompositeState.builder().setTextureState(textureStateShard).setAlphaState(DEFAULT_ALPHA).setTransparencyState(CRUMBLING_TRANSPARENCY).setWriteMaskState(COLOR_WRITE).setLayeringState(POLYGON_OFFSET_LAYERING).createCompositeState(false));
    }

    public static RenderType text(ResourceLocation resourceLocation) {
        return RenderType.create("text", DefaultVertexFormat.POSITION_COLOR_TEX_LIGHTMAP, 7, 256, false, true, CompositeState.builder().setTextureState(new RenderStateShard.TextureStateShard(resourceLocation, false, false)).setAlphaState(DEFAULT_ALPHA).setTransparencyState(TRANSLUCENT_TRANSPARENCY).setLightmapState(LIGHTMAP).createCompositeState(false));
    }

    public static RenderType textSeeThrough(ResourceLocation resourceLocation) {
        return RenderType.create("text_see_through", DefaultVertexFormat.POSITION_COLOR_TEX_LIGHTMAP, 7, 256, false, true, CompositeState.builder().setTextureState(new RenderStateShard.TextureStateShard(resourceLocation, false, false)).setAlphaState(DEFAULT_ALPHA).setTransparencyState(TRANSLUCENT_TRANSPARENCY).setLightmapState(LIGHTMAP).setDepthTestState(NO_DEPTH_TEST).setWriteMaskState(COLOR_WRITE).createCompositeState(false));
    }

    public static RenderType lightning() {
        return LIGHTNING;
    }

    private static CompositeState tripwireState() {
        return CompositeState.builder().setShadeModelState(SMOOTH_SHADE).setLightmapState(LIGHTMAP).setTextureState(BLOCK_SHEET_MIPPED).setTransparencyState(TRANSLUCENT_TRANSPARENCY).setOutputState(WEATHER_TARGET).createCompositeState(true);
    }

    public static RenderType tripwire() {
        return TRIPWIRE;
    }

    public static RenderType endPortal(int n) {
        RenderStateShard.TransparencyStateShard transparencyStateShard;
        RenderStateShard.TextureStateShard textureStateShard;
        if (n <= 1) {
            transparencyStateShard = TRANSLUCENT_TRANSPARENCY;
            textureStateShard = new RenderStateShard.TextureStateShard(TheEndPortalRenderer.END_SKY_LOCATION, false, false);
        } else {
            transparencyStateShard = ADDITIVE_TRANSPARENCY;
            textureStateShard = new RenderStateShard.TextureStateShard(TheEndPortalRenderer.END_PORTAL_LOCATION, false, false);
        }
        return RenderType.create("end_portal", DefaultVertexFormat.POSITION_COLOR, 7, 256, false, true, CompositeState.builder().setTransparencyState(transparencyStateShard).setTextureState(textureStateShard).setTexturingState(new RenderStateShard.PortalTexturingStateShard(n)).setFogState(BLACK_FOG).createCompositeState(false));
    }

    public static RenderType lines() {
        return LINES;
    }

    public RenderType(String string, VertexFormat vertexFormat, int n, int n2, boolean bl, boolean bl2, Runnable runnable, Runnable runnable2) {
        super(string, runnable, runnable2);
        this.format = vertexFormat;
        this.mode = n;
        this.bufferSize = n2;
        this.affectsCrumbling = bl;
        this.sortOnUpload = bl2;
        this.asOptional = Optional.of(this);
    }

    public static CompositeRenderType create(String string, VertexFormat vertexFormat, int n, int n2, CompositeState compositeState) {
        return RenderType.create(string, vertexFormat, n, n2, false, false, compositeState);
    }

    public static CompositeRenderType create(String string, VertexFormat vertexFormat, int n, int n2, boolean bl, boolean bl2, CompositeState compositeState) {
        return CompositeRenderType.memoize(string, vertexFormat, n, n2, bl, bl2, compositeState);
    }

    public void end(BufferBuilder bufferBuilder, int n, int n2, int n3) {
        if (!bufferBuilder.building()) {
            return;
        }
        if (this.sortOnUpload) {
            bufferBuilder.sortQuads(n, n2, n3);
        }
        bufferBuilder.end();
        this.setupRenderState();
        BufferUploader.end(bufferBuilder);
        this.clearRenderState();
    }

    @Override
    public String toString() {
        return this.name;
    }

    public static List<RenderType> chunkBufferLayers() {
        return ImmutableList.of((Object)RenderType.solid(), (Object)RenderType.cutoutMipped(), (Object)RenderType.cutout(), (Object)RenderType.translucent(), (Object)RenderType.tripwire());
    }

    public int bufferSize() {
        return this.bufferSize;
    }

    public VertexFormat format() {
        return this.format;
    }

    public int mode() {
        return this.mode;
    }

    public Optional<RenderType> outline() {
        return Optional.empty();
    }

    public boolean isOutline() {
        return false;
    }

    public boolean affectsCrumbling() {
        return this.affectsCrumbling;
    }

    public Optional<RenderType> asOptional() {
        return this.asOptional;
    }

    static final class CompositeRenderType
    extends RenderType {
        private static final ObjectOpenCustomHashSet<CompositeRenderType> INSTANCES = new ObjectOpenCustomHashSet((Hash.Strategy)EqualsStrategy.INSTANCE);
        private final CompositeState state;
        private final int hashCode;
        private final Optional<RenderType> outline;
        private final boolean isOutline;

        private CompositeRenderType(String string, VertexFormat vertexFormat, int n, int n2, boolean bl, boolean bl2, CompositeState compositeState) {
            super(string, vertexFormat, n, n2, bl, bl2, () -> compositeState.states.forEach(RenderStateShard::setupRenderState), () -> compositeState.states.forEach(RenderStateShard::clearRenderState));
            this.state = compositeState;
            this.outline = compositeState.outlineProperty == OutlineProperty.AFFECTS_OUTLINE ? compositeState.textureState.texture().map(resourceLocation -> CompositeRenderType.outline(resourceLocation, compositeState.cullState)) : Optional.empty();
            this.isOutline = compositeState.outlineProperty == OutlineProperty.IS_OUTLINE;
            this.hashCode = Objects.hash(super.hashCode(), compositeState);
        }

        private static CompositeRenderType memoize(String string, VertexFormat vertexFormat, int n, int n2, boolean bl, boolean bl2, CompositeState compositeState) {
            return (CompositeRenderType)INSTANCES.addOrGet((Object)new CompositeRenderType(string, vertexFormat, n, n2, bl, bl2, compositeState));
        }

        @Override
        public Optional<RenderType> outline() {
            return this.outline;
        }

        @Override
        public boolean isOutline() {
            return this.isOutline;
        }

        @Override
        public boolean equals(@Nullable Object object) {
            return this == object;
        }

        @Override
        public int hashCode() {
            return this.hashCode;
        }

        @Override
        public String toString() {
            return "RenderType[" + this.state + ']';
        }

        static enum EqualsStrategy implements Hash.Strategy<CompositeRenderType>
        {
            INSTANCE;
            

            public int hashCode(@Nullable CompositeRenderType compositeRenderType) {
                if (compositeRenderType == null) {
                    return 0;
                }
                return compositeRenderType.hashCode;
            }

            public boolean equals(@Nullable CompositeRenderType compositeRenderType, @Nullable CompositeRenderType compositeRenderType2) {
                if (compositeRenderType == compositeRenderType2) {
                    return true;
                }
                if (compositeRenderType == null || compositeRenderType2 == null) {
                    return false;
                }
                return Objects.equals(compositeRenderType.state, compositeRenderType2.state);
            }

            public /* synthetic */ boolean equals(@Nullable Object object, @Nullable Object object2) {
                return this.equals((CompositeRenderType)object, (CompositeRenderType)object2);
            }

            public /* synthetic */ int hashCode(@Nullable Object object) {
                return this.hashCode((CompositeRenderType)object);
            }
        }

    }

    public static final class CompositeState {
        private final RenderStateShard.TextureStateShard textureState;
        private final RenderStateShard.TransparencyStateShard transparencyState;
        private final RenderStateShard.DiffuseLightingStateShard diffuseLightingState;
        private final RenderStateShard.ShadeModelStateShard shadeModelState;
        private final RenderStateShard.AlphaStateShard alphaState;
        private final RenderStateShard.DepthTestStateShard depthTestState;
        private final RenderStateShard.CullStateShard cullState;
        private final RenderStateShard.LightmapStateShard lightmapState;
        private final RenderStateShard.OverlayStateShard overlayState;
        private final RenderStateShard.FogStateShard fogState;
        private final RenderStateShard.LayeringStateShard layeringState;
        private final RenderStateShard.OutputStateShard outputState;
        private final RenderStateShard.TexturingStateShard texturingState;
        private final RenderStateShard.WriteMaskStateShard writeMaskState;
        private final RenderStateShard.LineStateShard lineState;
        private final OutlineProperty outlineProperty;
        private final ImmutableList<RenderStateShard> states;

        private CompositeState(RenderStateShard.TextureStateShard textureStateShard, RenderStateShard.TransparencyStateShard transparencyStateShard, RenderStateShard.DiffuseLightingStateShard diffuseLightingStateShard, RenderStateShard.ShadeModelStateShard shadeModelStateShard, RenderStateShard.AlphaStateShard alphaStateShard, RenderStateShard.DepthTestStateShard depthTestStateShard, RenderStateShard.CullStateShard cullStateShard, RenderStateShard.LightmapStateShard lightmapStateShard, RenderStateShard.OverlayStateShard overlayStateShard, RenderStateShard.FogStateShard fogStateShard, RenderStateShard.LayeringStateShard layeringStateShard, RenderStateShard.OutputStateShard outputStateShard, RenderStateShard.TexturingStateShard texturingStateShard, RenderStateShard.WriteMaskStateShard writeMaskStateShard, RenderStateShard.LineStateShard lineStateShard, OutlineProperty outlineProperty) {
            this.textureState = textureStateShard;
            this.transparencyState = transparencyStateShard;
            this.diffuseLightingState = diffuseLightingStateShard;
            this.shadeModelState = shadeModelStateShard;
            this.alphaState = alphaStateShard;
            this.depthTestState = depthTestStateShard;
            this.cullState = cullStateShard;
            this.lightmapState = lightmapStateShard;
            this.overlayState = overlayStateShard;
            this.fogState = fogStateShard;
            this.layeringState = layeringStateShard;
            this.outputState = outputStateShard;
            this.texturingState = texturingStateShard;
            this.writeMaskState = writeMaskStateShard;
            this.lineState = lineStateShard;
            this.outlineProperty = outlineProperty;
            this.states = ImmutableList.of((Object)this.textureState, (Object)this.transparencyState, (Object)this.diffuseLightingState, (Object)this.shadeModelState, (Object)this.alphaState, (Object)this.depthTestState, (Object)this.cullState, (Object)this.lightmapState, (Object)this.overlayState, (Object)this.fogState, (Object)this.layeringState, (Object)this.outputState, (Object[])new RenderStateShard[]{this.texturingState, this.writeMaskState, this.lineState});
        }

        public boolean equals(Object object) {
            if (this == object) {
                return true;
            }
            if (object == null || this.getClass() != object.getClass()) {
                return false;
            }
            CompositeState compositeState = (CompositeState)object;
            return this.outlineProperty == compositeState.outlineProperty && this.states.equals(compositeState.states);
        }

        public int hashCode() {
            return Objects.hash(new Object[]{this.states, this.outlineProperty});
        }

        public String toString() {
            return "CompositeState[" + this.states + ", outlineProperty=" + (Object)((Object)this.outlineProperty) + ']';
        }

        public static CompositeStateBuilder builder() {
            return new CompositeStateBuilder();
        }

        public static class CompositeStateBuilder {
            private RenderStateShard.TextureStateShard textureState = RenderStateShard.NO_TEXTURE;
            private RenderStateShard.TransparencyStateShard transparencyState = RenderStateShard.NO_TRANSPARENCY;
            private RenderStateShard.DiffuseLightingStateShard diffuseLightingState = RenderStateShard.NO_DIFFUSE_LIGHTING;
            private RenderStateShard.ShadeModelStateShard shadeModelState = RenderStateShard.FLAT_SHADE;
            private RenderStateShard.AlphaStateShard alphaState = RenderStateShard.NO_ALPHA;
            private RenderStateShard.DepthTestStateShard depthTestState = RenderStateShard.LEQUAL_DEPTH_TEST;
            private RenderStateShard.CullStateShard cullState = RenderStateShard.CULL;
            private RenderStateShard.LightmapStateShard lightmapState = RenderStateShard.NO_LIGHTMAP;
            private RenderStateShard.OverlayStateShard overlayState = RenderStateShard.NO_OVERLAY;
            private RenderStateShard.FogStateShard fogState = RenderStateShard.FOG;
            private RenderStateShard.LayeringStateShard layeringState = RenderStateShard.NO_LAYERING;
            private RenderStateShard.OutputStateShard outputState = RenderStateShard.MAIN_TARGET;
            private RenderStateShard.TexturingStateShard texturingState = RenderStateShard.DEFAULT_TEXTURING;
            private RenderStateShard.WriteMaskStateShard writeMaskState = RenderStateShard.COLOR_DEPTH_WRITE;
            private RenderStateShard.LineStateShard lineState = RenderStateShard.DEFAULT_LINE;

            private CompositeStateBuilder() {
            }

            public CompositeStateBuilder setTextureState(RenderStateShard.TextureStateShard textureStateShard) {
                this.textureState = textureStateShard;
                return this;
            }

            public CompositeStateBuilder setTransparencyState(RenderStateShard.TransparencyStateShard transparencyStateShard) {
                this.transparencyState = transparencyStateShard;
                return this;
            }

            public CompositeStateBuilder setDiffuseLightingState(RenderStateShard.DiffuseLightingStateShard diffuseLightingStateShard) {
                this.diffuseLightingState = diffuseLightingStateShard;
                return this;
            }

            public CompositeStateBuilder setShadeModelState(RenderStateShard.ShadeModelStateShard shadeModelStateShard) {
                this.shadeModelState = shadeModelStateShard;
                return this;
            }

            public CompositeStateBuilder setAlphaState(RenderStateShard.AlphaStateShard alphaStateShard) {
                this.alphaState = alphaStateShard;
                return this;
            }

            public CompositeStateBuilder setDepthTestState(RenderStateShard.DepthTestStateShard depthTestStateShard) {
                this.depthTestState = depthTestStateShard;
                return this;
            }

            public CompositeStateBuilder setCullState(RenderStateShard.CullStateShard cullStateShard) {
                this.cullState = cullStateShard;
                return this;
            }

            public CompositeStateBuilder setLightmapState(RenderStateShard.LightmapStateShard lightmapStateShard) {
                this.lightmapState = lightmapStateShard;
                return this;
            }

            public CompositeStateBuilder setOverlayState(RenderStateShard.OverlayStateShard overlayStateShard) {
                this.overlayState = overlayStateShard;
                return this;
            }

            public CompositeStateBuilder setFogState(RenderStateShard.FogStateShard fogStateShard) {
                this.fogState = fogStateShard;
                return this;
            }

            public CompositeStateBuilder setLayeringState(RenderStateShard.LayeringStateShard layeringStateShard) {
                this.layeringState = layeringStateShard;
                return this;
            }

            public CompositeStateBuilder setOutputState(RenderStateShard.OutputStateShard outputStateShard) {
                this.outputState = outputStateShard;
                return this;
            }

            public CompositeStateBuilder setTexturingState(RenderStateShard.TexturingStateShard texturingStateShard) {
                this.texturingState = texturingStateShard;
                return this;
            }

            public CompositeStateBuilder setWriteMaskState(RenderStateShard.WriteMaskStateShard writeMaskStateShard) {
                this.writeMaskState = writeMaskStateShard;
                return this;
            }

            public CompositeStateBuilder setLineState(RenderStateShard.LineStateShard lineStateShard) {
                this.lineState = lineStateShard;
                return this;
            }

            public CompositeState createCompositeState(boolean bl) {
                return this.createCompositeState(bl ? OutlineProperty.AFFECTS_OUTLINE : OutlineProperty.NONE);
            }

            public CompositeState createCompositeState(OutlineProperty outlineProperty) {
                return new CompositeState(this.textureState, this.transparencyState, this.diffuseLightingState, this.shadeModelState, this.alphaState, this.depthTestState, this.cullState, this.lightmapState, this.overlayState, this.fogState, this.layeringState, this.outputState, this.texturingState, this.writeMaskState, this.lineState, outlineProperty);
            }
        }

    }

    static enum OutlineProperty {
        NONE("none"),
        IS_OUTLINE("is_outline"),
        AFFECTS_OUTLINE("affects_outline");
        
        private final String name;

        private OutlineProperty(String string2) {
            this.name = string2;
        }

        public String toString() {
            return this.name;
        }
    }

}

