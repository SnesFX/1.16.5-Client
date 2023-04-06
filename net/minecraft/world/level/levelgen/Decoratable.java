/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.world.level.levelgen;

import net.minecraft.util.UniformInt;
import net.minecraft.world.level.levelgen.feature.configurations.CountConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.NoneDecoratorConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.RangeDecoratorConfiguration;
import net.minecraft.world.level.levelgen.placement.ChanceDecoratorConfiguration;
import net.minecraft.world.level.levelgen.placement.ConfiguredDecorator;
import net.minecraft.world.level.levelgen.placement.FeatureDecorator;

public interface Decoratable<R> {
    public R decorated(ConfiguredDecorator<?> var1);

    default public R chance(int n) {
        return this.decorated(FeatureDecorator.CHANCE.configured(new ChanceDecoratorConfiguration(n)));
    }

    default public R count(UniformInt uniformInt) {
        return this.decorated(FeatureDecorator.COUNT.configured(new CountConfiguration(uniformInt)));
    }

    default public R count(int n) {
        return this.count(UniformInt.fixed(n));
    }

    default public R countRandom(int n) {
        return this.count(UniformInt.of(0, n));
    }

    default public R range(int n) {
        return this.decorated(FeatureDecorator.RANGE.configured(new RangeDecoratorConfiguration(0, 0, n)));
    }

    default public R squared() {
        return this.decorated(FeatureDecorator.SQUARE.configured(NoneDecoratorConfiguration.INSTANCE));
    }
}

