/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.world.level.saveddata;

import java.io.File;
import java.io.IOException;
import net.minecraft.SharedConstants;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.Tag;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class SavedData {
    private static final Logger LOGGER = LogManager.getLogger();
    private final String id;
    private boolean dirty;

    public SavedData(String string) {
        this.id = string;
    }

    public abstract void load(CompoundTag var1);

    public abstract CompoundTag save(CompoundTag var1);

    public void setDirty() {
        this.setDirty(true);
    }

    public void setDirty(boolean bl) {
        this.dirty = bl;
    }

    public boolean isDirty() {
        return this.dirty;
    }

    public String getId() {
        return this.id;
    }

    public void save(File file) {
        if (!this.isDirty()) {
            return;
        }
        CompoundTag compoundTag = new CompoundTag();
        compoundTag.put("data", this.save(new CompoundTag()));
        compoundTag.putInt("DataVersion", SharedConstants.getCurrentVersion().getWorldVersion());
        try {
            NbtIo.writeCompressed(compoundTag, file);
        }
        catch (IOException iOException) {
            LOGGER.error("Could not save data {}", (Object)this, (Object)iOException);
        }
        this.setDirty(false);
    }
}

