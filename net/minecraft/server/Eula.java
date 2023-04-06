/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.server;

import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.util.Properties;
import net.minecraft.SharedConstants;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Eula {
    private static final Logger LOGGER = LogManager.getLogger();
    private final Path file;
    private final boolean agreed;

    public Eula(Path path) {
        this.file = path;
        this.agreed = SharedConstants.IS_RUNNING_IN_IDE || this.readFile();
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    private boolean readFile() {
        try {
            try (InputStream inputStream = Files.newInputStream(this.file, new OpenOption[0]);){
                Properties properties = new Properties();
                properties.load(inputStream);
                boolean bl = Boolean.parseBoolean(properties.getProperty("eula", "false"));
                return bl;
            }
        }
        catch (Exception exception) {
            LOGGER.warn("Failed to load {}", (Object)this.file);
            this.saveDefaults();
            return false;
        }
    }

    public boolean hasAgreedToEULA() {
        return this.agreed;
    }

    private void saveDefaults() {
        if (SharedConstants.IS_RUNNING_IN_IDE) {
            return;
        }
        try {
            try (OutputStream outputStream = Files.newOutputStream(this.file, new OpenOption[0]);){
                Properties properties = new Properties();
                properties.setProperty("eula", "false");
                properties.store(outputStream, "By changing the setting below to TRUE you are indicating your agreement to our EULA (https://account.mojang.com/documents/minecraft_eula).");
            }
        }
        catch (Exception exception) {
            LOGGER.warn("Failed to save {}", (Object)this.file, (Object)exception);
        }
    }
}

