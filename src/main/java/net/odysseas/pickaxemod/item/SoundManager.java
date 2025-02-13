package net.odysseas.pickaxemod.item;

import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.odysseas.pickaxemod.sound.ModSounds;

public class SoundManager {

    public static void playEnergyUseSound(Player player, float energyPercentage) {
        if (energyPercentage > 0.8F && energyPercentage > 0.0F && ModSounds.ENERGY_USE.isPresent()) {
            player.level().playSound(null, player.blockPosition(),
                    ModSounds.ENERGY_USE.get(), SoundSource.PLAYERS, 1.0F, 1.0F);
        }
    }


    public static void playCooldownSound(Player player) {
        if (ModSounds.COOLDOWN_USE.isPresent()) {
            player.level().playSound(null, player.blockPosition(),
                    ModSounds.COOLDOWN_USE.get(), SoundSource.PLAYERS, 1.0F, 1.0F);
        }
    }

}
