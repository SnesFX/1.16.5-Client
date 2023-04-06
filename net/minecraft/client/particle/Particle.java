/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.client.particle;

import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.Random;
import java.util.stream.Stream;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.util.RewindableStream;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public abstract class Particle {
    private static final AABB INITIAL_AABB = new AABB(0.0, 0.0, 0.0, 0.0, 0.0, 0.0);
    protected final ClientLevel level;
    protected double xo;
    protected double yo;
    protected double zo;
    protected double x;
    protected double y;
    protected double z;
    protected double xd;
    protected double yd;
    protected double zd;
    private AABB bb = INITIAL_AABB;
    protected boolean onGround;
    protected boolean hasPhysics = true;
    private boolean stoppedByCollision;
    protected boolean removed;
    protected float bbWidth = 0.6f;
    protected float bbHeight = 1.8f;
    protected final Random random = new Random();
    protected int age;
    protected int lifetime;
    protected float gravity;
    protected float rCol = 1.0f;
    protected float gCol = 1.0f;
    protected float bCol = 1.0f;
    protected float alpha = 1.0f;
    protected float roll;
    protected float oRoll;

    protected Particle(ClientLevel clientLevel, double d, double d2, double d3) {
        this.level = clientLevel;
        this.setSize(0.2f, 0.2f);
        this.setPos(d, d2, d3);
        this.xo = d;
        this.yo = d2;
        this.zo = d3;
        this.lifetime = (int)(4.0f / (this.random.nextFloat() * 0.9f + 0.1f));
    }

    public Particle(ClientLevel clientLevel, double d, double d2, double d3, double d4, double d5, double d6) {
        this(clientLevel, d, d2, d3);
        this.xd = d4 + (Math.random() * 2.0 - 1.0) * 0.4000000059604645;
        this.yd = d5 + (Math.random() * 2.0 - 1.0) * 0.4000000059604645;
        this.zd = d6 + (Math.random() * 2.0 - 1.0) * 0.4000000059604645;
        float f = (float)(Math.random() + Math.random() + 1.0) * 0.15f;
        float f2 = Mth.sqrt(this.xd * this.xd + this.yd * this.yd + this.zd * this.zd);
        this.xd = this.xd / (double)f2 * (double)f * 0.4000000059604645;
        this.yd = this.yd / (double)f2 * (double)f * 0.4000000059604645 + 0.10000000149011612;
        this.zd = this.zd / (double)f2 * (double)f * 0.4000000059604645;
    }

    public Particle setPower(float f) {
        this.xd *= (double)f;
        this.yd = (this.yd - 0.10000000149011612) * (double)f + 0.10000000149011612;
        this.zd *= (double)f;
        return this;
    }

    public Particle scale(float f) {
        this.setSize(0.2f * f, 0.2f * f);
        return this;
    }

    public void setColor(float f, float f2, float f3) {
        this.rCol = f;
        this.gCol = f2;
        this.bCol = f3;
    }

    protected void setAlpha(float f) {
        this.alpha = f;
    }

    public void setLifetime(int n) {
        this.lifetime = n;
    }

    public int getLifetime() {
        return this.lifetime;
    }

    public void tick() {
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;
        if (this.age++ >= this.lifetime) {
            this.remove();
            return;
        }
        this.yd -= 0.04 * (double)this.gravity;
        this.move(this.xd, this.yd, this.zd);
        this.xd *= 0.9800000190734863;
        this.yd *= 0.9800000190734863;
        this.zd *= 0.9800000190734863;
        if (this.onGround) {
            this.xd *= 0.699999988079071;
            this.zd *= 0.699999988079071;
        }
    }

    public abstract void render(VertexConsumer var1, Camera var2, float var3);

    public abstract ParticleRenderType getRenderType();

    public String toString() {
        return this.getClass().getSimpleName() + ", Pos (" + this.x + "," + this.y + "," + this.z + "), RGBA (" + this.rCol + "," + this.gCol + "," + this.bCol + "," + this.alpha + "), Age " + this.age;
    }

    public void remove() {
        this.removed = true;
    }

    protected void setSize(float f, float f2) {
        if (f != this.bbWidth || f2 != this.bbHeight) {
            this.bbWidth = f;
            this.bbHeight = f2;
            AABB aABB = this.getBoundingBox();
            double d = (aABB.minX + aABB.maxX - (double)f) / 2.0;
            double d2 = (aABB.minZ + aABB.maxZ - (double)f) / 2.0;
            this.setBoundingBox(new AABB(d, aABB.minY, d2, d + (double)this.bbWidth, aABB.minY + (double)this.bbHeight, d2 + (double)this.bbWidth));
        }
    }

    public void setPos(double d, double d2, double d3) {
        this.x = d;
        this.y = d2;
        this.z = d3;
        float f = this.bbWidth / 2.0f;
        float f2 = this.bbHeight;
        this.setBoundingBox(new AABB(d - (double)f, d2, d3 - (double)f, d + (double)f, d2 + (double)f2, d3 + (double)f));
    }

    public void move(double d, double d2, double d3) {
        if (this.stoppedByCollision) {
            return;
        }
        double d4 = d;
        double d5 = d2;
        double d6 = d3;
        if (this.hasPhysics && (d != 0.0 || d2 != 0.0 || d3 != 0.0)) {
            Vec3 vec3 = Entity.collideBoundingBoxHeuristically(null, new Vec3(d, d2, d3), this.getBoundingBox(), this.level, CollisionContext.empty(), new RewindableStream<VoxelShape>(Stream.empty()));
            d = vec3.x;
            d2 = vec3.y;
            d3 = vec3.z;
        }
        if (d != 0.0 || d2 != 0.0 || d3 != 0.0) {
            this.setBoundingBox(this.getBoundingBox().move(d, d2, d3));
            this.setLocationFromBoundingbox();
        }
        if (Math.abs(d5) >= 9.999999747378752E-6 && Math.abs(d2) < 9.999999747378752E-6) {
            this.stoppedByCollision = true;
        }
        boolean bl = this.onGround = d5 != d2 && d5 < 0.0;
        if (d4 != d) {
            this.xd = 0.0;
        }
        if (d6 != d3) {
            this.zd = 0.0;
        }
    }

    protected void setLocationFromBoundingbox() {
        AABB aABB = this.getBoundingBox();
        this.x = (aABB.minX + aABB.maxX) / 2.0;
        this.y = aABB.minY;
        this.z = (aABB.minZ + aABB.maxZ) / 2.0;
    }

    protected int getLightColor(float f) {
        BlockPos blockPos = new BlockPos(this.x, this.y, this.z);
        if (this.level.hasChunkAt(blockPos)) {
            return LevelRenderer.getLightColor(this.level, blockPos);
        }
        return 0;
    }

    public boolean isAlive() {
        return !this.removed;
    }

    public AABB getBoundingBox() {
        return this.bb;
    }

    public void setBoundingBox(AABB aABB) {
        this.bb = aABB;
    }
}

