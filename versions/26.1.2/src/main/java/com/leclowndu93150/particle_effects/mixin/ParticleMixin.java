package com.leclowndu93150.particle_effects.mixin;

import com.leclowndu93150.particle_effects.utils.PEDebugParticle;
import net.minecraft.client.particle.Particle;
import org.spongepowered.asm.mixin.*;

@Mixin(Particle.class)
public class ParticleMixin implements PEDebugParticle {

	@Unique
	private int particleEffects$debugData = 0;

	@Override
	public void particleEffects$setDebugData(int i) {
		this.particleEffects$debugData = i;
	}

	@Override
	public int particleEffects$getDebugData() {
		return this.particleEffects$debugData;
	}
}
