package me.qtq.downpour.mixin;

import me.qtq.downpour.IBrightness;
import net.minecraft.entity.mob.DrownedEntity;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;


/*
 * The primary purpose of this mixin is to prevent Drowned from pursuing targets when it is thundering globally
 * If it is not thundering locally
 */
@Mixin(DrownedEntity.class)
public class DrownedEntityMixin {

    @Redirect(method="canDrownedAttackTarget", at=@At(value="INVOKE", target="Lnet/minecraft/world/World;isDay()Z"))
    private boolean canDrownedAttackTargetIsDay(World instance) {
        return ((IBrightness)instance).isLocallyDay(((DrownedEntity)(Object)this).getBlockPos());
    }

    @Mixin(targets="net.minecraft.entity.mob.DrownedEntity$LeaveWaterGoal")
    public static class LeaveWaterGoalMixin {
        @Shadow DrownedEntity drowned;
        @Redirect(method="canStart", at=@At(value="INVOKE", target="Lnet/minecraft/world/World;isDay()Z"))
        private boolean canStartIsDay(World instance) {
            return ((IBrightness)(instance)).isLocallyDay(drowned.getBlockPos());
        }
    }

    @Mixin(targets="net.minecraft.entity.mob.DrownedEntity$TargetAboveWaterGoal")
    public static class TargetAboveWaterGoalMixin {
        @Shadow DrownedEntity drowned;
        @Redirect(method="canStart", at=@At(value="INVOKE", target="Lnet/minecraft/world/World;isDay()Z"))
        private boolean canStartIsDay(World instance) {
            return ((IBrightness)(instance)).isLocallyDay(drowned.getBlockPos());
        }
    }

    @Mixin(targets="net.minecraft.entity.mob.DrownedEntity$WanderAroundOnSurfaceGoal")
    public static class WanderAroundOnSurfaceGoalMixin {
        @Shadow PathAwareEntity mob;
        @Redirect(method="canStart", at=@At(value="INVOKE", target="Lnet/minecraft/world/World;isDay()Z"))
        private boolean canStartIsDay(World instance) {
            return ((IBrightness)(instance)).isLocallyDay(mob.getBlockPos());
        }
    }

}
