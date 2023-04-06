/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.client.particle;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderBuffers;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.phys.Vec3;

public class ItemPickupParticle
extends Particle {
    private final RenderBuffers renderBuffers;
    private final Entity itemEntity;
    private final Entity target;
    private int life;
    private final EntityRenderDispatcher entityRenderDispatcher;

    public ItemPickupParticle(EntityRenderDispatcher entityRenderDispatcher, RenderBuffers renderBuffers, ClientLevel clientLevel, Entity entity, Entity entity2) {
        this(entityRenderDispatcher, renderBuffers, clientLevel, entity, entity2, entity.getDeltaMovement());
    }

    private ItemPickupParticle(EntityRenderDispatcher entityRenderDispatcher, RenderBuffers renderBuffers, ClientLevel clientLevel, Entity entity, Entity entity2, Vec3 vec3) {
        super(clientLevel, entity.getX(), entity.getY(), entity.getZ(), vec3.x, vec3.y, vec3.z);
        this.renderBuffers = renderBuffers;
        this.itemEntity = this.getSafeCopy(entity);
        this.target = entity2;
        this.entityRenderDispatcher = entityRenderDispatcher;
    }

    private Entity getSafeCopy(Entity entity) {
        if (!(entity instanceof ItemEntity)) {
            return entity;
        }
        return ((ItemEntity)entity).copy();
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.CUSTOM;
    }

    @Override
    public void render(VertexConsumer vertexConsumer, Camera camera, float f) {
        float f2 = ((float)this.life + f) / 3.0f;
        f2 *= f2;
        double d = Mth.lerp((double)f, this.target.xOld, this.target.getX());
        double d2 = Mth.lerp((double)f, this.target.yOld, this.target.getY()) + 0.5;
        double d3 = Mth.lerp((double)f, this.target.zOld, this.target.getZ());
        double d4 = Mth.lerp((double)f2, this.itemEntity.getX(), d);
        double d5 = Mth.lerp((double)f2, this.itemEntity.getY(), d2);
        double d6 = Mth.lerp((double)f2, this.itemEntity.getZ(), d3);
        MultiBufferSource.BufferSource bufferSource = this.renderBuffers.bufferSource();
        Vec3 vec3 = camera.getPosition();
        this.entityRenderDispatcher.render(this.itemEntity, d4 - vec3.x(), d5 - vec3.y(), d6 - vec3.z(), this.itemEntity.yRot, f, new PoseStack(), bufferSource, this.entityRenderDispatcher.getPackedLightCoords(this.itemEntity, f));
        bufferSource.endBatch();
    }

    @Override
    public void tick() {
        ++this.life;
        if (this.life == 3) {
            this.remove();
        }
    }
}

