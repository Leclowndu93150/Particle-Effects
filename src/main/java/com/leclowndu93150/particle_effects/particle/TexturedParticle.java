package com.leclowndu93150.particle_effects.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;

public class TexturedParticle extends SpellParticle {

	private int holderColor;

	protected TexturedParticle(ClientLevel clientWorld, double d, double e, double f, double g, double h, double i, SpriteSet spriteProvider) {
		super(clientWorld, d, e, f, g, h, i, spriteProvider);
		super.pickSprite(spriteProvider);
	}

	public int getHolderColor() {
		return this.holderColor;
	}

	public void setHolderColor(int holderColor) {
		this.holderColor = holderColor;
	}

	@Override
	public void setColor(float red, float green, float blue) {

	}

	@Override
	public void setSpriteFromAge(SpriteSet spriteProvider) {

	}

	@Override
	public void setAlpha(float alpha) {
		super.setAlpha(alpha);
	}

	@Override
	public void tick() {
		super.tick();
	}
}
