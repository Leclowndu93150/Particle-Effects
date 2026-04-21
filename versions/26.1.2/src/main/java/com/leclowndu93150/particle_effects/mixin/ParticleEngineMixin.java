package com.leclowndu93150.particle_effects.mixin;

import java.util.*;
import net.minecraft.client.particle.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ParticleEngine.class)
public interface ParticleEngineMixin {

	@Accessor("particles")
	Map<ParticleRenderType, ParticleGroup<?>> getParticles();
}
