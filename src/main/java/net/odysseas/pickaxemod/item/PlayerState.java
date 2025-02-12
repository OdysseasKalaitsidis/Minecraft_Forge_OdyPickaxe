package net.odysseas.pickaxemod.item;

public class PlayerState {
    int energy;  // Stores the pickaxe's energy level
    boolean energySoundPlayed = false;
    boolean cooldownSoundPlayed = false;
    CustomBossEvent bossBar;

    public PlayerState(int energy) {
        this.energy = energy;  // Initializes energy with a starting value
    }
}
