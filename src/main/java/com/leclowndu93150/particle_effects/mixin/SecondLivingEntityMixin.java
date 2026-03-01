package com.leclowndu93150.particle_effects.mixin;

import com.leclowndu93150.particle_effects.capture.ParticleCaptures;
import com.llamalad7.mixinextras.injector.wrapoperation.*;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(LivingEntity.class)
public class SecondLivingEntityMixin {

	@WrapOperation(at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;addParticle(Lnet/minecraft/core/particles/ParticleOptions;DDDDDD)V"), method = "tickEffects")
	private void markParticle(Level instance, ParticleOptions parameters, double x, double y, double z, double velocityX, double velocityY, double velocityZ, Operation<Void> original) {
		ParticleCaptures.setParticle(parameters);
		original.call(instance, parameters, x, y, z, velocityX, velocityY, velocityZ);
		ParticleCaptures.setParticle(null);
	}
}
