/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.ImmutableSet
 *  com.mojang.datafixers.util.Pair
 */
package net.minecraft.world.entity.ai.behavior;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.util.Pair;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import net.minecraft.core.GlobalPos;
import net.minecraft.world.entity.AgableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.ai.behavior.AcquirePoi;
import net.minecraft.world.entity.ai.behavior.AssignProfessionFromJobSite;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.behavior.CelebrateVillagersSurvivedRaid;
import net.minecraft.world.entity.ai.behavior.DoNothing;
import net.minecraft.world.entity.ai.behavior.GateBehavior;
import net.minecraft.world.entity.ai.behavior.GiveGiftToHero;
import net.minecraft.world.entity.ai.behavior.GoOutsideToCelebrate;
import net.minecraft.world.entity.ai.behavior.GoToClosestVillage;
import net.minecraft.world.entity.ai.behavior.GoToPotentialJobSite;
import net.minecraft.world.entity.ai.behavior.GoToWantedItem;
import net.minecraft.world.entity.ai.behavior.HarvestFarmland;
import net.minecraft.world.entity.ai.behavior.InsideBrownianWalk;
import net.minecraft.world.entity.ai.behavior.InteractWith;
import net.minecraft.world.entity.ai.behavior.InteractWithDoor;
import net.minecraft.world.entity.ai.behavior.JumpOnBed;
import net.minecraft.world.entity.ai.behavior.LocateHidingPlace;
import net.minecraft.world.entity.ai.behavior.LocateHidingPlaceDuringRaid;
import net.minecraft.world.entity.ai.behavior.LookAndFollowTradingPlayerSink;
import net.minecraft.world.entity.ai.behavior.LookAtTargetSink;
import net.minecraft.world.entity.ai.behavior.MoveToTargetSink;
import net.minecraft.world.entity.ai.behavior.PlayTagWithOtherKids;
import net.minecraft.world.entity.ai.behavior.PoiCompetitorScan;
import net.minecraft.world.entity.ai.behavior.ReactToBell;
import net.minecraft.world.entity.ai.behavior.ResetProfession;
import net.minecraft.world.entity.ai.behavior.ResetRaidStatus;
import net.minecraft.world.entity.ai.behavior.RingBell;
import net.minecraft.world.entity.ai.behavior.RunOne;
import net.minecraft.world.entity.ai.behavior.SetClosestHomeAsWalkTarget;
import net.minecraft.world.entity.ai.behavior.SetEntityLookTarget;
import net.minecraft.world.entity.ai.behavior.SetHiddenState;
import net.minecraft.world.entity.ai.behavior.SetLookAndInteract;
import net.minecraft.world.entity.ai.behavior.SetRaidStatus;
import net.minecraft.world.entity.ai.behavior.SetWalkTargetAwayFrom;
import net.minecraft.world.entity.ai.behavior.SetWalkTargetFromBlockMemory;
import net.minecraft.world.entity.ai.behavior.SetWalkTargetFromLookTarget;
import net.minecraft.world.entity.ai.behavior.ShowTradesToPlayer;
import net.minecraft.world.entity.ai.behavior.SleepInBed;
import net.minecraft.world.entity.ai.behavior.SocializeAtBell;
import net.minecraft.world.entity.ai.behavior.StrollAroundPoi;
import net.minecraft.world.entity.ai.behavior.StrollToPoi;
import net.minecraft.world.entity.ai.behavior.StrollToPoiList;
import net.minecraft.world.entity.ai.behavior.Swim;
import net.minecraft.world.entity.ai.behavior.TradeWithVillager;
import net.minecraft.world.entity.ai.behavior.UpdateActivityFromSchedule;
import net.minecraft.world.entity.ai.behavior.UseBonemeal;
import net.minecraft.world.entity.ai.behavior.ValidateNearbyPoi;
import net.minecraft.world.entity.ai.behavior.VictoryStroll;
import net.minecraft.world.entity.ai.behavior.VillageBoundRandomStroll;
import net.minecraft.world.entity.ai.behavior.VillagerCalmDown;
import net.minecraft.world.entity.ai.behavior.VillagerMakeLove;
import net.minecraft.world.entity.ai.behavior.VillagerPanicTrigger;
import net.minecraft.world.entity.ai.behavior.WakeUp;
import net.minecraft.world.entity.ai.behavior.WorkAtComposter;
import net.minecraft.world.entity.ai.behavior.WorkAtPoi;
import net.minecraft.world.entity.ai.behavior.YieldJobSite;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.entity.animal.Cat;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.player.Player;

