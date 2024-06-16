package me.qtq.downpour.mixin;

import me.qtq.downpour.IRainable;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/*
 * The main purpose of this mixin is to have bees act according to the local weather of their hive
 * If they do not have a hive, then they should act according to their own local weather
 */
@Mixin(BeeEntity.class)
public class BeeEntityMixin {

    private static boolean isRainingLocally(World world, BeeEntity bee) {
        if (bee.hasHive())
            return ((IRainable)world).isRainingAtPos(bee.getHivePos());
        else
            return ((IRainable)world).isRainingAtPos(bee.getBlockPos());
    }

    @Redirect(method="canEnterHive", at=@At(value="INVOKE",target="Lnet/minecraft/world/World;isRaining()Z"))
    public boolean canEnterHiveIsRaining(World instance) {
        return isRainingLocally(instance, (BeeEntity)(Object)this);
    }

    @Mixin(targets="net.minecraft.entity.passive.BeeEntity$PollinateGoal")
    public static class PollinateGoalMixin {

        private static boolean isRainingLocally(World world, BeeEntity bee) {
            if (bee.hasHive())
                return ((IRainable)world).isRainingAtPos(bee.getHivePos());
            else
                return ((IRainable)world).isRainingAtPos(bee.getBlockPos());
        }

        @Shadow BeeEntity field_20377; // BeeEntity.this - there's a high chance that this will change in updates!
        private BeeEntity bee = field_20377;

        @Redirect(method="canBeeStart", at=@At(value="INVOKE", target="Lnet/minecraft/world/World;isRaining()Z"))
        public boolean canBeeStartIsRaining(World instance) {
            return isRainingLocally(instance, bee);
        }

        @Redirect(method="canBeeContinue", at=@At(value="INVOKE", target="Lnet/minecraft/world/World;isRaining()Z"))
        public boolean canBeeContinueIsRaining(World instance) {
            return isRainingLocally(instance, bee);
        }
    }
}
