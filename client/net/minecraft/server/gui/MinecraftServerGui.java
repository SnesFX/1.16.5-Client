/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.mojang.util.QueueLogAppender
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.server.gui;

import com.google.common.collect.Lists;
import com.mojang.util.QueueLogAppender;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.LayoutManager;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.Collection;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JViewport;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import net.minecraft.DefaultUncaughtExceptionHandler;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.server.gui.PlayerListComponent;
import net.minecraft.server.gui.StatsComponent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MinecraftServerGui
extends JComponent {
    private static final Font MONOSPACED = new Font("Monospaced", 0, 12);
    private static final Logger LOGGER = LogManager.getLogger();
    private final DedicatedServer server;
    private Thread logAppenderThread;
    private final Collection<Runnable> finalizers = Lists.newArrayList();
    private final AtomicBoolean isClosing = new AtomicBoolean();

    public static MinecraftServerGui showFrameFor(final DedicatedServer dedicatedServer) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        }
        catch (Exception exception) {
            // empty catch block
        }
        final JFrame jFrame = new JFrame("Minecraft server");
        final MinecraftServerGui minecraftServerGui = new MinecraftServerGui(dedicatedServer);
        jFrame.setDefaultCloseOperation(2);
        jFrame.add(minecraftServerGui);
        jFrame.pack();
        jFrame.setLocationRelativeTo(null);
        jFrame.setVisible(true);
        jFrame.addWindowListener(new WindowAdapter(){

            @Override
            public void windowClosing(WindowEvent windowEvent) {
                if (!minecraftServerGui.isClosing.getAndSet(true)) {
                    jFrame.setTitle("Minecraft server - shutting down!");
                    dedicatedServer.halt(true);
                    minecraftServerGui.runFinalizers();
                }
            }
        });
        minecraftServerGui.addFinalizer(jFrame::dispose);
        minecraftServerGui.start();
        return minecraftServerGui;
    }

    private MinecraftServerGui(DedicatedServer dedicatedServer) {
        this.server = dedicatedServer;
        this.setPreferredSize(new Dimension(854, 480));
        this.setLayout(new BorderLayout());
        try {
            this.add((Component)this.buildChatPanel(), "Center");
            this.add((Component)this.buildInfoPanel(), "West");
        }
        catch (Exception exception) {
            LOGGER.error("Couldn't build server GUI", (Throwable)exception);
        }
    }

    public void addFinalizer(Runnable runnable) {
        this.finalizers.add(runnable);
    }

    private JComponent buildInfoPanel() {
        JPanel jPanel = new JPanel(new BorderLayout());
        StatsComponent statsComponent = new StatsComponent(this.server);
        this.finalizers.add(statsComponent::close);
        jPanel.add((Component)statsComponent, "North");
        jPanel.add((Component)this.buildPlayerPanel(), "Center");
        jPanel.setBorder(new TitledBorder(new EtchedBorder(), "Stats"));
        return jPanel;
    }

    private JComponent buildPlayerPanel() {
        PlayerListComponent playerListComponent = new PlayerListComponent(this.server);
        JScrollPane jScrollPane = new JScrollPane(playerListComponent, 22, 30);
        jScrollPane.setBorder(new TitledBorder(new EtchedBorder(), "Players"));
        return jScrollPane;
    }

    private JComponent buildChatPanel() {
        JPanel jPanel = new JPanel(new BorderLayout());
        JTextArea jTextArea = new JTextArea();
        JScrollPane jScrollPane = new JScrollPane(jTextArea, 22, 30);
        jTextArea.setEditable(false);
        jTextArea.setFont(MONOSPACED);
        JTextField jTextField = new JTextField();
        jTextField.addActionListener(actionEvent -> {
            String string = jTextField.getText().trim();
            if (!string.isEmpty()) {
                this.server.handleConsoleInput(string, this.server.createCommandSourceStack());
            }
            jTextField.setText("");
        });
        jTextArea.addFocusListener(new FocusAdapter(){

            @Override
            public void focusGained(FocusEvent focusEvent) {
            }
        });
        jPanel.add((Component)jScrollPane, "Center");
        jPanel.add((Component)jTextField, "South");
        jPanel.setBorder(new TitledBorder(new EtchedBorder(), "Log and chat"));
        this.logAppenderThread = new Thread(() -> {
            String string;
            while ((string = QueueLogAppender.getNextLogEvent((String)"ServerGuiConsole")) != null) {
                this.print(jTextArea, jScrollPane, string);
            }
        });
        this.logAppenderThread.setUncaughtExceptionHandler(new DefaultUncaughtExceptionHandler(LOGGER));
        this.logAppenderThread.setDaemon(true);
        return jPanel;
    }

    public void start() {
        this.logAppenderThread.start();
    }

    public void close() {
        if (!this.isClosing.getAndSet(true)) {
            this.runFinalizers();
        }
    }

    private void runFinalizers() {
        this.finalizers.forEach(Runnable::run);
    }

    public void print(JTextArea jTextArea, JScrollPane jScrollPane, String string) {
        if (!SwingUtilities.isEventDispatchThread()) {
            SwingUtilities.invokeLater(() -> this.print(jTextArea, jScrollPane, string));
            return;
        }
        Document document = jTextArea.getDocument();
        JScrollBar jScrollBar = jScrollPane.getVerticalScrollBar();
        boolean bl = false;
        if (jScrollPane.getViewport().getView() == jTextArea) {
            bl = (double)jScrollBar.getValue() + jScrollBar.getSize().getHeight() + (double)(MONOSPACED.getSize() * 4) > (double)jScrollBar.getMaximum();
        }
        try {
            document.insertString(document.getLength(), string, null);
        }
        catch (BadLocationException badLocationException) {
            // empty catch block
        }
        if (bl) {
            jScrollBar.setValue(Integer.MAX_VALUE);
        }
    }

}

