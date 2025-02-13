package net.odysseas.pickaxemod.item;

import net.minecraft.world.item.PickaxeItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;

public class CustomPickaxe extends PickaxeItem {

    public CustomPickaxe(Tier tier, Properties properties) {
        super(tier, properties);
        // Δηλώνει το Pickaxe
        MinecraftForge.EVENT_BUS.register(new CustomPickaxeEvents());
    }

}
