package net.odysseas.pickaxemod.item;

public class PlayerState {
    int energy;  // Αποθηκεύει το επίπεδο ενέργειας της αξίνας
    boolean energySoundPlayed = false;
    boolean cooldownSoundPlayed = false;
    boolean firstTimeHolding = true; // Εξασφαλίζει ότι ο ήχος παίζει την πρώτη φορά
    CustomBossEvent bossBar;

    public PlayerState(int energy) {
        this.energy = energy;  // Αρχικοποιεί την ενέργεια με μια αρχική τιμή
    }

    public void resetCooldownSound() {
        cooldownSoundPlayed = false; // Επαναφορά της σημαίας ήχου cooldown
    }

    public void resetEnergySound() {
        energySoundPlayed = false; // Επαναφορά της σημαίας ήχου ενέργειας
    }
}