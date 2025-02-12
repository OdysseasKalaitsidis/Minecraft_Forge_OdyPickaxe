package net.odysseas.pickaxemod.item;

import net.minecraft.world.item.PickaxeItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;

public class CustomPickaxe extends PickaxeItem {

    public CustomPickaxe(Tier tier, Properties properties) {
        super(tier, properties);
        // Register event handler
        MinecraftForge.EVENT_BUS.register(new CustomPickaxeEvents());
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return true; // Gives a glowing effect
    }
}
