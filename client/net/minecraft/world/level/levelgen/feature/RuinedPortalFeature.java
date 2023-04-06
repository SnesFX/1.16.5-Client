/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.mojang.serialization.Codec
 */
package net.minecraft.world.level.levelgen.feature;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.Vec3i;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.WorldgenRandom;
import net.minecraft.world.level.levelgen.feature.StructureFeature;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.RuinedPortalConfiguration;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.RuinedPortalPiece;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureManager;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;

public class RuinedPortalFeature
extends StructureFeature<RuinedPortalConfiguration> {
    private static final String[] STRUCTURE_LOCATION_PORTALS = new String[]{"ruined_portal/portal_1", "ruined_portal/portal_2", "ruined_portal/portal_3", "ruined_portal/portal_4", "ruined_portal/portal_5", "ruined_portal/portal_6", "ruined_portal/portal_7", "ruined_portal/portal_8", "ruined_portal/portal_9", "ruined_portal/portal_10"};
    private static final String[] STRUCTURE_LOCATION_GIANT_PORTALS = new String[]{"ruined_portal/giant_portal_1", "ruined_portal/giant_portal_2", "ruined_portal/giant_portal_3"};

    public RuinedPortalFeature(Codec<RuinedPortalConfiguration> codec) {
        super(codec);
    }

    @Override
    public StructureFeature.StructureStartFactory<RuinedPortalConfiguration> getStartFactory() {
        return (arg_0, arg_1, arg_2, arg_3, arg_4, arg_5) -> FeatureStart.new(arg_0, arg_1, arg_2, arg_3, arg_4, arg_5);
    }

    private static boolean isCold(BlockPos blockPos, Biome biome) {
        return biome.getTemperature(blockPos) < 0.15f;
    }

    private static int findSuitableY(Random random, ChunkGenerator chunkGenerator, RuinedPortalPiece.VerticalPlacement verticalPlacement, boolean bl, int n, int n2, BoundingBox boundingBox) {
        int n3;
        int n4;
        int n5;
        if (verticalPlacement == RuinedPortalPiece.VerticalPlacement.IN_NETHER) {
            n5 = bl ? RuinedPortalFeature.randomIntInclusive(random, 32, 100) : (random.nextFloat() < 0.5f ? RuinedPortalFeature.randomIntInclusive(random, 27, 29) : RuinedPortalFeature.randomIntInclusive(random, 29, 100));
        } else if (verticalPlacement == RuinedPortalPiece.VerticalPlacement.IN_MOUNTAIN) {
            n3 = n - n2;
            n5 = RuinedPortalFeature.getRandomWithinInterval(random, 70, n3);
        } else if (verticalPlacement == RuinedPortalPiece.VerticalPlacement.UNDERGROUND) {
            n3 = n - n2;
            n5 = RuinedPortalFeature.getRandomWithinInterval(random, 15, n3);
        } else {
            n5 = verticalPlacement == RuinedPortalPiece.VerticalPlacement.PARTLY_BURIED ? n - n2 + RuinedPortalFeature.randomIntInclusive(random, 2, 8) : n;
        }
        ImmutableList immutableList = ImmutableList.of((Object)new BlockPos(boundingBox.x0, 0, boundingBox.z0), (Object)new BlockPos(boundingBox.x1, 0, boundingBox.z0), (Object)new BlockPos(boundingBox.x0, 0, boundingBox.z1), (Object)new BlockPos(boundingBox.x1, 0, boundingBox.z1));
        List list = immutableList.stream().map(blockPos -> chunkGenerator.getBaseColumn(blockPos.getX(), blockPos.getZ())).collect(Collectors.toList());
        Heightmap.Types types = verticalPlacement == RuinedPortalPiece.VerticalPlacement.ON_OCEAN_FLOOR ? Heightmap.Types.OCEAN_FLOOR_WG : Heightmap.Types.WORLD_SURFACE_WG;
        BlockPos.MutableBlockPos mutableBlockPos = new BlockPos.MutableBlockPos();
        block0 : for (n4 = n5; n4 > 15; --n4) {
            int n6 = 0;
            mutableBlockPos.set(0, n4, 0);
            for (BlockGetter blockGetter : list) {
                BlockState blockState = blockGetter.getBlockState(mutableBlockPos);
                if (blockState == null || !types.isOpaque().test(blockState) || ++n6 != 3) continue;
                break block0;
            }
        }
        return n4;
    }

    private static int randomIntInclusive(Random random, int n, int n2) {
        return random.nextInt(n2 - n + 1) + n;
    }

    private static int getRandomWithinInterval(Random random, int n, int n2) {
        if (n < n2) {
            return RuinedPortalFeature.randomIntInclusive(random, n, n2);
        }
        return n2;
    }

    public static enum Type implements StringRepresentable
    {
        STANDARD("standard"),
        DESERT("desert"),
        JUNGLE("jungle"),
        SWAMP("swamp"),
        MOUNTAIN("mountain"),
        OCEAN("ocean"),
        NETHER("nether");
        
        public static final Codec<Type> CODEC;
        private static final Map<String, Type> BY_NAME;
        private final String name;

        private Type(String string2) {
            this.name = string2;
        }

        public String getName() {
            return this.name;
        }

        public static Type byName(String string) {
            return BY_NAME.get(string);
        }

        @Override
        public String getSerializedName() {
            return this.name;
        }

        static {
            CODEC = StringRepresentable.fromEnum(Type::values, Type::byName);
            BY_NAME = Arrays.stream(Type.values()).collect(Collectors.toMap(Type::getName, type -> type));
        }
    }

    public static class FeatureStart
    extends StructureStart<RuinedPortalConfiguration> {
        protected FeatureStart(StructureFeature<RuinedPortalConfiguration> structureFeature, int n, int n2, BoundingBox boundingBox, int n3, long l) {
            super(structureFeature, n, n2, boundingBox, n3, l);
        }

        @Override
        public void generatePieces(RegistryAccess registryAccess, ChunkGenerator chunkGenerator, StructureManager structureManager, int n, int n2, Biome biome, RuinedPortalConfiguration ruinedPortalConfiguration) {
            boolean bl;
            RuinedPortalPiece.VerticalPlacement verticalPlacement;
            RuinedPortalPiece.Properties properties = new RuinedPortalPiece.Properties();
            if (ruinedPortalConfiguration.portalType == Type.DESERT) {
                verticalPlacement = RuinedPortalPiece.VerticalPlacement.PARTLY_BURIED;
                properties.airPocket = false;
                properties.mossiness = 0.0f;
            } else if (ruinedPortalConfiguration.portalType == Type.JUNGLE) {
                verticalPlacement = RuinedPortalPiece.VerticalPlacement.ON_LAND_SURFACE;
                properties.airPocket = this.random.nextFloat() < 0.5f;
                properties.mossiness = 0.8f;
                properties.overgrown = true;
                properties.vines = true;
            } else if (ruinedPortalConfiguration.portalType == Type.SWAMP) {
                verticalPlacement = RuinedPortalPiece.VerticalPlacement.ON_OCEAN_FLOOR;
                properties.airPocket = false;
                properties.mossiness = 0.5f;
                properties.vines = true;
            } else if (ruinedPortalConfiguration.portalType == Type.MOUNTAIN) {
                bl = this.random.nextFloat() < 0.5f;
                verticalPlacement = bl ? RuinedPortalPiece.VerticalPlacement.IN_MOUNTAIN : RuinedPortalPiece.VerticalPlacement.ON_LAND_SURFACE;
                properties.airPocket = bl || this.random.nextFloat() < 0.5f;
            } else if (ruinedPortalConfiguration.portalType == Type.OCEAN) {
                verticalPlacement = RuinedPortalPiece.VerticalPlacement.ON_OCEAN_FLOOR;
                properties.airPocket = false;
                properties.mossiness = 0.8f;
            } else if (ruinedPortalConfiguration.portalType == Type.NETHER) {
                verticalPlacement = RuinedPortalPiece.VerticalPlacement.IN_NETHER;
                properties.airPocket = this.random.nextFloat() < 0.5f;
                properties.mossiness = 0.0f;
                properties.replaceWithBlackstone = true;
            } else {
                bl = this.random.nextFloat() < 0.5f;
                verticalPlacement = bl ? RuinedPortalPiece.VerticalPlacement.UNDERGROUND : RuinedPortalPiece.VerticalPlacement.ON_LAND_SURFACE;
                properties.airPocket = bl || this.random.nextFloat() < 0.5f;
            }
            ResourceLocation resourceLocation = this.random.nextFloat() < 0.05f ? new ResourceLocation(STRUCTURE_LOCATION_GIANT_PORTALS[this.random.nextInt(STRUCTURE_LOCATION_GIANT_PORTALS.length)]) : new ResourceLocation(STRUCTURE_LOCATION_PORTALS[this.random.nextInt(STRUCTURE_LOCATION_PORTALS.length)]);
            StructureTemplate structureTemplate = structureManager.getOrCreate(resourceLocation);
            Rotation rotation = Util.getRandom(Rotation.values(), (Random)this.random);
            Mirror mirror = this.random.nextFloat() < 0.5f ? Mirror.NONE : Mirror.FRONT_BACK;
            BlockPos blockPos = new BlockPos(structureTemplate.getSize().getX() / 2, 0, structureTemplate.getSize().getZ() / 2);
            BlockPos blockPos2 = new ChunkPos(n, n2).getWorldPosition();
            BoundingBox boundingBox = structureTemplate.getBoundingBox(blockPos2, rotation, blockPos, mirror);
            Vec3i vec3i = boundingBox.getCenter();
            int n3 = vec3i.getX();
            int n4 = vec3i.getZ();
            int n5 = chunkGenerator.getBaseHeight(n3, n4, RuinedPortalPiece.getHeightMapType(verticalPlacement)) - 1;
            int n6 = RuinedPortalFeature.findSuitableY(this.random, chunkGenerator, verticalPlacement, properties.airPocket, n5, boundingBox.getYSpan(), boundingBox);
            BlockPos blockPos3 = new BlockPos(blockPos2.getX(), n6, blockPos2.getZ());
            if (ruinedPortalConfiguration.portalType == Type.MOUNTAIN || ruinedPortalConfiguration.portalType == Type.OCEAN || ruinedPortalConfiguration.portalType == Type.STANDARD) {
                properties.cold = RuinedPortalFeature.isCold(blockPos3, biome);
            }
            this.pieces.add(new RuinedPortalPiece(blockPos3, verticalPlacement, properties, resourceLocation, structureTemplate, rotation, mirror, blockPos));
            this.calculateBoundingBox();
        }
    }

}

