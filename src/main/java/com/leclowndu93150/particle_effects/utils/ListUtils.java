package com.leclowndu93150.particle_effects.utils;

import java.util.List;
import net.minecraft.util.RandomSource;

import javax.annotation.Nullable;

public class ListUtils {

	@Nullable
	public static <T> T getRandomElement(List<T> list, RandomSource random) {
		if (list.isEmpty()) {
			return null;
		}
		return list.get(random.nextIntBetweenInclusive(0, list.size() - 1));
	}
}
