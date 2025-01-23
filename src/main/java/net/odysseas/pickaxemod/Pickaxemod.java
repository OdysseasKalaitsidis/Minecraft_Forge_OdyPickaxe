package net.odysseas.pickaxemod;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.odysseas.pickaxemod.registry.ModItems;

@Mod(Pickaxemod.MODID)
public class Pickaxemod {
    public static final String MODID = "pickaxemod";

    public Pickaxemod() {
        // Register common setup
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);

        // Register mod items in the mod event bus
        ModItems.ITEMS.register(FMLJavaModLoadingContext.get().getModEventBus());

        // Register the mod with the Forge event bus
        MinecraftForge.EVENT_BUS.register(this);
    }

    private void setup(final FMLCommonSetupEvent event) {
        // Add any setup code here
        System.out.println("Pickaxemod setup complete!");
    }
}
