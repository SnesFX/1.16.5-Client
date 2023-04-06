/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.objects.ObjectArrayList
 *  it.unimi.dsi.fastutil.objects.ObjectList
 */
package net.minecraft.client.model.geom;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Matrix3f;
import com.mojang.math.Matrix4f;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;
import com.mojang.math.Vector4f;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;
import java.util.Random;
import net.minecraft.client.model.Model;
import net.minecraft.core.Direction;

public class ModelPart {
    private float xTexSize = 64.0f;
    private float yTexSize = 32.0f;
    private int xTexOffs;
    private int yTexOffs;
    public float x;
    public float y;
    public float z;
    public float xRot;
    public float yRot;
    public float zRot;
    public boolean mirror;
    public boolean visible = true;
    private final ObjectList<Cube> cubes = new ObjectArrayList();
    private final ObjectList<ModelPart> children = new ObjectArrayList();

    public ModelPart(Model model) {
        model.accept(this);
        this.setTexSize(model.texWidth, model.texHeight);
    }

    public ModelPart(Model model, int n, int n2) {
        this(model.texWidth, model.texHeight, n, n2);
        model.accept(this);
    }

    public ModelPart(int n, int n2, int n3, int n4) {
        this.setTexSize(n, n2);
        this.texOffs(n3, n4);
    }

    private ModelPart() {
    }

    public ModelPart createShallowCopy() {
        ModelPart modelPart = new ModelPart();
        modelPart.copyFrom(this);
        return modelPart;
    }

    public void copyFrom(ModelPart modelPart) {
        this.xRot = modelPart.xRot;
        this.yRot = modelPart.yRot;
        this.zRot = modelPart.zRot;
        this.x = modelPart.x;
        this.y = modelPart.y;
        this.z = modelPart.z;
    }

    public void addChild(ModelPart modelPart) {
        this.children.add((Object)modelPart);
    }

    public ModelPart texOffs(int n, int n2) {
        this.xTexOffs = n;
        this.yTexOffs = n2;
        return this;
    }

    public ModelPart addBox(String string, float f, float f2, float f3, int n, int n2, int n3, float f4, int n4, int n5) {
        this.texOffs(n4, n5);
        this.addBox(this.xTexOffs, this.yTexOffs, f, f2, f3, n, n2, n3, f4, f4, f4, this.mirror, false);
        return this;
    }

    public ModelPart addBox(float f, float f2, float f3, float f4, float f5, float f6) {
        this.addBox(this.xTexOffs, this.yTexOffs, f, f2, f3, f4, f5, f6, 0.0f, 0.0f, 0.0f, this.mirror, false);
        return this;
    }

    public ModelPart addBox(float f, float f2, float f3, float f4, float f5, float f6, boolean bl) {
        this.addBox(this.xTexOffs, this.yTexOffs, f, f2, f3, f4, f5, f6, 0.0f, 0.0f, 0.0f, bl, false);
        return this;
    }

    public void addBox(float f, float f2, float f3, float f4, float f5, float f6, float f7) {
        this.addBox(this.xTexOffs, this.yTexOffs, f, f2, f3, f4, f5, f6, f7, f7, f7, this.mirror, false);
    }

    public void addBox(float f, float f2, float f3, float f4, float f5, float f6, float f7, float f8, float f9) {
        this.addBox(this.xTexOffs, this.yTexOffs, f, f2, f3, f4, f5, f6, f7, f8, f9, this.mirror, false);
    }

    public void addBox(float f, float f2, float f3, float f4, float f5, float f6, float f7, boolean bl) {
        this.addBox(this.xTexOffs, this.yTexOffs, f, f2, f3, f4, f5, f6, f7, f7, f7, bl, false);
    }

    private void addBox(int n, int n2, float f, float f2, float f3, float f4, float f5, float f6, float f7, float f8, float f9, boolean bl, boolean bl2) {
        this.cubes.add((Object)new Cube(n, n2, f, f2, f3, f4, f5, f6, f7, f8, f9, bl, this.xTexSize, this.yTexSize));
    }

