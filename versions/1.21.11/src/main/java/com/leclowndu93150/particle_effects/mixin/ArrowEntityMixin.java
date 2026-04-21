package com.leclowndu93150.particle_effects.mixin;

import com.leclowndu93150.particle_effects.capture.ParticleCaptures;
import com.leclowndu93150.particle_effects.manager.ParticleEffectsManager;
import com.leclowndu93150.particle_effects.utils.*;
import com.llamalad7.mixinextras.injector.wrapoperation.*;
import com.llamalad7.mixinextras.sugar.Local;
import java.util.List;
import net.minecraft.world.entity.*;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.world.entity.projectile.arrow.Arrow;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;

@Mixin(Arrow.class)
public abstract class ArrowEntityMixin extends Entity {

	public ArrowEntityMixin(EntityType<?> type, Level world) {
		super(type, world);
	}

	@WrapOperation(at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;addParticle(Lnet/minecraft/core/particles/ParticleOptions;DDDDDD)V"), method = "makeParticle")
	private void markParticleFromArrow(Level instance, ParticleOptions parameters, double x, double y, double z, double velocityX, double velocityY, double velocityZ, Operation<Void> original, @Local(ordinal = 1) int color) {
		Runnable originalCall = () -> original.call(instance, parameters, x, y, z, velocityX, velocityY, velocityZ);

		List<ParticleOptions> list = ParticleEffectsManager.getParticleEffects(ArgbUtils.getColorWithoutAlpha(color));
		if (list == null || list.isEmpty()) {
			ParticleCaptures.setDebugData(41);
			originalCall.run();
			ParticleCaptures.setDebugData(null);
			return;
		}

		ParticleOptions particleEffect = ListUtils.getRandomElement(list, this.level().getRandom());
		if (particleEffect == null) {
			ParticleCaptures.setDebugData(44);
			originalCall.run();
			ParticleCaptures.setDebugData(null);
			return;
		}

		((PEType) particleEffect).particleEffects$setColor(color);
		ParticleCaptures.setParticle(particleEffect);
		original.call(instance, particleEffect, x, y, z, velocityX, velocityY, velocityZ);
		ParticleCaptures.setParticle(null);
	}

}
