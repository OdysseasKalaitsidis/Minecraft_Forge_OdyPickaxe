package net.odysseas.pickaxemod.registry;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.odysseas.pickaxemod.PickaxeMod;

@Mod.EventBusSubscriber(modid = PickaxeMod.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModCreativeTabs {

    @SubscribeEvent
    public static void addItemsToCreativeTab(BuildCreativeModeTabContentsEvent event) {
        ResourceKey<CreativeModeTab> tabKey = event.getTabKey();

        if (tabKey == CreativeModeTabs.TOOLS_AND_UTILITIES) {
            event.accept(ModItems.ODY_PICKAXE.get());
        }
    }
}
