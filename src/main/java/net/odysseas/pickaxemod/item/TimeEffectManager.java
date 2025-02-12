package net.odysseas.pickaxemod.item;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

import java.util.List;

public class TimeEffectManager {

    public static void applyTimeSlow(Player player) {
        double range = 5.0;
        List<LivingEntity> nearbyEntities = player.level().getEntitiesOfClass(
                LivingEntity.class, player.getBoundingBox().inflate(range)
        );

        // Εφαρμογή του εφέ επιβράδυνσης σε κοντινά entities
        for (LivingEntity entity : nearbyEntities) {
            if (entity == player) continue;
            entity.addEffect(new MobEffectInstance(
                    MobEffects.MOVEMENT_SLOWDOWN, 20, 2, true, true
            ));
        }
    }
}
