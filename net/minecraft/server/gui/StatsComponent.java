/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.server.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;
import javax.swing.JComponent;
import javax.swing.Timer;
import net.minecraft.Util;
import net.minecraft.server.MinecraftServer;

public class StatsComponent
extends JComponent {
    private static final DecimalFormat DECIMAL_FORMAT = Util.make(new DecimalFormat("########0.000"), decimalFormat -> decimalFormat.setDecimalFormatSymbols(DecimalFormatSymbols.getInstance(Locale.ROOT)));
    private final int[] values = new int[256];
    private int vp;
    private final String[] msgs = new String[11];
    private final MinecraftServer server;
    private final Timer timer;

    public StatsComponent(MinecraftServer minecraftServer) {
        this.server = minecraftServer;
        this.setPreferredSize(new Dimension(456, 246));
        this.setMinimumSize(new Dimension(456, 246));
        this.setMaximumSize(new Dimension(456, 246));
        this.timer = new Timer(500, actionEvent -> this.tick());
        this.timer.start();
        this.setBackground(Color.BLACK);
    }

    private void tick() {
        long l = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
        this.msgs[0] = "Memory use: " + l / 1024L / 1024L + " mb (" + Runtime.getRuntime().freeMemory() * 100L / Runtime.getRuntime().maxMemory() + "% free)";
        this.msgs[1] = "Avg tick: " + DECIMAL_FORMAT.format(this.getAverage(this.server.tickTimes) * 1.0E-6) + " ms";
        this.values[this.vp++ & 255] = (int)(l * 100L / Runtime.getRuntime().maxMemory());
        this.repaint();
    }

    private double getAverage(long[] arrl) {
        long l = 0L;
        for (long l2 : arrl) {
            l += l2;
        }
        return (double)l / (double)arrl.length;
    }

    @Override
    public void paint(Graphics graphics) {
        int n;
        graphics.setColor(new Color(16777215));
        graphics.fillRect(0, 0, 456, 246);
        for (n = 0; n < 256; ++n) {
            int n2 = this.values[n + this.vp & 0xFF];
            graphics.setColor(new Color(n2 + 28 << 16));
            graphics.fillRect(n, 100 - n2, 1, n2);
        }
        graphics.setColor(Color.BLACK);
        for (n = 0; n < this.msgs.length; ++n) {
            String string = this.msgs[n];
            if (string == null) continue;
            graphics.drawString(string, 32, 116 + n * 16);
        }
    }

    public void close() {
        this.timer.stop();
    }
}

