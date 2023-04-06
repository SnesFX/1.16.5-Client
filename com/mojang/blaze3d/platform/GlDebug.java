/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Joiner
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.Maps
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 *  org.lwjgl.opengl.ARBDebugOutput
 *  org.lwjgl.opengl.GL
 *  org.lwjgl.opengl.GL11
 *  org.lwjgl.opengl.GLCapabilities
 *  org.lwjgl.opengl.GLDebugMessageARBCallback
 *  org.lwjgl.opengl.GLDebugMessageARBCallbackI
 *  org.lwjgl.opengl.GLDebugMessageCallback
 *  org.lwjgl.opengl.GLDebugMessageCallbackI
 *  org.lwjgl.opengl.KHRDebug
 */
package com.mojang.blaze3d.platform;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import com.mojang.blaze3d.platform.DebugMemoryUntracker;
import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.platform.MemoryTracker;
import com.mojang.blaze3d.systems.RenderSystem;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.opengl.ARBDebugOutput;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GLCapabilities;
import org.lwjgl.opengl.GLDebugMessageARBCallback;
import org.lwjgl.opengl.GLDebugMessageARBCallbackI;
import org.lwjgl.opengl.GLDebugMessageCallback;
import org.lwjgl.opengl.GLDebugMessageCallbackI;
import org.lwjgl.opengl.KHRDebug;

public class GlDebug {
    private static final Logger LOGGER = LogManager.getLogger();
    protected static final ByteBuffer BYTE_BUFFER = MemoryTracker.createByteBuffer(64);
    protected static final FloatBuffer FLOAT_BUFFER = BYTE_BUFFER.asFloatBuffer();
    protected static final IntBuffer INT_BUFFER = BYTE_BUFFER.asIntBuffer();
    private static final Joiner NEWLINE_JOINER = Joiner.on((char)'\n');
    private static final Joiner STATEMENT_JOINER = Joiner.on((String)"; ");
    private static final Map<Integer, String> BY_ID = Maps.newHashMap();
    private static final List<Integer> DEBUG_LEVELS = ImmutableList.of((Object)37190, (Object)37191, (Object)37192, (Object)33387);
    private static final List<Integer> DEBUG_LEVELS_ARB = ImmutableList.of((Object)37190, (Object)37191, (Object)37192);
    private static final Map<String, List<String>> SAVED_STATES;

    private static String printUnknownToken(int n) {
        return "Unknown (0x" + Integer.toHexString(n).toUpperCase() + ")";
    }

    private static String sourceToString(int n) {
        switch (n) {
            case 33350: {
                return "API";
            }
            case 33351: {
                return "WINDOW SYSTEM";
            }
            case 33352: {
                return "SHADER COMPILER";
            }
            case 33353: {
                return "THIRD PARTY";
            }
            case 33354: {
                return "APPLICATION";
            }
            case 33355: {
                return "OTHER";
            }
        }
        return GlDebug.printUnknownToken(n);
    }

    private static String typeToString(int n) {
        switch (n) {
            case 33356: {
                return "ERROR";
            }
            case 33357: {
                return "DEPRECATED BEHAVIOR";
            }
            case 33358: {
                return "UNDEFINED BEHAVIOR";
            }
            case 33359: {
                return "PORTABILITY";
            }
            case 33360: {
                return "PERFORMANCE";
            }
            case 33361: {
                return "OTHER";
            }
            case 33384: {
                return "MARKER";
            }
        }
        return GlDebug.printUnknownToken(n);
    }

    private static String severityToString(int n) {
        switch (n) {
            case 37190: {
                return "HIGH";
            }
            case 37191: {
                return "MEDIUM";
            }
            case 37192: {
                return "LOW";
            }
            case 33387: {
                return "NOTIFICATION";
            }
        }
        return GlDebug.printUnknownToken(n);
    }

    private static void printDebugLog(int n, int n2, int n3, int n4, int n5, long l, long l2) {
        LOGGER.info("OpenGL debug message, id={}, source={}, type={}, severity={}, message={}", (Object)n3, (Object)GlDebug.sourceToString(n), (Object)GlDebug.typeToString(n2), (Object)GlDebug.severityToString(n4), (Object)GLDebugMessageCallback.getMessage((int)n5, (long)l));
    }

    private static void setup(int n, String string3) {
        BY_ID.merge(n, string3, (string, string2) -> string + "/" + string2);
    }

    public static void enableDebugCallback(int n, boolean bl) {
        RenderSystem.assertThread(RenderSystem::isInInitPhase);
        if (n <= 0) {
            return;
        }
        GLCapabilities gLCapabilities = GL.getCapabilities();
        if (gLCapabilities.GL_KHR_debug) {
            GL11.glEnable((int)37600);
            if (bl) {
                GL11.glEnable((int)33346);
            }
            for (int i = 0; i < DEBUG_LEVELS.size(); ++i) {
                boolean bl2 = i < n;
                KHRDebug.glDebugMessageControl((int)4352, (int)4352, (int)DEBUG_LEVELS.get(i), (int[])null, (boolean)bl2);
            }
            KHRDebug.glDebugMessageCallback((GLDebugMessageCallbackI)((GLDebugMessageCallbackI)GLX.make(GLDebugMessageCallback.create((arg_0, arg_1, arg_2, arg_3, arg_4, arg_5, arg_6) -> GlDebug.printDebugLog(arg_0, arg_1, arg_2, arg_3, arg_4, arg_5, arg_6)), DebugMemoryUntracker::untrack)), (long)0L);
        } else if (gLCapabilities.GL_ARB_debug_output) {
            if (bl) {
                GL11.glEnable((int)33346);
            }
            for (int i = 0; i < DEBUG_LEVELS_ARB.size(); ++i) {
                boolean bl3 = i < n;
                ARBDebugOutput.glDebugMessageControlARB((int)4352, (int)4352, (int)DEBUG_LEVELS_ARB.get(i), (int[])null, (boolean)bl3);
            }
            ARBDebugOutput.glDebugMessageCallbackARB((GLDebugMessageARBCallbackI)((GLDebugMessageARBCallbackI)GLX.make(GLDebugMessageARBCallback.create((arg_0, arg_1, arg_2, arg_3, arg_4, arg_5, arg_6) -> GlDebug.printDebugLog(arg_0, arg_1, arg_2, arg_3, arg_4, arg_5, arg_6)), DebugMemoryUntracker::untrack)), (long)0L);
        }
    }

