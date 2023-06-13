package me.qtq.downpour;

import me.qtq.downpour.mixin.BiomeAccessor;
import net.fabricmc.api.ModInitializer;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Downpour implements ModInitializer {

    public static final Logger LOGGER = LoggerFactory.getLogger("Downpour");

	// This is the actual formula for determining when it is raining based on downfall and rainStrength.
	// Rarely do you need to access this directly, but it is nice if for no other reason to reduce the "knowing" responsibilities of other methods
	public static boolean isRainingByValue(float downfall, float rainStrength) {
		return 1.0 - downfall < rainStrength;
	}
	// Given a biome and the current rainStrength, it determines if it should rain in this biome.
	// Use this method if you do not have access to the exact position OR the current world.
	public static boolean isRainingInBiome(Biome biome, float rainStrength) {
		if (!biome.hasPrecipitation())
			return false;
		float downfall = ((BiomeAccessor)(Object)biome).getWeather().downfall();
		return isRainingByValue(downfall, rainStrength);
	}
	// Given a position in a world, find if it is raining at this location.
	public static boolean isRainingAtPos(World world, BlockPos pos) {
		if (world == null)
			return false;

		Biome biome = world.getBiome(pos).value();
		float rainStrength = ((IRainStrength)world).getRainStrength();

		return isRainingInBiome(biome, rainStrength);
	}


	@Override
	public void onInitialize() {

	}
}