    public void setPos(float f, float f2, float f3) {
        this.x = f;
        this.y = f2;
        this.z = f3;
    }

    public void render(PoseStack poseStack, VertexConsumer vertexConsumer, int n, int n2) {
        this.render(poseStack, vertexConsumer, n, n2, 1.0f, 1.0f, 1.0f, 1.0f);
    }

    public void render(PoseStack poseStack, VertexConsumer vertexConsumer, int n, int n2, float f, float f2, float f3, float f4) {
        if (!this.visible) {
            return;
        }
        if (this.cubes.isEmpty() && this.children.isEmpty()) {
            return;
        }
        poseStack.pushPose();
        this.translateAndRotate(poseStack);
        this.compile(poseStack.last(), vertexConsumer, n, n2, f, f2, f3, f4);
        for (ModelPart modelPart : this.children) {
            modelPart.render(poseStack, vertexConsumer, n, n2, f, f2, f3, f4);
        }
        poseStack.popPose();
    }

    public void translateAndRotate(PoseStack poseStack) {
        poseStack.translate(this.x / 16.0f, this.y / 16.0f, this.z / 16.0f);
        if (this.zRot != 0.0f) {
            poseStack.mulPose(Vector3f.ZP.rotation(this.zRot));
        }
        if (this.yRot != 0.0f) {
            poseStack.mulPose(Vector3f.YP.rotation(this.yRot));
        }
        if (this.xRot != 0.0f) {
            poseStack.mulPose(Vector3f.XP.rotation(this.xRot));
        }
    }

    private void compile(PoseStack.Pose pose, VertexConsumer vertexConsumer, int n, int n2, float f, float f2, float f3, float f4) {
        Matrix4f matrix4f = pose.pose();
        Matrix3f matrix3f = pose.normal();
        for (Cube cube : this.cubes) {
            for (Polygon polygon : cube.polygons) {
                Vector3f vector3f = polygon.normal.copy();
                vector3f.transform(matrix3f);
                float f5 = vector3f.x();
                float f6 = vector3f.y();
                float f7 = vector3f.z();
                for (int i = 0; i < 4; ++i) {
                    Vertex vertex = polygon.vertices[i];
                    float f8 = vertex.pos.x() / 16.0f;
                    float f9 = vertex.pos.y() / 16.0f;
                    float f10 = vertex.pos.z() / 16.0f;
                    Vector4f vector4f = new Vector4f(f8, f9, f10, 1.0f);
                    vector4f.transform(matrix4f);
                    vertexConsumer.vertex(vector4f.x(), vector4f.y(), vector4f.z(), f, f2, f3, f4, vertex.u, vertex.v, n2, n, f5, f6, f7);
                }
            }
        }
    }

    public ModelPart setTexSize(int n, int n2) {
        this.xTexSize = n;
        this.yTexSize = n2;
        return this;
    }

    public Cube getRandomCube(Random random) {
        return (Cube)this.cubes.get(random.nextInt(this.cubes.size()));
    }

    static class Vertex {
        public final Vector3f pos;
        public final float u;
        public final float v;

        public Vertex(float f, float f2, float f3, float f4, float f5) {
            this(new Vector3f(f, f2, f3), f4, f5);
        }

        public Vertex remap(float f, float f2) {
            return new Vertex(this.pos, f, f2);
        }

        public Vertex(Vector3f vector3f, float f, float f2) {
            this.pos = vector3f;
            this.u = f;
            this.v = f2;
        }
    }

    static class Polygon {
        public final Vertex[] vertices;
        public final Vector3f normal;

