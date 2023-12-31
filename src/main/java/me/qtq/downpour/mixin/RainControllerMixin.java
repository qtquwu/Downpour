package me.qtq.downpour.mixin;

import me.qtq.downpour.Downpour;
import me.qtq.downpour.IRainable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(World.class)
public abstract class RainControllerMixin implements IRainable {
	@Unique
	private static float rainStrength;

	public void setRainStrength(float strength) {
		rainStrength = strength;
	}

	public float getRainStrength() {
		return rainStrength;
	}

	@Inject(at = @At("HEAD"), method = "setRainGradient")
	public void setRainGradient(float rainGradient, CallbackInfo ci) {
		// intentionally left blank; we just want to be able to override this in clientworld
	}
	@Inject(at = @At("HEAD"), method = "getRainGradient", cancellable = true)
	public void getRainGradient(float delta, CallbackInfoReturnable<Float> cir) {
		// intentionally left blank; we just want to be able to override this in clientworld
	}

	// Get the biome at the position, determine its downfall level, and determine if the rain gradient matches that level
	@Inject(at = @At("TAIL"), method = "hasRain", cancellable = true)
	protected void posHasRain(BlockPos pos, CallbackInfoReturnable<Boolean> info) {
		info.setReturnValue(Downpour.isRainingAtPos((World)(Object)this, pos));
	}
}