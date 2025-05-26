package com.leclowndu93150.particle_effects;

import lombok.*;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.loading.FMLLoader;
import org.slf4j.*;
import com.leclowndu93150.particle_effects.config.ParticleEffectsConfig;
import com.leclowndu93150.particle_effects.manager.ParticleEffectsManager;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

@Mod(ParticleEffects.MOD_ID)
public class ParticleEffects{

	public static final String MOD_NAME = "Particle Effects";
	public static final String MOD_ID = "particle_effects";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_NAME);

	public static ResourceLocation id(String path) {
		return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
	}

	public static Component text(String path, Object... args) {
		return Component.translatable(String.format("%s.%s", MOD_ID, path), args);
	}

	public ParticleEffects(IEventBus eventBus, ModContainer modContainer){
		ParticleEffectsManager.PARTICLES.register(eventBus);
		eventBus.addListener(ParticleEffectsManager::onCommonSetup);
		eventBus.addListener(ParticleEffectsManager::onRegisterParticleProviders);
		modContainer.registerConfig(ModConfig.Type.CLIENT, ParticleEffectsConfig.CLIENT_SPEC);
		ParticleEffectsManager.onInitialize();
	}
}