package net.odysseas.pickaxemod.sound;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.common.util.ForgeSoundType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.odysseas.pickaxemod.PickaxeMod;

public class ModSounds {
    public static final DeferredRegister<SoundEvent> SOUND_EVENTS =
            DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, PickaxeMod.MODID);

    // Registering sound events
    public static final RegistryObject<SoundEvent> ENERGY_USE = registerSoundEvent("energy");
    public static final RegistryObject<SoundEvent> COOLDOWN_USE = registerSoundEvent("recharge");

    // Custom sound type for tools or blocks
    public static final ForgeSoundType ENERGY_SOUND = new ForgeSoundType(
            1f,  // Volume
            1f,  // Pitch
            () -> ENERGY_USE.get(),  // Break sound
            () -> ENERGY_USE.get(),  // Step sound
            () -> COOLDOWN_USE.get(),  // Place sound
            () -> ENERGY_USE.get(),  // Hit sound
            () -> COOLDOWN_USE.get()  // Fall sound
    );

    private static RegistryObject<SoundEvent> registerSoundEvent(String name) {
        return SOUND_EVENTS.register(name,
                () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(PickaxeMod.MODID, name)));
    }

    public static void register(IEventBus eventBus) {
        SOUND_EVENTS.register(eventBus);
    }
}
