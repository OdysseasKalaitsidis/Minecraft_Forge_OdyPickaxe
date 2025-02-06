package net.odysseas.pickaxemod.registry;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.monster.Monster;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.odysseas.pickaxemod.PickaxeMod;
import net.odysseas.pickaxemod.entity.CustomMobEntity;

public class ModEntities {
    public static final DeferredRegister<EntityType<?>> ENTITIES =
            DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, PickaxeMod.MODID);

    public static final RegistryObject<EntityType<CustomMobEntity>> CUSTOM_MOB =
            ENTITIES.register("custom_mob",
                    () -> EntityType.Builder.of(CustomMobEntity::new, MobCategory.MONSTER)
                            .sized(0.6F, 1.95F)
                            .build("custom_mob")); // ΑΦΑΙΡΕΘΗΚΕ το ResourceLocation
}