public class VillagerGoalPackages {
    public static ImmutableList<Pair<Integer, ? extends Behavior<? super Villager>>> getCorePackage(VillagerProfession villagerProfession, float f) {
        return ImmutableList.of((Object)Pair.of((Object)0, (Object)new Swim(0.8f)), (Object)Pair.of((Object)0, (Object)new InteractWithDoor()), (Object)Pair.of((Object)0, (Object)new LookAtTargetSink(45, 90)), (Object)Pair.of((Object)0, (Object)new VillagerPanicTrigger()), (Object)Pair.of((Object)0, (Object)new WakeUp()), (Object)Pair.of((Object)0, (Object)new ReactToBell()), (Object)Pair.of((Object)0, (Object)new SetRaidStatus()), (Object)Pair.of((Object)0, (Object)new ValidateNearbyPoi(villagerProfession.getJobPoiType(), MemoryModuleType.JOB_SITE)), (Object)Pair.of((Object)0, (Object)new ValidateNearbyPoi(villagerProfession.getJobPoiType(), MemoryModuleType.POTENTIAL_JOB_SITE)), (Object)Pair.of((Object)1, (Object)new MoveToTargetSink()), (Object)Pair.of((Object)2, (Object)new PoiCompetitorScan(villagerProfession)), (Object)Pair.of((Object)3, (Object)new LookAndFollowTradingPlayerSink(f)), (Object[])new Pair[]{Pair.of((Object)5, new GoToWantedItem(f, false, 4)), Pair.of((Object)6, (Object)new AcquirePoi(villagerProfession.getJobPoiType(), MemoryModuleType.JOB_SITE, MemoryModuleType.POTENTIAL_JOB_SITE, true, Optional.empty())), Pair.of((Object)7, (Object)new GoToPotentialJobSite(f)), Pair.of((Object)8, (Object)new YieldJobSite(f)), Pair.of((Object)10, (Object)new AcquirePoi(PoiType.HOME, MemoryModuleType.HOME, false, Optional.of((byte)14))), Pair.of((Object)10, (Object)new AcquirePoi(PoiType.MEETING, MemoryModuleType.MEETING_POINT, true, Optional.of((byte)14))), Pair.of((Object)10, (Object)new AssignProfessionFromJobSite()), Pair.of((Object)10, (Object)new ResetProfession())});
    }

    public static ImmutableList<Pair<Integer, ? extends Behavior<? super Villager>>> getWorkPackage(VillagerProfession villagerProfession, float f) {
        WorkAtPoi workAtPoi = villagerProfession == VillagerProfession.FARMER ? new WorkAtComposter() : new WorkAtPoi();
        return ImmutableList.of(VillagerGoalPackages.getMinimalLookBehavior(), (Object)Pair.of((Object)5, new RunOne(ImmutableList.of((Object)Pair.of((Object)workAtPoi, (Object)7), (Object)Pair.of((Object)new StrollAroundPoi(MemoryModuleType.JOB_SITE, 0.4f, 4), (Object)2), (Object)Pair.of((Object)new StrollToPoi(MemoryModuleType.JOB_SITE, 0.4f, 1, 10), (Object)5), (Object)Pair.of((Object)new StrollToPoiList(MemoryModuleType.SECONDARY_JOB_SITE, f, 1, 6, MemoryModuleType.JOB_SITE), (Object)5), (Object)Pair.of((Object)new HarvestFarmland(), (Object)(villagerProfession == VillagerProfession.FARMER ? 2 : 5)), (Object)Pair.of((Object)new UseBonemeal(), (Object)(villagerProfession == VillagerProfession.FARMER ? 4 : 7))))), (Object)Pair.of((Object)10, (Object)new ShowTradesToPlayer(400, 1600)), (Object)Pair.of((Object)10, (Object)new SetLookAndInteract(EntityType.PLAYER, 4)), (Object)Pair.of((Object)2, (Object)new SetWalkTargetFromBlockMemory(MemoryModuleType.JOB_SITE, f, 9, 100, 1200)), (Object)Pair.of((Object)3, (Object)new GiveGiftToHero(100)), (Object)Pair.of((Object)99, (Object)new UpdateActivityFromSchedule()));
    }

