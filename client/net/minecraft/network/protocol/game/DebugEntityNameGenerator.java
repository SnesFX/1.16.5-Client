/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.network.protocol.game;

import java.util.Random;
import java.util.UUID;
import net.minecraft.Util;

public class DebugEntityNameGenerator {
    private static final String[] NAMES_FIRST_PART = new String[]{"Slim", "Far", "River", "Silly", "Fat", "Thin", "Fish", "Bat", "Dark", "Oak", "Sly", "Bush", "Zen", "Bark", "Cry", "Slack", "Soup", "Grim", "Hook", "Dirt", "Mud", "Sad", "Hard", "Crook", "Sneak", "Stink", "Weird", "Fire", "Soot", "Soft", "Rough", "Cling", "Scar"};
    private static final String[] NAMES_SECOND_PART = new String[]{"Fox", "Tail", "Jaw", "Whisper", "Twig", "Root", "Finder", "Nose", "Brow", "Blade", "Fry", "Seek", "Wart", "Tooth", "Foot", "Leaf", "Stone", "Fall", "Face", "Tongue", "Voice", "Lip", "Mouth", "Snail", "Toe", "Ear", "Hair", "Beard", "Shirt", "Fist"};

    public static String getEntityName(UUID uUID) {
        Random random = DebugEntityNameGenerator.getRandom(uUID);
        return DebugEntityNameGenerator.getRandomString(random, NAMES_FIRST_PART) + DebugEntityNameGenerator.getRandomString(random, NAMES_SECOND_PART);
    }

    private static String getRandomString(Random random, String[] arrstring) {
        return Util.getRandom(arrstring, random);
    }

    private static Random getRandom(UUID uUID) {
        return new Random(uUID.hashCode() >> 2);
    }
}

