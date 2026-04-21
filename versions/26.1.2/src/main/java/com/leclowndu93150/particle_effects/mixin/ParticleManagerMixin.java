package com.leclowndu93150.particle_effects.mixin;

import com.leclowndu93150.particle_effects.capture.ParticleCaptures;
import com.leclowndu93150.particle_effects.utils.PEDebugParticle;
import com.llamalad7.mixinextras.injector.wrapoperation.*;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.util.RandomSource;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;

@Mixin(ParticleEngine.class)
public class ParticleManagerMixin {

	@WrapOperation(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/particle/ParticleProvider;createParticle(Lnet/minecraft/core/particles/ParticleOptions;Lnet/minecraft/client/multiplayer/ClientLevel;DDDDDDLnet/minecraft/util/RandomSource;)Lnet/minecraft/client/particle/Particle;"), method = "makeParticle")
	private Particle markParticle(ParticleProvider<?> instance, ParticleOptions t, ClientLevel clientWorld, double a, double b, double c, double d, double e, double v, RandomSource random, Operation<Particle> original) {
		Particle particle = original.call(instance, t, clientWorld, a, b, c, d, e, v, random);
		Integer debugData = ParticleCaptures.getDebugData();
		if (debugData != null && particle != null) {
			((PEDebugParticle) particle).particleEffects$setDebugData(debugData);
		}
		return particle;
	}
}
