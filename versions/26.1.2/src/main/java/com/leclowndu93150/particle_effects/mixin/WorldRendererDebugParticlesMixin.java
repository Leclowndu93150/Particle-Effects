package com.leclowndu93150.particle_effects.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.vertex.PoseStack;
import com.leclowndu93150.particle_effects.debug.DebugParticleInfoRenderer;
import net.minecraft.client.*;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.state.level.LevelRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LevelRenderer.class)
public class WorldRendererDebugParticlesMixin {

	@Inject(
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/client/renderer/MultiBufferSource$BufferSource;endBatch()V",
					ordinal = 0
			),
			method = "lambda$addMainPass$0"
	)
	private void renderDebugParticles(CallbackInfo ci, @Local PoseStack stack, @Local(argsOnly = true) LevelRenderState state) {
		DebugParticleInfoRenderer.renderAll(stack, state.cameraRenderState.pos, state.cameraRenderState.orientation, Minecraft.getInstance().getDeltaTracker().getGameTimeDeltaPartialTick(false));
	}
}
