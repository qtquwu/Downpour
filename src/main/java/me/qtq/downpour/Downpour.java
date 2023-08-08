package me.qtq.downpour;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import me.qtq.downpour.config.DownpourConfig;
import me.qtq.downpour.mixin.BiomeAccessor;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import org.apache.commons.math3.distribution.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class Downpour implements ModInitializer {

    public static final Logger LOGGER = LoggerFactory.getLogger("Downpour");
	public static AbstractRealDistribution rainTimerDistribution = new BetaDistribution(0.5, 2);

	public static DownpourConfig config;

	// Note: getNewRainTime() is distribution-agnostic, as long as the distribution
	// generates a number between 0.0 and 1.0
	public static int getNewRainTime() {
		return (int) (MathHelper.clamp(Downpour.rainTimerDistribution.sample() * getMaxRainTime(),
				getMinRainTime() / getMaxRainTime(), getMaxRainTime()));
	}

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
		float rainStrength = ((IRainable)world).getRainStrength();

		return isRainingInBiome(biome, rainStrength);
	}

	public static float getMaxRainTime() {
		return config.maxRainTime;
	}
	public static float getMinRainTime() {
		return config.minRainTime;
	}

	public static void updateDistribution(DownpourConfig.RandomDistribution distribution, float arg1, float arg2) {
		// This updates the rainTimerDistribution object from the given parameters
		switch(distribution) {
			case BETA:
				rainTimerDistribution = new BetaDistribution(arg1, arg2);
				break;
			case GAMMA:
				rainTimerDistribution = new GammaDistribution(arg1, arg2);
				break;
			case NORMAL:
				rainTimerDistribution = new NormalDistribution(arg1, arg2);
				break;
			case UNIFORM:
				rainTimerDistribution = new UniformRealDistribution(0.0, 1.0);
				break;
		}
	}

	@Override
	public void onInitialize() {
		loadConfig();
		updateDistribution(config.rainDistribution, config.distributionArg1, config.distributionArg2);

		LOGGER.info("Downpour initialized (common)");
	}


	// Config code largely taken from Actually Unbreaking (https://github.com/wutdahack/ActuallyUnbreakingFabric)
	// no hard feelings?
	public static void saveConfig() {
		// here's where we will save the config :D
		File configFile = new File(FabricLoader.getInstance().getConfigDir().toFile(), "downpour.json");

		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		// If the config directory doesn't exist, we want to make it
		if (!configFile.getParentFile().exists()) {
			configFile.getParentFile().mkdir();
		}
		try {
			FileWriter writer = new FileWriter(configFile);
			writer.write(gson.toJson(config));
			writer.close();
		} catch (IOException e) {
			LOGGER.warn("Could not save Downpour config: " + e.getLocalizedMessage());
		}
		// We have to update the distribution here because it's a saved object
		updateDistribution(config.rainDistribution, config.distributionArg1, config.distributionArg2);
	}
	public static void loadConfig() {
		File configFile = new File(FabricLoader.getInstance().getConfigDir().toFile(), "downpour.json");
		Gson gson = new GsonBuilder().setPrettyPrinting().create();

		if (!configFile.exists()) {
			config = new DownpourConfig();
			saveConfig();
			return;
		}

		try {
			FileReader reader = new FileReader(configFile);
			config = gson.fromJson(reader, DownpourConfig.class);
			reader.close();
		} catch (IOException e) {
			LOGGER.warn("Could not load Downpour config: " + e.getLocalizedMessage());
		}

	}

}