    public static ImmutableList<Pair<Integer, ? extends Behavior<? super Villager>>> getPlayPackage(float f) {
        return ImmutableList.of((Object)Pair.of((Object)0, (Object)new MoveToTargetSink(80, 120)), VillagerGoalPackages.getFullLookBehavior(), (Object)Pair.of((Object)5, (Object)new PlayTagWithOtherKids()), (Object)Pair.of((Object)5, new RunOne((Map<MemoryModuleType<?>, MemoryStatus>)ImmutableMap.of(MemoryModuleType.VISIBLE_VILLAGER_BABIES, (Object)((Object)MemoryStatus.VALUE_ABSENT)), ImmutableList.of((Object)Pair.of(InteractWith.of(EntityType.VILLAGER, 8, MemoryModuleType.INTERACTION_TARGET, f, 2), (Object)2), (Object)Pair.of(InteractWith.of(EntityType.CAT, 8, MemoryModuleType.INTERACTION_TARGET, f, 2), (Object)1), (Object)Pair.of((Object)new VillageBoundRandomStroll(f), (Object)1), (Object)Pair.of((Object)new SetWalkTargetFromLookTarget(f, 2), (Object)1), (Object)Pair.of((Object)new JumpOnBed(f), (Object)2), (Object)Pair.of((Object)new DoNothing(20, 40), (Object)2)))), (Object)Pair.of((Object)99, (Object)new UpdateActivityFromSchedule()));
    }

    public static ImmutableList<Pair<Integer, ? extends Behavior<? super Villager>>> getRestPackage(VillagerProfession villagerProfession, float f) {
        return ImmutableList.of((Object)Pair.of((Object)2, (Object)new SetWalkTargetFromBlockMemory(MemoryModuleType.HOME, f, 1, 150, 1200)), (Object)Pair.of((Object)3, (Object)new ValidateNearbyPoi(PoiType.HOME, MemoryModuleType.HOME)), (Object)Pair.of((Object)3, (Object)new SleepInBed()), (Object)Pair.of((Object)5, new RunOne((Map<MemoryModuleType<?>, MemoryStatus>)ImmutableMap.of(MemoryModuleType.HOME, (Object)((Object)MemoryStatus.VALUE_ABSENT)), ImmutableList.of((Object)Pair.of((Object)new SetClosestHomeAsWalkTarget(f), (Object)1), (Object)Pair.of((Object)new InsideBrownianWalk(f), (Object)4), (Object)Pair.of((Object)new GoToClosestVillage(f, 4), (Object)2), (Object)Pair.of((Object)new DoNothing(20, 40), (Object)2)))), VillagerGoalPackages.getMinimalLookBehavior(), (Object)Pair.of((Object)99, (Object)new UpdateActivityFromSchedule()));
    }

