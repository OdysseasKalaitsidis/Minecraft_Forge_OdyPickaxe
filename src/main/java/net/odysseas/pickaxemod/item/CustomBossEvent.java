package net.odysseas.pickaxemod.item;

import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBossEventPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.BossEvent;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class CustomBossEvent extends BossEvent {

    private final Set<ServerPlayer> players = new HashSet<>();

    public CustomBossEvent(Component name, BossBarColor color, BossBarOverlay overlay) {
        super(UUID.randomUUID(), name, color, overlay); // Generate a unique UUID for the boss bar
    }

    // Add a player to the boss bar
    public void addPlayer(ServerPlayer player) {
        if (players.add(player)) {
            // Send a packet to show the boss bar on the client
            player.connection.send(ClientboundBossEventPacket.createAddPacket(this));
            System.out.println("Added player to CustomBossEvent: " + player.getName().getString());
        }
    }

    // Remove a player from the boss bar
    public void removePlayer(ServerPlayer player) {
        if (players.remove(player)) {
            // Send a packet to remove the boss bar from the client
            player.connection.send(ClientboundBossEventPacket.createRemovePacket(this.getId()));
            System.out.println("Removed player from CustomBossEvent: " + player.getName().getString());
        }
    }

    @Override
    public void setName(Component name) {
        super.setName(name);
        for (ServerPlayer player : players) {
            // Send a packet to update the boss bar's name on the client
            player.connection.send(ClientboundBossEventPacket.createUpdateNamePacket(this));
        }
    }

    @Override
    public void setProgress(float progress) {
        super.setProgress(progress);
        for (ServerPlayer player : players) {
            // Send a packet to update the boss bar's progress on the client
            player.connection.send(ClientboundBossEventPacket.createUpdateProgressPacket(this));
        }
    }

    // Get the set of players tracking this boss bar
    public Set<ServerPlayer> getPlayers() {
        return players;
    }
}
