package net.odysseas.pickaxemod.item;

import net.minecraft.world.entity.player.Player;

import java.util.Map;

public class EnergyManager {

    public static void handleEnergy(Player player, Map<Player, PlayerState> playerStates) {
        PlayerState state = playerStates.computeIfAbsent(player, p -> new PlayerState(2400));

        if (state.energy > 0) {
            reduceEnergy(player, state);
        } else if (state.energy == 0) {
            startCooldown(player, state);
        } else {
            handleCooldown(player, state);
        }
    }

    private static void reduceEnergy(Player player, PlayerState state) {
        state.energy--;

        if (!state.energySoundPlayed) { // Play sound when draining starts
            SoundManager.playEnergyUseSound(player, state.energy / 2400f);
            state.energySoundPlayed = true;
        }

        TimeEffectManager.applyTimeSlow(player);
        BossBarManager.updateBossBar(player, state, state.energy / 2400f, "Energy Remaining");

        if (state.energy == 0) {
            state.energySoundPlayed = false; // Reset energy sound trigger
        }
    }


    private static void startCooldown(Player player, PlayerState state) {
        state.energy = -300; // Start cooldown

        if (!state.cooldownSoundPlayed) { // Play cooldown sound only once
            SoundManager.playCooldownSound(player);
            state.cooldownSoundPlayed = true;
        }

        BossBarManager.updateBossBar(player, state, 0.0f, "Cooldown 5 seconds");
    }


    private static void handleCooldown(Player player, PlayerState state) {
        state.energy++;
        float progress = (300 + state.energy) / 300f;
        BossBarManager.updateBossBar(player, state, progress, "Cooldown Progress");

        if (state.energy == 0) {
            state.energy = 1200;
            state.cooldownSoundPlayed = false;
            BossBarManager.updateBossBar(player, state, 1.0f, "Energy Restored");
        }
    }
}