        public Polygon(Vertex[] arrvertex, float f, float f2, float f3, float f4, float f5, float f6, boolean bl, Direction direction) {
            this.vertices = arrvertex;
            float f7 = 0.0f / f5;
            float f8 = 0.0f / f6;
            arrvertex[0] = arrvertex[0].remap(f3 / f5 - f7, f2 / f6 + f8);
            arrvertex[1] = arrvertex[1].remap(f / f5 + f7, f2 / f6 + f8);
            arrvertex[2] = arrvertex[2].remap(f / f5 + f7, f4 / f6 - f8);
            arrvertex[3] = arrvertex[3].remap(f3 / f5 - f7, f4 / f6 - f8);
            if (bl) {
                int n = arrvertex.length;
                for (int i = 0; i < n / 2; ++i) {
                    Vertex vertex = arrvertex[i];
                    arrvertex[i] = arrvertex[n - 1 - i];
                    arrvertex[n - 1 - i] = vertex;
                }
            }
            this.normal = direction.step();
            if (bl) {
                this.normal.mul(-1.0f, 1.0f, 1.0f);
            }
        }
    }

    public static class Cube {
        private final Polygon[] polygons;
        public final float minX;
        public final float minY;
        public final float minZ;
        public final float maxX;
        public final float maxY;
        public final float maxZ;

        public Cube(int n, int n2, float f, float f2, float f3, float f4, float f5, float f6, float f7, float f8, float f9, boolean bl, float f10, float f11) {
            this.minX = f;
            this.minY = f2;
            this.minZ = f3;
            this.maxX = f + f4;
            this.maxY = f2 + f5;
            this.maxZ = f3 + f6;
            this.polygons = new Polygon[6];
            float f12 = f + f4;
            float f13 = f2 + f5;
            float f14 = f3 + f6;
            f -= f7;
            f2 -= f8;
            f3 -= f9;
            f12 += f7;
            f13 += f8;
            f14 += f9;
            if (bl) {
                float f15 = f12;
                f12 = f;
                f = f15;
            }
            Vertex vertex = new Vertex(f, f2, f3, 0.0f, 0.0f);
            Vertex vertex2 = new Vertex(f12, f2, f3, 0.0f, 8.0f);
            Vertex vertex3 = new Vertex(f12, f13, f3, 8.0f, 8.0f);
            Vertex vertex4 = new Vertex(f, f13, f3, 8.0f, 0.0f);
            Vertex vertex5 = new Vertex(f, f2, f14, 0.0f, 0.0f);
            Vertex vertex6 = new Vertex(f12, f2, f14, 0.0f, 8.0f);
            Vertex vertex7 = new Vertex(f12, f13, f14, 8.0f, 8.0f);
            Vertex vertex8 = new Vertex(f, f13, f14, 8.0f, 0.0f);
            float f16 = n;
            float f17 = (float)n + f6;
            float f18 = (float)n + f6 + f4;
            float f19 = (float)n + f6 + f4 + f4;
            float f20 = (float)n + f6 + f4 + f6;
            float f21 = (float)n + f6 + f4 + f6 + f4;
            float f22 = n2;
            float f23 = (float)n2 + f6;
            float f24 = (float)n2 + f6 + f5;
            this.polygons[2] = new Polygon(new Vertex[]{vertex6, vertex5, vertex, vertex2}, f17, f22, f18, f23, f10, f11, bl, Direction.DOWN);
            this.polygons[3] = new Polygon(new Vertex[]{vertex3, vertex4, vertex8, vertex7}, f18, f23, f19, f22, f10, f11, bl, Direction.UP);
            this.polygons[1] = new Polygon(new Vertex[]{vertex, vertex5, vertex8, vertex4}, f16, f23, f17, f24, f10, f11, bl, Direction.WEST);
            this.polygons[4] = new Polygon(new Vertex[]{vertex2, vertex, vertex4, vertex3}, f17, f23, f18, f24, f10, f11, bl, Direction.NORTH);
            this.polygons[0] = new Polygon(new Vertex[]{vertex6, vertex2, vertex3, vertex7}, f18, f23, f20, f24, f10, f11, bl, Direction.EAST);
            this.polygons[5] = new Polygon(new Vertex[]{vertex5, vertex6, vertex7, vertex8}, f20, f23, f21, f24, f10, f11, bl, Direction.SOUTH);
        }
    }

}

