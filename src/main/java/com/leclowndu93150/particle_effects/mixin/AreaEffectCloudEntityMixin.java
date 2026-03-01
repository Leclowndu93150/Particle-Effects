package com.leclowndu93150.particle_effects.mixin;

import com.leclowndu93150.particle_effects.capture.ParticleCaptures;
import com.leclowndu93150.particle_effects.config.ParticleEffectsConfig;
import com.llamalad7.mixinextras.injector.wrapoperation.*;
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
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AreaEffectCloud.class)
public abstract class AreaEffectCloudEntityMixin extends Entity {

	@Unique
	private boolean particleEffects$needReset;

	public AreaEffectCloudEntityMixin(EntityType<?> type, Level world) {
		super(type, world);
	}

	// LINGERING POTION
	@WrapOperation(at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/AreaEffectCloud;getParticle()Lnet/minecraft/core/particles/ParticleOptions;"), method = "tick")
	private ParticleOptions swapParticleType(AreaEffectCloud instance, Operation<ParticleOptions> original) {
		ParticleOptions originalParticle = original.call(instance);

		if (!ParticleEffectsConfig.CLIENT.modEnabled.get()) {
			return originalParticle;
		}

		if (!(originalParticle instanceof ColorParticleOption effect)) {
			return originalParticle;
		}
		int color = effect.color;

		List<ParticleOptions> list = ParticleEffectsManager.getParticleEffects(ArgbUtils.getColorWithoutAlpha(color));
		if (list == null || list.isEmpty()) {
			return originalParticle;
		}

		ParticleOptions particleEffect = ListUtils.getRandomElement(list, this.level().getRandom());
		if (particleEffect == null) {
			return originalParticle;
		}

		((PEType) particleEffect).particleEffects$setColor(color);

		ParticleCaptures.setParticle(particleEffect);
		this.particleEffects$needReset = true;
		return particleEffect;
	}

	@Inject(at = @At("TAIL"), method = "tick")
	private void resetParticle(CallbackInfo ci) {
		if (this.particleEffects$needReset) {
			this.particleEffects$needReset = false;
			ParticleCaptures.setParticle(null);
		}
	}

}
