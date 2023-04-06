/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.StringReader
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.world.level.levelgen.structure;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import net.minecraft.commands.arguments.blocks.BlockStateParser;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.StructureFeatureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.StructureMode;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.StructurePieceType;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class TemplateStructurePiece
extends StructurePiece {
    private static final Logger LOGGER = LogManager.getLogger();
    protected StructureTemplate template;
    protected StructurePlaceSettings placeSettings;
    protected BlockPos templatePosition;

    public TemplateStructurePiece(StructurePieceType structurePieceType, int n) {
        super(structurePieceType, n);
    }

    public TemplateStructurePiece(StructurePieceType structurePieceType, CompoundTag compoundTag) {
        super(structurePieceType, compoundTag);
        this.templatePosition = new BlockPos(compoundTag.getInt("TPX"), compoundTag.getInt("TPY"), compoundTag.getInt("TPZ"));
    }

    protected void setup(StructureTemplate structureTemplate, BlockPos blockPos, StructurePlaceSettings structurePlaceSettings) {
        this.template = structureTemplate;
        this.setOrientation(Direction.NORTH);
        this.templatePosition = blockPos;
        this.placeSettings = structurePlaceSettings;
        this.boundingBox = structureTemplate.getBoundingBox(structurePlaceSettings, blockPos);
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compoundTag) {
        compoundTag.putInt("TPX", this.templatePosition.getX());
        compoundTag.putInt("TPY", this.templatePosition.getY());
        compoundTag.putInt("TPZ", this.templatePosition.getZ());
    }

    @Override
    public boolean postProcess(WorldGenLevel worldGenLevel, StructureFeatureManager structureFeatureManager, ChunkGenerator chunkGenerator, Random random, BoundingBox boundingBox, ChunkPos chunkPos, BlockPos blockPos) {
        this.placeSettings.setBoundingBox(boundingBox);
        this.boundingBox = this.template.getBoundingBox(this.placeSettings, this.templatePosition);
        if (this.template.placeInWorld(worldGenLevel, this.templatePosition, blockPos, this.placeSettings, random, 2)) {
            Object object;
            List<StructureTemplate.StructureBlockInfo> list = this.template.filterBlocks(this.templatePosition, this.placeSettings, Blocks.STRUCTURE_BLOCK);
            for (StructureTemplate.StructureBlockInfo object3 : list) {
                if (object3.nbt == null || (object = StructureMode.valueOf(object3.nbt.getString("mode"))) != StructureMode.DATA) continue;
                this.handleDataMarker(object3.nbt.getString("metadata"), object3.pos, worldGenLevel, random, boundingBox);
            }
            List<StructureTemplate.StructureBlockInfo> list2 = this.template.filterBlocks(this.templatePosition, this.placeSettings, Blocks.JIGSAW);
            Iterator iterator = list2.iterator();
            while (iterator.hasNext()) {
                object = (StructureTemplate.StructureBlockInfo)iterator.next();
                if (((StructureTemplate.StructureBlockInfo)object).nbt == null) continue;
                String string = ((StructureTemplate.StructureBlockInfo)object).nbt.getString("final_state");
                BlockStateParser blockStateParser = new BlockStateParser(new StringReader(string), false);
                BlockState blockState = Blocks.AIR.defaultBlockState();
                try {
                    blockStateParser.parse(true);
                    BlockState commandSyntaxException = blockStateParser.getState();
                    if (commandSyntaxException != null) {
                        blockState = commandSyntaxException;
                    } else {
                        LOGGER.error("Error while parsing blockstate {} in jigsaw block @ {}", (Object)string, (Object)((StructureTemplate.StructureBlockInfo)object).pos);
                    }
                }
                catch (CommandSyntaxException commandSyntaxException) {
                    LOGGER.error("Error while parsing blockstate {} in jigsaw block @ {}", (Object)string, (Object)((StructureTemplate.StructureBlockInfo)object).pos);
                }
                worldGenLevel.setBlock(((StructureTemplate.StructureBlockInfo)object).pos, blockState, 3);
            }
        }
        return true;
    }

    protected abstract void handleDataMarker(String var1, BlockPos var2, ServerLevelAccessor var3, Random var4, BoundingBox var5);

    @Override
    public void move(int n, int n2, int n3) {
        super.move(n, n2, n3);
        this.templatePosition = this.templatePosition.offset(n, n2, n3);
    }

    @Override
    public Rotation getRotation() {
        return this.placeSettings.getRotation();
    }
}

