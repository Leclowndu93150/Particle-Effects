package com.leclowndu93150.particle_effects.utils;

import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraftforge.fml.ModList;

import java.util.Collection;

public class StatusEffectUtils {

	public static void swapParticle(MobEffect statusEffect, ParticleOptions particleEffect) {
		((PEStatusEffect) statusEffect).particleEffects$setParticleEffect(particleEffect);
	}

	public static int getColor(Collection<MobEffectInstance> effects) {
		if (!ModList.get().isLoaded("alexscaves")) {
			return PotionUtils.getColor(effects);
		}

		if (effects.isEmpty()) {
			return 3694022;
		} else {
			float f = 0.0F;
			float g = 0.0F;
			float h = 0.0F;
			int j = 0;

			for (MobEffectInstance statusEffectInstance : effects) {
				if (statusEffectInstance.isVisible()) {
					int k = statusEffectInstance.getEffect().getColor();
					int l = statusEffectInstance.getAmplifier() + 1;
					f += (float) (l * (k >> 16 & 255)) / 255.0F;
					g += (float) (l * (k >> 8 & 255)) / 255.0F;
					h += (float) (l * (k & 255)) / 255.0F;
					j += l;
				}
			}

			if (j == 0) {
				return 0;
			} else {
				f = f / (float) j * 255.0F;
				g = g / (float) j * 255.0F;
				h = h / (float) j * 255.0F;
				return (int) f << 16 | (int) g << 8 | (int) h;
			}
		}
	}

}