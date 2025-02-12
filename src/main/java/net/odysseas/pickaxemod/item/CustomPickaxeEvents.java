package net.odysseas.pickaxemod.item;

import net.minecraft.server.level.ServerPlayer;
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
    private final Map<Player, PlayerState> playerStates = new HashMap<>();

    @SubscribeEvent
    public void onAttackEntity(AttackEntityEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        if (!(event.getTarget() instanceof LivingEntity target)) return;
        if (!(player.getMainHandItem().getItem() instanceof CustomPickaxe)) return;

        PlayerState state = playerStates.computeIfAbsent(player, p -> new PlayerState(MAX_ENERGY_TICKS));

        if (state.energy <= 0) return; // Exit if energy is depleted

        // Scale damage based on energy percentage
        float energyPercentage = state.energy / (float) MAX_ENERGY_TICKS;
        float baseDamage = 5.0F;
        float finalDamage = baseDamage * energyPercentage;

        target.hurt(player.damageSources().playerAttack(player), finalDamage);
        state.energy = Math.max(state.energy - 20, 0);

        SoundManager.playEnergyUseSound(player, energyPercentage);
    }

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        Player player = event.player;

        if (player.getMainHandItem().getItem() instanceof CustomPickaxe) {
            EnergyManager.handleEnergy(player, playerStates);
        } else {
            BossBarManager.removeBossBar(player, playerStates);
        }
    }
}
