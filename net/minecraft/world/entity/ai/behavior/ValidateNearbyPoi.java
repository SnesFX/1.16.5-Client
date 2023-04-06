/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 */
package net.minecraft.world.entity.ai.behavior;

import com.google.common.collect.ImmutableMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.Position;
import net.minecraft.network.protocol.game.DebugPackets;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.Tag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.village.poi.PoiManager;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.Vec3;

public class ValidateNearbyPoi
extends Behavior<LivingEntity> {
    private final MemoryModuleType<GlobalPos> memoryType;
    private final Predicate<PoiType> poiPredicate;

    public ValidateNearbyPoi(PoiType poiType, MemoryModuleType<GlobalPos> memoryModuleType) {
        super((Map<MemoryModuleType<?>, MemoryStatus>)ImmutableMap.of(memoryModuleType, (Object)((Object)MemoryStatus.VALUE_PRESENT)));
        this.poiPredicate = poiType.getPredicate();
        this.memoryType = memoryModuleType;
    }

    @Override
    protected boolean checkExtraStartConditions(ServerLevel serverLevel, LivingEntity livingEntity) {
        GlobalPos globalPos = livingEntity.getBrain().getMemory(this.memoryType).get();
        return serverLevel.dimension() == globalPos.dimension() && globalPos.pos().closerThan(livingEntity.position(), 16.0);
    }

    @Override
    protected void start(ServerLevel serverLevel, LivingEntity livingEntity, long l) {
        Brain<?> brain = livingEntity.getBrain();
        GlobalPos globalPos = brain.getMemory(this.memoryType).get();
        BlockPos blockPos = globalPos.pos();
        ServerLevel serverLevel2 = serverLevel.getServer().getLevel(globalPos.dimension());
        if (serverLevel2 == null || this.poiDoesntExist(serverLevel2, blockPos)) {
            brain.eraseMemory(this.memoryType);
        } else if (this.bedIsOccupied(serverLevel2, blockPos, livingEntity)) {
            brain.eraseMemory(this.memoryType);
            serverLevel.getPoiManager().release(blockPos);
            DebugPackets.sendPoiTicketCountPacket(serverLevel, blockPos);
        }
    }

    private boolean bedIsOccupied(ServerLevel serverLevel, BlockPos blockPos, LivingEntity livingEntity) {
        BlockState blockState = serverLevel.getBlockState(blockPos);
        return blockState.getBlock().is(BlockTags.BEDS) && blockState.getValue(BedBlock.OCCUPIED) != false && !livingEntity.isSleeping();
    }

    private boolean poiDoesntExist(ServerLevel serverLevel, BlockPos blockPos) {
        return !serverLevel.getPoiManager().exists(blockPos, this.poiPredicate);
    }
}

