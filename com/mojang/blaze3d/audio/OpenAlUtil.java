/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 *  org.lwjgl.openal.AL10
 *  org.lwjgl.openal.ALC10
 */
package com.mojang.blaze3d.audio;

import javax.sound.sampled.AudioFormat;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.openal.AL10;
import org.lwjgl.openal.ALC10;

public class OpenAlUtil {
    private static final Logger LOGGER = LogManager.getLogger();

    private static String alErrorToString(int n) {
        switch (n) {
            case 40961: {
                return "Invalid name parameter.";
            }
            case 40962: {
                return "Invalid enumerated parameter value.";
            }
            case 40963: {
                return "Invalid parameter parameter value.";
            }
            case 40964: {
                return "Invalid operation.";
            }
            case 40965: {
                return "Unable to allocate memory.";
            }
        }
        return "An unrecognized error occurred.";
    }

    static boolean checkALError(String string) {
        int n = AL10.alGetError();
        if (n != 0) {
            LOGGER.error("{}: {}", (Object)string, (Object)OpenAlUtil.alErrorToString(n));
            return true;
        }
        return false;
    }

    private static String alcErrorToString(int n) {
        switch (n) {
            case 40961: {
                return "Invalid device.";
            }
            case 40962: {
                return "Invalid context.";
            }
            case 40964: {
                return "Invalid value.";
            }
            case 40963: {
                return "Illegal enum.";
            }
            case 40965: {
                return "Unable to allocate memory.";
            }
        }
        return "An unrecognized error occurred.";
    }

    static boolean checkALCError(long l, String string) {
        int n = ALC10.alcGetError((long)l);
        if (n != 0) {
            LOGGER.error("{}{}: {}", (Object)string, (Object)l, (Object)OpenAlUtil.alcErrorToString(n));
            return true;
        }
        return false;
    }

    static int audioFormatToOpenAl(AudioFormat audioFormat) {
        AudioFormat.Encoding encoding = audioFormat.getEncoding();
        int n = audioFormat.getChannels();
        int n2 = audioFormat.getSampleSizeInBits();
        if (encoding.equals(AudioFormat.Encoding.PCM_UNSIGNED) || encoding.equals(AudioFormat.Encoding.PCM_SIGNED)) {
            if (n == 1) {
                if (n2 == 8) {
                    return 4352;
                }
                if (n2 == 16) {
                    return 4353;
                }
            } else if (n == 2) {
                if (n2 == 8) {
                    return 4354;
                }
                if (n2 == 16) {
                    return 4355;
                }
            }
        }
        throw new IllegalArgumentException("Invalid audio format: " + audioFormat);
    }
}

