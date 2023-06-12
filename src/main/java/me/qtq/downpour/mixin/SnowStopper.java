package me.qtq.downpour.mixin;

import me.qtq.downpour.Downpour;
import me.qtq.downpour.IRainStrength;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import net.minecraft.world.biome.Biome;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Biome.class)
public abstract class SnowStopper {
    // Do not set snow if it is not actually snowing!
    @Inject(method = "canSetSnow", at = @At("HEAD"), cancellable = true)
    public void cannotSetSnow(WorldView world, BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
        if (world instanceof ServerWorld) {
            if (!Downpour.isRainingAtPos((World) world, pos)) {
                cir.setReturnValue(false);
            }
        }
    }
}
