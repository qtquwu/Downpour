package me.qtq.downpour.mixin;

import me.qtq.downpour.Downpour;
import me.qtq.downpour.IRainStrength;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.world.MutableWorldProperties;
import net.minecraft.world.World;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.function.Supplier;

@Mixin(World.class)
public abstract class RainControllerMixin implements IRainStrength {
	@Unique
	private static float rainStrength;

	public void setRainStrength(float strength) {
		rainStrength = strength;
	}
	public float getRainStrength() {
		return rainStrength;
	}

	@Inject(at = @At("RETURN"), method = "<init>(Lnet/minecraft/world/MutableWorldProperties;Lnet/minecraft/registry/RegistryKey;Lnet/minecraft/registry/DynamicRegistryManager;Lnet/minecraft/registry/entry/RegistryEntry;Ljava/util/function/Supplier;ZZJI)V")
	private void init(MutableWorldProperties properties, RegistryKey registryRef, DynamicRegistryManager registryManager, RegistryEntry dimensionEntry, Supplier profiler, boolean isClient, boolean debugWorld, long biomeAccess, int maxChainedNeighborUpdates, CallbackInfo info) {
		Downpour.LOGGER.info("rainStrength initialized to " + rainStrength + " on the " + (isClient ? "client" : "server"));
		rainStrength = properties.isRaining() ? 1.0f : 0.0f; // unfortunately without modifying save files we gotta assume it's always raining at max or min strength on startup
	}

	// Get the biome at the position, determine its downfall level, and determine if the rain gradient matches that level
	@Inject(at = @At("TAIL"), method = "hasRain", cancellable = true)
	protected void scaleWithBiome(BlockPos pos, CallbackInfoReturnable<Boolean> info) {
		info.setReturnValue(Downpour.isRainingAtPos((World)(Object)this, pos));
	}
}