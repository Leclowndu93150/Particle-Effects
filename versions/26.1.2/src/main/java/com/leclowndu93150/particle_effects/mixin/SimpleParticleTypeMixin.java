package com.leclowndu93150.particle_effects.mixin;

import org.spongepowered.asm.mixin.*;
import com.leclowndu93150.particle_effects.utils.PEType;
import net.minecraft.core.particles.SimpleParticleType;

@Mixin(SimpleParticleType.class)
public class SimpleParticleTypeMixin implements PEType {

	@Unique
	private int particleEffects$color;

	@Override
	public int particleEffects$getColor() {
		return this.particleEffects$color;
	}

	@Override
	public void particleEffects$setColor(int color) {
		this.particleEffects$color = color;
	}
}
