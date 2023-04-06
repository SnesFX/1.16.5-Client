/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 */
package net.minecraft.world.level.material;

import com.google.common.collect.ImmutableList;
import net.minecraft.core.DefaultedRegistry;
import net.minecraft.core.IdMapper;
import net.minecraft.core.Registry;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.material.EmptyFluid;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.LavaFluid;
import net.minecraft.world.level.material.WaterFluid;

public class Fluids {
    public static final Fluid EMPTY = Fluids.register("empty", new EmptyFluid());
    public static final FlowingFluid FLOWING_WATER = Fluids.register("flowing_water", new WaterFluid.Flowing());
    public static final FlowingFluid WATER = Fluids.register("water", new WaterFluid.Source());
    public static final FlowingFluid FLOWING_LAVA = Fluids.register("flowing_lava", new LavaFluid.Flowing());
    public static final FlowingFluid LAVA = Fluids.register("lava", new LavaFluid.Source());

    private static <T extends Fluid> T register(String string, T t) {
        return (T)Registry.register(Registry.FLUID, string, t);
    }

    static {
        for (Fluid fluid : Registry.FLUID) {
            for (FluidState fluidState : fluid.getStateDefinition().getPossibleStates()) {
                Fluid.FLUID_STATE_REGISTRY.add(fluidState);
            }
        }
    }
}

