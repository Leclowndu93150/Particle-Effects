package com.leclowndu93150.particle_effects.mixin;

import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.world.effect.MobEffect;
import org.spongepowered.asm.mixin.*;

import com.leclowndu93150.particle_effects.utils.PEStatusEffect;

@Mixin(MobEffect.class)
public class StatusEffectMixin implements PEStatusEffect {

	@Unique
	private ParticleOptions particleEffects$particleEffect;

	@Override
	public void particleEffects$setParticleEffect(ParticleOptions particleEffect) {
		this.particleEffects$particleEffect = particleEffect;
	}

	public ParticleOptions particleEffects$getParticleEffect() {
		return this.particleEffects$particleEffect;
	}
}
