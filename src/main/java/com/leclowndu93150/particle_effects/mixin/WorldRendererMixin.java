package com.leclowndu93150.particle_effects.mixin;

import com.leclowndu93150.particle_effects.config.ParticleEffectsConfig;
import com.llamalad7.mixinextras.injector.wrapoperation.*;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.core.particles.ColorParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.world.effect.MobEffect;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;

import com.leclowndu93150.particle_effects.manager.ParticleEffectsManager;
import com.leclowndu93150.particle_effects.utils.*;
import java.util.List;
import org.jetbrains.annotations.Nullable;

@Debug(export = true)
@Mixin(LevelRenderer.class)
public class WorldRendererMixin {

	@Shadow
	@Nullable
	private ClientLevel level;

	// ENTITY PARTICLES
	@WrapOperation(method = "addParticle(Lnet/minecraft/core/particles/ParticleOptions;ZZDDDDDD)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/LevelRenderer;addParticleInternal(Lnet/minecraft/core/particles/ParticleOptions;ZZDDDDDD)Lnet/minecraft/client/particle/Particle;"))
	private Particle swapParticle(LevelRenderer instance, ParticleOptions parameters, boolean alwaysSpawn, boolean canSpawnOnMinimal, double x, double y, double z, double velocityX, double velocityY, double velocityZ, Operation<Particle> original) {
		if (!ParticleEffectsConfig.CLIENT.modEnabled.get()) {
			return original.call(instance, parameters, alwaysSpawn, canSpawnOnMinimal, x, y, z, velocityX, velocityY, velocityZ);
		}
		int color;

		if (parameters instanceof ColorParticleOption effect) { // RECEIVES IN SINGLEPLAYER AND IN MULTIPLAYER
			color = effect.color;
		} else {
			MobEffect statusEffect = ParticleEffectsManager.getVanillaStatusEffectByStatusEffect(parameters);
			color = statusEffect == null ? 0 : ArgbUtils.getColorWithoutAlpha(statusEffect.getColor());
		}

		if (color == 0) {
			return original.call(instance, parameters, alwaysSpawn, canSpawnOnMinimal, x, y, z, velocityX, velocityY, velocityZ);
		}

		List<ParticleOptions> list = ParticleEffectsManager.getParticleEffects(ArgbUtils.getColorWithoutAlpha(color));
		if (list == null || this.level == null) {
			return original.call(instance, parameters, alwaysSpawn, canSpawnOnMinimal, x, y, z, velocityX, velocityY, velocityZ);
		}

		ParticleOptions particleEffect = ListUtils.getRandomElement(list, this.level.getRandom());
		if (particleEffect == null) {
			return original.call(instance, parameters, alwaysSpawn, canSpawnOnMinimal, x, y, z, velocityX, velocityY, velocityZ);
		}

		((PEType) particleEffect).particleEffects$setColor(color);
		return original.call(instance, particleEffect, alwaysSpawn, canSpawnOnMinimal, x, y, z, velocityX, velocityY, velocityZ);
	}
}