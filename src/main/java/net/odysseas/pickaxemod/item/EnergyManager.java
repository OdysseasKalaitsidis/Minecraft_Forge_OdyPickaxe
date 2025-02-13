package net.odysseas.pickaxemod.item;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.odysseas.pickaxemod.registry.ModItems;

import java.util.HashMap;
import java.util.Map;

public class EnergyManager {
    private static final Map<Player, Boolean> wasHoldingPickaxe = new HashMap<>();

    public static void handleEnergy(Player player, Map<Player, PlayerState> playerStates) {
        boolean isHoldingPickaxe = isHoldingPickaxe(player);
        boolean wasHolding = wasHoldingPickaxe.getOrDefault(player, false);

        if (!isHoldingPickaxe) {
            wasHoldingPickaxe.put(player, false); // Reset if player is not holding pickaxe
            return;
        }

        PlayerState state = playerStates.computeIfAbsent(player, p -> new PlayerState(1400)); // Energy lasts 70 sec (1400 ticks)

        // Play energy sound immediately when first holding the pickaxe
        if (!wasHolding) {
            state.energySoundPlayed = false; // Reset sound trigger
            SoundManager.playEnergyUseSound(player, 1.0f);
        }

        wasHoldingPickaxe.put(player, true);

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
            SoundManager.playEnergyUseSound(player, state.energy / 1400f);
            state.energySoundPlayed = true;
        }

        TimeEffectManager.applyTimeSlow(player);
        BossBarManager.updateBossBar(player, state, state.energy / 1400f, "Energy Remaining");

        if (state.energy == 0) {
            state.energySoundPlayed = false; // Reset energy sound trigger when energy is depleted
        }
    }

    private static void startCooldown(Player player, PlayerState state) {
        // 5 seconds = 100 ticks of cooldown
        state.energy = -100;

        // Play the 5-second cooldown sound exactly once at the start
        if (!state.cooldownSoundPlayed) {
            SoundManager.playCooldownSound(player);
            state.cooldownSoundPlayed = true;
        }

        BossBarManager.updateBossBar(player, state, 0.0f, "Cooldown 5 seconds");
    }

    private static void handleCooldown(Player player, PlayerState state) {
        // Increment energy from negative back up to 0 over 100 ticks (5 seconds)
        state.energy++;

        // Show progress in the boss bar: from -100 (0%) up to 0 (100%)
        float progress = (100 + state.energy) / 100f;
        BossBarManager.updateBossBar(player, state, progress, "Cooldown Progress");

        // Once energy reaches 0, cooldown is finished
        if (state.energy == 0) {
            // Restore energy to full
            state.energy = 1400; // 70 seconds
            state.cooldownSoundPlayed = false;
            state.energySoundPlayed   = false;

            // Play "restored" sound (optional)
            SoundManager.playEnergyUseSound(player, 1.0f);

            // Update the boss bar to 100% energy
            BossBarManager.updateBossBar(player, state, 1.0f, "Energy Restored");
        }
    }





    private static boolean isHoldingPickaxe(Player player) {
        ItemStack mainHandItem = player.getMainHandItem();
        ItemStack offHandItem = player.getOffhandItem();

        // Check if the player is holding the custom pickaxe in either hand
        return mainHandItem.getItem() == ModItems.ODY_PICKAXE.get() || offHandItem.getItem() == ModItems.ODY_PICKAXE.get();
    }
}
