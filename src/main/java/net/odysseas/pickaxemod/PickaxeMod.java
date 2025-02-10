package net.odysseas.pickaxemod;

import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.odysseas.pickaxemod.registry.ModItems;
import net.odysseas.pickaxemod.sound.ModSounds;

@Mod(PickaxeMod.MODID)
public class PickaxeMod {
    public static final String MODID = "pickaxemod";

    public PickaxeMod() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        ModItems.register(modEventBus); // Register items
        ModSounds.register(modEventBus); // Register sounds
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        // Common setup logic here
    }
}