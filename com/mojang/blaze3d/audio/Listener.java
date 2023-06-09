/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  org.lwjgl.openal.AL10
 */
package com.mojang.blaze3d.audio;

import com.mojang.math.Vector3f;
import net.minecraft.world.phys.Vec3;
import org.lwjgl.openal.AL10;

public class Listener {
    private float gain = 1.0f;
    private Vec3 position = Vec3.ZERO;

    public void setListenerPosition(Vec3 vec3) {
        this.position = vec3;
        AL10.alListener3f((int)4100, (float)((float)vec3.x), (float)((float)vec3.y), (float)((float)vec3.z));
    }

    public Vec3 getListenerPosition() {
        return this.position;
    }

    public void setListenerOrientation(Vector3f vector3f, Vector3f vector3f2) {
        AL10.alListenerfv((int)4111, (float[])new float[]{vector3f.x(), vector3f.y(), vector3f.z(), vector3f2.x(), vector3f2.y(), vector3f2.z()});
    }

    public void setGain(float f) {
        AL10.alListenerf((int)4106, (float)f);
        this.gain = f;
    }

    public float getGain() {
        return this.gain;
    }

    public void reset() {
        this.setListenerPosition(Vec3.ZERO);
        this.setListenerOrientation(Vector3f.ZN, Vector3f.YP);
    }
}

