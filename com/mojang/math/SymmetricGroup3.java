/*
 * Decompiled with CFR 0.146.
 */
package com.mojang.math;

import com.mojang.math.Matrix3f;
import java.util.Arrays;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Stream;
import net.minecraft.Util;

public enum SymmetricGroup3 {
    P123(0, 1, 2),
    P213(1, 0, 2),
    P132(0, 2, 1),
    P231(1, 2, 0),
    P312(2, 0, 1),
    P321(2, 1, 0);
    
    private final int[] permutation;
    private final Matrix3f transformation;
    private static final SymmetricGroup3[][] cayleyTable;

    private SymmetricGroup3(int n2, int n3, int n4) {
        this.permutation = new int[]{n2, n3, n4};
        this.transformation = new Matrix3f();
        this.transformation.set(0, this.permutation(0), 1.0f);
        this.transformation.set(1, this.permutation(1), 1.0f);
        this.transformation.set(2, this.permutation(2), 1.0f);
    }

    public SymmetricGroup3 compose(SymmetricGroup3 symmetricGroup3) {
        return cayleyTable[this.ordinal()][symmetricGroup3.ordinal()];
    }

    public int permutation(int n) {
        return this.permutation[n];
    }

    public Matrix3f transformation() {
        return this.transformation;
    }

    static {
        cayleyTable = Util.make(new SymmetricGroup3[SymmetricGroup3.values().length][SymmetricGroup3.values().length], arrsymmetricGroup3 -> {
            for (SymmetricGroup3 symmetricGroup32 : SymmetricGroup3.values()) {
                for (SymmetricGroup3 symmetricGroup33 : SymmetricGroup3.values()) {
                    SymmetricGroup3 symmetricGroup34;
                    int[] arrn = new int[3];
                    for (int i = 0; i < 3; ++i) {
                        arrn[i] = symmetricGroup32.permutation[symmetricGroup33.permutation[i]];
                    }
                    arrsymmetricGroup3[symmetricGroup32.ordinal()][symmetricGroup33.ordinal()] = symmetricGroup34 = Arrays.stream(SymmetricGroup3.values()).filter(symmetricGroup3 -> Arrays.equals(symmetricGroup3.permutation, arrn)).findFirst().get();
                }
            }
        });
    }
}

