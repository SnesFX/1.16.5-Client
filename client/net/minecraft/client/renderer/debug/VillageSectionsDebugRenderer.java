/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Sets
 */
package net.minecraft.client.renderer.debug;

import com.google.common.collect.Sets;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.Set;
import java.util.function.Consumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.debug.DebugRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.core.Vec3i;

public class VillageSectionsDebugRenderer
implements DebugRenderer.SimpleDebugRenderer {
    private final Set<SectionPos> villageSections = Sets.newHashSet();

    VillageSectionsDebugRenderer() {
    }

    @Override
    public void clear() {
        this.villageSections.clear();
    }

    public void setVillageSection(SectionPos sectionPos) {
        this.villageSections.add(sectionPos);
    }

    public void setNotVillageSection(SectionPos sectionPos) {
        this.villageSections.remove(sectionPos);
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource multiBufferSource, double d, double d2, double d3) {
        RenderSystem.pushMatrix();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableTexture();
        this.doRender(d, d2, d3);
        RenderSystem.enableTexture();
        RenderSystem.disableBlend();
        RenderSystem.popMatrix();
    }

    private void doRender(double d, double d2, double d3) {
        BlockPos blockPos = new BlockPos(d, d2, d3);
        this.villageSections.forEach(sectionPos -> {
            if (blockPos.closerThan(sectionPos.center(), 60.0)) {
                VillageSectionsDebugRenderer.highlightVillageSection(sectionPos);
            }
        });
    }

    private static void highlightVillageSection(SectionPos sectionPos) {
        float f = 1.0f;
        BlockPos blockPos = sectionPos.center();
        BlockPos blockPos2 = blockPos.offset(-1.0, -1.0, -1.0);
        BlockPos blockPos3 = blockPos.offset(1.0, 1.0, 1.0);
        DebugRenderer.renderFilledBox(blockPos2, blockPos3, 0.2f, 1.0f, 0.2f, 0.15f);
    }
}

