package net.odysseas.pickaxemod.item;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.odysseas.pickaxemod.registry.ModItems;

import java.util.HashMap;
import java.util.Map;

public class EnergyManager {
    // Χάρτης που κρατάει πληροφορία για το αν ο παίκτης κρατούσε την αξίνα στην προηγούμενη στιγμή
    private static final Map<Player, Boolean> wasHoldingPickaxe = new HashMap<>();

     //Διαχειρίζεται την ενέργεια του παίκτη όταν κρατάει την προσαρμοσμένη αξίνα
    public static void handleEnergy(Player player, Map<Player, PlayerState> playerStates) {
        boolean isHoldingPickaxe = isHoldingPickaxe(player); // Ελέγχει αν ο παίκτης κρατάει το pickaxe
        boolean wasHolding = wasHoldingPickaxe.getOrDefault(player, false); // Βρίσκει αν ο παίκτης το κρατούσε προηγουμένως

        if (!isHoldingPickaxe) {
            // Αν δεν κρατάει πλέον το pickaxe, επαναφέρουμε την κατάστασή του
            wasHoldingPickaxe.put(player, false);
            return;
        }

        // Αν δεν υπάρχει ήδη, δημιουργούμε νέο PlayerState για τον παίκτη με ενέργεια 1400 (70 δευτερόλεπτα)
        PlayerState state = playerStates.computeIfAbsent(player, p -> new PlayerState(1400));

        // Αν ο παίκτης μόλις έπιασε το pickaxe, παίζει ήχο ενεργοποίησης
        if (!wasHolding) {
            state.energySoundPlayed = false; // Επαναφορά για να επιτρέψουμε τον ήχο
            SoundManager.playEnergyUseSound(player, 1.0f); // Παίζει ήχο με μέγιστη ένταση
        }

        wasHoldingPickaxe.put(player, true); // Ενημερώνουμε ότι ο παίκτης κρατάει το pickaxe

        if (state.energy > 0) {
            reduceEnergy(player, state); // Αν έχει ενέργεια, μειώνεται σταδιακά
        } else if (state.energy == 0) {
            startCooldown(player, state); // Αν μηδενιστεί, ξεκινάει cooldown
        } else {
            handleCooldown(player, state); // Αν είναι σε cooldown, προχωρά η αντίστροφη μέτρηση
        }
    }


    //Μειώνει σταδιακά την ενέργεια του παίκτη όσο κρατάει την αξίνα

    private static void reduceEnergy(Player player, PlayerState state) {
        state.energy--; // Μείωση ενέργειας κατά 1 ανά tick

        // Παίζει ήχο όταν αρχίζει να καταναλώνεται ενέργεια
        if (!state.energySoundPlayed) {
            SoundManager.playEnergyUseSound(player, state.energy / 1400f);
            state.energySoundPlayed = true;
        }

        // Επιβράδυνση του χρόνου ενώ ο παίκτης κρατάει την αξίνα
        TimeEffectManager.applyTimeSlow(player);

        // Ενημέρωση της Boss Bar με το ποσοστό ενέργειας
        BossBarManager.updateBossBar(player, state, state.energy / 1400f, "Ενέργεια που απομένει");

        // Αν η ενέργεια φτάσει στο 0, σηματοδοτούμε ότι η αξίνα χρειάζεται cooldown
        if (state.energy == 0) {
            state.energySoundPlayed = false; // Επαναφορά ώστε να παίξει άλλος ήχος μετά
        }
    }

    //Ξεκινάει το cooldown των 5 δευτερολέπτων όταν η ενέργεια εξαντληθεί
    private static void startCooldown(Player player, PlayerState state) {
        state.energy = -100; // 100 ticks cooldown (5 δευτερόλεπτα)

        // Παίζει ήχο cooldown αν δεν έχει παιχτεί ήδη
        if (!state.cooldownSoundPlayed) {
            SoundManager.playCooldownSound(player);
            state.cooldownSoundPlayed = true;
        }

        // Ενημέρωση της Boss Bar με την αναμονή cooldown
        BossBarManager.updateBossBar(player, state, 0.0f, "Χρόνος αναμονής 5 δευτερόλεπτα");
    }

    //Χειρίζεται το cooldown και επαναφέρει σταδιακά την ενέργεια
    private static void handleCooldown(Player player, PlayerState state) {
        state.energy++; // Αργή επαναφόρτιση του timer (5 δευτερόλεπτα)

        // Υπολογισμός προόδου cooldown (-100 έως 0 μετατρέπεται σε 0% - 100%)
        float progress = (100 + state.energy) / 100f;
        BossBarManager.updateBossBar(player, state, progress, "Πρόοδος αναμονής");

        // Όταν η ενέργεια φτάσει στο 0, επαναφέρεται η πλήρης ενέργεια
        if (state.energy == 0) {
            state.energy = 1400; // Επιστροφή στη μέγιστη ενέργεια
            state.cooldownSoundPlayed = false;
            state.energySoundPlayed = false;

            // Παίζει ήχο όταν η ενέργεια επανέλθει πλήρως
            SoundManager.playEnergyUseSound(player, 1.0f);

            // Ενημέρωση της Boss Bar στην πλήρη ενέργεια
            BossBarManager.updateBossBar(player, state, 1.0f, "Ενέργεια επανήλθε");
        }
    }


     //Ελέγχει αν ο παίκτης κρατάει την ειδική αξίνα στο χέρι του

    private static boolean isHoldingPickaxe(Player player) {
        ItemStack mainHandItem = player.getMainHandItem();
        ItemStack offHandItem = player.getOffhandItem();

        // Επιστρέφει true αν η αξίνα βρίσκεται είτε στο κύριο είτε στο βοηθητικό χέρι
        return mainHandItem.getItem() == ModItems.ody_pickaxe.get() || offHandItem.getItem() == ModItems.ody_pickaxe.get();
    }
}
