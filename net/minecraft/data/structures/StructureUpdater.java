/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.data.structures;

import net.minecraft.data.structures.SnbtToNbt;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.util.datafix.DataFixers;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class StructureUpdater
implements SnbtToNbt.Filter {
    private static final Logger LOGGER = LogManager.getLogger();

    @Override
    public CompoundTag apply(String string, CompoundTag compoundTag) {
        if (string.startsWith("data/minecraft/structures/")) {
            return StructureUpdater.updateStructure(string, StructureUpdater.patchVersion(compoundTag));
        }
        return compoundTag;
    }

    private static CompoundTag patchVersion(CompoundTag compoundTag) {
        if (!compoundTag.contains("DataVersion", 99)) {
            compoundTag.putInt("DataVersion", 500);
        }
        return compoundTag;
    }

    private static CompoundTag updateStructure(String string, CompoundTag compoundTag) {
        StructureTemplate structureTemplate = new StructureTemplate();
        int n = compoundTag.getInt("DataVersion");
        int n2 = 2532;
        if (n < 2532) {
            LOGGER.warn("SNBT Too old, do not forget to update: " + n + " < " + 2532 + ": " + string);
        }
        CompoundTag compoundTag2 = NbtUtils.update(DataFixers.getDataFixer(), DataFixTypes.STRUCTURE, compoundTag, n);
        structureTemplate.load(compoundTag2);
        return structureTemplate.save(new CompoundTag());
    }
}

