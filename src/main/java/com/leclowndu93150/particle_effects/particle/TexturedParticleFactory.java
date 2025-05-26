package com.leclowndu93150.particle_effects.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;
import com.leclowndu93150.particle_effects.utils.*;

public class TexturedParticleFactory implements ParticleProvider<SimpleParticleType> {

	private final SpriteSet spriteProvider;

	public TexturedParticleFactory(SpriteSet spriteProvider) {
		this.spriteProvider = spriteProvider;
	}

	@Override
	public Particle createParticle(SimpleParticleType effect, ClientLevel clientWorld, double d, double e, double f, double g, double h, double i) {
		TexturedParticle texturedParticle = new TexturedParticle(clientWorld, d, e, f, g, h, i, this.spriteProvider);
		int color = ((PEType) effect).particleEffects$getColor();
		texturedParticle.setAlpha((float) ArgbUtils.getAlpha(color) / 255F);
		return texturedParticle;
	}
}