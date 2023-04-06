/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.mojang.bridge.game.Language
 */
package net.minecraft.client.resources.language;

import com.mojang.bridge.game.Language;

public class LanguageInfo
implements Language,
Comparable<LanguageInfo> {
    private final String code;
    private final String region;
    private final String name;
    private final boolean bidirectional;

    public LanguageInfo(String string, String string2, String string3, boolean bl) {
        this.code = string;
        this.region = string2;
        this.name = string3;
        this.bidirectional = bl;
    }

    public String getCode() {
        return this.code;
    }

    public String getName() {
        return this.name;
    }

    public String getRegion() {
        return this.region;
    }

    public boolean isBidirectional() {
        return this.bidirectional;
    }

    public String toString() {
        return String.format("%s (%s)", this.name, this.region);
    }

    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (!(object instanceof LanguageInfo)) {
            return false;
        }
        return this.code.equals(((LanguageInfo)object).code);
    }

    public int hashCode() {
        return this.code.hashCode();
    }

    @Override
    public int compareTo(LanguageInfo languageInfo) {
        return this.code.compareTo(languageInfo.code);
    }

    @Override
    public /* synthetic */ int compareTo(Object object) {
        return this.compareTo((LanguageInfo)object);
    }
}

