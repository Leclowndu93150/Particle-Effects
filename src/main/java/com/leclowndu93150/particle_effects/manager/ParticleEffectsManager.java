package com.leclowndu93150.particle_effects.manager;

import com.leclowndu93150.particle_effects.config.ParticleEffectsConfig;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.core.Holder;
import net.minecraft.core.Holder.Reference;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.level.Level;
import com.leclowndu93150.particle_effects.ParticleEffects;
import com.leclowndu93150.particle_effects.particle.*;
import com.leclowndu93150.particle_effects.utils.*;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

import javax.annotation.Nullable;
import java.util.*;
import java.util.stream.*;

public class ParticleEffectsManager {

	public static final DeferredRegister<ParticleType<?>> PARTICLES = DeferredRegister.create(Registries.PARTICLE_TYPE, ParticleEffects.MOD_ID);

	private static final Map<String, RegistryObject<SimpleParticleType>> REGISTERED_PARTICLES = new HashMap<>();
	private static final Map<MobEffect, RegistryObject<SimpleParticleType>> EFFECT_TO_PARTICLE = new HashMap<>();
	private static final Map<Integer, List<ParticleOptions>> COLOR_TO_PARTICLES_MAP = new HashMap<>();
	private static final HashMap<ParticleOptions, MobEffect> MINECRAFT_EFFECTS_WITH_TEXTURED_PARTICLE = getMinecraftEffectWidthTexturedParticles();

	@Nullable
	public static List<ParticleOptions> getParticleEffects(Integer i) {
		return COLOR_TO_PARTICLES_MAP.get(i);
	}

	private static RegistryObject<SimpleParticleType> registerParticleTypeForEffect(MobEffect statusEffect, ResourceLocation effectId) {
		ResourceLocation modEffectId = getModEffectId(statusEffect, effectId);
		String registryName = modEffectId.getPath();

		RegistryObject<SimpleParticleType> holder = PARTICLES.register(registryName, () -> new SimpleParticleType(false));
		REGISTERED_PARTICLES.put(registryName, holder);

		return holder;
	}

	private static ResourceLocation getModEffectId(MobEffect statusEffect, ResourceLocation effectId) {
		boolean bl = MINECRAFT_EFFECTS_WITH_TEXTURED_PARTICLE.containsValue(statusEffect);
		return ParticleEffects.id(effectId.getPath() + (bl ? "_new" : ""));
	}

	public static void onInitialize() {
		for (Reference<MobEffect> reference : BuiltInRegistries.MOB_EFFECT.holders().toList()) {
			MobEffect statusEffect = reference.value();
			ResourceLocation id = reference.key().location();
			if (!id.getNamespace().equals("minecraft")) {
				continue;
			}

			RegistryObject<SimpleParticleType> holder = ParticleEffectsManager.registerParticleTypeForEffect(statusEffect, id);
			EFFECT_TO_PARTICLE.put(statusEffect, holder);
		}
	}