    static {
        GlDebug.setup(256, "GL11.GL_ACCUM");
        GlDebug.setup(257, "GL11.GL_LOAD");
        GlDebug.setup(258, "GL11.GL_RETURN");
        GlDebug.setup(259, "GL11.GL_MULT");
        GlDebug.setup(260, "GL11.GL_ADD");
        GlDebug.setup(512, "GL11.GL_NEVER");
        GlDebug.setup(513, "GL11.GL_LESS");
        GlDebug.setup(514, "GL11.GL_EQUAL");
        GlDebug.setup(515, "GL11.GL_LEQUAL");
        GlDebug.setup(516, "GL11.GL_GREATER");
        GlDebug.setup(517, "GL11.GL_NOTEQUAL");
        GlDebug.setup(518, "GL11.GL_GEQUAL");
        GlDebug.setup(519, "GL11.GL_ALWAYS");
        GlDebug.setup(0, "GL11.GL_POINTS");
        GlDebug.setup(1, "GL11.GL_LINES");
        GlDebug.setup(2, "GL11.GL_LINE_LOOP");
        GlDebug.setup(3, "GL11.GL_LINE_STRIP");
        GlDebug.setup(4, "GL11.GL_TRIANGLES");
        GlDebug.setup(5, "GL11.GL_TRIANGLE_STRIP");
        GlDebug.setup(6, "GL11.GL_TRIANGLE_FAN");
        GlDebug.setup(7, "GL11.GL_QUADS");
        GlDebug.setup(8, "GL11.GL_QUAD_STRIP");
        GlDebug.setup(9, "GL11.GL_POLYGON");
        GlDebug.setup(0, "GL11.GL_ZERO");
        GlDebug.setup(1, "GL11.GL_ONE");
        GlDebug.setup(768, "GL11.GL_SRC_COLOR");
        GlDebug.setup(769, "GL11.GL_ONE_MINUS_SRC_COLOR");
        GlDebug.setup(770, "GL11.GL_SRC_ALPHA");
        GlDebug.setup(771, "GL11.GL_ONE_MINUS_SRC_ALPHA");
        GlDebug.setup(772, "GL11.GL_DST_ALPHA");
        GlDebug.setup(773, "GL11.GL_ONE_MINUS_DST_ALPHA");
        GlDebug.setup(774, "GL11.GL_DST_COLOR");
        GlDebug.setup(775, "GL11.GL_ONE_MINUS_DST_COLOR");
        GlDebug.setup(776, "GL11.GL_SRC_ALPHA_SATURATE");
        GlDebug.setup(32769, "GL14.GL_CONSTANT_COLOR");
        GlDebug.setup(32770, "GL14.GL_ONE_MINUS_CONSTANT_COLOR");
        GlDebug.setup(32771, "GL14.GL_CONSTANT_ALPHA");
        GlDebug.setup(32772, "GL14.GL_ONE_MINUS_CONSTANT_ALPHA");
        GlDebug.setup(1, "GL11.GL_TRUE");
        GlDebug.setup(0, "GL11.GL_FALSE");
        GlDebug.setup(12288, "GL11.GL_CLIP_PLANE0");
        GlDebug.setup(12289, "GL11.GL_CLIP_PLANE1");
        GlDebug.setup(12290, "GL11.GL_CLIP_PLANE2");
        GlDebug.setup(12291, "GL11.GL_CLIP_PLANE3");
        GlDebug.setup(12292, "GL11.GL_CLIP_PLANE4");
        GlDebug.setup(12293, "GL11.GL_CLIP_PLANE5");
        GlDebug.setup(5120, "GL11.GL_BYTE");
        GlDebug.setup(5121, "GL11.GL_UNSIGNED_BYTE");
        GlDebug.setup(5122, "GL11.GL_SHORT");
        GlDebug.setup(5123, "GL11.GL_UNSIGNED_SHORT");
        GlDebug.setup(5124, "GL11.GL_INT");
        GlDebug.setup(5125, "GL11.GL_UNSIGNED_INT");
        GlDebug.setup(5126, "GL11.GL_FLOAT");
        GlDebug.setup(5127, "GL11.GL_2_BYTES");
        GlDebug.setup(5128, "GL11.GL_3_BYTES");
        GlDebug.setup(5129, "GL11.GL_4_BYTES");
        GlDebug.setup(5130, "GL11.GL_DOUBLE");
        GlDebug.setup(0, "GL11.GL_NONE");
        GlDebug.setup(1024, "GL11.GL_FRONT_LEFT");
        GlDebug.setup(1025, "GL11.GL_FRONT_RIGHT");
        GlDebug.setup(1026, "GL11.GL_BACK_LEFT");
        GlDebug.setup(1027, "GL11.GL_BACK_RIGHT");
        GlDebug.setup(1028, "GL11.GL_FRONT");
        GlDebug.setup(1029, "GL11.GL_BACK");
        GlDebug.setup(1030, "GL11.GL_LEFT");
        GlDebug.setup(1031, "GL11.GL_RIGHT");
        GlDebug.setup(1032, "GL11.GL_FRONT_AND_BACK");
        GlDebug.setup(1033, "GL11.GL_AUX0");
        GlDebug.setup(1034, "GL11.GL_AUX1");
        GlDebug.setup(1035, "GL11.GL_AUX2");
        GlDebug.setup(1036, "GL11.GL_AUX3");
        GlDebug.setup(0, "GL11.GL_NO_ERROR");
        GlDebug.setup(1280, "GL11.GL_INVALID_ENUM");
        GlDebug.setup(1281, "GL11.GL_INVALID_VALUE");
        GlDebug.setup(1282, "GL11.GL_INVALID_OPERATION");
        GlDebug.setup(1283, "GL11.GL_STACK_OVERFLOW");
        GlDebug.setup(1284, "GL11.GL_STACK_UNDERFLOW");
        GlDebug.setup(1285, "GL11.GL_OUT_OF_MEMORY");
        GlDebug.setup(1536, "GL11.GL_2D");
        GlDebug.setup(1537, "GL11.GL_3D");
        GlDebug.setup(1538, "GL11.GL_3D_COLOR");
        GlDebug.setup(1539, "GL11.GL_3D_COLOR_TEXTURE");
        GlDebug.setup(1540, "GL11.GL_4D_COLOR_TEXTURE");
        GlDebug.setup(1792, "GL11.GL_PASS_THROUGH_TOKEN");
        GlDebug.setup(1793, "GL11.GL_POINT_TOKEN");
        GlDebug.setup(1794, "GL11.GL_LINE_TOKEN");
        GlDebug.setup(1795, "GL11.GL_POLYGON_TOKEN");
        GlDebug.setup(1796, "GL11.GL_BITMAP_TOKEN");
        GlDebug.setup(1797, "GL11.GL_DRAW_PIXEL_TOKEN");
        GlDebug.setup(1798, "GL11.GL_COPY_PIXEL_TOKEN");
        GlDebug.setup(1799, "GL11.GL_LINE_RESET_TOKEN");
        GlDebug.setup(2048, "GL11.GL_EXP");
        GlDebug.setup(2049, "GL11.GL_EXP2");
        GlDebug.setup(2304, "GL11.GL_CW");
        GlDebug.setup(2305, "GL11.GL_CCW");
        GlDebug.setup(2560, "GL11.GL_COEFF");
        GlDebug.setup(2561, "GL11.GL_ORDER");
        GlDebug.setup(2562, "GL11.GL_DOMAIN");
        GlDebug.setup(2816, "GL11.GL_CURRENT_COLOR");
        GlDebug.setup(2817, "GL11.GL_CURRENT_INDEX");
        GlDebug.setup(2818, "GL11.GL_CURRENT_NORMAL");
        GlDebug.setup(2819, "GL11.GL_CURRENT_TEXTURE_COORDS");
        GlDebug.setup(2820, "GL11.GL_CURRENT_RASTER_COLOR");
        GlDebug.setup(2821, "GL11.GL_CURRENT_RASTER_INDEX");
        GlDebug.setup(2822, "GL11.GL_CURRENT_RASTER_TEXTURE_COORDS");
        GlDebug.setup(2823, "GL11.GL_CURRENT_RASTER_POSITION");
        GlDebug.setup(2824, "GL11.GL_CURRENT_RASTER_POSITION_VALID");
        GlDebug.setup(2825, "GL11.GL_CURRENT_RASTER_DISTANCE");
        GlDebug.setup(2832, "GL11.GL_POINT_SMOOTH");
        GlDebug.setup(2833, "GL11.GL_POINT_SIZE");
        GlDebug.setup(2834, "GL11.GL_POINT_SIZE_RANGE");
        GlDebug.setup(2835, "GL11.GL_POINT_SIZE_GRANULARITY");
        GlDebug.setup(2848, "GL11.GL_LINE_SMOOTH");
        GlDebug.setup(2849, "GL11.GL_LINE_WIDTH");
        GlDebug.setup(2850, "GL11.GL_LINE_WIDTH_RANGE");
        GlDebug.setup(2851, "GL11.GL_LINE_WIDTH_GRANULARITY");
        GlDebug.setup(2852, "GL11.GL_LINE_STIPPLE");
        GlDebug.setup(2853, "GL11.GL_LINE_STIPPLE_PATTERN");
        GlDebug.setup(2854, "GL11.GL_LINE_STIPPLE_REPEAT");
        GlDebug.setup(2864, "GL11.GL_LIST_MODE");
        GlDebug.setup(2865, "GL11.GL_MAX_LIST_NESTING");
        GlDebug.setup(2866, "GL11.GL_LIST_BASE");
        GlDebug.setup(2867, "GL11.GL_LIST_INDEX");
        GlDebug.setup(2880, "GL11.GL_POLYGON_MODE");
        GlDebug.setup(2881, "GL11.GL_POLYGON_SMOOTH");
        GlDebug.setup(2882, "GL11.GL_POLYGON_STIPPLE");
        GlDebug.setup(2883, "GL11.GL_EDGE_FLAG");
        GlDebug.setup(2884, "GL11.GL_CULL_FACE");
        GlDebug.setup(2885, "GL11.GL_CULL_FACE_MODE");
        GlDebug.setup(2886, "GL11.GL_FRONT_FACE");
        GlDebug.setup(2896, "GL11.GL_LIGHTING");
        GlDebug.setup(2897, "GL11.GL_LIGHT_MODEL_LOCAL_VIEWER");
        GlDebug.setup(2898, "GL11.GL_LIGHT_MODEL_TWO_SIDE");
        GlDebug.setup(2899, "GL11.GL_LIGHT_MODEL_AMBIENT");
        GlDebug.setup(2900, "GL11.GL_SHADE_MODEL");
        GlDebug.setup(2901, "GL11.GL_COLOR_MATERIAL_FACE");
        GlDebug.setup(2902, "GL11.GL_COLOR_MATERIAL_PARAMETER");
        GlDebug.setup(2903, "GL11.GL_COLOR_MATERIAL");
        GlDebug.setup(2912, "GL11.GL_FOG");
        GlDebug.setup(2913, "GL11.GL_FOG_INDEX");
        GlDebug.setup(2914, "GL11.GL_FOG_DENSITY");
        GlDebug.setup(2915, "GL11.GL_FOG_START");
        GlDebug.setup(2916, "GL11.GL_FOG_END");
        GlDebug.setup(2917, "GL11.GL_FOG_MODE");
        GlDebug.setup(2918, "GL11.GL_FOG_COLOR");
        GlDebug.setup(2928, "GL11.GL_DEPTH_RANGE");
        GlDebug.setup(2929, "GL11.GL_DEPTH_TEST");
        GlDebug.setup(2930, "GL11.GL_DEPTH_WRITEMASK");
        GlDebug.setup(2931, "GL11.GL_DEPTH_CLEAR_VALUE");
        GlDebug.setup(2932, "GL11.GL_DEPTH_FUNC");
        GlDebug.setup(2944, "GL11.GL_ACCUM_CLEAR_VALUE");
        GlDebug.setup(2960, "GL11.GL_STENCIL_TEST");
        GlDebug.setup(2961, "GL11.GL_STENCIL_CLEAR_VALUE");
        GlDebug.setup(2962, "GL11.GL_STENCIL_FUNC");
        GlDebug.setup(2963, "GL11.GL_STENCIL_VALUE_MASK");
        GlDebug.setup(2964, "GL11.GL_STENCIL_FAIL");
        GlDebug.setup(2965, "GL11.GL_STENCIL_PASS_DEPTH_FAIL");
        GlDebug.setup(2966, "GL11.GL_STENCIL_PASS_DEPTH_PASS");
        GlDebug.setup(2967, "GL11.GL_STENCIL_REF");
        GlDebug.setup(2968, "GL11.GL_STENCIL_WRITEMASK");
        GlDebug.setup(2976, "GL11.GL_MATRIX_MODE");
        GlDebug.setup(2977, "GL11.GL_NORMALIZE");
        GlDebug.setup(2978, "GL11.GL_VIEWPORT");
        GlDebug.setup(2979, "GL11.GL_MODELVIEW_STACK_DEPTH");
        GlDebug.setup(2980, "GL11.GL_PROJECTION_STACK_DEPTH");
        GlDebug.setup(2981, "GL11.GL_TEXTURE_STACK_DEPTH");
        GlDebug.setup(2982, "GL11.GL_MODELVIEW_MATRIX");
        GlDebug.setup(2983, "GL11.GL_PROJECTION_MATRIX");
        GlDebug.setup(2984, "GL11.GL_TEXTURE_MATRIX");
        GlDebug.setup(2992, "GL11.GL_ATTRIB_STACK_DEPTH");
        GlDebug.setup(2993, "GL11.GL_CLIENT_ATTRIB_STACK_DEPTH");
        GlDebug.setup(3008, "GL11.GL_ALPHA_TEST");
        GlDebug.setup(3009, "GL11.GL_ALPHA_TEST_FUNC");
        GlDebug.setup(3010, "GL11.GL_ALPHA_TEST_REF");
        GlDebug.setup(3024, "GL11.GL_DITHER");
        GlDebug.setup(3040, "GL11.GL_BLEND_DST");
        GlDebug.setup(3041, "GL11.GL_BLEND_SRC");
        GlDebug.setup(3042, "GL11.GL_BLEND");
        GlDebug.setup(3056, "GL11.GL_LOGIC_OP_MODE");
        GlDebug.setup(3057, "GL11.GL_INDEX_LOGIC_OP");
        GlDebug.setup(3058, "GL11.GL_COLOR_LOGIC_OP");
        GlDebug.setup(3072, "GL11.GL_AUX_BUFFERS");
        GlDebug.setup(3073, "GL11.GL_DRAW_BUFFER");
        GlDebug.setup(3074, "GL11.GL_READ_BUFFER");
        GlDebug.setup(3088, "GL11.GL_SCISSOR_BOX");
        GlDebug.setup(3089, "GL11.GL_SCISSOR_TEST");
        GlDebug.setup(3104, "GL11.GL_INDEX_CLEAR_VALUE");
        GlDebug.setup(3105, "GL11.GL_INDEX_WRITEMASK");
        GlDebug.setup(3106, "GL11.GL_COLOR_CLEAR_VALUE");
        GlDebug.setup(3107, "GL11.GL_COLOR_WRITEMASK");
        GlDebug.setup(3120, "GL11.GL_INDEX_MODE");
        GlDebug.setup(3121, "GL11.GL_RGBA_MODE");
        GlDebug.setup(3122, "GL11.GL_DOUBLEBUFFER");
        GlDebug.setup(3123, "GL11.GL_STEREO");
        GlDebug.setup(3136, "GL11.GL_RENDER_MODE");
        GlDebug.setup(3152, "GL11.GL_PERSPECTIVE_CORRECTION_HINT");
        GlDebug.setup(3153, "GL11.GL_POINT_SMOOTH_HINT");
        GlDebug.setup(3154, "GL11.GL_LINE_SMOOTH_HINT");
        GlDebug.setup(3155, "GL11.GL_POLYGON_SMOOTH_HINT");
        GlDebug.setup(3156, "GL11.GL_FOG_HINT");
        GlDebug.setup(3168, "GL11.GL_TEXTURE_GEN_S");
        GlDebug.setup(3169, "GL11.GL_TEXTURE_GEN_T");
        GlDebug.setup(3170, "GL11.GL_TEXTURE_GEN_R");
        GlDebug.setup(3171, "GL11.GL_TEXTURE_GEN_Q");
        GlDebug.setup(3184, "GL11.GL_PIXEL_MAP_I_TO_I");
        GlDebug.setup(3185, "GL11.GL_PIXEL_MAP_S_TO_S");
        GlDebug.setup(3186, "GL11.GL_PIXEL_MAP_I_TO_R");
        GlDebug.setup(3187, "GL11.GL_PIXEL_MAP_I_TO_G");
        GlDebug.setup(3188, "GL11.GL_PIXEL_MAP_I_TO_B");
        GlDebug.setup(3189, "GL11.GL_PIXEL_MAP_I_TO_A");
        GlDebug.setup(3190, "GL11.GL_PIXEL_MAP_R_TO_R");
        GlDebug.setup(3191, "GL11.GL_PIXEL_MAP_G_TO_G");
        GlDebug.setup(3192, "GL11.GL_PIXEL_MAP_B_TO_B");
        GlDebug.setup(3193, "GL11.GL_PIXEL_MAP_A_TO_A");
        GlDebug.setup(3248, "GL11.GL_PIXEL_MAP_I_TO_I_SIZE");
        GlDebug.setup(3249, "GL11.GL_PIXEL_MAP_S_TO_S_SIZE");
        GlDebug.setup(3250, "GL11.GL_PIXEL_MAP_I_TO_R_SIZE");
        GlDebug.setup(3251, "GL11.GL_PIXEL_MAP_I_TO_G_SIZE");
        GlDebug.setup(3252, "GL11.GL_PIXEL_MAP_I_TO_B_SIZE");
        GlDebug.setup(3253, "GL11.GL_PIXEL_MAP_I_TO_A_SIZE");
        GlDebug.setup(3254, "GL11.GL_PIXEL_MAP_R_TO_R_SIZE");
        GlDebug.setup(3255, "GL11.GL_PIXEL_MAP_G_TO_G_SIZE");
        GlDebug.setup(3256, "GL11.GL_PIXEL_MAP_B_TO_B_SIZE");
        GlDebug.setup(3257, "GL11.GL_PIXEL_MAP_A_TO_A_SIZE");
        GlDebug.setup(3312, "GL11.GL_UNPACK_SWAP_BYTES");
        GlDebug.setup(3313, "GL11.GL_UNPACK_LSB_FIRST");
        GlDebug.setup(3314, "GL11.GL_UNPACK_ROW_LENGTH");
        GlDebug.setup(3315, "GL11.GL_UNPACK_SKIP_ROWS");
        GlDebug.setup(3316, "GL11.GL_UNPACK_SKIP_PIXELS");
        GlDebug.setup(3317, "GL11.GL_UNPACK_ALIGNMENT");
        GlDebug.setup(3328, "GL11.GL_PACK_SWAP_BYTES");
        GlDebug.setup(3329, "GL11.GL_PACK_LSB_FIRST");
        GlDebug.setup(3330, "GL11.GL_PACK_ROW_LENGTH");
        GlDebug.setup(3331, "GL11.GL_PACK_SKIP_ROWS");
        GlDebug.setup(3332, "GL11.GL_PACK_SKIP_PIXELS");
        GlDebug.setup(3333, "GL11.GL_PACK_ALIGNMENT");
        GlDebug.setup(3344, "GL11.GL_MAP_COLOR");
        GlDebug.setup(3345, "GL11.GL_MAP_STENCIL");
        GlDebug.setup(3346, "GL11.GL_INDEX_SHIFT");
        GlDebug.setup(3347, "GL11.GL_INDEX_OFFSET");
        GlDebug.setup(3348, "GL11.GL_RED_SCALE");
        GlDebug.setup(3349, "GL11.GL_RED_BIAS");
        GlDebug.setup(3350, "GL11.GL_ZOOM_X");
        GlDebug.setup(3351, "GL11.GL_ZOOM_Y");
        GlDebug.setup(3352, "GL11.GL_GREEN_SCALE");
        GlDebug.setup(3353, "GL11.GL_GREEN_BIAS");
        GlDebug.setup(3354, "GL11.GL_BLUE_SCALE");
        GlDebug.setup(3355, "GL11.GL_BLUE_BIAS");
        GlDebug.setup(3356, "GL11.GL_ALPHA_SCALE");
        GlDebug.setup(3357, "GL11.GL_ALPHA_BIAS");
        GlDebug.setup(3358, "GL11.GL_DEPTH_SCALE");
        GlDebug.setup(3359, "GL11.GL_DEPTH_BIAS");
        GlDebug.setup(3376, "GL11.GL_MAX_EVAL_ORDER");
        GlDebug.setup(3377, "GL11.GL_MAX_LIGHTS");
        GlDebug.setup(3378, "GL11.GL_MAX_CLIP_PLANES");
        GlDebug.setup(3379, "GL11.GL_MAX_TEXTURE_SIZE");
        GlDebug.setup(3380, "GL11.GL_MAX_PIXEL_MAP_TABLE");
        GlDebug.setup(3381, "GL11.GL_MAX_ATTRIB_STACK_DEPTH");
        GlDebug.setup(3382, "GL11.GL_MAX_MODELVIEW_STACK_DEPTH");
        GlDebug.setup(3383, "GL11.GL_MAX_NAME_STACK_DEPTH");
        GlDebug.setup(3384, "GL11.GL_MAX_PROJECTION_STACK_DEPTH");
        GlDebug.setup(3385, "GL11.GL_MAX_TEXTURE_STACK_DEPTH");
        GlDebug.setup(3386, "GL11.GL_MAX_VIEWPORT_DIMS");
        GlDebug.setup(3387, "GL11.GL_MAX_CLIENT_ATTRIB_STACK_DEPTH");
        GlDebug.setup(3408, "GL11.GL_SUBPIXEL_BITS");
        GlDebug.setup(3409, "GL11.GL_INDEX_BITS");
        GlDebug.setup(3410, "GL11.GL_RED_BITS");
        GlDebug.setup(3411, "GL11.GL_GREEN_BITS");
        GlDebug.setup(3412, "GL11.GL_BLUE_BITS");
        GlDebug.setup(3413, "GL11.GL_ALPHA_BITS");
        GlDebug.setup(3414, "GL11.GL_DEPTH_BITS");
        GlDebug.setup(3415, "GL11.GL_STENCIL_BITS");
        GlDebug.setup(3416, "GL11.GL_ACCUM_RED_BITS");
        GlDebug.setup(3417, "GL11.GL_ACCUM_GREEN_BITS");
        GlDebug.setup(3418, "GL11.GL_ACCUM_BLUE_BITS");
        GlDebug.setup(3419, "GL11.GL_ACCUM_ALPHA_BITS");
        GlDebug.setup(3440, "GL11.GL_NAME_STACK_DEPTH");
        GlDebug.setup(3456, "GL11.GL_AUTO_NORMAL");
        GlDebug.setup(3472, "GL11.GL_MAP1_COLOR_4");
        GlDebug.setup(3473, "GL11.GL_MAP1_INDEX");
        GlDebug.setup(3474, "GL11.GL_MAP1_NORMAL");
        GlDebug.setup(3475, "GL11.GL_MAP1_TEXTURE_COORD_1");
        GlDebug.setup(3476, "GL11.GL_MAP1_TEXTURE_COORD_2");
        GlDebug.setup(3477, "GL11.GL_MAP1_TEXTURE_COORD_3");
        GlDebug.setup(3478, "GL11.GL_MAP1_TEXTURE_COORD_4");
        GlDebug.setup(3479, "GL11.GL_MAP1_VERTEX_3");
        GlDebug.setup(3480, "GL11.GL_MAP1_VERTEX_4");
        GlDebug.setup(3504, "GL11.GL_MAP2_COLOR_4");
        GlDebug.setup(3505, "GL11.GL_MAP2_INDEX");
        GlDebug.setup(3506, "GL11.GL_MAP2_NORMAL");
        GlDebug.setup(3507, "GL11.GL_MAP2_TEXTURE_COORD_1");
        GlDebug.setup(3508, "GL11.GL_MAP2_TEXTURE_COORD_2");
        GlDebug.setup(3509, "GL11.GL_MAP2_TEXTURE_COORD_3");
        GlDebug.setup(3510, "GL11.GL_MAP2_TEXTURE_COORD_4");
        GlDebug.setup(3511, "GL11.GL_MAP2_VERTEX_3");
        GlDebug.setup(3512, "GL11.GL_MAP2_VERTEX_4");
        GlDebug.setup(3536, "GL11.GL_MAP1_GRID_DOMAIN");
        GlDebug.setup(3537, "GL11.GL_MAP1_GRID_SEGMENTS");
        GlDebug.setup(3538, "GL11.GL_MAP2_GRID_DOMAIN");
        GlDebug.setup(3539, "GL11.GL_MAP2_GRID_SEGMENTS");
        GlDebug.setup(3552, "GL11.GL_TEXTURE_1D");
        GlDebug.setup(3553, "GL11.GL_TEXTURE_2D");
        GlDebug.setup(3568, "GL11.GL_FEEDBACK_BUFFER_POINTER");
        GlDebug.setup(3569, "GL11.GL_FEEDBACK_BUFFER_SIZE");
        GlDebug.setup(3570, "GL11.GL_FEEDBACK_BUFFER_TYPE");
        GlDebug.setup(3571, "GL11.GL_SELECTION_BUFFER_POINTER");
        GlDebug.setup(3572, "GL11.GL_SELECTION_BUFFER_SIZE");
        GlDebug.setup(4096, "GL11.GL_TEXTURE_WIDTH");
        GlDebug.setup(4097, "GL11.GL_TEXTURE_HEIGHT");
        GlDebug.setup(4099, "GL11.GL_TEXTURE_INTERNAL_FORMAT");
        GlDebug.setup(4100, "GL11.GL_TEXTURE_BORDER_COLOR");
        GlDebug.setup(4101, "GL11.GL_TEXTURE_BORDER");
        GlDebug.setup(4352, "GL11.GL_DONT_CARE");
        GlDebug.setup(4353, "GL11.GL_FASTEST");
        GlDebug.setup(4354, "GL11.GL_NICEST");
        GlDebug.setup(16384, "GL11.GL_LIGHT0");
        GlDebug.setup(16385, "GL11.GL_LIGHT1");
        GlDebug.setup(16386, "GL11.GL_LIGHT2");
        GlDebug.setup(16387, "GL11.GL_LIGHT3");
        GlDebug.setup(16388, "GL11.GL_LIGHT4");
        GlDebug.setup(16389, "GL11.GL_LIGHT5");
        GlDebug.setup(16390, "GL11.GL_LIGHT6");
        GlDebug.setup(16391, "GL11.GL_LIGHT7");
        GlDebug.setup(4608, "GL11.GL_AMBIENT");
        GlDebug.setup(4609, "GL11.GL_DIFFUSE");
        GlDebug.setup(4610, "GL11.GL_SPECULAR");
        GlDebug.setup(4611, "GL11.GL_POSITION");
        GlDebug.setup(4612, "GL11.GL_SPOT_DIRECTION");
        GlDebug.setup(4613, "GL11.GL_SPOT_EXPONENT");
        GlDebug.setup(4614, "GL11.GL_SPOT_CUTOFF");
        GlDebug.setup(4615, "GL11.GL_CONSTANT_ATTENUATION");
        GlDebug.setup(4616, "GL11.GL_LINEAR_ATTENUATION");
        GlDebug.setup(4617, "GL11.GL_QUADRATIC_ATTENUATION");
        GlDebug.setup(4864, "GL11.GL_COMPILE");
        GlDebug.setup(4865, "GL11.GL_COMPILE_AND_EXECUTE");
        GlDebug.setup(5376, "GL11.GL_CLEAR");
        GlDebug.setup(5377, "GL11.GL_AND");
        GlDebug.setup(5378, "GL11.GL_AND_REVERSE");
        GlDebug.setup(5379, "GL11.GL_COPY");
        GlDebug.setup(5380, "GL11.GL_AND_INVERTED");
        GlDebug.setup(5381, "GL11.GL_NOOP");
        GlDebug.setup(5382, "GL11.GL_XOR");
        GlDebug.setup(5383, "GL11.GL_OR");
        GlDebug.setup(5384, "GL11.GL_NOR");
        GlDebug.setup(5385, "GL11.GL_EQUIV");
        GlDebug.setup(5386, "GL11.GL_INVERT");
        GlDebug.setup(5387, "GL11.GL_OR_REVERSE");
        GlDebug.setup(5388, "GL11.GL_COPY_INVERTED");
        GlDebug.setup(5389, "GL11.GL_OR_INVERTED");
        GlDebug.setup(5390, "GL11.GL_NAND");
        GlDebug.setup(5391, "GL11.GL_SET");
        GlDebug.setup(5632, "GL11.GL_EMISSION");
        GlDebug.setup(5633, "GL11.GL_SHININESS");
        GlDebug.setup(5634, "GL11.GL_AMBIENT_AND_DIFFUSE");
        GlDebug.setup(5635, "GL11.GL_COLOR_INDEXES");
        GlDebug.setup(5888, "GL11.GL_MODELVIEW");
        GlDebug.setup(5889, "GL11.GL_PROJECTION");
        GlDebug.setup(5890, "GL11.GL_TEXTURE");
        GlDebug.setup(6144, "GL11.GL_COLOR");
        GlDebug.setup(6145, "GL11.GL_DEPTH");
        GlDebug.setup(6146, "GL11.GL_STENCIL");
        GlDebug.setup(6400, "GL11.GL_COLOR_INDEX");
        GlDebug.setup(6401, "GL11.GL_STENCIL_INDEX");
        GlDebug.setup(6402, "GL11.GL_DEPTH_COMPONENT");
        GlDebug.setup(6403, "GL11.GL_RED");
        GlDebug.setup(6404, "GL11.GL_GREEN");
        GlDebug.setup(6405, "GL11.GL_BLUE");
        GlDebug.setup(6406, "GL11.GL_ALPHA");
        GlDebug.setup(6407, "GL11.GL_RGB");
        GlDebug.setup(6408, "GL11.GL_RGBA");
        GlDebug.setup(6409, "GL11.GL_LUMINANCE");
        GlDebug.setup(6410, "GL11.GL_LUMINANCE_ALPHA");
        GlDebug.setup(6656, "GL11.GL_BITMAP");
        GlDebug.setup(6912, "GL11.GL_POINT");
        GlDebug.setup(6913, "GL11.GL_LINE");
        GlDebug.setup(6914, "GL11.GL_FILL");
        GlDebug.setup(7168, "GL11.GL_RENDER");
        GlDebug.setup(7169, "GL11.GL_FEEDBACK");
        GlDebug.setup(7170, "GL11.GL_SELECT");
        GlDebug.setup(7424, "GL11.GL_FLAT");
        GlDebug.setup(7425, "GL11.GL_SMOOTH");
        GlDebug.setup(7680, "GL11.GL_KEEP");
        GlDebug.setup(7681, "GL11.GL_REPLACE");
        GlDebug.setup(7682, "GL11.GL_INCR");
        GlDebug.setup(7683, "GL11.GL_DECR");
        GlDebug.setup(7936, "GL11.GL_VENDOR");
        GlDebug.setup(7937, "GL11.GL_RENDERER");
        GlDebug.setup(7938, "GL11.GL_VERSION");
        GlDebug.setup(7939, "GL11.GL_EXTENSIONS");
        GlDebug.setup(8192, "GL11.GL_S");
        GlDebug.setup(8193, "GL11.GL_T");
        GlDebug.setup(8194, "GL11.GL_R");
        GlDebug.setup(8195, "GL11.GL_Q");
        GlDebug.setup(8448, "GL11.GL_MODULATE");
        GlDebug.setup(8449, "GL11.GL_DECAL");
        GlDebug.setup(8704, "GL11.GL_TEXTURE_ENV_MODE");
        GlDebug.setup(8705, "GL11.GL_TEXTURE_ENV_COLOR");
        GlDebug.setup(8960, "GL11.GL_TEXTURE_ENV");
        GlDebug.setup(9216, "GL11.GL_EYE_LINEAR");
        GlDebug.setup(9217, "GL11.GL_OBJECT_LINEAR");
        GlDebug.setup(9218, "GL11.GL_SPHERE_MAP");
        GlDebug.setup(9472, "GL11.GL_TEXTURE_GEN_MODE");
        GlDebug.setup(9473, "GL11.GL_OBJECT_PLANE");
        GlDebug.setup(9474, "GL11.GL_EYE_PLANE");
        GlDebug.setup(9728, "GL11.GL_NEAREST");
        GlDebug.setup(9729, "GL11.GL_LINEAR");
        GlDebug.setup(9984, "GL11.GL_NEAREST_MIPMAP_NEAREST");
        GlDebug.setup(9985, "GL11.GL_LINEAR_MIPMAP_NEAREST");
        GlDebug.setup(9986, "GL11.GL_NEAREST_MIPMAP_LINEAR");
        GlDebug.setup(9987, "GL11.GL_LINEAR_MIPMAP_LINEAR");
        GlDebug.setup(10240, "GL11.GL_TEXTURE_MAG_FILTER");
        GlDebug.setup(10241, "GL11.GL_TEXTURE_MIN_FILTER");
        GlDebug.setup(10242, "GL11.GL_TEXTURE_WRAP_S");
        GlDebug.setup(10243, "GL11.GL_TEXTURE_WRAP_T");
        GlDebug.setup(10496, "GL11.GL_CLAMP");
        GlDebug.setup(10497, "GL11.GL_REPEAT");
        GlDebug.setup(-1, "GL11.GL_ALL_CLIENT_ATTRIB_BITS");
        GlDebug.setup(32824, "GL11.GL_POLYGON_OFFSET_FACTOR");
        GlDebug.setup(10752, "GL11.GL_POLYGON_OFFSET_UNITS");
        GlDebug.setup(10753, "GL11.GL_POLYGON_OFFSET_POINT");
        GlDebug.setup(10754, "GL11.GL_POLYGON_OFFSET_LINE");
        GlDebug.setup(32823, "GL11.GL_POLYGON_OFFSET_FILL");
        GlDebug.setup(32827, "GL11.GL_ALPHA4");
        GlDebug.setup(32828, "GL11.GL_ALPHA8");
        GlDebug.setup(32829, "GL11.GL_ALPHA12");
        GlDebug.setup(32830, "GL11.GL_ALPHA16");
        GlDebug.setup(32831, "GL11.GL_LUMINANCE4");
        GlDebug.setup(32832, "GL11.GL_LUMINANCE8");
        GlDebug.setup(32833, "GL11.GL_LUMINANCE12");
        GlDebug.setup(32834, "GL11.GL_LUMINANCE16");
        GlDebug.setup(32835, "GL11.GL_LUMINANCE4_ALPHA4");
        GlDebug.setup(32836, "GL11.GL_LUMINANCE6_ALPHA2");
        GlDebug.setup(32837, "GL11.GL_LUMINANCE8_ALPHA8");
        GlDebug.setup(32838, "GL11.GL_LUMINANCE12_ALPHA4");
        GlDebug.setup(32839, "GL11.GL_LUMINANCE12_ALPHA12");
        GlDebug.setup(32840, "GL11.GL_LUMINANCE16_ALPHA16");
        GlDebug.setup(32841, "GL11.GL_INTENSITY");
        GlDebug.setup(32842, "GL11.GL_INTENSITY4");
        GlDebug.setup(32843, "GL11.GL_INTENSITY8");
        GlDebug.setup(32844, "GL11.GL_INTENSITY12");
        GlDebug.setup(32845, "GL11.GL_INTENSITY16");
        GlDebug.setup(10768, "GL11.GL_R3_G3_B2");
        GlDebug.setup(32847, "GL11.GL_RGB4");
        GlDebug.setup(32848, "GL11.GL_RGB5");
        GlDebug.setup(32849, "GL11.GL_RGB8");
        GlDebug.setup(32850, "GL11.GL_RGB10");
        GlDebug.setup(32851, "GL11.GL_RGB12");
        GlDebug.setup(32852, "GL11.GL_RGB16");
        GlDebug.setup(32853, "GL11.GL_RGBA2");
        GlDebug.setup(32854, "GL11.GL_RGBA4");
        GlDebug.setup(32855, "GL11.GL_RGB5_A1");
        GlDebug.setup(32856, "GL11.GL_RGBA8");
        GlDebug.setup(32857, "GL11.GL_RGB10_A2");
        GlDebug.setup(32858, "GL11.GL_RGBA12");
        GlDebug.setup(32859, "GL11.GL_RGBA16");
        GlDebug.setup(32860, "GL11.GL_TEXTURE_RED_SIZE");
        GlDebug.setup(32861, "GL11.GL_TEXTURE_GREEN_SIZE");
        GlDebug.setup(32862, "GL11.GL_TEXTURE_BLUE_SIZE");
        GlDebug.setup(32863, "GL11.GL_TEXTURE_ALPHA_SIZE");
        GlDebug.setup(32864, "GL11.GL_TEXTURE_LUMINANCE_SIZE");
        GlDebug.setup(32865, "GL11.GL_TEXTURE_INTENSITY_SIZE");
        GlDebug.setup(32867, "GL11.GL_PROXY_TEXTURE_1D");
        GlDebug.setup(32868, "GL11.GL_PROXY_TEXTURE_2D");
        GlDebug.setup(32870, "GL11.GL_TEXTURE_PRIORITY");
        GlDebug.setup(32871, "GL11.GL_TEXTURE_RESIDENT");
        GlDebug.setup(32872, "GL11.GL_TEXTURE_BINDING_1D");
        GlDebug.setup(32873, "GL11.GL_TEXTURE_BINDING_2D");
        GlDebug.setup(32884, "GL11.GL_VERTEX_ARRAY");
        GlDebug.setup(32885, "GL11.GL_NORMAL_ARRAY");
        GlDebug.setup(32886, "GL11.GL_COLOR_ARRAY");
        GlDebug.setup(32887, "GL11.GL_INDEX_ARRAY");
        GlDebug.setup(32888, "GL11.GL_TEXTURE_COORD_ARRAY");
        GlDebug.setup(32889, "GL11.GL_EDGE_FLAG_ARRAY");
        GlDebug.setup(32890, "GL11.GL_VERTEX_ARRAY_SIZE");
        GlDebug.setup(32891, "GL11.GL_VERTEX_ARRAY_TYPE");
        GlDebug.setup(32892, "GL11.GL_VERTEX_ARRAY_STRIDE");
        GlDebug.setup(32894, "GL11.GL_NORMAL_ARRAY_TYPE");
        GlDebug.setup(32895, "GL11.GL_NORMAL_ARRAY_STRIDE");
        GlDebug.setup(32897, "GL11.GL_COLOR_ARRAY_SIZE");
        GlDebug.setup(32898, "GL11.GL_COLOR_ARRAY_TYPE");
        GlDebug.setup(32899, "GL11.GL_COLOR_ARRAY_STRIDE");
        GlDebug.setup(32901, "GL11.GL_INDEX_ARRAY_TYPE");
        GlDebug.setup(32902, "GL11.GL_INDEX_ARRAY_STRIDE");
        GlDebug.setup(32904, "GL11.GL_TEXTURE_COORD_ARRAY_SIZE");
        GlDebug.setup(32905, "GL11.GL_TEXTURE_COORD_ARRAY_TYPE");
        GlDebug.setup(32906, "GL11.GL_TEXTURE_COORD_ARRAY_STRIDE");
        GlDebug.setup(32908, "GL11.GL_EDGE_FLAG_ARRAY_STRIDE");
        GlDebug.setup(32910, "GL11.GL_VERTEX_ARRAY_POINTER");
        GlDebug.setup(32911, "GL11.GL_NORMAL_ARRAY_POINTER");
        GlDebug.setup(32912, "GL11.GL_COLOR_ARRAY_POINTER");
        GlDebug.setup(32913, "GL11.GL_INDEX_ARRAY_POINTER");
        GlDebug.setup(32914, "GL11.GL_TEXTURE_COORD_ARRAY_POINTER");
        GlDebug.setup(32915, "GL11.GL_EDGE_FLAG_ARRAY_POINTER");
        GlDebug.setup(10784, "GL11.GL_V2F");
        GlDebug.setup(10785, "GL11.GL_V3F");
        GlDebug.setup(10786, "GL11.GL_C4UB_V2F");
        GlDebug.setup(10787, "GL11.GL_C4UB_V3F");
        GlDebug.setup(10788, "GL11.GL_C3F_V3F");
        GlDebug.setup(10789, "GL11.GL_N3F_V3F");
        GlDebug.setup(10790, "GL11.GL_C4F_N3F_V3F");
        GlDebug.setup(10791, "GL11.GL_T2F_V3F");
        GlDebug.setup(10792, "GL11.GL_T4F_V4F");
        GlDebug.setup(10793, "GL11.GL_T2F_C4UB_V3F");
        GlDebug.setup(10794, "GL11.GL_T2F_C3F_V3F");
        GlDebug.setup(10795, "GL11.GL_T2F_N3F_V3F");
        GlDebug.setup(10796, "GL11.GL_T2F_C4F_N3F_V3F");
        GlDebug.setup(10797, "GL11.GL_T4F_C4F_N3F_V4F");
        GlDebug.setup(3057, "GL11.GL_LOGIC_OP");
        GlDebug.setup(4099, "GL11.GL_TEXTURE_COMPONENTS");
        GlDebug.setup(32874, "GL12.GL_TEXTURE_BINDING_3D");
        GlDebug.setup(32875, "GL12.GL_PACK_SKIP_IMAGES");
        GlDebug.setup(32876, "GL12.GL_PACK_IMAGE_HEIGHT");
        GlDebug.setup(32877, "GL12.GL_UNPACK_SKIP_IMAGES");
        GlDebug.setup(32878, "GL12.GL_UNPACK_IMAGE_HEIGHT");
        GlDebug.setup(32879, "GL12.GL_TEXTURE_3D");
        GlDebug.setup(32880, "GL12.GL_PROXY_TEXTURE_3D");
        GlDebug.setup(32881, "GL12.GL_TEXTURE_DEPTH");
        GlDebug.setup(32882, "GL12.GL_TEXTURE_WRAP_R");
        GlDebug.setup(32883, "GL12.GL_MAX_3D_TEXTURE_SIZE");
        GlDebug.setup(32992, "GL12.GL_BGR");
        GlDebug.setup(32993, "GL12.GL_BGRA");
        GlDebug.setup(32818, "GL12.GL_UNSIGNED_BYTE_3_3_2");
        GlDebug.setup(33634, "GL12.GL_UNSIGNED_BYTE_2_3_3_REV");
        GlDebug.setup(33635, "GL12.GL_UNSIGNED_SHORT_5_6_5");
        GlDebug.setup(33636, "GL12.GL_UNSIGNED_SHORT_5_6_5_REV");
        GlDebug.setup(32819, "GL12.GL_UNSIGNED_SHORT_4_4_4_4");
        GlDebug.setup(33637, "GL12.GL_UNSIGNED_SHORT_4_4_4_4_REV");
        GlDebug.setup(32820, "GL12.GL_UNSIGNED_SHORT_5_5_5_1");
        GlDebug.setup(33638, "GL12.GL_UNSIGNED_SHORT_1_5_5_5_REV");
        GlDebug.setup(32821, "GL12.GL_UNSIGNED_INT_8_8_8_8");
        GlDebug.setup(33639, "GL12.GL_UNSIGNED_INT_8_8_8_8_REV");
        GlDebug.setup(32822, "GL12.GL_UNSIGNED_INT_10_10_10_2");
        GlDebug.setup(33640, "GL12.GL_UNSIGNED_INT_2_10_10_10_REV");
        GlDebug.setup(32826, "GL12.GL_RESCALE_NORMAL");
        GlDebug.setup(33272, "GL12.GL_LIGHT_MODEL_COLOR_CONTROL");
        GlDebug.setup(33273, "GL12.GL_SINGLE_COLOR");
        GlDebug.setup(33274, "GL12.GL_SEPARATE_SPECULAR_COLOR");
        GlDebug.setup(33071, "GL12.GL_CLAMP_TO_EDGE");
        GlDebug.setup(33082, "GL12.GL_TEXTURE_MIN_LOD");
        GlDebug.setup(33083, "GL12.GL_TEXTURE_MAX_LOD");
        GlDebug.setup(33084, "GL12.GL_TEXTURE_BASE_LEVEL");
        GlDebug.setup(33085, "GL12.GL_TEXTURE_MAX_LEVEL");
        GlDebug.setup(33000, "GL12.GL_MAX_ELEMENTS_VERTICES");
        GlDebug.setup(33001, "GL12.GL_MAX_ELEMENTS_INDICES");
        GlDebug.setup(33901, "GL12.GL_ALIASED_POINT_SIZE_RANGE");
        GlDebug.setup(33902, "GL12.GL_ALIASED_LINE_WIDTH_RANGE");
        GlDebug.setup(33984, "GL13.GL_TEXTURE0");
        GlDebug.setup(33985, "GL13.GL_TEXTURE1");
        GlDebug.setup(33986, "GL13.GL_TEXTURE2");
        GlDebug.setup(33987, "GL13.GL_TEXTURE3");
        GlDebug.setup(33988, "GL13.GL_TEXTURE4");
        GlDebug.setup(33989, "GL13.GL_TEXTURE5");
        GlDebug.setup(33990, "GL13.GL_TEXTURE6");
        GlDebug.setup(33991, "GL13.GL_TEXTURE7");
        GlDebug.setup(33992, "GL13.GL_TEXTURE8");
        GlDebug.setup(33993, "GL13.GL_TEXTURE9");
        GlDebug.setup(33994, "GL13.GL_TEXTURE10");
        GlDebug.setup(33995, "GL13.GL_TEXTURE11");
        GlDebug.setup(33996, "GL13.GL_TEXTURE12");
        GlDebug.setup(33997, "GL13.GL_TEXTURE13");
        GlDebug.setup(33998, "GL13.GL_TEXTURE14");
        GlDebug.setup(33999, "GL13.GL_TEXTURE15");
        GlDebug.setup(34000, "GL13.GL_TEXTURE16");
        GlDebug.setup(34001, "GL13.GL_TEXTURE17");
        GlDebug.setup(34002, "GL13.GL_TEXTURE18");
        GlDebug.setup(34003, "GL13.GL_TEXTURE19");
        GlDebug.setup(34004, "GL13.GL_TEXTURE20");
        GlDebug.setup(34005, "GL13.GL_TEXTURE21");
        GlDebug.setup(34006, "GL13.GL_TEXTURE22");
        GlDebug.setup(34007, "GL13.GL_TEXTURE23");
        GlDebug.setup(34008, "GL13.GL_TEXTURE24");
        GlDebug.setup(34009, "GL13.GL_TEXTURE25");
        GlDebug.setup(34010, "GL13.GL_TEXTURE26");
        GlDebug.setup(34011, "GL13.GL_TEXTURE27");
        GlDebug.setup(34012, "GL13.GL_TEXTURE28");
        GlDebug.setup(34013, "GL13.GL_TEXTURE29");
        GlDebug.setup(34014, "GL13.GL_TEXTURE30");
        GlDebug.setup(34015, "GL13.GL_TEXTURE31");
        GlDebug.setup(34016, "GL13.GL_ACTIVE_TEXTURE");
        GlDebug.setup(34017, "GL13.GL_CLIENT_ACTIVE_TEXTURE");
        GlDebug.setup(34018, "GL13.GL_MAX_TEXTURE_UNITS");
        GlDebug.setup(34065, "GL13.GL_NORMAL_MAP");
        GlDebug.setup(34066, "GL13.GL_REFLECTION_MAP");
        GlDebug.setup(34067, "GL13.GL_TEXTURE_CUBE_MAP");
        GlDebug.setup(34068, "GL13.GL_TEXTURE_BINDING_CUBE_MAP");
        GlDebug.setup(34069, "GL13.GL_TEXTURE_CUBE_MAP_POSITIVE_X");
        GlDebug.setup(34070, "GL13.GL_TEXTURE_CUBE_MAP_NEGATIVE_X");
        GlDebug.setup(34071, "GL13.GL_TEXTURE_CUBE_MAP_POSITIVE_Y");
        GlDebug.setup(34072, "GL13.GL_TEXTURE_CUBE_MAP_NEGATIVE_Y");
        GlDebug.setup(34073, "GL13.GL_TEXTURE_CUBE_MAP_POSITIVE_Z");
        GlDebug.setup(34074, "GL13.GL_TEXTURE_CUBE_MAP_NEGATIVE_Z");
        GlDebug.setup(34075, "GL13.GL_PROXY_TEXTURE_CUBE_MAP");
        GlDebug.setup(34076, "GL13.GL_MAX_CUBE_MAP_TEXTURE_SIZE");
        GlDebug.setup(34025, "GL13.GL_COMPRESSED_ALPHA");
        GlDebug.setup(34026, "GL13.GL_COMPRESSED_LUMINANCE");
        GlDebug.setup(34027, "GL13.GL_COMPRESSED_LUMINANCE_ALPHA");
        GlDebug.setup(34028, "GL13.GL_COMPRESSED_INTENSITY");
        GlDebug.setup(34029, "GL13.GL_COMPRESSED_RGB");
        GlDebug.setup(34030, "GL13.GL_COMPRESSED_RGBA");
        GlDebug.setup(34031, "GL13.GL_TEXTURE_COMPRESSION_HINT");
        GlDebug.setup(34464, "GL13.GL_TEXTURE_COMPRESSED_IMAGE_SIZE");
        GlDebug.setup(34465, "GL13.GL_TEXTURE_COMPRESSED");
        GlDebug.setup(34466, "GL13.GL_NUM_COMPRESSED_TEXTURE_FORMATS");
        GlDebug.setup(34467, "GL13.GL_COMPRESSED_TEXTURE_FORMATS");
        GlDebug.setup(32925, "GL13.GL_MULTISAMPLE");
        GlDebug.setup(32926, "GL13.GL_SAMPLE_ALPHA_TO_COVERAGE");
        GlDebug.setup(32927, "GL13.GL_SAMPLE_ALPHA_TO_ONE");
        GlDebug.setup(32928, "GL13.GL_SAMPLE_COVERAGE");
        GlDebug.setup(32936, "GL13.GL_SAMPLE_BUFFERS");
        GlDebug.setup(32937, "GL13.GL_SAMPLES");
        GlDebug.setup(32938, "GL13.GL_SAMPLE_COVERAGE_VALUE");
        GlDebug.setup(32939, "GL13.GL_SAMPLE_COVERAGE_INVERT");
        GlDebug.setup(34019, "GL13.GL_TRANSPOSE_MODELVIEW_MATRIX");
        GlDebug.setup(34020, "GL13.GL_TRANSPOSE_PROJECTION_MATRIX");
        GlDebug.setup(34021, "GL13.GL_TRANSPOSE_TEXTURE_MATRIX");
        GlDebug.setup(34022, "GL13.GL_TRANSPOSE_COLOR_MATRIX");
        GlDebug.setup(34160, "GL13.GL_COMBINE");
        GlDebug.setup(34161, "GL13.GL_COMBINE_RGB");
        GlDebug.setup(34162, "GL13.GL_COMBINE_ALPHA");
        GlDebug.setup(34176, "GL13.GL_SOURCE0_RGB");
        GlDebug.setup(34177, "GL13.GL_SOURCE1_RGB");
        GlDebug.setup(34178, "GL13.GL_SOURCE2_RGB");
        GlDebug.setup(34184, "GL13.GL_SOURCE0_ALPHA");
        GlDebug.setup(34185, "GL13.GL_SOURCE1_ALPHA");
        GlDebug.setup(34186, "GL13.GL_SOURCE2_ALPHA");
        GlDebug.setup(34192, "GL13.GL_OPERAND0_RGB");
        GlDebug.setup(34193, "GL13.GL_OPERAND1_RGB");
        GlDebug.setup(34194, "GL13.GL_OPERAND2_RGB");
        GlDebug.setup(34200, "GL13.GL_OPERAND0_ALPHA");
        GlDebug.setup(34201, "GL13.GL_OPERAND1_ALPHA");
        GlDebug.setup(34202, "GL13.GL_OPERAND2_ALPHA");
        GlDebug.setup(34163, "GL13.GL_RGB_SCALE");
        GlDebug.setup(34164, "GL13.GL_ADD_SIGNED");
        GlDebug.setup(34165, "GL13.GL_INTERPOLATE");
        GlDebug.setup(34023, "GL13.GL_SUBTRACT");
        GlDebug.setup(34166, "GL13.GL_CONSTANT");
        GlDebug.setup(34167, "GL13.GL_PRIMARY_COLOR");
        GlDebug.setup(34168, "GL13.GL_PREVIOUS");
        GlDebug.setup(34478, "GL13.GL_DOT3_RGB");
        GlDebug.setup(34479, "GL13.GL_DOT3_RGBA");
        GlDebug.setup(33069, "GL13.GL_CLAMP_TO_BORDER");
        GlDebug.setup(33169, "GL14.GL_GENERATE_MIPMAP");
        GlDebug.setup(33170, "GL14.GL_GENERATE_MIPMAP_HINT");
        GlDebug.setup(33189, "GL14.GL_DEPTH_COMPONENT16");
        GlDebug.setup(33190, "GL14.GL_DEPTH_COMPONENT24");
        GlDebug.setup(33191, "GL14.GL_DEPTH_COMPONENT32");
        GlDebug.setup(34890, "GL14.GL_TEXTURE_DEPTH_SIZE");
        GlDebug.setup(34891, "GL14.GL_DEPTH_TEXTURE_MODE");
        GlDebug.setup(34892, "GL14.GL_TEXTURE_COMPARE_MODE");
        GlDebug.setup(34893, "GL14.GL_TEXTURE_COMPARE_FUNC");
        GlDebug.setup(34894, "GL14.GL_COMPARE_R_TO_TEXTURE");
        GlDebug.setup(33872, "GL14.GL_FOG_COORDINATE_SOURCE");
        GlDebug.setup(33873, "GL14.GL_FOG_COORDINATE");
        GlDebug.setup(33874, "GL14.GL_FRAGMENT_DEPTH");
        GlDebug.setup(33875, "GL14.GL_CURRENT_FOG_COORDINATE");
        GlDebug.setup(33876, "GL14.GL_FOG_COORDINATE_ARRAY_TYPE");
        GlDebug.setup(33877, "GL14.GL_FOG_COORDINATE_ARRAY_STRIDE");
        GlDebug.setup(33878, "GL14.GL_FOG_COORDINATE_ARRAY_POINTER");
        GlDebug.setup(33879, "GL14.GL_FOG_COORDINATE_ARRAY");
        GlDebug.setup(33062, "GL14.GL_POINT_SIZE_MIN");
        GlDebug.setup(33063, "GL14.GL_POINT_SIZE_MAX");
        GlDebug.setup(33064, "GL14.GL_POINT_FADE_THRESHOLD_SIZE");
        GlDebug.setup(33065, "GL14.GL_POINT_DISTANCE_ATTENUATION");
        GlDebug.setup(33880, "GL14.GL_COLOR_SUM");
        GlDebug.setup(33881, "GL14.GL_CURRENT_SECONDARY_COLOR");
        GlDebug.setup(33882, "GL14.GL_SECONDARY_COLOR_ARRAY_SIZE");
        GlDebug.setup(33883, "GL14.GL_SECONDARY_COLOR_ARRAY_TYPE");
        GlDebug.setup(33884, "GL14.GL_SECONDARY_COLOR_ARRAY_STRIDE");
        GlDebug.setup(33885, "GL14.GL_SECONDARY_COLOR_ARRAY_POINTER");
        GlDebug.setup(33886, "GL14.GL_SECONDARY_COLOR_ARRAY");
        GlDebug.setup(32968, "GL14.GL_BLEND_DST_RGB");
        GlDebug.setup(32969, "GL14.GL_BLEND_SRC_RGB");
        GlDebug.setup(32970, "GL14.GL_BLEND_DST_ALPHA");
        GlDebug.setup(32971, "GL14.GL_BLEND_SRC_ALPHA");
        GlDebug.setup(34055, "GL14.GL_INCR_WRAP");
        GlDebug.setup(34056, "GL14.GL_DECR_WRAP");
        GlDebug.setup(34048, "GL14.GL_TEXTURE_FILTER_CONTROL");
        GlDebug.setup(34049, "GL14.GL_TEXTURE_LOD_BIAS");
        GlDebug.setup(34045, "GL14.GL_MAX_TEXTURE_LOD_BIAS");
        GlDebug.setup(33648, "GL14.GL_MIRRORED_REPEAT");
        GlDebug.setup(32773, "ARBImaging.GL_BLEND_COLOR");
        GlDebug.setup(32777, "ARBImaging.GL_BLEND_EQUATION");
        GlDebug.setup(32774, "GL14.GL_FUNC_ADD");
        GlDebug.setup(32778, "GL14.GL_FUNC_SUBTRACT");
        GlDebug.setup(32779, "GL14.GL_FUNC_REVERSE_SUBTRACT");
        GlDebug.setup(32775, "GL14.GL_MIN");
        GlDebug.setup(32776, "GL14.GL_MAX");
        GlDebug.setup(34962, "GL15.GL_ARRAY_BUFFER");
        GlDebug.setup(34963, "GL15.GL_ELEMENT_ARRAY_BUFFER");
        GlDebug.setup(34964, "GL15.GL_ARRAY_BUFFER_BINDING");
        GlDebug.setup(34965, "GL15.GL_ELEMENT_ARRAY_BUFFER_BINDING");
        GlDebug.setup(34966, "GL15.GL_VERTEX_ARRAY_BUFFER_BINDING");
        GlDebug.setup(34967, "GL15.GL_NORMAL_ARRAY_BUFFER_BINDING");
        GlDebug.setup(34968, "GL15.GL_COLOR_ARRAY_BUFFER_BINDING");
        GlDebug.setup(34969, "GL15.GL_INDEX_ARRAY_BUFFER_BINDING");
        GlDebug.setup(34970, "GL15.GL_TEXTURE_COORD_ARRAY_BUFFER_BINDING");
        GlDebug.setup(34971, "GL15.GL_EDGE_FLAG_ARRAY_BUFFER_BINDING");
        GlDebug.setup(34972, "GL15.GL_SECONDARY_COLOR_ARRAY_BUFFER_BINDING");
        GlDebug.setup(34973, "GL15.GL_FOG_COORDINATE_ARRAY_BUFFER_BINDING");
        GlDebug.setup(34974, "GL15.GL_WEIGHT_ARRAY_BUFFER_BINDING");
        GlDebug.setup(34975, "GL15.GL_VERTEX_ATTRIB_ARRAY_BUFFER_BINDING");
        GlDebug.setup(35040, "GL15.GL_STREAM_DRAW");
        GlDebug.setup(35041, "GL15.GL_STREAM_READ");
        GlDebug.setup(35042, "GL15.GL_STREAM_COPY");
        GlDebug.setup(35044, "GL15.GL_STATIC_DRAW");
        GlDebug.setup(35045, "GL15.GL_STATIC_READ");
        GlDebug.setup(35046, "GL15.GL_STATIC_COPY");
        GlDebug.setup(35048, "GL15.GL_DYNAMIC_DRAW");
        GlDebug.setup(35049, "GL15.GL_DYNAMIC_READ");
        GlDebug.setup(35050, "GL15.GL_DYNAMIC_COPY");
        GlDebug.setup(35000, "GL15.GL_READ_ONLY");
        GlDebug.setup(35001, "GL15.GL_WRITE_ONLY");
        GlDebug.setup(35002, "GL15.GL_READ_WRITE");
        GlDebug.setup(34660, "GL15.GL_BUFFER_SIZE");
        GlDebug.setup(34661, "GL15.GL_BUFFER_USAGE");
        GlDebug.setup(35003, "GL15.GL_BUFFER_ACCESS");
        GlDebug.setup(35004, "GL15.GL_BUFFER_MAPPED");
        GlDebug.setup(35005, "GL15.GL_BUFFER_MAP_POINTER");
        GlDebug.setup(34138, "NVFogDistance.GL_FOG_DISTANCE_MODE_NV");
        GlDebug.setup(34139, "NVFogDistance.GL_EYE_RADIAL_NV");
        GlDebug.setup(34140, "NVFogDistance.GL_EYE_PLANE_ABSOLUTE_NV");
        SAVED_STATES = Maps.newHashMap();
    }
}