    public static ImmutableList<Pair<Integer, ? extends Behavior<? super Villager>>> getMeetPackage(VillagerProfession villagerProfession, float f) {
        return ImmutableList.of((Object)Pair.of((Object)2, new RunOne(ImmutableList.of((Object)Pair.of((Object)new StrollAroundPoi(MemoryModuleType.MEETING_POINT, 0.4f, 40), (Object)2), (Object)Pair.of((Object)new SocializeAtBell(), (Object)2)))), (Object)Pair.of((Object)10, (Object)new ShowTradesToPlayer(400, 1600)), (Object)Pair.of((Object)10, (Object)new SetLookAndInteract(EntityType.PLAYER, 4)), (Object)Pair.of((Object)2, (Object)new SetWalkTargetFromBlockMemory(MemoryModuleType.MEETING_POINT, f, 6, 100, 200)), (Object)Pair.of((Object)3, (Object)new GiveGiftToHero(100)), (Object)Pair.of((Object)3, (Object)new ValidateNearbyPoi(PoiType.MEETING, MemoryModuleType.MEETING_POINT)), (Object)Pair.of((Object)3, new GateBehavior((Map<MemoryModuleType<?>, MemoryStatus>)ImmutableMap.of(), (Set<MemoryModuleType<?>>)ImmutableSet.of(MemoryModuleType.INTERACTION_TARGET), GateBehavior.OrderPolicy.ORDERED, GateBehavior.RunningPolicy.RUN_ONE, ImmutableList.of((Object)Pair.of((Object)new TradeWithVillager(), (Object)1)))), VillagerGoalPackages.getFullLookBehavior(), (Object)Pair.of((Object)99, (Object)new UpdateActivityFromSchedule()));
    }

    public static ImmutableList<Pair<Integer, ? extends Behavior<? super Villager>>> getIdlePackage(VillagerProfession villagerProfession, float f) {
        return ImmutableList.of((Object)Pair.of((Object)2, new RunOne(ImmutableList.of((Object)Pair.of(InteractWith.of(EntityType.VILLAGER, 8, MemoryModuleType.INTERACTION_TARGET, f, 2), (Object)2), (Object)Pair.of(new InteractWith<Villager, AgableMob>(EntityType.VILLAGER, 8, AgableMob::canBreed, AgableMob::canBreed, MemoryModuleType.BREED_TARGET, f, 2), (Object)1), (Object)Pair.of(InteractWith.of(EntityType.CAT, 8, MemoryModuleType.INTERACTION_TARGET, f, 2), (Object)1), (Object)Pair.of((Object)new VillageBoundRandomStroll(f), (Object)1), (Object)Pair.of((Object)new SetWalkTargetFromLookTarget(f, 2), (Object)1), (Object)Pair.of((Object)new JumpOnBed(f), (Object)1), (Object)Pair.of((Object)new DoNothing(30, 60), (Object)1)))), (Object)Pair.of((Object)3, (Object)new GiveGiftToHero(100)), (Object)Pair.of((Object)3, (Object)new SetLookAndInteract(EntityType.PLAYER, 4)), (Object)Pair.of((Object)3, (Object)new ShowTradesToPlayer(400, 1600)), (Object)Pair.of((Object)3, new GateBehavior((Map<MemoryModuleType<?>, MemoryStatus>)ImmutableMap.of(), (Set<MemoryModuleType<?>>)ImmutableSet.of(MemoryModuleType.INTERACTION_TARGET), GateBehavior.OrderPolicy.ORDERED, GateBehavior.RunningPolicy.RUN_ONE, ImmutableList.of((Object)Pair.of((Object)new TradeWithVillager(), (Object)1)))), (Object)Pair.of((Object)3, new GateBehavior((Map<MemoryModuleType<?>, MemoryStatus>)ImmutableMap.of(), (Set<MemoryModuleType<?>>)ImmutableSet.of(MemoryModuleType.BREED_TARGET), GateBehavior.OrderPolicy.ORDERED, GateBehavior.RunningPolicy.RUN_ONE, ImmutableList.of((Object)Pair.of((Object)new VillagerMakeLove(), (Object)1)))), VillagerGoalPackages.getFullLookBehavior(), (Object)Pair.of((Object)99, (Object)new UpdateActivityFromSchedule()));
    }

    public static ImmutableList<Pair<Integer, ? extends Behavior<? super Villager>>> getPanicPackage(VillagerProfession villagerProfession, float f) {
        float f2 = f * 1.5f;
        return ImmutableList.of((Object)Pair.of((Object)0, (Object)new VillagerCalmDown()), (Object)Pair.of((Object)1, SetWalkTargetAwayFrom.entity(MemoryModuleType.NEAREST_HOSTILE, f2, 6, false)), (Object)Pair.of((Object)1, SetWalkTargetAwayFrom.entity(MemoryModuleType.HURT_BY_ENTITY, f2, 6, false)), (Object)Pair.of((Object)3, (Object)new VillageBoundRandomStroll(f2, 2, 2)), VillagerGoalPackages.getMinimalLookBehavior());
    }

