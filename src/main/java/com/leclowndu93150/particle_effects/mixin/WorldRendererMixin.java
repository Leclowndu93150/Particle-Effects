package com.leclowndu93150.particle_effects.mixin;

import com.leclowndu93150.particle_effects.config.ParticleEffectsConfig;
import com.llamalad7.mixinextras.injector.wrapoperation.*;

import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.effect.MobEffect;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;

import com.leclowndu93150.particle_effects.manager.ParticleEffectsManager;
import com.leclowndu93150.particle_effects.utils.*;
import java.util.List;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;

@Debug(export = true)
@Mixin(LevelRenderer.class)
public class WorldRendererMixin {

	@Shadow(aliases = "level")
	@Nullable
	private ClientLevel world;

	// SPLASH POTION
	@Inject(method = "levelEvent", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/phys/Vec3;atBottomCenterOf(Lnet/minecraft/core/Vec3i;)Lnet/minecraft/world/phys/Vec3;"))
	private void modifyParticleEffect(int eventId, BlockPos pos, int data, CallbackInfo ci, @Share("tp_effects") LocalRef<List<ParticleOptions>> localParticleEffects) {
		ParticleEffectsManager.processSplashPotionStageOne(localParticleEffects, data);
	}

	// SPLASH POTION
	@WrapOperation(method = "levelEvent", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/LevelRenderer;addParticleInternal(Lnet/minecraft/core/particles/ParticleOptions;ZDDDDDD)Lnet/minecraft/client/particle/Particle;", ordinal = 0))
	private Particle swapParticles(LevelRenderer instance, ParticleOptions parameters, boolean alwaysSpawn, double x, double y, double z, double velocityX, double velocityY, double velocityZ, Operation<Particle> original, @Share("tp_effects") LocalRef<List<ParticleOptions>> localParticleEffects, @Local(argsOnly = true, ordinal = 1) int color) {
		return ParticleEffectsManager.processSplashPotionStageTwo(this.world, instance, parameters, alwaysSpawn, x, y, z, velocityX, velocityY, velocityZ, original, localParticleEffects, color);
	}

	@WrapOperation(method = "addParticle(Lnet/minecraft/core/particles/ParticleOptions;ZZDDDDDD)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/LevelRenderer;addParticleInternal(Lnet/minecraft/core/particles/ParticleOptions;ZZDDDDDD)Lnet/minecraft/client/particle/Particle;"))
	private Particle swapParticle(LevelRenderer instance, ParticleOptions parameters, boolean alwaysSpawn, boolean canSpawnOnMinimal, double x, double y, double z, double velocityX, double velocityY, double velocityZ, Operation<Particle> original) {
		if (!ParticleEffectsConfig.CLIENT.modEnabled.get()) {
			return original.call(instance, parameters, alwaysSpawn, canSpawnOnMinimal, x, y, z, velocityX, velocityY, velocityZ);
		}
		boolean bl = parameters.equals(ParticleTypes.ENTITY_EFFECT);
		boolean bl2 = parameters.equals(ParticleTypes.AMBIENT_ENTITY_EFFECT);
		if (!bl && !bl2) {
			return original.call(instance, parameters, alwaysSpawn, canSpawnOnMinimal, x, y, z, velocityX, velocityY, velocityZ);
		}

		int color = ArgbUtils.getArgb(bl2 ? 38 : 255, (int) (velocityX * 255), (int) (velocityY * 255), (int) (velocityZ * 255));

		List<ParticleOptions> list = ParticleEffectsManager.getParticleEffects(ArgbUtils.getColorWithoutAlpha(color));
		if (list == null || this.world == null) {
			return original.call(instance, parameters, alwaysSpawn, canSpawnOnMinimal, x, y, z, velocityX, velocityY, velocityZ);
		}

		ParticleOptions particleEffect = ListUtils.getRandomElement(list, this.world.getRandom());
		if (particleEffect == null) {
			return original.call(instance, parameters, alwaysSpawn, canSpawnOnMinimal, x, y, z, velocityX, velocityY, velocityZ);
		}

		((PEType) particleEffect).particleEffects$setColor(color);
		return original.call(instance, particleEffect, alwaysSpawn, canSpawnOnMinimal, x, y, z, velocityX, velocityY, velocityZ);
	}

}