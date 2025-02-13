package net.odysseas.pickaxemod.item;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.BossEvent.BossBarColor;
import net.minecraft.world.BossEvent.BossBarOverlay;
import net.minecraft.world.entity.player.Player;

import java.util.Map;

public class BossBarManager {

    // Ενημερώνει τη γραμμή boss bar για έναν παίκτη
    public static void updateBossBar(Player player, PlayerState state, float progress, String title) {
        if (!(player instanceof ServerPlayer serverPlayer)) return;

        // Αν η γραμμή boss bar δεν έχει δημιουργηθεί ακόμα, τη δημιουργεί
        if (state.bossBar == null) {
            state.bossBar = new CustomBossEvent(Component.literal(title), BossBarColor.RED, BossBarOverlay.PROGRESS);
            state.bossBar.addPlayer(serverPlayer);
        }

        // Ορίζει το όνομα και την πρόοδο της γραμμής boss bar
        state.bossBar.setName(Component.literal(title));
        state.bossBar.setProgress(progress);
    }

    // Αφαιρεί τη γραμμή boss bar για έναν παίκτη
    public static void removeBossBar(Player player, Map<Player, PlayerState> playerStates) {
        if (!(player instanceof ServerPlayer serverPlayer)) return;
        PlayerState state = playerStates.remove(player);

        // Αν υπάρχει ενεργή γραμμή boss bar, την αφαιρεί από τον παίκτη
        if (state != null && state.bossBar != null) {
            state.bossBar.removePlayer(serverPlayer);
        }
    }
}
