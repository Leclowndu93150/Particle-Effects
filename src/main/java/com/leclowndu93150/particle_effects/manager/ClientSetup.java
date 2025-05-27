package com.leclowndu93150.particle_effects.manager;

import com.leclowndu93150.particle_effects.config.ParticleEffectsConfig;
import com.leclowndu93150.particle_effects.manager.ParticleEffectsManager;
import com.leclowndu93150.particle_effects.particle.TexturedParticleFactory;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.registries.RegistryObject;

public class ClientSetup {

    public static void init(IEventBus eventBus) {
        eventBus.register(ClientSetup.class);
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, ParticleEffectsConfig.CLIENT_SPEC);
    }

    @SubscribeEvent
    public static void onRegisterParticleProviders(RegisterParticleProvidersEvent event) {
        for (RegistryObject<SimpleParticleType> holder : ParticleEffectsManager.getRegisteredParticles().values()) {
            event.registerSpriteSet(holder.get(), TexturedParticleFactory::new);
        }
    }
}