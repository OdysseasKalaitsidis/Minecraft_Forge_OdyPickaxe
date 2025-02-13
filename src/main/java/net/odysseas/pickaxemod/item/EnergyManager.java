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
            wasHoldingPickaxe.put(player, false); // Επαναφορά αν ο παίκτης δεν κρατάει την αξίνα
            return;
        }

        PlayerState state = playerStates.computeIfAbsent(player, p -> new PlayerState(1400)); // Η ενέργεια διαρκεί 70 δευτερόλεπτα (1400 ticks)

        // Αναπαραγωγή ήχου ενέργειας μόλις ο παίκτης πιάσει την αξίνα για πρώτη φορά
        if (!wasHolding) {
            state.energySoundPlayed = false; // Επαναφορά του ήχου
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

        if (!state.energySoundPlayed) { // Αναπαραγωγή ήχου όταν ξεκινά η μείωση ενέργειας
            SoundManager.playEnergyUseSound(player, state.energy / 1400f);
            state.energySoundPlayed = true;
        }

        TimeEffectManager.applyTimeSlow(player);
        BossBarManager.updateBossBar(player, state, state.energy / 1400f, "Ενέργεια που απομένει");

        if (state.energy == 0) {
            state.energySoundPlayed = false; // Επαναφορά του ήχου όταν εξαντληθεί η ενέργεια
        }
    }

    private static void startCooldown(Player player, PlayerState state) {
        // 5 δευτερόλεπτα = 100 ticks ψύξης
        state.energy = -100;

        // Αναπαραγωγή ήχου cooldown μόνο μία φορά στην αρχή
        if (!state.cooldownSoundPlayed) {
            SoundManager.playCooldownSound(player);
            state.cooldownSoundPlayed = true;
        }

        BossBarManager.updateBossBar(player, state, 0.0f, "Χρόνος αναμονής 5 δευτερόλεπτα");
    }

    private static void handleCooldown(Player player, PlayerState state) {
        // Αύξηση ενέργειας από αρνητικό πίσω στο 0 σε 100 ticks (5 δευτερόλεπτα)
        state.energy++;

        // Εμφάνιση προόδου στη γραμμή boss: από -100 (0%) έως 0 (100%)
        float progress = (100 + state.energy) / 100f;
        BossBarManager.updateBossBar(player, state, progress, "Πρόοδος αναμονής");

        // Όταν η ενέργεια φτάσει στο 0, το cooldown τελειώνει
        if (state.energy == 0) {
            // Επαναφορά της ενέργειας στο μέγιστο
            state.energy = 1400; // 70 δευτερόλεπτα
            state.cooldownSoundPlayed = false;
            state.energySoundPlayed = false;

            // Αναπαραγωγή ήχου "επαναφοράς" (προαιρετικό)
            SoundManager.playEnergyUseSound(player, 1.0f);

            // Ενημέρωση της γραμμής boss στο 100% ενέργεια
            BossBarManager.updateBossBar(player, state, 1.0f, "Ενέργεια επανήλθε");
        }
    }

    private static boolean isHoldingPickaxe(Player player) {
        ItemStack mainHandItem = player.getMainHandItem();
        ItemStack offHandItem = player.getOffhandItem();

        // Έλεγχος αν ο παίκτης κρατάει την προσαρμοσμένη αξίνα σε κάποιο από τα δύο χέρια
        return mainHandItem.getItem() == ModItems.ody_pickaxe.get() || offHandItem.getItem() == ModItems.ody_pickaxe.get();
    }
}