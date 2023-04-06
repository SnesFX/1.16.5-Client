/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.world.level.levelgen.structure;

import net.minecraft.world.level.levelgen.feature.StructureFeature;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.StructureStart;

public abstract class BeardedStructureStart<C extends FeatureConfiguration>
extends StructureStart<C> {
    public BeardedStructureStart(StructureFeature<C> structureFeature, int n, int n2, BoundingBox boundingBox, int n3, long l) {
        super(structureFeature, n, n2, boundingBox, n3, l);
    }

    @Override
    protected void calculateBoundingBox() {
        super.calculateBoundingBox();
        int n = 12;
        this.boundingBox.x0 -= 12;
        this.boundingBox.y0 -= 12;
        this.boundingBox.z0 -= 12;
        this.boundingBox.x1 += 12;
        this.boundingBox.y1 += 12;
        this.boundingBox.z1 += 12;
    }
}

