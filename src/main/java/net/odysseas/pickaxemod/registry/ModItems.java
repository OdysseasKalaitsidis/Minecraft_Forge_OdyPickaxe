package net.odysseas.pickaxemod.registry;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.odysseas.pickaxemod.Pickaxemod;
import net.odysseas.pickaxemod.item.CustomPickaxe;

@Mod.EventBusSubscriber(modid = Pickaxemod.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModItems {

    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, Pickaxemod.MODID);

    // Register the custom pickaxeA
    public static final RegistryObject<Item> ODY_PICKAXE = ITEMS.register("ody_pickaxe",
            () -> new CustomPickaxe(Tiers.DIAMOND, new Item.Properties()));


    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }

    // Add the item to the "Tools and Utilities" creative tab
    @SubscribeEvent
    public static void addItemsToCreativeTab(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.TOOLS_AND_UTILITIES) {
            event.accept(ODY_PICKAXE.get());
        }
    }
}
