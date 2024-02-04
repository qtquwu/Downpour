package me.qtq.downpour.mixin;

import me.qtq.downpour.Downpour;
import me.qtq.downpour.IBrightness;
import me.qtq.downpour.IRainable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(World.class)
public abstract class RainControllerMixin implements IRainable, IBrightness {

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


	public float getLocalBrightness(BlockPos pos) {
		int ambientDarkness = calculateLocalAmbientDarkness(pos);

		float f = (float)((World)(Object)this).getLightLevel(pos, ambientDarkness) / 15.0F;
		float g = f / (4.0F - 3.0F * f);

		return MathHelper.lerp(((World)(Object)this).getDimension().ambientLight(), g, 1.0F);
	}

	// Get the ambient darkness at a pos, taking into account rain
	public int calculateLocalAmbientDarkness(BlockPos pos) {
		boolean raining = Downpour.isRainingAtPos((World)(Object)this, pos);

		double d = 1.0D - (raining ? 1.0 : 0.0)
				* (double)(((World)(Object)this).getRainGradient(1.0F) * 5.0F) / 16.0D;
		double e = 1.0D - (raining ? 1.0 : 0.0)
				* (double)(((World)(Object)this).getThunderGradient(1.0F) * 5.0F) / 16.0D;
		double f = 0.5D + 2.0D * MathHelper.clamp((double)MathHelper.cos(((World)(Object)this).getSkyAngle(1.0F) * 6.2831855F), -0.25D, 0.25D);

		int ambientDarkness = (int)((1.0D - f * d * e) * 11.0D);

		return ambientDarkness;
	}


	// Get the biome at the position, determine its downfall level, and determine if the rain gradient matches that level
	@Inject(at = @At("TAIL"), method = "hasRain", cancellable = true)
	protected void posHasRain(BlockPos pos, CallbackInfoReturnable<Boolean> info) {
		info.setReturnValue(Downpour.isRainingAtPos((World)(Object)this, pos));
	}

}