/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.arguments.StringArgumentType
 *  javax.annotation.Nullable
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.server.packs.repository;

import com.mojang.brigadier.arguments.StringArgumentType;
import java.io.IOException;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.SharedConstants;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.metadata.MetadataSectionSerializer;
import net.minecraft.server.packs.metadata.pack.PackMetadataSection;
import net.minecraft.server.packs.metadata.pack.PackMetadataSectionSerializer;
import net.minecraft.server.packs.repository.PackCompatibility;
import net.minecraft.server.packs.repository.PackSource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Pack
implements AutoCloseable {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final PackMetadataSection BROKEN_ASSETS_FALLBACK = new PackMetadataSection(new TranslatableComponent("resourcePack.broken_assets").withStyle(ChatFormatting.RED, ChatFormatting.ITALIC), SharedConstants.getCurrentVersion().getPackVersion());
    private final String id;
    private final Supplier<PackResources> supplier;
    private final Component title;
    private final Component description;
    private final PackCompatibility compatibility;
    private final Position defaultPosition;
    private final boolean required;
    private final boolean fixedPosition;
    private final PackSource packSource;

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    @Nullable
    public static Pack create(String string, boolean bl, Supplier<PackResources> supplier, PackConstructor packConstructor, Position position, PackSource packSource) {
        try {
            try (PackResources packResources = supplier.get();){
                PackMetadataSection packMetadataSection = packResources.getMetadataSection(PackMetadataSection.SERIALIZER);
                if (bl && packMetadataSection == null) {
                    LOGGER.error("Broken/missing pack.mcmeta detected, fudging it into existance. Please check that your launcher has downloaded all assets for the game correctly!");
                    packMetadataSection = BROKEN_ASSETS_FALLBACK;
                }
                if (packMetadataSection != null) {
                    Pack pack = packConstructor.create(string, bl, supplier, packResources, packMetadataSection, position, packSource);
                    return pack;
                }
                LOGGER.warn("Couldn't find pack meta for pack {}", (Object)string);
                return null;
            }
        }
        catch (IOException iOException) {
            LOGGER.warn("Couldn't get pack info for: {}", (Object)iOException.toString());
        }
        return null;
    }

    public Pack(String string, boolean bl, Supplier<PackResources> supplier, Component component, Component component2, PackCompatibility packCompatibility, Position position, boolean bl2, PackSource packSource) {
        this.id = string;
        this.supplier = supplier;
        this.title = component;
        this.description = component2;
        this.compatibility = packCompatibility;
        this.required = bl;
        this.defaultPosition = position;
        this.fixedPosition = bl2;
        this.packSource = packSource;
    }

    public Pack(String string, boolean bl, Supplier<PackResources> supplier, PackResources packResources, PackMetadataSection packMetadataSection, Position position, PackSource packSource) {
        this(string, bl, supplier, new TextComponent(packResources.getName()), packMetadataSection.getDescription(), PackCompatibility.forFormat(packMetadataSection.getPackFormat()), position, false, packSource);
    }

    public Component getTitle() {
        return this.title;
    }

    public Component getDescription() {
        return this.description;
    }

    public Component getChatLink(boolean bl) {
        return ComponentUtils.wrapInSquareBrackets(this.packSource.decorate(new TextComponent(this.id))).withStyle(style -> style.withColor(bl ? ChatFormatting.GREEN : ChatFormatting.RED).withInsertion(StringArgumentType.escapeIfRequired((String)this.id)).withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponent("").append(this.title).append("\n").append(this.description))));
    }

    public PackCompatibility getCompatibility() {
        return this.compatibility;
    }

    public PackResources open() {
        return this.supplier.get();
    }

    public String getId() {
        return this.id;
    }

    public boolean isRequired() {
        return this.required;
    }

    public boolean isFixedPosition() {
        return this.fixedPosition;
    }

    public Position getDefaultPosition() {
        return this.defaultPosition;
    }

    public PackSource getPackSource() {
        return this.packSource;
    }

    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (!(object instanceof Pack)) {
            return false;
        }
        Pack pack = (Pack)object;
        return this.id.equals(pack.id);
    }

    public int hashCode() {
        return this.id.hashCode();
    }

    @Override
    public void close() {
    }

    public static enum Position {
        TOP,
        BOTTOM;
        

        public <T> int insert(List<T> list, T t, Function<T, Pack> function, boolean bl) {
            int n;
            Position position;
            Pack pack;
            Position position2 = position = bl ? this.opposite() : this;
            if (position == BOTTOM) {
                int n2;
                Pack pack2;
                for (n2 = 0; n2 < list.size() && (pack2 = function.apply(list.get(n2))).isFixedPosition() && pack2.getDefaultPosition() == this; ++n2) {
                }
                list.add(n2, t);
                return n2;
            }
            for (n = list.size() - 1; n >= 0 && (pack = function.apply(list.get(n))).isFixedPosition() && pack.getDefaultPosition() == this; --n) {
            }
            list.add(n + 1, t);
            return n + 1;
        }

        public Position opposite() {
            return this == TOP ? BOTTOM : TOP;
        }
    }

    @FunctionalInterface
    public static interface PackConstructor {
        @Nullable
        public Pack create(String var1, boolean var2, Supplier<PackResources> var3, PackResources var4, PackMetadataSection var5, Position var6, PackSource var7);
    }

}

