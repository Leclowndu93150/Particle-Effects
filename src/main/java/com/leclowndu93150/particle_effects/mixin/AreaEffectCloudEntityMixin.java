package com.leclowndu93150.particle_effects.mixin;

import com.leclowndu93150.particle_effects.config.ParticleEffectsConfig;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.core.particles.ColorParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.world.entity.AreaEffectCloud;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;

import com.leclowndu93150.particle_effects.manager.ParticleEffectsManager;
import com.leclowndu93150.particle_effects.utils.*;

import java.util.List;

@Mixin(AreaEffectCloud.class)
public abstract class AreaEffectCloudEntityMixin extends Entity {

	public AreaEffectCloudEntityMixin(EntityType<?> type, Level world) {
		super(type, world);
	}

	// LINGERING POTION
	@ModifyReturnValue(at = @At(value = "RETURN"), method = "getParticle")
	private ParticleOptions swapParticleType(ParticleOptions original) {
		if (!ParticleEffectsConfig.CLIENT.modEnabled.get()) {
			return original;
		}

		if (!(original instanceof ColorParticleOption effect)) {
			return original;
		}
		int color = effect.color;

		List<ParticleOptions> list = ParticleEffectsManager.getParticleEffects(ArgbUtils.getColorWithoutAlpha(color));
		if (list == null || list.isEmpty()) {
			return original;
		}

		ParticleOptions particleEffect = ListUtils.getRandomElement(list, this.level().getRandom());
		if (particleEffect == null) {
			return original;
		}

		((PEType) particleEffect).particleEffects$setColor(color);

		return particleEffect;
	}
}
