package com.leclowndu93150.particle_effects.compat;

import java.util.stream.Stream;
import net.neoforged.fml.ModList;

public class LoadedMods {

	public static boolean isAnyOldPotionsModLoaded() {
		return Stream.of("oldpotions", "legacy_potion_colors").anyMatch(id -> ModList.get().isLoaded(id));
	}
}
