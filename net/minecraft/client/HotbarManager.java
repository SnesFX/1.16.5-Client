/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.DataFixer
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.client;

import com.mojang.datafixers.DataFixer;
import java.io.File;
import net.minecraft.SharedConstants;
import net.minecraft.client.player.inventory.Hotbar;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.util.datafix.DataFixTypes;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class HotbarManager {
    private static final Logger LOGGER = LogManager.getLogger();
    private final File optionsFile;
    private final DataFixer fixerUpper;
    private final Hotbar[] hotbars = new Hotbar[9];
    private boolean loaded;

    public HotbarManager(File file, DataFixer dataFixer) {
        this.optionsFile = new File(file, "hotbar.nbt");
        this.fixerUpper = dataFixer;
        for (int i = 0; i < 9; ++i) {
            this.hotbars[i] = new Hotbar();
        }
    }

    private void load() {
        try {
            CompoundTag compoundTag = NbtIo.read(this.optionsFile);
            if (compoundTag == null) {
                return;
            }
            if (!compoundTag.contains("DataVersion", 99)) {
                compoundTag.putInt("DataVersion", 1343);
            }
            compoundTag = NbtUtils.update(this.fixerUpper, DataFixTypes.HOTBAR, compoundTag, compoundTag.getInt("DataVersion"));
            for (int i = 0; i < 9; ++i) {
                this.hotbars[i].fromTag(compoundTag.getList(String.valueOf(i), 10));
            }
        }
        catch (Exception exception) {
            LOGGER.error("Failed to load creative mode options", (Throwable)exception);
        }
    }

    public void save() {
        try {
            CompoundTag compoundTag = new CompoundTag();
            compoundTag.putInt("DataVersion", SharedConstants.getCurrentVersion().getWorldVersion());
            for (int i = 0; i < 9; ++i) {
                compoundTag.put(String.valueOf(i), this.get(i).createTag());
            }
            NbtIo.write(compoundTag, this.optionsFile);
        }
        catch (Exception exception) {
            LOGGER.error("Failed to save creative mode options", (Throwable)exception);
        }
    }

    public Hotbar get(int n) {
        if (!this.loaded) {
            this.load();
            this.loaded = true;
        }
        return this.hotbars[n];
    }
}

