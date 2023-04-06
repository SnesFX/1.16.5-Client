/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.DataFixer
 *  javax.annotation.Nullable
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.world.level.storage;

import com.mojang.datafixers.DataFixer;
import java.io.File;
import java.nio.file.Path;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.storage.LevelResource;
import net.minecraft.world.level.storage.LevelStorageSource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class PlayerDataStorage {
    private static final Logger LOGGER = LogManager.getLogger();
    private final File playerDir;
    protected final DataFixer fixerUpper;

    public PlayerDataStorage(LevelStorageSource.LevelStorageAccess levelStorageAccess, DataFixer dataFixer) {
        this.fixerUpper = dataFixer;
        this.playerDir = levelStorageAccess.getLevelPath(LevelResource.PLAYER_DATA_DIR).toFile();
        this.playerDir.mkdirs();
    }

    public void save(Player player) {
        try {
            CompoundTag compoundTag = player.saveWithoutId(new CompoundTag());
            File file = File.createTempFile(player.getStringUUID() + "-", ".dat", this.playerDir);
            NbtIo.writeCompressed(compoundTag, file);
            File file2 = new File(this.playerDir, player.getStringUUID() + ".dat");
            File file3 = new File(this.playerDir, player.getStringUUID() + ".dat_old");
            Util.safeReplaceFile(file2, file, file3);
        }
        catch (Exception exception) {
            LOGGER.warn("Failed to save player data for {}", (Object)player.getName().getString());
        }
    }

    @Nullable
    public CompoundTag load(Player player) {
        CompoundTag compoundTag = null;
        try {
            File file = new File(this.playerDir, player.getStringUUID() + ".dat");
            if (file.exists() && file.isFile()) {
                compoundTag = NbtIo.readCompressed(file);
            }
        }
        catch (Exception exception) {
            LOGGER.warn("Failed to load player data for {}", (Object)player.getName().getString());
        }
        if (compoundTag != null) {
            int n = compoundTag.contains("DataVersion", 3) ? compoundTag.getInt("DataVersion") : -1;
            player.load(NbtUtils.update(this.fixerUpper, DataFixTypes.PLAYER, compoundTag, n));
        }
        return compoundTag;
    }

    public String[] getSeenPlayers() {
        String[] arrstring = this.playerDir.list();
        if (arrstring == null) {
            arrstring = new String[]{};
        }
        for (int i = 0; i < arrstring.length; ++i) {
            if (!arrstring[i].endsWith(".dat")) continue;
            arrstring[i] = arrstring[i].substring(0, arrstring[i].length() - 4);
        }
        return arrstring;
    }
}

