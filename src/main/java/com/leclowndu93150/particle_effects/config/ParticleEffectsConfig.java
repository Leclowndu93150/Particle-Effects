package com.leclowndu93150.particle_effects.config;

import lombok.Getter;
import net.minecraftforge.common.ForgeConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

public class ParticleEffectsConfig {

	public static final ForgeConfigSpec CLIENT_SPEC;
	public static final ClientConfig CLIENT;

	static {
		final Pair<ClientConfig, ForgeConfigSpec> clientSpecPair = new ForgeConfigSpec.Builder().configure(ClientConfig::new);
		CLIENT_SPEC = clientSpecPair.getRight();
		CLIENT = clientSpecPair.getLeft();
	}

	@Getter
	public static class ClientConfig {
		public final ForgeConfigSpec.BooleanValue modEnabled;
		public final ForgeConfigSpec.BooleanValue debugLogEnabled;

		public ClientConfig(ForgeConfigSpec.Builder builder) {
			builder.comment("Particle Effects Configuration").push("general");

			modEnabled = builder
					.comment("Enable/disable the mod")
					.define("mod_enabled", true);

			debugLogEnabled = builder
					.comment("Enable debug logging")
					.define("debug_log", false);

			builder.pop();
		}
	}
}