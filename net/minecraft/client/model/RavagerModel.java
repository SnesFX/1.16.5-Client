/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 */
package net.minecraft.client.model;

import com.google.common.collect.ImmutableList;
import net.minecraft.client.model.ListModel;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.monster.Ravager;

public class RavagerModel
extends ListModel<Ravager> {
    private final ModelPart head;
    private final ModelPart mouth;
    private final ModelPart body;
    private final ModelPart leg0;
    private final ModelPart leg1;
    private final ModelPart leg2;
    private final ModelPart leg3;
    private final ModelPart neck;

    public RavagerModel() {
        this.texWidth = 128;
        this.texHeight = 128;
        int n = 16;
        float f = 0.0f;
        this.neck = new ModelPart(this);
        this.neck.setPos(0.0f, -7.0f, -1.5f);
        this.neck.texOffs(68, 73).addBox(-5.0f, -1.0f, -18.0f, 10.0f, 10.0f, 18.0f, 0.0f);
        this.head = new ModelPart(this);
        this.head.setPos(0.0f, 16.0f, -17.0f);
        this.head.texOffs(0, 0).addBox(-8.0f, -20.0f, -14.0f, 16.0f, 20.0f, 16.0f, 0.0f);
        this.head.texOffs(0, 0).addBox(-2.0f, -6.0f, -18.0f, 4.0f, 8.0f, 4.0f, 0.0f);
        ModelPart modelPart = new ModelPart(this);
        modelPart.setPos(-10.0f, -14.0f, -8.0f);
        modelPart.texOffs(74, 55).addBox(0.0f, -14.0f, -2.0f, 2.0f, 14.0f, 4.0f, 0.0f);
        modelPart.xRot = 1.0995574f;
        this.head.addChild(modelPart);
        ModelPart modelPart2 = new ModelPart(this);
        modelPart2.mirror = true;
        modelPart2.setPos(8.0f, -14.0f, -8.0f);
        modelPart2.texOffs(74, 55).addBox(0.0f, -14.0f, -2.0f, 2.0f, 14.0f, 4.0f, 0.0f);
        modelPart2.xRot = 1.0995574f;
        this.head.addChild(modelPart2);
        this.mouth = new ModelPart(this);
        this.mouth.setPos(0.0f, -2.0f, 2.0f);
        this.mouth.texOffs(0, 36).addBox(-8.0f, 0.0f, -16.0f, 16.0f, 3.0f, 16.0f, 0.0f);
        this.head.addChild(this.mouth);
        this.neck.addChild(this.head);
        this.body = new ModelPart(this);
        this.body.texOffs(0, 55).addBox(-7.0f, -10.0f, -7.0f, 14.0f, 16.0f, 20.0f, 0.0f);
        this.body.texOffs(0, 91).addBox(-6.0f, 6.0f, -7.0f, 12.0f, 13.0f, 18.0f, 0.0f);
        this.body.setPos(0.0f, 1.0f, 2.0f);
        this.leg0 = new ModelPart(this, 96, 0);
        this.leg0.addBox(-4.0f, 0.0f, -4.0f, 8.0f, 37.0f, 8.0f, 0.0f);
        this.leg0.setPos(-8.0f, -13.0f, 18.0f);
        this.leg1 = new ModelPart(this, 96, 0);
        this.leg1.mirror = true;
        this.leg1.addBox(-4.0f, 0.0f, -4.0f, 8.0f, 37.0f, 8.0f, 0.0f);
        this.leg1.setPos(8.0f, -13.0f, 18.0f);
        this.leg2 = new ModelPart(this, 64, 0);
        this.leg2.addBox(-4.0f, 0.0f, -4.0f, 8.0f, 37.0f, 8.0f, 0.0f);
        this.leg2.setPos(-8.0f, -13.0f, -5.0f);
        this.leg3 = new ModelPart(this, 64, 0);
        this.leg3.mirror = true;
        this.leg3.addBox(-4.0f, 0.0f, -4.0f, 8.0f, 37.0f, 8.0f, 0.0f);
        this.leg3.setPos(8.0f, -13.0f, -5.0f);
    }

    @Override
    public Iterable<ModelPart> parts() {
        return ImmutableList.of((Object)this.neck, (Object)this.body, (Object)this.leg0, (Object)this.leg1, (Object)this.leg2, (Object)this.leg3);
    }

    @Override
    public void setupAnim(Ravager ravager, float f, float f2, float f3, float f4, float f5) {
        this.head.xRot = f5 * 0.017453292f;
        this.head.yRot = f4 * 0.017453292f;
        this.body.xRot = 1.5707964f;
        float f6 = 0.4f * f2;
        this.leg0.xRot = Mth.cos(f * 0.6662f) * f6;
        this.leg1.xRot = Mth.cos(f * 0.6662f + 3.1415927f) * f6;
        this.leg2.xRot = Mth.cos(f * 0.6662f + 3.1415927f) * f6;
        this.leg3.xRot = Mth.cos(f * 0.6662f) * f6;
    }

    @Override
    public void prepareMobModel(Ravager ravager, float f, float f2, float f3) {
        super.prepareMobModel(ravager, f, f2, f3);
        int n = ravager.getStunnedTick();
        int n2 = ravager.getRoarTick();
        int n3 = 20;
        int n4 = ravager.getAttackTick();
        int n5 = 10;
        if (n4 > 0) {
            float f4 = Mth.triangleWave((float)n4 - f3, 10.0f);
            float f5 = (1.0f + f4) * 0.5f;
            float f6 = f5 * f5 * f5 * 12.0f;
            float f7 = f6 * Mth.sin(this.neck.xRot);
            this.neck.z = -6.5f + f6;
            this.neck.y = -7.0f - f7;
            float f8 = Mth.sin(((float)n4 - f3) / 10.0f * 3.1415927f * 0.25f);
            this.mouth.xRot = 1.5707964f * f8;
            this.mouth.xRot = n4 > 5 ? Mth.sin(((float)(-4 + n4) - f3) / 4.0f) * 3.1415927f * 0.4f : 0.15707964f * Mth.sin(3.1415927f * ((float)n4 - f3) / 10.0f);
        } else {
            float f9 = -1.0f;
            float f10 = -1.0f * Mth.sin(this.neck.xRot);
            this.neck.x = 0.0f;
            this.neck.y = -7.0f - f10;
            this.neck.z = 5.5f;
            boolean bl = n > 0;
            this.neck.xRot = bl ? 0.21991149f : 0.0f;
            this.mouth.xRot = 3.1415927f * (bl ? 0.05f : 0.01f);
            if (bl) {
                double d = (double)n / 40.0;
                this.neck.x = (float)Math.sin(d * 10.0) * 3.0f;
            } else if (n2 > 0) {
                float f11 = Mth.sin(((float)(20 - n2) - f3) / 20.0f * 3.1415927f * 0.25f);
                this.mouth.xRot = 1.5707964f * f11;
            }
        }
    }
}

