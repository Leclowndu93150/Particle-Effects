package com.leclowndu93150.particle_effects.manager;

import com.leclowndu93150.particle_effects.config.ParticleEffectsConfig;
import com.leclowndu93150.particle_effects.utils.ArgbUtils;
import com.leclowndu93150.particle_effects.utils.ListUtils;
import com.leclowndu93150.particle_effects.utils.PEType;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.List;

public class ClientHandler {

    public static void processSplashPotionStageOne(LocalRef<List<ParticleOptions>> localParticleEffects, int color) {
        localParticleEffects.set(null);

        if (!ParticleEffectsConfig.CLIENT.modEnabled.get()) {
            return;
        }

        List<ParticleOptions> list = ParticleEffectsManager.getParticleEffects(ArgbUtils.getColorWithoutAlpha(color));
        if (list == null) {
            return;
        }

        localParticleEffects.set(list);
    }

    public static Particle processSplashPotionStageTwo(@Nullable Level world, LevelRenderer instance, ParticleOptions parameters, boolean alwaysSpawn, double x, double y, double z, double velocityX, double velocityY, double velocityZ, com.llamalad7.mixinextras.injector.wrapoperation.Operation<Particle> original, LocalRef<List<ParticleOptions>> localParticleEffects, int color) {
        if (!ParticleEffectsConfig.CLIENT.modEnabled.get()) {
            return original.call(instance, parameters, alwaysSpawn, x, y, z, velocityX, velocityY, velocityZ);
        }

        List<ParticleOptions> list = localParticleEffects.get();
        if (list == null || world == null) {
            return original.call(instance, parameters, alwaysSpawn, x, y, z, velocityX, velocityY, velocityZ);
        }
        ParticleOptions particleEffect = ListUtils.getRandomElement(list, world.getRandom());
        if (particleEffect == null) {
            return original.call(instance, parameters, alwaysSpawn, x, y, z, velocityX, velocityY, velocityZ);
        }
        ((PEType) particleEffect).particleEffects$setColor(-1);
        return original.call(instance, particleEffect, alwaysSpawn, x, y, z, velocityX, velocityY, velocityZ);
    }
}