	@SubscribeEvent
	public static void onCommonSetup(FMLCommonSetupEvent event) {
		event.enqueueWork(() -> {
			for (Map.Entry<MobEffect, RegistryObject<SimpleParticleType>> entry : EFFECT_TO_PARTICLE.entrySet()) {
				StatusEffectUtils.swapParticle(entry.getKey(), entry.getValue().get());
			}

			for (Reference<Potion> reference : BuiltInRegistries.POTION.holders().toList()) {
				Potion potion = reference.value();
				ResourceLocation id = reference.key().location();
				if (!id.getNamespace().equals("minecraft")) {
					continue;
				}

				List<MobEffectInstance> effects = potion.getEffects();

				int color = ArgbUtils.getColorWithoutAlpha(StatusEffectUtils.getColor(effects));

				List<ParticleOptions> particleEffects = effects.stream()
						.map(MobEffectInstance::getEffect)
						.flatMap((effect) -> {
							ParticleOptions particleEffect = ((PEStatusEffect) effect).particleEffects$getParticleEffect();
							if (particleEffect == null) {
								ParticleEffects.LOGGER.error("[DEV/Potion Registration] Looks like {} effect with color {} doesn't have textured particle, this shouldn't happen! Skipping it registration.", color, effect.getDisplayName().getString());
								return Stream.empty();
							}
							return Stream.of(particleEffect);
						})
						.toList();

				COLOR_TO_PARTICLES_MAP.put(color, particleEffects);
			}

			for (Reference<MobEffect> reference : BuiltInRegistries.MOB_EFFECT.holders().toList()) {
				MobEffect statusEffect = reference.value();
				ResourceLocation id = reference.key().location();
				if (!id.getNamespace().equals("minecraft")) {
					continue;
				}

				int color = ArgbUtils.getColorWithoutAlpha(statusEffect.getColor());

				ParticleOptions particleEffect = ((PEStatusEffect) statusEffect).particleEffects$getParticleEffect();

				if (particleEffect == null) {
					ParticleEffects.LOGGER.error("[DEV/Effect Registration] Looks like {} effect with color {} doesn't have textured particle, this shouldn't happen! Skipping it registration.", color, statusEffect.getDisplayName().getString());
					continue;
				}

				List<ParticleOptions> effects = COLOR_TO_PARTICLES_MAP.get(color);
				if (effects != null) {
					if (ParticleEffectsConfig.CLIENT.debugLogEnabled.get()) {
						ParticleEffects.LOGGER.warn("[DEV/Effect Registration] Found registered effects for color {} from {} effect, skipping it registration. If you just mod user, ignore it.", color, statusEffect.getDisplayName().getString());
					}
				} else {
					COLOR_TO_PARTICLES_MAP.put(color, List.of(particleEffect));
				}
			}
		});
	}

	@SubscribeEvent
	public static void onRegisterParticleProviders(RegisterParticleProvidersEvent event) {
		for (RegistryObject<SimpleParticleType> holder : REGISTERED_PARTICLES.values()) {
			event.registerSpriteSet(holder.get(), TexturedParticleFactory::new);
		}
	}

	private static HashMap<ParticleOptions, MobEffect> getMinecraftEffectWidthTexturedParticles() {
		HashMap<ParticleOptions, MobEffect> map = new HashMap<>();

		return new HashMap<>();
	}

	public static MobEffect getVanillaStatusEffectByStatusEffect(ParticleOptions parameters) {
		return MINECRAFT_EFFECTS_WITH_TEXTURED_PARTICLE.get(parameters);
	}

	public static void processSplashPotionStageOne(LocalRef<List<ParticleOptions>> localParticleEffects, int color) {
		localParticleEffects.set(null);

		if (!ParticleEffectsConfig.CLIENT.modEnabled.get()) {
			return;
		}

		List<ParticleOptions> list = ParticleEffectsManager.getParticleEffects(ArgbUtils.getColorWithoutAlpha(color));
		if (list == null) {
			return;
		}

		localParticleEffects.set(list);
	}

	public static Particle processSplashPotionStageTwo(@Nullable Level world, LevelRenderer instance, ParticleOptions parameters, boolean alwaysSpawn, double x, double y, double z, double velocityX, double velocityY, double velocityZ, Operation<Particle> original, LocalRef<List<ParticleOptions>> localParticleEffects, int color) {
		if (!ParticleEffectsConfig.CLIENT.modEnabled.get()) {
			return original.call(instance, parameters, alwaysSpawn, x, y, z, velocityX, velocityY, velocityZ);
		}

		List<ParticleOptions> list = localParticleEffects.get();
		if (list == null || world == null) {
			return original.call(instance, parameters, alwaysSpawn, x, y, z, velocityX, velocityY, velocityZ);
		}
		ParticleOptions particleEffect = ListUtils.getRandomElement(list, world.getRandom());
		if (particleEffect == null) {
			return original.call(instance, parameters, alwaysSpawn, x, y, z, velocityX, velocityY, velocityZ);
		}
		((PEType) particleEffect).particleEffects$setColor(-1);
		return original.call(instance, particleEffect, alwaysSpawn, x, y, z, velocityX, velocityY, velocityZ);
	}
}