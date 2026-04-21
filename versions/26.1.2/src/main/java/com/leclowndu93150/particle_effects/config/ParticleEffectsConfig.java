package com.leclowndu93150.particle_effects.config;

import net.neoforged.neoforge.common.ModConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

public class ParticleEffectsConfig {

	public static final ModConfigSpec CLIENT_SPEC;
	public static final ClientConfig CLIENT;

	static {
		final Pair<ClientConfig, ModConfigSpec> clientSpecPair = new ModConfigSpec.Builder().configure(ClientConfig::new);
		CLIENT_SPEC = clientSpecPair.getRight();
		CLIENT = clientSpecPair.getLeft();
	}

	public static class ClientConfig {
		public final ModConfigSpec.BooleanValue modEnabled;
		public final ModConfigSpec.BooleanValue debugLogEnabled;

		public ClientConfig(ModConfigSpec.Builder builder) {
			builder.push("general");

			modEnabled = builder
					.define("mod_enabled", true);

			debugLogEnabled = builder
					.define("debug_log", false);

			builder.pop();
		}
	}
}
