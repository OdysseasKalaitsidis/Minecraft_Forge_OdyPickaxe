# ⛏️ Custom Pickaxe Mod  
*A Minecraft Forge 1.21 mod that introduces a unique pickaxe with a time-slowing mechanic, custom sounds, and an energy-based attack system.*

---

## 🌟 Features  

### 🕰️ Time-Slowing Pickaxe  
- When held, **time slows down** for the player.
- Uses an **energy system** to balance its effects.
- Custom **sounds and effects** when activated.

### ⚡ Energy-Based Combat System  
- The pickaxe has a **limited energy pool** (1200 ticks = 30 seconds).
- **Attacking entities** consumes energy but scales damage based on remaining energy.
- **Cooldown system** prevents infinite energy usage (**5s cooldown**).
- A **boss bar UI**  can show energy levels.

### 🔊 Custom Sound Effects  
- When energy is used, a **custom sound effect** plays based on the energy percentage.

---

## 🛠️ Installation  

1. Install **Minecraft Forge 1.21**.
2. Download the mod `.jar` file.
3. Place it in the `mods` folder of your Minecraft installation directory.
4. Launch the game and enjoy!

---

## 🖥️ How It Works  

### 📌 Attack Mechanics  
- If the **pickaxe is in the main hand**, attacking an entity will:
  - Scale **damage** based on energy percentage.
  - Play a **sound effect** reflecting the energy usage.
  - Drain **energy** (20 ticks per attack).

### 📌 Energy Regeneration & Cooldown  
- Energy **recharges** when the pickaxe is not in use.
- If energy **runs out**, the pickaxe **disables itself** temporarily.
- A **5-second cooldown** prevents instant energy recovery.

### 📌 Custom Effects  
- **Visual & sound effects** trigger based on the player's energy level.
- The **boss bar UI**  tracks energy.

---

## 🎮 Controls & Usage  

| Action               | Effect |
|----------------------|--------|
| **Hold Pickaxe**     | Time slows down |
| **Attack Entity**    | Deals energy-based damage |
| **Out of Energy**    | Pickaxe disables, cooldown starts |
| **Stop Holding**     | Energy starts regenerating |


---

## 📢 Credits  
- Developed by **Odysseas Kalaitsidis**
- Built with **Minecraft Forge 1.21**
