/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.google.gson.annotations.SerializedName
 */
package com.mojang.realmsclient.dto;

import com.google.common.collect.Lists;
import com.google.gson.annotations.SerializedName;
import com.mojang.realmsclient.dto.ReflectionBasedSerialization;
import com.mojang.realmsclient.dto.RegionPingResult;
import com.mojang.realmsclient.dto.ValueObject;
import java.util.List;

public class PingResult
extends ValueObject
implements ReflectionBasedSerialization {
    @SerializedName(value="pingResults")
    public List<RegionPingResult> pingResults = Lists.newArrayList();
    @SerializedName(value="worldIds")
    public List<Long> worldIds = Lists.newArrayList();
}

