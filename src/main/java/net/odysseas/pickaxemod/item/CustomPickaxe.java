package net.odysseas.pickaxemod.item;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.BossEvent;
import net.minecraft.world.BossEvent.BossBarColor;
import net.minecraft.world.BossEvent.BossBarOverlay;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.PickaxeItem;
import net.minecraft.world.item.Tier;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CustomPickaxe extends PickaxeItem {

    private static final int MAX_ENERGY_TICKS = 2400; // 1 minute (60 seconds * 20 ticks)
    private static final int COOLDOWN_TICKS = 300; // 5 seconds (5 seconds * 20 ticks)
    private final Map<Player, Integer> energyMap = new HashMap<>();
    private final Map<Player, CustomBossEvent> bossBars = new HashMap<>();

    public CustomPickaxe(Tier tier, Properties properties) {
        super(tier, properties);
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event) {
        Player player = event.player;

        if (player.getMainHandItem().getItem() instanceof CustomPickaxe) {
            handleEnergy(player);
        } else {
            removeBossBar(player); // Αφαιρούμε το boss bar αν ο παίκτης δεν κρατά την αξίνα
        }
    }


    private void handleEnergy(Player player) {
        energyMap.putIfAbsent(player, MAX_ENERGY_TICKS); // Αρχικοποιούμε την ενέργεια
        int energy = energyMap.get(player);

        if (energy > 0) {
            reduceEnergy(player, energy); // Διαχειριζόμαστε τη μείωση ενέργειας
        } else if (energy == 0) {
            startCooldown(player); // Ξεκινάμε το cooldown
        } else {
            handleCooldown(player, energy); // Χειριζόμαστε την κατάσταση cooldown
        }
    }

    private void reduceEnergy(Player player, int energy) {
        energyMap.put(player, energy - 1); // Μειώνουμε την ενέργεια
        applyTimeSlow(player); // Εφαρμόζουμε slowdown στα κοντινά πλάσματα
        updateBossBar(player, (energy - 1) / (float) MAX_ENERGY_TICKS, "Energy Remaining");
    }

    private void startCooldown(Player player) {
        energyMap.put(player, -COOLDOWN_TICKS); // Θέτουμε την αρχική τιμή cooldown
        updateBossBar(player, 0.0f, "Cooldown (5 seconds)");
    }

    private void handleCooldown(Player player, int cooldown) {
        energyMap.put(player, cooldown + 1); // Μειώνουμε το χρόνο cooldown

        if (cooldown + 1 == 0) { // Όταν τελειώσει το cooldown
            energyMap.put(player, MAX_ENERGY_TICKS); // Επαναφέρουμε την ενέργεια
            updateBossBar(player, 1.0f, "Energy Restored");
            System.out.println("Energy restored for: " + player.getName().getString());
        }
    }



    private void applyTimeSlow(Player player) {
        double range = 5.0; // Ακτίνα 5 blocks
        List<LivingEntity> nearbyEntities = getNearbyEntities(player, range);

        for (LivingEntity entity : nearbyEntities) {
            applySlowEffect(entity);
        }
    }

    private List<LivingEntity> getNearbyEntities(Player player, double range) {
        return player.getCommandSenderWorld().getEntitiesOfClass(
                LivingEntity.class,
                player.getBoundingBox().inflate(range)
        );
    }

    private void applySlowEffect(LivingEntity entity) {
        entity.addEffect(new MobEffectInstance(
                MobEffects.MOVEMENT_SLOWDOWN, // Εφέ επιβράδυνσης
                20,                          // Διάρκεια
                1,                           // Επίπεδο
                true,                        // Χωρίς particles
                false                        // Μην εμφανίζεις particles
        ));
    }



    private void updateBossBar(Player player, float progress, String title) {
        if (!(player instanceof ServerPlayer serverPlayer)) return;

        CustomBossEvent bossBar = bossBars.computeIfAbsent(player, p -> createBossBar(serverPlayer, title));
        bossBar.setName(Component.literal(title)); // Ενημερώνουμε τον τίτλο
        bossBar.setProgress(progress); // Ενημερώνουμε την πρόοδο
    }

    private CustomBossEvent createBossBar(ServerPlayer player, String title) {
        CustomBossEvent newBar = new CustomBossEvent(
                Component.literal(title),
                BossBarColor.PURPLE,
                BossBarOverlay.PROGRESS
        );
        newBar.addPlayer(player);
        return newBar;
    }


    private void removeBossBar(Player player) {
        if (!(player instanceof ServerPlayer serverPlayer)) return;

        CustomBossEvent bossBar = bossBars.remove(player); // Αφαιρούμε από τον χάρτη
        if (bossBar != null) {
            bossBar.removePlayer(serverPlayer); // Αφαιρούμε τον παίκτη από το boss bar
        }
        energyMap.remove(player); // Αφαιρούμε την ενέργεια του παίκτη
    }










}
