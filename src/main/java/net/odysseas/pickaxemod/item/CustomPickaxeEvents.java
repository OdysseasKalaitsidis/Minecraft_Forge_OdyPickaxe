package net.odysseas.pickaxemod.item;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.HashMap;
import java.util.Map;

public class CustomPickaxeEvents {

    private static final int MAX_ENERGY_TICKS = 2400; // 1 min (60s * 20 ticks)
    private static final int COOLDOWN_TICKS = 300;    // 5 sec (5s * 20 ticks)

    // Map για να αποθηκεύουμε την κατάσταση (state) του κάθε παίκτη
    private final Map<Player, PlayerState> playerStates = new HashMap<>();


    private boolean isHoldingCustomPickaxe(Player player) {
        if (player == null) return false;
        return (player.getMainHandItem().getItem() instanceof CustomPickaxe) ||
                (player.getOffhandItem().getItem() instanceof CustomPickaxe);
    }

    @SubscribeEvent
    public void onAttackEntity(AttackEntityEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        if (!(event.getTarget() instanceof LivingEntity target)) return;

        // Έλεγχος: Αν ο παίκτης δεν κρατάει στο χέρι το δικό μας pickaxe, σταματάμε
        if (!isHoldingCustomPickaxe(player)) return;

        // Παίρνουμε/φτιάχνουμε την κατάσταση ενέργειας για τον παίκτη
        PlayerState state = playerStates.computeIfAbsent(player, p -> new PlayerState(MAX_ENERGY_TICKS));

        if (state.energy <= 0) {
            // Αν έχει μηδενιστεί η ενέργεια, δεν κάνουμε τίποτα
            return;
        }

        // Παράδειγμα υπολογισμού ζημιάς με βάση το ποσοστό ενέργειας
        float energyPercentage = state.energy / (float) MAX_ENERGY_TICKS;
        float baseDamage = 5.0F;
        float finalDamage = baseDamage * energyPercentage;

        // Κάνουμε “apply” τη ζημιά στον στόχο
        target.hurt(player.damageSources().playerAttack(player), finalDamage);

        // Μειώνουμε την ενέργεια του παίκτη
        state.energy = Math.max(state.energy - 20, 0);

        // Παράδειγμα κλήσης ενός sound manager
        SoundManager.playEnergyUseSound(player, energyPercentage);
    }

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event) {
        // Εκτελούμε λογική μόνο στο END phase
        if (event.phase != TickEvent.Phase.END) return;

        Player player = event.player;

        // Αν ΔΕΝ κρατάει το pickaxe, αφαιρούμε το boss bar (ή ό,τι άλλο)
        // και επιστρέφουμε χωρίς να κάνουμε άλλη λογική.
        if (!isHoldingCustomPickaxe(player)) {
            BossBarManager.removeBossBar(player, playerStates);
            return;
        }

        // Αν κρατάει το pickaxe, χειριζόμαστε την ενέργεια.
        EnergyManager.handleEnergy(player, playerStates);
    }
}
