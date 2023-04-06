/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.server.dedicated;

import java.nio.file.Path;
import java.util.function.UnaryOperator;
import net.minecraft.core.RegistryAccess;
import net.minecraft.server.dedicated.DedicatedServerProperties;

public class DedicatedServerSettings {
    private final Path source;
    private DedicatedServerProperties properties;

    public DedicatedServerSettings(RegistryAccess registryAccess, Path path) {
        this.source = path;
        this.properties = DedicatedServerProperties.fromFile(registryAccess, path);
    }

    public DedicatedServerProperties getProperties() {
        return this.properties;
    }

    public void forceSave() {
        this.properties.store(this.source);
    }

    public DedicatedServerSettings update(UnaryOperator<DedicatedServerProperties> unaryOperator) {
        this.properties = (DedicatedServerProperties)unaryOperator.apply(this.properties);
        this.properties.store(this.source);
        return this;
    }
}