    public static ImmutableList<Pair<Integer, ? extends Behavior<? super Villager>>> getPreRaidPackage(VillagerProfession villagerProfession, float f) {
        return ImmutableList.of((Object)Pair.of((Object)0, (Object)new RingBell()), (Object)Pair.of((Object)0, new RunOne(ImmutableList.of((Object)Pair.of((Object)new SetWalkTargetFromBlockMemory(MemoryModuleType.MEETING_POINT, f * 1.5f, 2, 150, 200), (Object)6), (Object)Pair.of((Object)new VillageBoundRandomStroll(f * 1.5f), (Object)2)))), VillagerGoalPackages.getMinimalLookBehavior(), (Object)Pair.of((Object)99, (Object)new ResetRaidStatus()));
    }

    public static ImmutableList<Pair<Integer, ? extends Behavior<? super Villager>>> getRaidPackage(VillagerProfession villagerProfession, float f) {
        return ImmutableList.of((Object)Pair.of((Object)0, new RunOne(ImmutableList.of((Object)Pair.of((Object)new GoOutsideToCelebrate(f), (Object)5), (Object)Pair.of((Object)new VictoryStroll(f * 1.1f), (Object)2)))), (Object)Pair.of((Object)0, (Object)new CelebrateVillagersSurvivedRaid(600, 600)), (Object)Pair.of((Object)2, (Object)new LocateHidingPlaceDuringRaid(24, f * 1.4f)), VillagerGoalPackages.getMinimalLookBehavior(), (Object)Pair.of((Object)99, (Object)new ResetRaidStatus()));
    }

    public static ImmutableList<Pair<Integer, ? extends Behavior<? super Villager>>> getHidePackage(VillagerProfession villagerProfession, float f) {
        int n = 2;
        return ImmutableList.of((Object)Pair.of((Object)0, (Object)new SetHiddenState(15, 3)), (Object)Pair.of((Object)1, (Object)new LocateHidingPlace(32, f * 1.25f, 2)), VillagerGoalPackages.getMinimalLookBehavior());
    }

    private static Pair<Integer, Behavior<LivingEntity>> getFullLookBehavior() {
        return Pair.of((Object)5, new RunOne(ImmutableList.of((Object)Pair.of((Object)new SetEntityLookTarget(EntityType.CAT, 8.0f), (Object)8), (Object)Pair.of((Object)new SetEntityLookTarget(EntityType.VILLAGER, 8.0f), (Object)2), (Object)Pair.of((Object)new SetEntityLookTarget(EntityType.PLAYER, 8.0f), (Object)2), (Object)Pair.of((Object)new SetEntityLookTarget(MobCategory.CREATURE, 8.0f), (Object)1), (Object)Pair.of((Object)new SetEntityLookTarget(MobCategory.WATER_CREATURE, 8.0f), (Object)1), (Object)Pair.of((Object)new SetEntityLookTarget(MobCategory.WATER_AMBIENT, 8.0f), (Object)1), (Object)Pair.of((Object)new SetEntityLookTarget(MobCategory.MONSTER, 8.0f), (Object)1), (Object)Pair.of((Object)new DoNothing(30, 60), (Object)2))));
    }

    private static Pair<Integer, Behavior<LivingEntity>> getMinimalLookBehavior() {
        return Pair.of((Object)5, new RunOne(ImmutableList.of((Object)Pair.of((Object)new SetEntityLookTarget(EntityType.VILLAGER, 8.0f), (Object)2), (Object)Pair.of((Object)new SetEntityLookTarget(EntityType.PLAYER, 8.0f), (Object)2), (Object)Pair.of((Object)new DoNothing(30, 60), (Object)8))));
    }
}

