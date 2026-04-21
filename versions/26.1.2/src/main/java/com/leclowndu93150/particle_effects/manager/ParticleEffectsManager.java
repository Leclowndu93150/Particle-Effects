package com.leclowndu93150.particle_effects.manager;

import com.leclowndu93150.particle_effects.capture.ParticleCaptures;
import com.leclowndu93150.particle_effects.compat.LoadedMods;
import com.leclowndu93150.particle_effects.config.ParticleEffectsConfig;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import net.minecraft.client.particle.Particle;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.*;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.effect.*;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import com.leclowndu93150.particle_effects.ParticleEffects;
import com.leclowndu93150.particle_effects.particle.*;
import com.leclowndu93150.particle_effects.utils.*;
import java.util.*;
import java.util.function.*;
import java.util.stream.*;
import org.jetbrains.annotations.Nullable;

public class ParticleEffectsManager {

	public static boolean redirectEnabled = false;
	public static boolean redirectToVanillaEffectColors = true;

	public static final DeferredRegister<ParticleType<?>> PARTICLES = DeferredRegister.create(Registries.PARTICLE_TYPE, ParticleEffects.MOD_ID);

	private static final Map<String, DeferredHolder<ParticleType<?>, SimpleParticleType>> REGISTERED_PARTICLES = new HashMap<>();
	private static final Map<MobEffect, DeferredHolder<ParticleType<?>, SimpleParticleType>> EFFECT_TO_PARTICLE = new HashMap<>();
	private static final Map<Integer, List<ParticleOptions>> COLOR_TO_PARTICLES_MAP = new HashMap<>();
	private static final HashMap<ParticleOptions, MobEffect> MINECRAFT_EFFECTS_WITH_TEXTURED_PARTICLE = getMinecraftEffectWidthTexturedParticles();

	@Nullable
	public static List<ParticleOptions> getParticleEffects(Integer i) {
		return COLOR_TO_PARTICLES_MAP.get(i);
	}

	private static DeferredHolder<ParticleType<?>, SimpleParticleType> registerParticleTypeForEffect(MobEffect statusEffect, Identifier effectId) {
		Identifier modEffectId = getModEffectId(statusEffect, effectId);
		String registryName = modEffectId.getPath();
		DeferredHolder<ParticleType<?>, SimpleParticleType> holder = PARTICLES.register(registryName, () -> new SimpleParticleType(false));
		REGISTERED_PARTICLES.put(registryName, holder);
		return holder;
	}

	private static Identifier getModEffectId(MobEffect statusEffect, Identifier effectId) {
		boolean bl = MINECRAFT_EFFECTS_WITH_TEXTURED_PARTICLE.containsValue(statusEffect);
		return ParticleEffects.id(effectId.getPath() + (bl ? "_new" : ""));
	}

	public static void onInitialize() {
		for (Map.Entry<ResourceKey<MobEffect>, MobEffect> entry : BuiltInRegistries.MOB_EFFECT.entrySet()) {
			MobEffect statusEffect = entry.getValue();
			Identifier id = entry.getKey().identifier();
			if (!id.getNamespace().equals("minecraft")) {
				continue;
			}

			DeferredHolder<ParticleType<?>, SimpleParticleType> holder = registerParticleTypeForEffect(statusEffect, id);
			EFFECT_TO_PARTICLE.put(statusEffect, holder);
		}
	}

	@SubscribeEvent
	public static void onCommonSetup(FMLCommonSetupEvent event) {
		event.enqueueWork(() -> {
			for (Map.Entry<MobEffect, DeferredHolder<ParticleType<?>, SimpleParticleType>> entry : EFFECT_TO_PARTICLE.entrySet()) {
				StatusEffectUtils.swapParticle(entry.getKey(), entry.getValue().get());
			}

			if (LoadedMods.isAnyOldPotionsModLoaded()) {
				ParticleEffectsManager.redirectEnabled = true;
				ParticleEffectsManager.redirectToVanillaEffectColors = true;
				ParticleEffectsManager.registerParticleColorsForTypes();
				ParticleEffectsManager.redirectToVanillaEffectColors = false;
				ParticleEffectsManager.registerParticleColorsForTypes();
				ParticleEffectsManager.redirectEnabled = false;
			} else {
				registerParticleColorsForTypes();
			}
		});
	}

