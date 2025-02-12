package net.odysseas.pickaxemod.registry;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.Tiers;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.odysseas.pickaxemod.PickaxeMod;
import net.odysseas.pickaxemod.item.CustomPickaxe;

public class ModItems {

    // Create a deferred register for items
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, PickaxeMod.MODID);

    // Register the custom pickaxe
    public static final RegistryObject<Item> ODY_PICKAXE = ITEMS.register("ody_pickaxe",
            () -> new CustomPickaxe(Tiers.DIAMOND, new Item.Properties().stacksTo(1)));

    // Register this class with the event bus
    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
