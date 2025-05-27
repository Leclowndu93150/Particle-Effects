package com.leclowndu93150.particle_effects;

import com.leclowndu93150.particle_effects.manager.ClientSetup;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;
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
		return new ResourceLocation(MOD_ID, path);
	}

	public static Component text(String path, Object... args) {
		return Component.translatable(String.format("%s.%s", MOD_ID, path), args);
	}

	public ParticleEffects(){
		IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();
		ParticleEffectsManager.PARTICLES.register(eventBus);
		eventBus.addListener(ParticleEffectsManager::onCommonSetup);

		if (FMLEnvironment.dist.isClient()) {
			ClientSetup.init(eventBus);
		}

		ParticleEffectsManager.onInitialize();
	}
}