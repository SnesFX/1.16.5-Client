/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  javax.annotation.Nullable
 */
package net.minecraft.world.level.block.entity;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.function.Predicate;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.ResourceLocationException;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.util.StringUtil;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.StructureBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.state.properties.StructureMode;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.templatesystem.BlockRotProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureManager;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;

public class StructureBlockEntity
extends BlockEntity {
    private ResourceLocation structureName;
    private String author = "";
    private String metaData = "";
    private BlockPos structurePos = new BlockPos(0, 1, 0);
    private BlockPos structureSize = BlockPos.ZERO;
    private Mirror mirror = Mirror.NONE;
    private Rotation rotation = Rotation.NONE;
    private StructureMode mode = StructureMode.DATA;
    private boolean ignoreEntities = true;
    private boolean powered;
    private boolean showAir;
    private boolean showBoundingBox = true;
    private float integrity = 1.0f;
    private long seed;

    public StructureBlockEntity() {
        super(BlockEntityType.STRUCTURE_BLOCK);
    }

    @Override
    public double getViewDistance() {
        return 96.0;
    }

    @Override
    public CompoundTag save(CompoundTag compoundTag) {
        super.save(compoundTag);
        compoundTag.putString("name", this.getStructureName());
        compoundTag.putString("author", this.author);
        compoundTag.putString("metadata", this.metaData);
        compoundTag.putInt("posX", this.structurePos.getX());
        compoundTag.putInt("posY", this.structurePos.getY());
        compoundTag.putInt("posZ", this.structurePos.getZ());
        compoundTag.putInt("sizeX", this.structureSize.getX());
        compoundTag.putInt("sizeY", this.structureSize.getY());
        compoundTag.putInt("sizeZ", this.structureSize.getZ());
        compoundTag.putString("rotation", this.rotation.toString());
        compoundTag.putString("mirror", this.mirror.toString());
        compoundTag.putString("mode", this.mode.toString());
        compoundTag.putBoolean("ignoreEntities", this.ignoreEntities);
        compoundTag.putBoolean("powered", this.powered);
        compoundTag.putBoolean("showair", this.showAir);
        compoundTag.putBoolean("showboundingbox", this.showBoundingBox);
        compoundTag.putFloat("integrity", this.integrity);
        compoundTag.putLong("seed", this.seed);
        return compoundTag;
    }

    @Override
    public void load(BlockState blockState, CompoundTag compoundTag) {
        super.load(blockState, compoundTag);
        this.setStructureName(compoundTag.getString("name"));
        this.author = compoundTag.getString("author");
        this.metaData = compoundTag.getString("metadata");
        int n = Mth.clamp(compoundTag.getInt("posX"), -48, 48);
        int n2 = Mth.clamp(compoundTag.getInt("posY"), -48, 48);
        int n3 = Mth.clamp(compoundTag.getInt("posZ"), -48, 48);
        this.structurePos = new BlockPos(n, n2, n3);
        int n4 = Mth.clamp(compoundTag.getInt("sizeX"), 0, 48);
        int n5 = Mth.clamp(compoundTag.getInt("sizeY"), 0, 48);
        int n6 = Mth.clamp(compoundTag.getInt("sizeZ"), 0, 48);
        this.structureSize = new BlockPos(n4, n5, n6);
        try {
            this.rotation = Rotation.valueOf(compoundTag.getString("rotation"));
        }
        catch (IllegalArgumentException illegalArgumentException) {
            this.rotation = Rotation.NONE;
        }
        try {
            this.mirror = Mirror.valueOf(compoundTag.getString("mirror"));
        }
        catch (IllegalArgumentException illegalArgumentException) {
            this.mirror = Mirror.NONE;
        }
        try {
            this.mode = StructureMode.valueOf(compoundTag.getString("mode"));
        }
        catch (IllegalArgumentException illegalArgumentException) {
            this.mode = StructureMode.DATA;
        }
        this.ignoreEntities = compoundTag.getBoolean("ignoreEntities");
        this.powered = compoundTag.getBoolean("powered");
        this.showAir = compoundTag.getBoolean("showair");
        this.showBoundingBox = compoundTag.getBoolean("showboundingbox");
        this.integrity = compoundTag.contains("integrity") ? compoundTag.getFloat("integrity") : 1.0f;
        this.seed = compoundTag.getLong("seed");
        this.updateBlockState();
    }

    private void updateBlockState() {
        if (this.level == null) {
            return;
        }
        BlockPos blockPos = this.getBlockPos();
        BlockState blockState = this.level.getBlockState(blockPos);
        if (blockState.is(Blocks.STRUCTURE_BLOCK)) {
            this.level.setBlock(blockPos, (BlockState)blockState.setValue(StructureBlock.MODE, this.mode), 2);
        }
    }

    @Nullable
    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return new ClientboundBlockEntityDataPacket(this.worldPosition, 7, this.getUpdateTag());
    }

    @Override
    public CompoundTag getUpdateTag() {
        return this.save(new CompoundTag());
    }

    public boolean usedBy(Player player) {
        if (!player.canUseGameMasterBlocks()) {
            return false;
        }
        if (player.getCommandSenderWorld().isClientSide) {
            player.openStructureBlock(this);
        }
        return true;
    }

    public String getStructureName() {
        return this.structureName == null ? "" : this.structureName.toString();
    }

    public String getStructurePath() {
        return this.structureName == null ? "" : this.structureName.getPath();
    }

    public boolean hasStructureName() {
        return this.structureName != null;
    }

    public void setStructureName(@Nullable String string) {
        this.setStructureName(StringUtil.isNullOrEmpty(string) ? null : ResourceLocation.tryParse(string));
    }

    public void setStructureName(@Nullable ResourceLocation resourceLocation) {
        this.structureName = resourceLocation;
    }

    public void createdBy(LivingEntity livingEntity) {
        this.author = livingEntity.getName().getString();
    }

    public BlockPos getStructurePos() {
        return this.structurePos;
    }

    public void setStructurePos(BlockPos blockPos) {
        this.structurePos = blockPos;
    }

    public BlockPos getStructureSize() {
        return this.structureSize;
    }

    public void setStructureSize(BlockPos blockPos) {
        this.structureSize = blockPos;
    }

    public Mirror getMirror() {
        return this.mirror;
    }

    public void setMirror(Mirror mirror) {
        this.mirror = mirror;
    }

    public Rotation getRotation() {
        return this.rotation;
    }

    public void setRotation(Rotation rotation) {
        this.rotation = rotation;
    }

    public String getMetaData() {
        return this.metaData;
    }

    public void setMetaData(String string) {
        this.metaData = string;
    }

    public StructureMode getMode() {
        return this.mode;
    }

    public void setMode(StructureMode structureMode) {
        this.mode = structureMode;
        BlockState blockState = this.level.getBlockState(this.getBlockPos());
        if (blockState.is(Blocks.STRUCTURE_BLOCK)) {
            this.level.setBlock(this.getBlockPos(), (BlockState)blockState.setValue(StructureBlock.MODE, structureMode), 2);
        }
    }

    public void nextMode() {
        switch (this.getMode()) {
            case SAVE: {
                this.setMode(StructureMode.LOAD);
                break;
            }
            case LOAD: {
                this.setMode(StructureMode.CORNER);
                break;
            }
            case CORNER: {
                this.setMode(StructureMode.DATA);
                break;
            }
            case DATA: {
                this.setMode(StructureMode.SAVE);
            }
        }
    }

    public boolean isIgnoreEntities() {
        return this.ignoreEntities;
    }

    public void setIgnoreEntities(boolean bl) {
        this.ignoreEntities = bl;
    }

    public float getIntegrity() {
        return this.integrity;
    }

    public void setIntegrity(float f) {
        this.integrity = f;
    }

    public long getSeed() {
        return this.seed;
    }

    public void setSeed(long l) {
        this.seed = l;
    }

    public boolean detectSize() {
        BlockPos blockPos;
        if (this.mode != StructureMode.SAVE) {
            return false;
        }
        BlockPos blockPos2 = this.getBlockPos();
        int n = 80;
        BlockPos blockPos3 = new BlockPos(blockPos2.getX() - 80, 0, blockPos2.getZ() - 80);
        List<StructureBlockEntity> list = this.getNearbyCornerBlocks(blockPos3, blockPos = new BlockPos(blockPos2.getX() + 80, 255, blockPos2.getZ() + 80));
        List<StructureBlockEntity> list2 = this.filterRelatedCornerBlocks(list);
        if (list2.size() < 1) {
            return false;
        }
        BoundingBox boundingBox = this.calculateEnclosingBoundingBox(blockPos2, list2);
        if (boundingBox.x1 - boundingBox.x0 > 1 && boundingBox.y1 - boundingBox.y0 > 1 && boundingBox.z1 - boundingBox.z0 > 1) {
            this.structurePos = new BlockPos(boundingBox.x0 - blockPos2.getX() + 1, boundingBox.y0 - blockPos2.getY() + 1, boundingBox.z0 - blockPos2.getZ() + 1);
            this.structureSize = new BlockPos(boundingBox.x1 - boundingBox.x0 - 1, boundingBox.y1 - boundingBox.y0 - 1, boundingBox.z1 - boundingBox.z0 - 1);
            this.setChanged();
            BlockState blockState = this.level.getBlockState(blockPos2);
            this.level.sendBlockUpdated(blockPos2, blockState, blockState, 3);
            return true;
        }
        return false;
    }

    private List<StructureBlockEntity> filterRelatedCornerBlocks(List<StructureBlockEntity> list) {
        Predicate<StructureBlockEntity> predicate = structureBlockEntity -> structureBlockEntity.mode == StructureMode.CORNER && Objects.equals(this.structureName, structureBlockEntity.structureName);
        return list.stream().filter(predicate).collect(Collectors.toList());
    }

    private List<StructureBlockEntity> getNearbyCornerBlocks(BlockPos blockPos, BlockPos blockPos2) {
        ArrayList arrayList = Lists.newArrayList();
        for (BlockPos blockPos3 : BlockPos.betweenClosed(blockPos, blockPos2)) {
            BlockEntity blockEntity;
            BlockState blockState = this.level.getBlockState(blockPos3);
            if (!blockState.is(Blocks.STRUCTURE_BLOCK) || (blockEntity = this.level.getBlockEntity(blockPos3)) == null || !(blockEntity instanceof StructureBlockEntity)) continue;
            arrayList.add((StructureBlockEntity)blockEntity);
        }
        return arrayList;
    }

    private BoundingBox calculateEnclosingBoundingBox(BlockPos blockPos, List<StructureBlockEntity> list) {
        BoundingBox boundingBox;
        if (list.size() > 1) {
            BlockPos blockPos2 = list.get(0).getBlockPos();
            boundingBox = new BoundingBox(blockPos2, blockPos2);
        } else {
            boundingBox = new BoundingBox(blockPos, blockPos);
        }
        for (StructureBlockEntity structureBlockEntity : list) {
            BlockPos blockPos3 = structureBlockEntity.getBlockPos();
            if (blockPos3.getX() < boundingBox.x0) {
                boundingBox.x0 = blockPos3.getX();
            } else if (blockPos3.getX() > boundingBox.x1) {
                boundingBox.x1 = blockPos3.getX();
            }
            if (blockPos3.getY() < boundingBox.y0) {
                boundingBox.y0 = blockPos3.getY();
            } else if (blockPos3.getY() > boundingBox.y1) {
                boundingBox.y1 = blockPos3.getY();
            }
            if (blockPos3.getZ() < boundingBox.z0) {
                boundingBox.z0 = blockPos3.getZ();
                continue;
            }
            if (blockPos3.getZ() <= boundingBox.z1) continue;
            boundingBox.z1 = blockPos3.getZ();
        }
        return boundingBox;
    }

    public boolean saveStructure() {
        return this.saveStructure(true);
    }

    public boolean saveStructure(boolean bl) {
        StructureTemplate structureTemplate;
        if (this.mode != StructureMode.SAVE || this.level.isClientSide || this.structureName == null) {
            return false;
        }
        BlockPos blockPos = this.getBlockPos().offset(this.structurePos);
        ServerLevel serverLevel = (ServerLevel)this.level;
        StructureManager structureManager = serverLevel.getStructureManager();
        try {
            structureTemplate = structureManager.getOrCreate(this.structureName);
        }
        catch (ResourceLocationException resourceLocationException) {
            return false;
        }
        structureTemplate.fillFromWorld(this.level, blockPos, this.structureSize, !this.ignoreEntities, Blocks.STRUCTURE_VOID);
        structureTemplate.setAuthor(this.author);
        if (bl) {
            try {
                return structureManager.save(this.structureName);
            }
            catch (ResourceLocationException resourceLocationException) {
                return false;
            }
        }
        return true;
    }

    public boolean loadStructure(ServerLevel serverLevel) {
        return this.loadStructure(serverLevel, true);
    }

    private static Random createRandom(long l) {
        if (l == 0L) {
            return new Random(Util.getMillis());
        }
        return new Random(l);
    }

    public boolean loadStructure(ServerLevel serverLevel, boolean bl) {
        StructureTemplate structureTemplate;
        if (this.mode != StructureMode.LOAD || this.structureName == null) {
            return false;
        }
        StructureManager structureManager = serverLevel.getStructureManager();
        try {
            structureTemplate = structureManager.get(this.structureName);
        }
        catch (ResourceLocationException resourceLocationException) {
            return false;
        }
        if (structureTemplate == null) {
            return false;
        }
        return this.loadStructure(serverLevel, bl, structureTemplate);
    }

    public boolean loadStructure(ServerLevel serverLevel, boolean bl, StructureTemplate structureTemplate) {
        BlockPos blockPos;
        boolean bl2;
        Object object;
        BlockPos blockPos2 = this.getBlockPos();
        if (!StringUtil.isNullOrEmpty(structureTemplate.getAuthor())) {
            this.author = structureTemplate.getAuthor();
        }
        if (!(bl2 = this.structureSize.equals(blockPos = structureTemplate.getSize()))) {
            this.structureSize = blockPos;
            this.setChanged();
            object = serverLevel.getBlockState(blockPos2);
            serverLevel.sendBlockUpdated(blockPos2, (BlockState)object, (BlockState)object, 3);
        }
        if (!bl || bl2) {
            object = new StructurePlaceSettings().setMirror(this.mirror).setRotation(this.rotation).setIgnoreEntities(this.ignoreEntities).setChunkPos(null);
            if (this.integrity < 1.0f) {
                ((StructurePlaceSettings)object).clearProcessors().addProcessor(new BlockRotProcessor(Mth.clamp(this.integrity, 0.0f, 1.0f))).setRandom(StructureBlockEntity.createRandom(this.seed));
            }
            BlockPos blockPos3 = blockPos2.offset(this.structurePos);
            structureTemplate.placeInWorldChunk(serverLevel, blockPos3, (StructurePlaceSettings)object, StructureBlockEntity.createRandom(this.seed));
            return true;
        }
        return false;
    }

    public void unloadStructure() {
        if (this.structureName == null) {
            return;
        }
        ServerLevel serverLevel = (ServerLevel)this.level;
        StructureManager structureManager = serverLevel.getStructureManager();
        structureManager.remove(this.structureName);
    }

    public boolean isStructureLoadable() {
        if (this.mode != StructureMode.LOAD || this.level.isClientSide || this.structureName == null) {
            return false;
        }
        ServerLevel serverLevel = (ServerLevel)this.level;
        StructureManager structureManager = serverLevel.getStructureManager();
        try {
            return structureManager.get(this.structureName) != null;
        }
        catch (ResourceLocationException resourceLocationException) {
            return false;
        }
    }

    public boolean isPowered() {
        return this.powered;
    }

    public void setPowered(boolean bl) {
        this.powered = bl;
    }

    public boolean getShowAir() {
        return this.showAir;
    }

    public void setShowAir(boolean bl) {
        this.showAir = bl;
    }

    public boolean getShowBoundingBox() {
        return this.showBoundingBox;
    }

    public void setShowBoundingBox(boolean bl) {
        this.showBoundingBox = bl;
    }

    public static enum UpdateType {
        UPDATE_DATA,
        SAVE_AREA,
        LOAD_AREA,
        SCAN_AREA;
        
    }

}

