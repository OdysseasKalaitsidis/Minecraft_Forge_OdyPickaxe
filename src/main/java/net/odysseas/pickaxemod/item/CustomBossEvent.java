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
        super(UUID.randomUUID(), name, color, overlay);
    }

    public void addPlayer(ServerPlayer player) {
        if (players.add(player)) {
            player.connection.send(ClientboundBossEventPacket.createAddPacket(this));
        }
    }

    public void removePlayer(ServerPlayer player) {
        if (players.remove(player)) {
            player.connection.send(ClientboundBossEventPacket.createRemovePacket(this.getId()));
        }
    }

    @Override
    public void setName(Component name) {
        super.setName(name);
        for (ServerPlayer player : players) {
            player.connection.send(ClientboundBossEventPacket.createUpdateNamePacket(this));
        }
    }

    @Override
    public void setProgress(float progress) {
        super.setProgress(progress);
        for (ServerPlayer player : players) {
            player.connection.send(ClientboundBossEventPacket.createUpdateProgressPacket(this));
        }
    }

    public Set<ServerPlayer> getPlayers() {
        return players;
    }
}
