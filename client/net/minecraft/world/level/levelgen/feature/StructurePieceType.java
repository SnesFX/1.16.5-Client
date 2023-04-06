/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.world.level.levelgen.feature;

import java.util.Locale;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.levelgen.structure.BuriedTreasurePieces;
import net.minecraft.world.level.levelgen.structure.DesertPyramidPiece;
import net.minecraft.world.level.levelgen.structure.EndCityPieces;
import net.minecraft.world.level.levelgen.structure.IglooPieces;
import net.minecraft.world.level.levelgen.structure.JunglePyramidPiece;
import net.minecraft.world.level.levelgen.structure.MineShaftPieces;
import net.minecraft.world.level.levelgen.structure.NetherBridgePieces;
import net.minecraft.world.level.levelgen.structure.NetherFossilPieces;
import net.minecraft.world.level.levelgen.structure.OceanMonumentPieces;
import net.minecraft.world.level.levelgen.structure.OceanRuinPieces;
import net.minecraft.world.level.levelgen.structure.PoolElementStructurePiece;
import net.minecraft.world.level.levelgen.structure.RuinedPortalPiece;
import net.minecraft.world.level.levelgen.structure.ShipwreckPieces;
import net.minecraft.world.level.levelgen.structure.StrongholdPieces;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.SwamplandHutPiece;
import net.minecraft.world.level.levelgen.structure.WoodlandMansionPieces;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureManager;

