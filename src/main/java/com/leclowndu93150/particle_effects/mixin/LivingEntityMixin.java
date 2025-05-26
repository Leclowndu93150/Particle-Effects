package com.leclowndu93150.particle_effects.mixin;

import com.leclowndu93150.particle_effects.config.ParticleEffectsConfig;
import com.leclowndu93150.particle_effects.utils.ArgbUtils;
import com.leclowndu93150.particle_effects.utils.ListUtils;
import com.llamalad7.mixinextras.injector.wrapoperation.*;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;


import java.util.*;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {

    @Shadow public abstract Map<MobEffect, MobEffectInstance> getActiveEffectsMap();

    @Shadow public abstract RandomSource getRandom();

    @WrapOperation(at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;addParticle(Lnet/minecraft/core/particles/ParticleOptions;DDDDDD)V"), method = "tickEffects")
    private void swapParticle(Level instance, ParticleOptions parameters, double x, double y, double z, double velocityX, double velocityY, double velocityZ, Operation<Void> original) {
        if (!ParticleEffectsConfig.CLIENT.modEnabled.get()) {
            original.call(instance, parameters, x, y, z, velocityX, velocityY, velocityZ);
            return;
        }

        Set<MobEffect> effects = this.getActiveEffectsMap().keySet();
        if (effects.isEmpty()) {
            original.call(instance, parameters, x, y, z, velocityX, velocityY, velocityZ);
            return;
        }

        MobEffect statusEffect = ListUtils.getRandomElement(effects.stream().toList(), this.getRandom());
        if (statusEffect == null) {
            return;
        }

        int color = statusEffect.getColor();

        double red = ArgbUtils.getRed(color) / 255.0;
        double green = ArgbUtils.getGreen(color) / 255.0;
        double blue = ArgbUtils.getBlue(color) / 255.0;

        original.call(instance, parameters, x, y, z, red, green, blue);
    }
}