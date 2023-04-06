/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.gametest.framework;

import java.util.Iterator;
import java.util.List;
import net.minecraft.gametest.framework.GameTestAssertException;
import net.minecraft.gametest.framework.GameTestEvent;
import net.minecraft.gametest.framework.GameTestInfo;

public class GameTestSequence {
    private final GameTestInfo parent;
    private final List<GameTestEvent> events;
    private long lastTick;

    public void tickAndContinue(long l) {
        try {
            this.tick(l);
        }
        catch (Exception exception) {
            // empty catch block
        }
    }

    public void tickAndFailIfNotComplete(long l) {
        try {
            this.tick(l);
        }
        catch (Exception exception) {
            this.parent.fail(exception);
        }
    }

    private void tick(long l) {
        Iterator<GameTestEvent> iterator = this.events.iterator();
        while (iterator.hasNext()) {
            GameTestEvent gameTestEvent = iterator.next();
            gameTestEvent.assertion.run();
            iterator.remove();
            long l2 = l - this.lastTick;
            long l3 = this.lastTick;
            this.lastTick = l;
            if (gameTestEvent.expectedDelay == null || gameTestEvent.expectedDelay == l2) continue;
            this.parent.fail(new GameTestAssertException("Succeeded in invalid tick: expected " + (l3 + gameTestEvent.expectedDelay) + ", but current tick is " + l));
            break;
        }
    }
}