public interface StructurePieceType {
    public static final StructurePieceType MINE_SHAFT_CORRIDOR = StructurePieceType.setPieceId((arg_0, arg_1) -> MineShaftPieces.MineShaftCorridor.new(arg_0, arg_1), "MSCorridor");
    public static final StructurePieceType MINE_SHAFT_CROSSING = StructurePieceType.setPieceId((arg_0, arg_1) -> MineShaftPieces.MineShaftCrossing.new(arg_0, arg_1), "MSCrossing");
    public static final StructurePieceType MINE_SHAFT_ROOM = StructurePieceType.setPieceId((arg_0, arg_1) -> MineShaftPieces.MineShaftRoom.new(arg_0, arg_1), "MSRoom");
    public static final StructurePieceType MINE_SHAFT_STAIRS = StructurePieceType.setPieceId((arg_0, arg_1) -> MineShaftPieces.MineShaftStairs.new(arg_0, arg_1), "MSStairs");
    public static final StructurePieceType NETHER_FORTRESS_BRIDGE_CROSSING = StructurePieceType.setPieceId((arg_0, arg_1) -> NetherBridgePieces.BridgeCrossing.new(arg_0, arg_1), "NeBCr");
    public static final StructurePieceType NETHER_FORTRESS_BRIDGE_END_FILLER = StructurePieceType.setPieceId((arg_0, arg_1) -> NetherBridgePieces.BridgeEndFiller.new(arg_0, arg_1), "NeBEF");
    public static final StructurePieceType NETHER_FORTRESS_BRIDGE_STRAIGHT = StructurePieceType.setPieceId((arg_0, arg_1) -> NetherBridgePieces.BridgeStraight.new(arg_0, arg_1), "NeBS");
    public static final StructurePieceType NETHER_FORTRESS_CASTLE_CORRIDOR_STAIRS = StructurePieceType.setPieceId((arg_0, arg_1) -> NetherBridgePieces.CastleCorridorStairsPiece.new(arg_0, arg_1), "NeCCS");
    public static final StructurePieceType NETHER_FORTRESS_CASTLE_CORRIDOR_T_BALCONY = StructurePieceType.setPieceId((arg_0, arg_1) -> NetherBridgePieces.CastleCorridorTBalconyPiece.new(arg_0, arg_1), "NeCTB");
    public static final StructurePieceType NETHER_FORTRESS_CASTLE_ENTRANCE = StructurePieceType.setPieceId((arg_0, arg_1) -> NetherBridgePieces.CastleEntrance.new(arg_0, arg_1), "NeCE");
    public static final StructurePieceType NETHER_FORTRESS_CASTLE_SMALL_CORRIDOR_CROSSING = StructurePieceType.setPieceId((arg_0, arg_1) -> NetherBridgePieces.CastleSmallCorridorCrossingPiece.new(arg_0, arg_1), "NeSCSC");
    public static final StructurePieceType NETHER_FORTRESS_CASTLE_SMALL_CORRIDOR_LEFT_TURN = StructurePieceType.setPieceId((arg_0, arg_1) -> NetherBridgePieces.CastleSmallCorridorLeftTurnPiece.new(arg_0, arg_1), "NeSCLT");
    public static final StructurePieceType NETHER_FORTRESS_CASTLE_SMALL_CORRIDOR = StructurePieceType.setPieceId((arg_0, arg_1) -> NetherBridgePieces.CastleSmallCorridorPiece.new(arg_0, arg_1), "NeSC");
    public static final StructurePieceType NETHER_FORTRESS_CASTLE_SMALL_CORRIDOR_RIGHT_TURN = StructurePieceType.setPieceId((arg_0, arg_1) -> NetherBridgePieces.CastleSmallCorridorRightTurnPiece.new(arg_0, arg_1), "NeSCRT");
    public static final StructurePieceType NETHER_FORTRESS_CASTLE_STALK_ROOM = StructurePieceType.setPieceId((arg_0, arg_1) -> NetherBridgePieces.CastleStalkRoom.new(arg_0, arg_1), "NeCSR");
    public static final StructurePieceType NETHER_FORTRESS_MONSTER_THRONE = StructurePieceType.setPieceId((arg_0, arg_1) -> NetherBridgePieces.MonsterThrone.new(arg_0, arg_1), "NeMT");
    public static final StructurePieceType NETHER_FORTRESS_ROOM_CROSSING = StructurePieceType.setPieceId((arg_0, arg_1) -> NetherBridgePieces.RoomCrossing.new(arg_0, arg_1), "NeRC");
    public static final StructurePieceType NETHER_FORTRESS_STAIRS_ROOM = StructurePieceType.setPieceId((arg_0, arg_1) -> NetherBridgePieces.StairsRoom.new(arg_0, arg_1), "NeSR");
    public static final StructurePieceType NETHER_FORTRESS_START = StructurePieceType.setPieceId((arg_0, arg_1) -> NetherBridgePieces.StartPiece.new(arg_0, arg_1), "NeStart");
    public static final StructurePieceType STRONGHOLD_CHEST_CORRIDOR = StructurePieceType.setPieceId((arg_0, arg_1) -> StrongholdPieces.ChestCorridor.new(arg_0, arg_1), "SHCC");
    public static final StructurePieceType STRONGHOLD_FILLER_CORRIDOR = StructurePieceType.setPieceId((arg_0, arg_1) -> StrongholdPieces.FillerCorridor.new(arg_0, arg_1), "SHFC");
    public static final StructurePieceType STRONGHOLD_FIVE_CROSSING = StructurePieceType.setPieceId((arg_0, arg_1) -> StrongholdPieces.FiveCrossing.new(arg_0, arg_1), "SH5C");
    public static final StructurePieceType STRONGHOLD_LEFT_TURN = StructurePieceType.setPieceId((arg_0, arg_1) -> StrongholdPieces.LeftTurn.new(arg_0, arg_1), "SHLT");
    public static final StructurePieceType STRONGHOLD_LIBRARY = StructurePieceType.setPieceId((arg_0, arg_1) -> StrongholdPieces.Library.new(arg_0, arg_1), "SHLi");
    public static final StructurePieceType STRONGHOLD_PORTAL_ROOM = StructurePieceType.setPieceId((arg_0, arg_1) -> StrongholdPieces.PortalRoom.new(arg_0, arg_1), "SHPR");
    public static final StructurePieceType STRONGHOLD_PRISON_HALL = StructurePieceType.setPieceId((arg_0, arg_1) -> StrongholdPieces.PrisonHall.new(arg_0, arg_1), "SHPH");
    public static final StructurePieceType STRONGHOLD_RIGHT_TURN = StructurePieceType.setPieceId((arg_0, arg_1) -> StrongholdPieces.RightTurn.new(arg_0, arg_1), "SHRT");
    public static final StructurePieceType STRONGHOLD_ROOM_CROSSING = StructurePieceType.setPieceId((arg_0, arg_1) -> StrongholdPieces.RoomCrossing.new(arg_0, arg_1), "SHRC");
    public static final StructurePieceType STRONGHOLD_STAIRS_DOWN = StructurePieceType.setPieceId((arg_0, arg_1) -> StrongholdPieces.StairsDown.new(arg_0, arg_1), "SHSD");
    public static final StructurePieceType STRONGHOLD_START = StructurePieceType.setPieceId((arg_0, arg_1) -> StrongholdPieces.StartPiece.new(arg_0, arg_1), "SHStart");
    public static final StructurePieceType STRONGHOLD_STRAIGHT = StructurePieceType.setPieceId((arg_0, arg_1) -> StrongholdPieces.Straight.new(arg_0, arg_1), "SHS");
    public static final StructurePieceType STRONGHOLD_STRAIGHT_STAIRS_DOWN = StructurePieceType.setPieceId((arg_0, arg_1) -> StrongholdPieces.StraightStairsDown.new(arg_0, arg_1), "SHSSD");
    public static final StructurePieceType JUNGLE_PYRAMID_PIECE = StructurePieceType.setPieceId((arg_0, arg_1) -> JunglePyramidPiece.new(arg_0, arg_1), "TeJP");
    public static final StructurePieceType OCEAN_RUIN = StructurePieceType.setPieceId((arg_0, arg_1) -> OceanRuinPieces.OceanRuinPiece.new(arg_0, arg_1), "ORP");
    public static final StructurePieceType IGLOO = StructurePieceType.setPieceId((arg_0, arg_1) -> IglooPieces.IglooPiece.new(arg_0, arg_1), "Iglu");
    public static final StructurePieceType RUINED_PORTAL = StructurePieceType.setPieceId((arg_0, arg_1) -> RuinedPortalPiece.new(arg_0, arg_1), "RUPO");
    public static final StructurePieceType SWAMPLAND_HUT = StructurePieceType.setPieceId((arg_0, arg_1) -> SwamplandHutPiece.new(arg_0, arg_1), "TeSH");
    public static final StructurePieceType DESERT_PYRAMID_PIECE = StructurePieceType.setPieceId((arg_0, arg_1) -> DesertPyramidPiece.new(arg_0, arg_1), "TeDP");
    public static final StructurePieceType OCEAN_MONUMENT_BUILDING = StructurePieceType.setPieceId((arg_0, arg_1) -> OceanMonumentPieces.MonumentBuilding.new(arg_0, arg_1), "OMB");
    public static final StructurePieceType OCEAN_MONUMENT_CORE_ROOM = StructurePieceType.setPieceId((arg_0, arg_1) -> OceanMonumentPieces.OceanMonumentCoreRoom.new(arg_0, arg_1), "OMCR");
    public static final StructurePieceType OCEAN_MONUMENT_DOUBLE_X_ROOM = StructurePieceType.setPieceId((arg_0, arg_1) -> OceanMonumentPieces.OceanMonumentDoubleXRoom.new(arg_0, arg_1), "OMDXR");
    public static final StructurePieceType OCEAN_MONUMENT_DOUBLE_XY_ROOM = StructurePieceType.setPieceId((arg_0, arg_1) -> OceanMonumentPieces.OceanMonumentDoubleXYRoom.new(arg_0, arg_1), "OMDXYR");
    public static final StructurePieceType OCEAN_MONUMENT_DOUBLE_Y_ROOM = StructurePieceType.setPieceId((arg_0, arg_1) -> OceanMonumentPieces.OceanMonumentDoubleYRoom.new(arg_0, arg_1), "OMDYR");
    public static final StructurePieceType OCEAN_MONUMENT_DOUBLE_YZ_ROOM = StructurePieceType.setPieceId((arg_0, arg_1) -> OceanMonumentPieces.OceanMonumentDoubleYZRoom.new(arg_0, arg_1), "OMDYZR");
    public static final StructurePieceType OCEAN_MONUMENT_DOUBLE_Z_ROOM = StructurePieceType.setPieceId((arg_0, arg_1) -> OceanMonumentPieces.OceanMonumentDoubleZRoom.new(arg_0, arg_1), "OMDZR");
    public static final StructurePieceType OCEAN_MONUMENT_ENTRY_ROOM = StructurePieceType.setPieceId((arg_0, arg_1) -> OceanMonumentPieces.OceanMonumentEntryRoom.new(arg_0, arg_1), "OMEntry");
    public static final StructurePieceType OCEAN_MONUMENT_PENTHOUSE = StructurePieceType.setPieceId((arg_0, arg_1) -> OceanMonumentPieces.OceanMonumentPenthouse.new(arg_0, arg_1), "OMPenthouse");
    public static final StructurePieceType OCEAN_MONUMENT_SIMPLE_ROOM = StructurePieceType.setPieceId((arg_0, arg_1) -> OceanMonumentPieces.OceanMonumentSimpleRoom.new(arg_0, arg_1), "OMSimple");
    public static final StructurePieceType OCEAN_MONUMENT_SIMPLE_TOP_ROOM = StructurePieceType.setPieceId((arg_0, arg_1) -> OceanMonumentPieces.OceanMonumentSimpleTopRoom.new(arg_0, arg_1), "OMSimpleT");
    public static final StructurePieceType OCEAN_MONUMENT_WING_ROOM = StructurePieceType.setPieceId((arg_0, arg_1) -> OceanMonumentPieces.OceanMonumentWingRoom.new(arg_0, arg_1), "OMWR");
    public static final StructurePieceType END_CITY_PIECE = StructurePieceType.setPieceId((arg_0, arg_1) -> EndCityPieces.EndCityPiece.new(arg_0, arg_1), "ECP");
    public static final StructurePieceType WOODLAND_MANSION_PIECE = StructurePieceType.setPieceId((arg_0, arg_1) -> WoodlandMansionPieces.WoodlandMansionPiece.new(arg_0, arg_1), "WMP");
    public static final StructurePieceType BURIED_TREASURE_PIECE = StructurePieceType.setPieceId((arg_0, arg_1) -> BuriedTreasurePieces.BuriedTreasurePiece.new(arg_0, arg_1), "BTP");
    public static final StructurePieceType SHIPWRECK_PIECE = StructurePieceType.setPieceId((arg_0, arg_1) -> ShipwreckPieces.ShipwreckPiece.new(arg_0, arg_1), "Shipwreck");
    public static final StructurePieceType NETHER_FOSSIL = StructurePieceType.setPieceId((arg_0, arg_1) -> NetherFossilPieces.NetherFossilPiece.new(arg_0, arg_1), "NeFos");
    public static final StructurePieceType JIGSAW = StructurePieceType.setPieceId((arg_0, arg_1) -> PoolElementStructurePiece.new(arg_0, arg_1), "jigsaw");

    public StructurePiece load(StructureManager var1, CompoundTag var2);

    public static StructurePieceType setPieceId(StructurePieceType structurePieceType, String string) {
        return Registry.register(Registry.STRUCTURE_PIECE, string.toLowerCase(Locale.ROOT), structurePieceType);
    }
}

