package me.qtq.downpour.mixin;

import me.qtq.downpour.Downpour;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.ServerWorldAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(HostileEntity.class)
public class HostileEntityMixin {
    // This inject stops mobs from spawning where it is not thundering when a thunderstorm is occurring globally
    @Inject(method = "isSpawnDark", at = @At("TAIL"), cancellable = true)
    private static void stopMobSpawning(ServerWorldAccess world, BlockPos pos, Random random, CallbackInfoReturnable<Boolean> info) {
        boolean isThunderingAtPos = world.toServerWorld().isThundering() && Downpour.isRainingAtPos(world.toServerWorld(), pos);
        int lightLevel = isThunderingAtPos ? world.getLightLevel(pos, 10) : world.getLightLevel(pos);
        info.setReturnValue(lightLevel <= world.getDimension().monsterSpawnLightTest().get(random));
    }
}
