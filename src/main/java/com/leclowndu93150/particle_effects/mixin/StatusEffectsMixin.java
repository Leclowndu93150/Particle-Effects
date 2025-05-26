package com.leclowndu93150.particle_effects.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.*;
import net.minecraft.world.effect.InstantenousMobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffects;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(MobEffects.class)
public class StatusEffectsMixin {

	@WrapOperation(at = @At(value = "NEW", target = "(Lnet/minecraft/world/effect/MobEffectCategory;I)Lnet/minecraft/world/effect/InstantenousMobEffect;"), method = "<clinit>")
	private static InstantenousMobEffect fixColor(MobEffectCategory statusEffectCategory, int i, Operation<InstantenousMobEffect> original) {
		return original.call(statusEffectCategory, 16262180);
	}

}