/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 */
package net.minecraft.world.level.dimension.end;

import com.google.common.collect.ImmutableList;
import java.util.List;
import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.boss.enderdragon.EndCrystal;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.dimension.end.EndDragonFight;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.SpikeFeature;
import net.minecraft.world.level.levelgen.feature.configurations.SpikeConfiguration;

public enum DragonRespawnAnimation {
    START{

        @Override
        public void tick(ServerLevel serverLevel, EndDragonFight endDragonFight, List<EndCrystal> list, int n, BlockPos blockPos) {
            BlockPos blockPos2 = new BlockPos(0, 128, 0);
            for (EndCrystal endCrystal : list) {
                endCrystal.setBeamTarget(blockPos2);
            }
            endDragonFight.setRespawnStage(PREPARING_TO_SUMMON_PILLARS);
        }
    }
    ,
    PREPARING_TO_SUMMON_PILLARS{

        @Override
        public void tick(ServerLevel serverLevel, EndDragonFight endDragonFight, List<EndCrystal> list, int n, BlockPos blockPos) {
            if (n < 100) {
                if (n == 0 || n == 50 || n == 51 || n == 52 || n >= 95) {
                    serverLevel.levelEvent(3001, new BlockPos(0, 128, 0), 0);
                }
            } else {
                endDragonFight.setRespawnStage(SUMMONING_PILLARS);
            }
        }
    }
    ,
    SUMMONING_PILLARS{

        @Override
        public void tick(ServerLevel serverLevel, EndDragonFight endDragonFight, List<EndCrystal> list, int n, BlockPos blockPos) {
            boolean bl;
            int n2 = 40;
            boolean bl2 = n % 40 == 0;
            boolean bl3 = bl = n % 40 == 39;
            if (bl2 || bl) {
                int n3 = n / 40;
                List<SpikeFeature.EndSpike> list2 = SpikeFeature.getSpikesForLevel(serverLevel);
                if (n3 < list2.size()) {
                    SpikeFeature.EndSpike endSpike = list2.get(n3);
                    if (bl2) {
                        for (EndCrystal endCrystal : list) {
                            endCrystal.setBeamTarget(new BlockPos(endSpike.getCenterX(), endSpike.getHeight() + 1, endSpike.getCenterZ()));
                        }
                    } else {
                        int n4 = 10;
                        for (BlockPos blockPos2 : BlockPos.betweenClosed(new BlockPos(endSpike.getCenterX() - 10, endSpike.getHeight() - 10, endSpike.getCenterZ() - 10), new BlockPos(endSpike.getCenterX() + 10, endSpike.getHeight() + 10, endSpike.getCenterZ() + 10))) {
                            serverLevel.removeBlock(blockPos2, false);
                        }
                        serverLevel.explode(null, (float)endSpike.getCenterX() + 0.5f, endSpike.getHeight(), (float)endSpike.getCenterZ() + 0.5f, 5.0f, Explosion.BlockInteraction.DESTROY);
                        SpikeConfiguration spikeConfiguration = new SpikeConfiguration(true, (List<SpikeFeature.EndSpike>)ImmutableList.of((Object)endSpike), new BlockPos(0, 128, 0));
                        Feature.END_SPIKE.configured(spikeConfiguration).place(serverLevel, serverLevel.getChunkSource().getGenerator(), new Random(), new BlockPos(endSpike.getCenterX(), 45, endSpike.getCenterZ()));
                    }
                } else if (bl2) {
                    endDragonFight.setRespawnStage(SUMMONING_DRAGON);
                }
            }
        }
    }
    ,
    SUMMONING_DRAGON{

        @Override
        public void tick(ServerLevel serverLevel, EndDragonFight endDragonFight, List<EndCrystal> list, int n, BlockPos blockPos) {
            if (n >= 100) {
                endDragonFight.setRespawnStage(END);
                endDragonFight.resetSpikeCrystals();
                for (EndCrystal endCrystal : list) {
                    endCrystal.setBeamTarget(null);
                    serverLevel.explode(endCrystal, endCrystal.getX(), endCrystal.getY(), endCrystal.getZ(), 6.0f, Explosion.BlockInteraction.NONE);
                    endCrystal.remove();
                }
            } else if (n >= 80) {
                serverLevel.levelEvent(3001, new BlockPos(0, 128, 0), 0);
            } else if (n == 0) {
                for (EndCrystal endCrystal : list) {
                    endCrystal.setBeamTarget(new BlockPos(0, 128, 0));
                }
            } else if (n < 5) {
                serverLevel.levelEvent(3001, new BlockPos(0, 128, 0), 0);
            }
        }
    }
    ,
    END{

        @Override
        public void tick(ServerLevel serverLevel, EndDragonFight endDragonFight, List<EndCrystal> list, int n, BlockPos blockPos) {
        }
    };
    

    public abstract void tick(ServerLevel var1, EndDragonFight var2, List<EndCrystal> var3, int var4, BlockPos var5);

}