	private static void registerParticleColorsForTypes() {
		for (Map.Entry<ResourceKey<Potion>, Potion> entry : BuiltInRegistries.POTION.entrySet()) {
			Potion potion = entry.getValue();
			Identifier id = entry.getKey().identifier();
			if (!id.getNamespace().equals("minecraft")) {
				continue;
			}

			List<MobEffectInstance> effects = potion.getEffects();

			OptionalInt optional = net.minecraft.world.item.alchemy.PotionContents.getColorOptional(effects);
			if (optional.isEmpty()) {
				continue;
			}

			int color = ArgbUtils.getColorWithoutAlpha(optional.getAsInt());

			List<ParticleOptions> particleEffects = effects.stream()
					.map(MobEffectInstance::getEffect)
					.map(Holder::value)
					.flatMap((effect) -> {
						ParticleOptions particleEffect = ((PEStatusEffect) effect).particleEffects$getParticleEffect();
						if (particleEffect == null) {
							ParticleEffects.LOGGER.error("[DEV/Potion Registration] Effect with color {} doesn't have textured particle, skipping.", color);
							return Stream.empty();
						}
						return Stream.of(particleEffect);
					})
					.toList();

			List<ParticleOptions> list = COLOR_TO_PARTICLES_MAP.get(color);
			if (list != null) {
				if (ParticleEffectsConfig.CLIENT.debugLogEnabled.get()) {
					String potionName = potion.name();
					ParticleEffects.LOGGER.warn("[DEV/Potion Registration] Found registered effects for color {} from {} potion, skipping.", color, potionName);
				}
			} else {
				COLOR_TO_PARTICLES_MAP.put(color, particleEffects);
			}
		}

		for (Map.Entry<ResourceKey<MobEffect>, MobEffect> entry : BuiltInRegistries.MOB_EFFECT.entrySet()) {
			MobEffect statusEffect = entry.getValue();
			Identifier id = entry.getKey().identifier();
			if (!id.getNamespace().equals("minecraft")) {
				continue;
			}

			int color = ArgbUtils.getColorWithoutAlpha(statusEffect.getColor());

			ParticleOptions particleEffect = ((PEStatusEffect) statusEffect).particleEffects$getParticleEffect();

			if (particleEffect == null) {
				ParticleEffects.LOGGER.error("[DEV/Effect Registration] Effect with color {} doesn't have textured particle, skipping.", color);
				continue;
			}

			List<ParticleOptions> effects = COLOR_TO_PARTICLES_MAP.get(color);
			if (effects != null) {
				if (ParticleEffectsConfig.CLIENT.debugLogEnabled.get()) {
					ParticleEffects.LOGGER.warn("[DEV/Effect Registration] Found registered effects for color {} from {} effect, skipping.", color, statusEffect.getDisplayName().getString());
				}
			} else {
				COLOR_TO_PARTICLES_MAP.put(color, List.of(particleEffect));
			}
		}
	}

	@SubscribeEvent
	public static void onRegisterParticleProviders(RegisterParticleProvidersEvent event) {
		for (DeferredHolder<ParticleType<?>, SimpleParticleType> holder : REGISTERED_PARTICLES.values()) {
			event.registerSpriteSet(holder.get(), TexturedParticleFactory::new);
		}
	}

	private static HashMap<ParticleOptions, MobEffect> getMinecraftEffectWidthTexturedParticles() {
		HashMap<ParticleOptions, MobEffect> map = new HashMap<>();
		map.put(ParticleTypes.ITEM_SLIME, MobEffects.OOZING.value());
		map.put(ParticleTypes.ITEM_COBWEB, MobEffects.WEAVING.value());
		map.put(ParticleTypes.INFESTED, MobEffects.INFESTED.value());
		map.put(ParticleTypes.TRIAL_OMEN, MobEffects.TRIAL_OMEN.value());
		map.put(ParticleTypes.RAID_OMEN, MobEffects.RAID_OMEN.value());
		map.put(ParticleTypes.SMALL_GUST, MobEffects.WIND_CHARGED.value());
		return map;
	}

	public static MobEffect getVanillaStatusEffectByStatusEffect(ParticleOptions parameters) {
		return MINECRAFT_EFFECTS_WITH_TEXTURED_PARTICLE.get(parameters);
	}

	public static void processSplashPotionStageOne(LocalRef<List<ParticleOptions>> localParticleEffects, int color) {
		localParticleEffects.set(null);

		if (!ParticleEffectsConfig.CLIENT.modEnabled.get()) {
			return;
		}

		List<ParticleOptions> list = getParticleEffects(ArgbUtils.getColorWithoutAlpha(color));
		if (list == null) {
			return;
		}

		localParticleEffects.set(list);
	}

	public static Particle processSplashPotionStageTwo(@Nullable Level world, ParticleOptions original, Function<ParticleOptions, Particle> function, LocalRef<List<ParticleOptions>> localParticleEffects, int color) {
		Supplier<Particle> particleSupplier = () -> function.apply(original);

		if (!ParticleEffectsConfig.CLIENT.modEnabled.get()) {
			return particleSupplier.get();
		}

		List<ParticleOptions> list = localParticleEffects.get();
		if (list == null || list.isEmpty() || world == null) {
			return particleSupplier.get();
		}

		ParticleOptions particleEffect = ListUtils.getRandomElement(list, world.getRandom());
		if (particleEffect == null) {
			return particleSupplier.get();
		}

		((PEType) particleEffect).particleEffects$setColor(color);

		ParticleCaptures.setParticle(particleEffect);
		Particle apply = function.apply(particleEffect);
		ParticleCaptures.setParticle(null);
		return apply;
	}

	public static Particle swapParticle(Level world, ParticleOptions original, Function<ParticleOptions, Particle> function, Supplier<Particle> originalCall) {
		if (!ParticleEffectsConfig.CLIENT.modEnabled.get()) {
			return originalCall.get();
		}

		if (ParticleCaptures.getParticle() != original) {
			return originalCall.get();
		}

		int color;

		if (original instanceof ColorParticleOption effect) {
			color = effect.color;
		} else if (original instanceof SpellParticleOption) {
			MobEffect statusEffect = getVanillaStatusEffectByStatusEffect(original);
			color = statusEffect == null ? 0 : ArgbUtils.getColorWithoutAlpha(statusEffect.getColor());
		} else {
			MobEffect statusEffect = getVanillaStatusEffectByStatusEffect(original);
			color = statusEffect == null ? 0 : ArgbUtils.getColorWithoutAlpha(statusEffect.getColor());
		}

		if (color == 0) {
			return originalCall.get();
		}

		List<ParticleOptions> list = getParticleEffects(ArgbUtils.getColorWithoutAlpha(color));
		if (list == null || list.isEmpty()) {
			return originalCall.get();
		}
		if (world == null) {
			return originalCall.get();
		}

		ParticleOptions particleEffect = ListUtils.getRandomElement(list, world.getRandom());
		if (particleEffect == null) {
			return originalCall.get();
		}

		((PEType) particleEffect).particleEffects$setColor(color);

		return function.apply(particleEffect);
	}
}
