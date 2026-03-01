package com.leclowndu93150.particle_effects.mixin;

import com.leclowndu93150.particle_effects.manager.ParticleEffectsManager;
import net.minecraft.world.effect.MobEffect;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = MobEffect.class, priority = 989)
public class OldPotionsMobEffectMixin {

	@Shadow @Final private int color;

	@Inject(at = @At("HEAD"), method = "getColor", cancellable = true)
	private void noTodayMisterOldPotions(CallbackInfoReturnable<Integer> cir) {
		if (!ParticleEffectsManager.redirectEnabled) {
			return;
		}
		if (ParticleEffectsManager.redirectToVanillaEffectColors) {
			cir.setReturnValue(this.color);
		}
	}

}
