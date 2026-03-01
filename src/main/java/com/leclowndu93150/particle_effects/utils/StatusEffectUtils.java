package com.leclowndu93150.particle_effects.utils;

import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.world.effect.MobEffect;

public class StatusEffectUtils {

	public static void swapParticle(MobEffect statusEffect, ParticleOptions particleEffect) {
		((PEStatusEffect) statusEffect).particleEffects$setParticleEffect(particleEffect);
	}

}