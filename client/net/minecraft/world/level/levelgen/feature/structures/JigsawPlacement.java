/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Queues
 *  org.apache.commons.lang3.mutable.MutableObject
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.world.level.levelgen.feature.structures;

import com.google.common.collect.Lists;
import com.google.common.collect.Queues;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.Random;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.function.ToIntFunction;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.Vec3i;
import net.minecraft.core.WritableRegistry;
import net.minecraft.data.worldgen.Pools;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.JigsawBlock;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.StructureFeature;
import net.minecraft.world.level.levelgen.feature.configurations.JigsawConfiguration;
import net.minecraft.world.level.levelgen.feature.structures.EmptyPoolElement;
import net.minecraft.world.level.levelgen.feature.structures.JigsawJunction;
import net.minecraft.world.level.levelgen.feature.structures.StructurePoolElement;
import net.minecraft.world.level.levelgen.feature.structures.StructureTemplatePool;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.PoolElementStructurePiece;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureManager;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.apache.commons.lang3.mutable.MutableObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class JigsawPlacement {
    private static final Logger LOGGER = LogManager.getLogger();

    public static void addPieces(RegistryAccess registryAccess, JigsawConfiguration jigsawConfiguration, PieceFactory pieceFactory, ChunkGenerator chunkGenerator, StructureManager structureManager, BlockPos blockPos, List<? super PoolElementStructurePiece> list, Random random, boolean bl, boolean bl2) {
        StructureFeature.bootstrap();
        WritableRegistry<StructureTemplatePool> writableRegistry = registryAccess.registryOrThrow(Registry.TEMPLATE_POOL_REGISTRY);
        Rotation rotation = Rotation.getRandom(random);
        StructureTemplatePool structureTemplatePool = jigsawConfiguration.startPool().get();
        StructurePoolElement structurePoolElement = structureTemplatePool.getRandomTemplate(random);
        PoolElementStructurePiece poolElementStructurePiece = pieceFactory.create(structureManager, structurePoolElement, blockPos, structurePoolElement.getGroundLevelDelta(), rotation, structurePoolElement.getBoundingBox(structureManager, blockPos, rotation));
        BoundingBox boundingBox = poolElementStructurePiece.getBoundingBox();
        int n = (boundingBox.x1 + boundingBox.x0) / 2;
        int n2 = (boundingBox.z1 + boundingBox.z0) / 2;
        int n3 = bl2 ? blockPos.getY() + chunkGenerator.getFirstFreeHeight(n, n2, Heightmap.Types.WORLD_SURFACE_WG) : blockPos.getY();
        int n4 = boundingBox.y0 + poolElementStructurePiece.getGroundLevelDelta();
        poolElementStructurePiece.move(0, n3 - n4, 0);
        list.add(poolElementStructurePiece);
        if (jigsawConfiguration.maxDepth() <= 0) {
            return;
        }
        int n5 = 80;
        AABB aABB = new AABB(n - 80, n3 - 80, n2 - 80, n + 80 + 1, n3 + 80 + 1, n2 + 80 + 1);
        Placer placer = new Placer(writableRegistry, jigsawConfiguration.maxDepth(), pieceFactory, chunkGenerator, structureManager, list, random);
        placer.placing.addLast(new PieceState(poolElementStructurePiece, new MutableObject((Object)Shapes.join(Shapes.create(aABB), Shapes.create(AABB.of(boundingBox)), BooleanOp.ONLY_FIRST)), n3 + 80, 0));
        while (!placer.placing.isEmpty()) {
            PieceState pieceState = (PieceState)placer.placing.removeFirst();
            placer.tryPlacingChildren(pieceState.piece, (MutableObject<VoxelShape>)pieceState.free, pieceState.boundsTop, pieceState.depth, bl);
        }
    }

    public static void addPieces(RegistryAccess registryAccess, PoolElementStructurePiece poolElementStructurePiece, int n, PieceFactory pieceFactory, ChunkGenerator chunkGenerator, StructureManager structureManager, List<? super PoolElementStructurePiece> list, Random random) {
        WritableRegistry<StructureTemplatePool> writableRegistry = registryAccess.registryOrThrow(Registry.TEMPLATE_POOL_REGISTRY);
        Placer placer = new Placer(writableRegistry, n, pieceFactory, chunkGenerator, structureManager, list, random);
        placer.placing.addLast(new PieceState(poolElementStructurePiece, new MutableObject((Object)Shapes.INFINITY), 0, 0));
        while (!placer.placing.isEmpty()) {
            PieceState pieceState = (PieceState)placer.placing.removeFirst();
            placer.tryPlacingChildren(pieceState.piece, (MutableObject<VoxelShape>)pieceState.free, pieceState.boundsTop, pieceState.depth, false);
        }
    }

    public static interface PieceFactory {
        public PoolElementStructurePiece create(StructureManager var1, StructurePoolElement var2, BlockPos var3, int var4, Rotation var5, BoundingBox var6);
    }

    static final class Placer {
        private final Registry<StructureTemplatePool> pools;
        private final int maxDepth;
        private final PieceFactory factory;
        private final ChunkGenerator chunkGenerator;
        private final StructureManager structureManager;
        private final List<? super PoolElementStructurePiece> pieces;
        private final Random random;
        private final Deque<PieceState> placing = Queues.newArrayDeque();

        private Placer(Registry<StructureTemplatePool> registry, int n, PieceFactory pieceFactory, ChunkGenerator chunkGenerator, StructureManager structureManager, List<? super PoolElementStructurePiece> list, Random random) {
            this.pools = registry;
            this.maxDepth = n;
            this.factory = pieceFactory;
            this.chunkGenerator = chunkGenerator;
            this.structureManager = structureManager;
            this.pieces = list;
            this.random = random;
        }

        private void tryPlacingChildren(PoolElementStructurePiece poolElementStructurePiece, MutableObject<VoxelShape> mutableObject, int n, int n2, boolean bl) {
            StructurePoolElement structurePoolElement = poolElementStructurePiece.getElement();
            BlockPos blockPos = poolElementStructurePiece.getPosition();
            Rotation rotation = poolElementStructurePiece.getRotation();
            StructureTemplatePool.Projection projection = structurePoolElement.getProjection();
            boolean bl2 = projection == StructureTemplatePool.Projection.RIGID;
            MutableObject<VoxelShape> mutableObject2 = new MutableObject<VoxelShape>();
            BoundingBox boundingBox = poolElementStructurePiece.getBoundingBox();
            int n3 = boundingBox.y0;
            block0 : for (StructureTemplate.StructureBlockInfo structureBlockInfo2 : structurePoolElement.getShuffledJigsawBlocks(this.structureManager, blockPos, rotation, this.random)) {
                int n4;
                StructurePoolElement structurePoolElement2;
                MutableObject<VoxelShape> mutableObject3;
                Direction direction = JigsawBlock.getFrontFacing(structureBlockInfo2.state);
                BlockPos blockPos2 = structureBlockInfo2.pos;
                BlockPos blockPos3 = blockPos2.relative(direction);
                int n5 = blockPos2.getY() - n3;
                int n6 = -1;
                ResourceLocation resourceLocation = new ResourceLocation(structureBlockInfo2.nbt.getString("pool"));
                Optional<StructureTemplatePool> optional = this.pools.getOptional(resourceLocation);
                if (!optional.isPresent() || optional.get().size() == 0 && !Objects.equals(resourceLocation, Pools.EMPTY.location())) {
                    LOGGER.warn("Empty or none existent pool: {}", (Object)resourceLocation);
                    continue;
                }
                ResourceLocation resourceLocation2 = optional.get().getFallback();
                Optional<StructureTemplatePool> optional2 = this.pools.getOptional(resourceLocation2);
                if (!optional2.isPresent() || optional2.get().size() == 0 && !Objects.equals(resourceLocation2, Pools.EMPTY.location())) {
                    LOGGER.warn("Empty or none existent fallback pool: {}", (Object)resourceLocation2);
                    continue;
                }
                boolean bl3 = boundingBox.isInside(blockPos3);
                if (bl3) {
                    mutableObject3 = mutableObject2;
                    n4 = n3;
                    if (mutableObject2.getValue() == null) {
                        mutableObject2.setValue((Object)Shapes.create(AABB.of(boundingBox)));
                    }
                } else {
                    mutableObject3 = mutableObject;
                    n4 = n;
                }
                ArrayList arrayList = Lists.newArrayList();
                if (n2 != this.maxDepth) {
                    arrayList.addAll(optional.get().getShuffledTemplates(this.random));
                }
                arrayList.addAll(optional2.get().getShuffledTemplates(this.random));
                Iterator iterator = arrayList.iterator();
                while (iterator.hasNext() && (structurePoolElement2 = (StructurePoolElement)iterator.next()) != EmptyPoolElement.INSTANCE) {
                    for (Rotation rotation2 : Rotation.getShuffled(this.random)) {
                        List<StructureTemplate.StructureBlockInfo> list = structurePoolElement2.getShuffledJigsawBlocks(this.structureManager, BlockPos.ZERO, rotation2, this.random);
                        BoundingBox boundingBox2 = structurePoolElement2.getBoundingBox(this.structureManager, BlockPos.ZERO, rotation2);
                        int n7 = !bl || boundingBox2.getYSpan() > 16 ? 0 : list.stream().mapToInt(structureBlockInfo -> {
                            if (!boundingBox2.isInside(structureBlockInfo.pos.relative(JigsawBlock.getFrontFacing(structureBlockInfo.state)))) {
                                return 0;
                            }
                            ResourceLocation resourceLocation = new ResourceLocation(structureBlockInfo.nbt.getString("pool"));
                            Optional<StructureTemplatePool> optional = this.pools.getOptional(resourceLocation);
                            Optional<Integer> optional2 = optional.flatMap(structureTemplatePool -> this.pools.getOptional(structureTemplatePool.getFallback()));
                            int n = optional.map(structureTemplatePool -> structureTemplatePool.getMaxSize(this.structureManager)).orElse(0);
                            int n2 = optional2.map(structureTemplatePool -> structureTemplatePool.getMaxSize(this.structureManager)).orElse(0);
                            return Math.max(n, n2);
                        }).max().orElse(0);
                        for (StructureTemplate.StructureBlockInfo structureBlockInfo3 : list) {
                            int n8;
                            int n9;
                            int n10;
                            if (!JigsawBlock.canAttach(structureBlockInfo2, structureBlockInfo3)) continue;
                            BlockPos blockPos4 = structureBlockInfo3.pos;
                            BlockPos blockPos5 = new BlockPos(blockPos3.getX() - blockPos4.getX(), blockPos3.getY() - blockPos4.getY(), blockPos3.getZ() - blockPos4.getZ());
                            BoundingBox boundingBox3 = structurePoolElement2.getBoundingBox(this.structureManager, blockPos5, rotation2);
                            int n11 = boundingBox3.y0;
                            StructureTemplatePool.Projection projection2 = structurePoolElement2.getProjection();
                            boolean bl4 = projection2 == StructureTemplatePool.Projection.RIGID;
                            int n12 = blockPos4.getY();
                            int n13 = n5 - n12 + JigsawBlock.getFrontFacing(structureBlockInfo2.state).getStepY();
                            if (bl2 && bl4) {
                                n10 = n3 + n13;
                            } else {
                                if (n6 == -1) {
                                    n6 = this.chunkGenerator.getFirstFreeHeight(blockPos2.getX(), blockPos2.getZ(), Heightmap.Types.WORLD_SURFACE_WG);
                                }
                                n10 = n6 - n12;
                            }
                            int n14 = n10 - n11;
                            BoundingBox boundingBox4 = boundingBox3.moved(0, n14, 0);
                            BlockPos blockPos6 = blockPos5.offset(0, n14, 0);
                            if (n7 > 0) {
                                n8 = Math.max(n7 + 1, boundingBox4.y1 - boundingBox4.y0);
                                boundingBox4.y1 = boundingBox4.y0 + n8;
                            }
                            if (Shapes.joinIsNotEmpty((VoxelShape)mutableObject3.getValue(), Shapes.create(AABB.of(boundingBox4).deflate(0.25)), BooleanOp.ONLY_SECOND)) continue;
                            mutableObject3.setValue((Object)Shapes.joinUnoptimized((VoxelShape)mutableObject3.getValue(), Shapes.create(AABB.of(boundingBox4)), BooleanOp.ONLY_FIRST));
                            n8 = poolElementStructurePiece.getGroundLevelDelta();
                            int n15 = bl4 ? n8 - n13 : structurePoolElement2.getGroundLevelDelta();
                            PoolElementStructurePiece poolElementStructurePiece2 = this.factory.create(this.structureManager, structurePoolElement2, blockPos6, n15, rotation2, boundingBox4);
                            if (bl2) {
                                n9 = n3 + n5;
                            } else if (bl4) {
                                n9 = n10 + n12;
                            } else {
                                if (n6 == -1) {
                                    n6 = this.chunkGenerator.getFirstFreeHeight(blockPos2.getX(), blockPos2.getZ(), Heightmap.Types.WORLD_SURFACE_WG);
                                }
                                n9 = n6 + n13 / 2;
                            }
                            poolElementStructurePiece.addJunction(new JigsawJunction(blockPos3.getX(), n9 - n5 + n8, blockPos3.getZ(), n13, projection2));
                            poolElementStructurePiece2.addJunction(new JigsawJunction(blockPos2.getX(), n9 - n12 + n15, blockPos2.getZ(), -n13, projection));
                            this.pieces.add(poolElementStructurePiece2);
                            if (n2 + 1 > this.maxDepth) continue block0;
                            this.placing.addLast(new PieceState(poolElementStructurePiece2, mutableObject3, n4, n2 + 1));
                            continue block0;
                        }
                    }
                }
            }
        }
    }

    static final class PieceState {
        private final PoolElementStructurePiece piece;
        private final MutableObject<VoxelShape> free;
        private final int boundsTop;
        private final int depth;

        private PieceState(PoolElementStructurePiece poolElementStructurePiece, MutableObject<VoxelShape> mutableObject, int n, int n2) {
            this.piece = poolElementStructurePiece;
            this.free = mutableObject;
            this.boundsTop = n;
            this.depth = n2;
        }
    }

}

