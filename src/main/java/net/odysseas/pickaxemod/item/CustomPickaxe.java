package net.odysseas.pickaxemod.item;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.BossEvent.BossBarColor;
import net.minecraft.world.BossEvent.BossBarOverlay;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.PickaxeItem;
import net.minecraft.world.item.Tier;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.odysseas.pickaxemod.sound.ModSounds;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CustomPickaxe extends PickaxeItem {

    private static final int MAX_ENERGY_TICKS = 2400; // 1 minute (60s * 20t)
    private static final int COOLDOWN_TICKS = 300;    // 5 seconds (5s * 20t)

    private final Map<Player, PlayerState> playerStates = new HashMap<>();

    public CustomPickaxe(Tier tier, Properties properties) {
        // Pass damage & speed if you want a custom base: e.g., super(tier, 6, -2.8F, properties);
        super(tier, properties);
        MinecraftForge.EVENT_BUS.register(this);
    }

    /**
     * 1) Handle the AttackEntityEvent to scale the damage based on pickaxe energy.
     */
    @SubscribeEvent
    public void onAttackEntity(AttackEntityEvent event) {
        // Ensure we have a Player attacking a LivingEntity
        if (!(event.getEntity() instanceof Player player)) return;
        if (!(event.getTarget() instanceof LivingEntity target)) return;

        // Check if the player is holding our CustomPickaxe in main hand
        if (player.getMainHandItem().getItem() instanceof CustomPickaxe) {
            PlayerState state = playerStates.get(player);
            if (state != null && state.energy > 0) {
                // Scale damage by energy percentage
                float energyPercentage = state.energy / (float) MAX_ENERGY_TICKS;
                float baseDamage = 5.0F; // The "extra" damage you want at full energy
                float finalDamage = baseDamage * energyPercentage;

                // Apply scaled damage
                target.hurt(player.damageSources().playerAttack(player), finalDamage);

                // (Optional) reduce some energy on each hit, e.g., 20 ticks worth
                state.energy = Math.max(state.energy - 20, 0);

                // Optional: play a unique "power hit" sound if above some threshold
                if (energyPercentage > 0.8F && ModSounds.ENERGY_USE.isPresent()) {
                    player.level().playSound(
                            null,
                            player.blockPosition(),
                            ModSounds.ENERGY_USE.get(),
                            SoundSource.PLAYERS,
                            1.0F,
                            1.0F
                    );
                }
            }
        }
    }

    /**
     * 2) Handle the PlayerTickEvent to manage the pickaxe's energy mechanics (drain, cooldown, boss bar).
     */
    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            Player player = event.player;

            // If player holds the CustomPickaxe in main hand, manage energy
            if (player.getMainHandItem().getItem() instanceof CustomPickaxe) {
                handleEnergy(player);
            } else {
                // Remove the boss bar if they're no longer holding the pickaxe
                removeBossBar(player);
            }
        }
    }

    private void handleEnergy(Player player) {
        playerStates.putIfAbsent(player, new PlayerState(MAX_ENERGY_TICKS));
        PlayerState state = playerStates.get(player);

        // If above 0, keep reducing naturally
        if (state.energy > 0) {
            reduceEnergy(player, state);

            // If exactly 0, start cooldown
        } else if (state.energy == 0) {
            startCooldown(player, state);

            // If below 0, we're in the cooldown phase
        } else {
            handleCooldown(player, state);
        }
    }

    private void reduceEnergy(Player player, PlayerState state) {
        state.energy--;

        // Play energy sound once when we first start draining
        if (!state.energySoundPlayed && ModSounds.ENERGY_USE.isPresent()) {
            player.level().playSound(
                    null,
                    player.blockPosition(),
                    ModSounds.ENERGY_USE.get(),
                    SoundSource.PLAYERS,
                    1.0F,
                    1.0F
            );
            state.energySoundPlayed = true;
        }

        // Apply "time slow" effect to nearby mobs each tick
        applyTimeSlow(player);

        // Update the boss bar with the current energy
        float progress = state.energy / (float) MAX_ENERGY_TICKS;
        updateBossBar(player, progress, "Energy Remaining", BossBarColor.RED);

        // Reset the energy sound once it's fully drained
        if (state.energy == 0) {
            state.energySoundPlayed = false;
        }
    }

    private void startCooldown(Player player, PlayerState state) {
        // We store negative values to track cooldown
        state.energy = -COOLDOWN_TICKS;

        // Play cooldown sound only once
        if (!state.cooldownSoundPlayed && ModSounds.COOLDOWN_USE.isPresent()) {
            player.level().playSound(
                    null,
                    player.blockPosition(),
                    ModSounds.COOLDOWN_USE.get(),
                    SoundSource.PLAYERS,
                    1.0F,
                    1.0F
            );
            state.cooldownSoundPlayed = true;
        }

        updateBossBar(player, 0.0f, "Cooldown 5 seconds", BossBarColor.BLUE);
    }

    private void handleCooldown(Player player, PlayerState state) {
        // Increment from negative back toward 0
        state.energy++;

        float progress = (COOLDOWN_TICKS + state.energy) / (float) COOLDOWN_TICKS;
        updateBossBar(player, progress, "Cooldown Progress", BossBarColor.BLUE);

        // When cooldown finishes, restore energy
        if (state.energy == 0) {
            state.energy = MAX_ENERGY_TICKS;
            state.cooldownSoundPlayed = false;
            updateBossBar(player, 1.0f, "Energy Restored", BossBarColor.RED);
        }
    }

    /**
     * (Optional) Time-Slow effect in a radius around the player
     */
    private void applyTimeSlow(Player player) {
        double range = 5.0;
        List<LivingEntity> nearbyEntities = player.level().getEntitiesOfClass(
                LivingEntity.class,
                player.getBoundingBox().inflate(range)
        );

        for (LivingEntity entity : nearbyEntities) {
            // Skip if it's the player themselves
            if (entity == player) {
                continue;
            }
            applySlowEffect(entity);
        }
    }

    private void applySlowEffect(LivingEntity entity) {
        entity.addEffect(new MobEffectInstance(
                MobEffects.MOVEMENT_SLOWDOWN,
                20,   // 1 second
                2,    // Amplifier
                true, // Ambient
                true  // Show particles
        ));
    }

    /**
     * Boss bar handling
     */
    private void updateBossBar(Player player, float progress, String title, BossBarColor color) {
        if (!(player instanceof ServerPlayer serverPlayer)) return;

        PlayerState state = playerStates.get(player);
        CustomBossEvent bossBar = state.bossBar;

        if (bossBar == null) {
            bossBar = createBossBar(serverPlayer, title, color);
            state.bossBar = bossBar;
        }

        bossBar.setName(Component.literal(title));
        bossBar.setColor(color);
        bossBar.setProgress(progress);
    }

    private CustomBossEvent createBossBar(ServerPlayer player, String title, BossBarColor color) {
        CustomBossEvent newBar = new CustomBossEvent(
                Component.literal(title),
                color,
                BossBarOverlay.PROGRESS
        );
        newBar.addPlayer(player);
        return newBar;
    }

    private void removeBossBar(Player player) {
        if (!(player instanceof ServerPlayer serverPlayer)) return;
        PlayerState state = playerStates.remove(player);
        if (state != null && state.bossBar != null) {
            state.bossBar.removePlayer(serverPlayer);
        }
    }

    /**
     * Inner class to track the player's pickaxe state.
     */
    private static class PlayerState {
        int energy;
        boolean energySoundPlayed = false;
        boolean cooldownSoundPlayed = false;
        CustomBossEvent bossBar;

        PlayerState(int energy) {
            this.energy = energy;
        }
    }
    @Override
    public boolean isFoil(ItemStack stack) {
        return true; // Δίνει ένα εφέ λάμψης
    }
}
