/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.client.particle;

import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;
import java.util.Random;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

public abstract class SingleQuadParticle
extends Particle {
    protected float quadSize;

    protected SingleQuadParticle(ClientLevel clientLevel, double d, double d2, double d3) {
        super(clientLevel, d, d2, d3);
        this.quadSize = 0.1f * (this.random.nextFloat() * 0.5f + 0.5f) * 2.0f;
    }

    protected SingleQuadParticle(ClientLevel clientLevel, double d, double d2, double d3, double d4, double d5, double d6) {
        super(clientLevel, d, d2, d3, d4, d5, d6);
        this.quadSize = 0.1f * (this.random.nextFloat() * 0.5f + 0.5f) * 2.0f;
    }

    @Override
    public void render(VertexConsumer vertexConsumer, Camera camera, float f) {
        Quaternion quaternion;
        Vec3 vec3 = camera.getPosition();
        float f2 = (float)(Mth.lerp((double)f, this.xo, this.x) - vec3.x());
        float f3 = (float)(Mth.lerp((double)f, this.yo, this.y) - vec3.y());
        float f4 = (float)(Mth.lerp((double)f, this.zo, this.z) - vec3.z());
        if (this.roll == 0.0f) {
            quaternion = camera.rotation();
        } else {
            quaternion = new Quaternion(camera.rotation());
            float f5 = Mth.lerp(f, this.oRoll, this.roll);
            quaternion.mul(Vector3f.ZP.rotation(f5));
        }
        Vector3f vector3f = new Vector3f(-1.0f, -1.0f, 0.0f);
        vector3f.transform(quaternion);
        Vector3f[] arrvector3f = new Vector3f[]{new Vector3f(-1.0f, -1.0f, 0.0f), new Vector3f(-1.0f, 1.0f, 0.0f), new Vector3f(1.0f, 1.0f, 0.0f), new Vector3f(1.0f, -1.0f, 0.0f)};
        float f6 = this.getQuadSize(f);
        for (int i = 0; i < 4; ++i) {
            Vector3f vector3f2 = arrvector3f[i];
            vector3f2.transform(quaternion);
            vector3f2.mul(f6);
            vector3f2.add(f2, f3, f4);
        }
        float f7 = this.getU0();
        float f8 = this.getU1();
        float f9 = this.getV0();
        float f10 = this.getV1();
        int n = this.getLightColor(f);
        vertexConsumer.vertex(arrvector3f[0].x(), arrvector3f[0].y(), arrvector3f[0].z()).uv(f8, f10).color(this.rCol, this.gCol, this.bCol, this.alpha).uv2(n).endVertex();
        vertexConsumer.vertex(arrvector3f[1].x(), arrvector3f[1].y(), arrvector3f[1].z()).uv(f8, f9).color(this.rCol, this.gCol, this.bCol, this.alpha).uv2(n).endVertex();
        vertexConsumer.vertex(arrvector3f[2].x(), arrvector3f[2].y(), arrvector3f[2].z()).uv(f7, f9).color(this.rCol, this.gCol, this.bCol, this.alpha).uv2(n).endVertex();
        vertexConsumer.vertex(arrvector3f[3].x(), arrvector3f[3].y(), arrvector3f[3].z()).uv(f7, f10).color(this.rCol, this.gCol, this.bCol, this.alpha).uv2(n).endVertex();
    }

    public float getQuadSize(float f) {
        return this.quadSize;
    }

    @Override
    public Particle scale(float f) {
        this.quadSize *= f;
        return super.scale(f);
    }

    protected abstract float getU0();

    protected abstract float getU1();

    protected abstract float getV0();

    protected abstract float getV1();
}

