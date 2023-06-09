/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.gametest.framework;

import net.minecraft.Util;
import net.minecraft.gametest.framework.GameTestInfo;
import net.minecraft.gametest.framework.TestReporter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LogTestReporter
implements TestReporter {
    private static final Logger LOGGER = LogManager.getLogger();

    @Override
    public void onTestFailed(GameTestInfo gameTestInfo) {
        if (gameTestInfo.isRequired()) {
            LOGGER.error(gameTestInfo.getTestName() + " failed! " + Util.describeError(gameTestInfo.getError()));
        } else {
            LOGGER.warn("(optional) " + gameTestInfo.getTestName() + " failed. " + Util.describeError(gameTestInfo.getError()));
        }
    }
}

