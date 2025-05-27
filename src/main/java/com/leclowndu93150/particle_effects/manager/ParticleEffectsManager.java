package com.leclowndu93150.particle_effects.manager;

import com.leclowndu93150.particle_effects.config.ParticleEffectsConfig;
import com.leclowndu93150.particle_effects.network.NetworkHandler;
import net.minecraft.core.Holder.Reference;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.alchemy.Potion;
import com.leclowndu93150.particle_effects.ParticleEffects;
import com.leclowndu93150.particle_effects.utils.*;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

import javax.annotation.Nullable;
import java.util.*;
import java.util.stream.*;

public class ParticleEffectsManager {

    public static final DeferredRegister<ParticleType<?>> PARTICLES = DeferredRegister.create(Registries.PARTICLE_TYPE, ParticleEffects.MOD_ID);

    private static final Map<String, RegistryObject<SimpleParticleType>> REGISTERED_PARTICLES = new HashMap<>();
    private static final Map<MobEffect, RegistryObject<SimpleParticleType>> EFFECT_TO_PARTICLE = new HashMap<>();
    private static final Map<Integer, List<ParticleOptions>> COLOR_TO_PARTICLES_MAP = new HashMap<>();
    private static final HashMap<ParticleOptions, MobEffect> MINECRAFT_EFFECTS_WITH_TEXTURED_PARTICLE = getMinecraftEffectWidthTexturedParticles();

    @Nullable
    public static List<ParticleOptions> getParticleEffects(Integer i) {
        return COLOR_TO_PARTICLES_MAP.get(i);
    }

    private static RegistryObject<SimpleParticleType> registerParticleTypeForEffect(MobEffect statusEffect, ResourceLocation effectId) {
        ResourceLocation modEffectId = getModEffectId(statusEffect, effectId);
        String registryName = modEffectId.getPath();

        RegistryObject<SimpleParticleType> holder = PARTICLES.register(registryName, () -> new SimpleParticleType(false));
        REGISTERED_PARTICLES.put(registryName, holder);

        return holder;
    }

    private static ResourceLocation getModEffectId(MobEffect statusEffect, ResourceLocation effectId) {
        boolean bl = MINECRAFT_EFFECTS_WITH_TEXTURED_PARTICLE.containsValue(statusEffect);
        return ParticleEffects.id(effectId.getPath() + (bl ? "_new" : ""));
    }

    public static void onInitialize() {
        for (Reference<MobEffect> reference : BuiltInRegistries.MOB_EFFECT.holders().toList()) {
            MobEffect statusEffect = reference.value();
            ResourceLocation id = reference.key().location();
            if (!id.getNamespace().equals("minecraft")) {
                continue;
            }

            RegistryObject<SimpleParticleType> holder = ParticleEffectsManager.registerParticleTypeForEffect(statusEffect, id);
            EFFECT_TO_PARTICLE.put(statusEffect, holder);
        }
    }

    @SubscribeEvent
    public static void onCommonSetup(FMLCommonSetupEvent event) {
        event.enqueueWork(NetworkHandler::init);
        event.enqueueWork(() -> {
            for (Map.Entry<MobEffect, RegistryObject<SimpleParticleType>> entry : EFFECT_TO_PARTICLE.entrySet()) {
                StatusEffectUtils.swapParticle(entry.getKey(), entry.getValue().get());
            }

            for (Reference<Potion> reference : BuiltInRegistries.POTION.holders().toList()) {
                Potion potion = reference.value();
                ResourceLocation id = reference.key().location();
                if (!id.getNamespace().equals("minecraft")) {
                    continue;
                }

                List<MobEffectInstance> effects = potion.getEffects();

                int color = ArgbUtils.getColorWithoutAlpha(StatusEffectUtils.getColor(effects));

                List<ParticleOptions> particleEffects = effects.stream()
                        .map(MobEffectInstance::getEffect)
                        .flatMap((effect) -> {
                            ParticleOptions particleEffect = ((PEStatusEffect) effect).particleEffects$getParticleEffect();
                            if (particleEffect == null) {
                                ParticleEffects.LOGGER.error("[DEV/Potion Registration] Looks like {} effect with color {} doesn't have textured particle, this shouldn't happen! Skipping it registration.", color, effect.getDisplayName().getString());
                                return Stream.empty();
                            }
                            return Stream.of(particleEffect);
                        })
                        .toList();

                COLOR_TO_PARTICLES_MAP.put(color, particleEffects);
            }

            for (Reference<MobEffect> reference : BuiltInRegistries.MOB_EFFECT.holders().toList()) {
                MobEffect statusEffect = reference.value();
                ResourceLocation id = reference.key().location();
                if (!id.getNamespace().equals("minecraft")) {
                    continue;
                }

                int color = ArgbUtils.getColorWithoutAlpha(statusEffect.getColor());

                ParticleOptions particleEffect = ((PEStatusEffect) statusEffect).particleEffects$getParticleEffect();

                if (particleEffect == null) {
                    ParticleEffects.LOGGER.error("[DEV/Effect Registration] Looks like {} effect with color {} doesn't have textured particle, this shouldn't happen! Skipping it registration.", color, statusEffect.getDisplayName().getString());
                    continue;
                }

                List<ParticleOptions> effects = COLOR_TO_PARTICLES_MAP.get(color);
                if (effects != null) {
                    if (FMLEnvironment.dist.isClient() && ParticleEffectsConfig.CLIENT.debugLogEnabled.get()) {
                        ParticleEffects.LOGGER.warn("[DEV/Effect Registration] Found registered effects for color {} from {} effect, skipping it registration. If you just mod user, ignore it.", color, statusEffect.getDisplayName().getString());
                    }
                } else {
                    COLOR_TO_PARTICLES_MAP.put(color, List.of(particleEffect));
                }
            }
        });
    }

    public static Map<String, RegistryObject<SimpleParticleType>> getRegisteredParticles() {
        return REGISTERED_PARTICLES;
    }

    private static HashMap<ParticleOptions, MobEffect> getMinecraftEffectWidthTexturedParticles() {
        return new HashMap<>();
    }

    public static MobEffect getVanillaStatusEffectByStatusEffect(ParticleOptions parameters) {
        return MINECRAFT_EFFECTS_WITH_TEXTURED_PARTICLE.get(parameters);
    }
}