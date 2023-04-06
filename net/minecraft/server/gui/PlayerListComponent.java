/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.mojang.authlib.GameProfile
 */
package net.minecraft.server.gui;

import com.mojang.authlib.GameProfile;
import java.util.List;
import java.util.Vector;
import javax.swing.JList;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;

public class PlayerListComponent
extends JList<String> {
    private final MinecraftServer server;
    private int tickCount;

    public PlayerListComponent(MinecraftServer minecraftServer) {
        this.server = minecraftServer;
        minecraftServer.addTickable(this::tick);
    }

    public void tick() {
        if (this.tickCount++ % 20 == 0) {
            Vector<String> vector = new Vector<String>();
            for (int i = 0; i < this.server.getPlayerList().getPlayers().size(); ++i) {
                vector.add(this.server.getPlayerList().getPlayers().get(i).getGameProfile().getName());
            }
            this.setListData(vector);
        }
    }
}

