package net.odysseas.pickaxemod.item;

public class PlayerState {
    int energy;  // Stores the pickaxe's energy level
    boolean energySoundPlayed = false;
    boolean cooldownSoundPlayed = false;
    boolean firstTimeHolding = true; // Ensure sound plays first time
    CustomBossEvent bossBar;

    public PlayerState(int energy) {
        this.energy = energy;  // Initializes energy with a starting value
    }

    public void resetCooldownSound() {
        cooldownSoundPlayed = false; // Reset cooldown sound flag
    }

    public void resetEnergySound() {
        energySoundPlayed = false; // Reset energy sound flag
    }
}
