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

	// This is su ed outside of isRainingAtPos
	public static boolean isRainingByValue(float downfall, float rainStrength) {
		return 1.0 - downfall < rainStrength;
	}
	public static boolean isRainingAtPos(World world, BlockPos pos) {
		if (world == null)
			return false;

		Biome biome = world.getBiome(pos).value();
		float downfall = ((BiomeAccessor)(Object)biome).getWeather().downfall();
		float rainStrength = ((IRainStrength)world).getRainStrength();

		return isRainingByValue(downfall, rainStrength);
	}


	@Override
	public void onInitialize() {

	}
}