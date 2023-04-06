/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.gson.annotations.SerializedName
 */
package com.mojang.realmsclient.dto;

import com.google.gson.annotations.SerializedName;
import com.mojang.realmsclient.dto.ReflectionBasedSerialization;
import com.mojang.realmsclient.dto.ValueObject;

public class RealmsDescriptionDto
extends ValueObject
implements ReflectionBasedSerialization {
    @SerializedName(value="name")
    public String name;
    @SerializedName(value="description")
    public String description;

    public RealmsDescriptionDto(String string, String string2) {
        this.name = string;
        this.description = string2;
    }
}

