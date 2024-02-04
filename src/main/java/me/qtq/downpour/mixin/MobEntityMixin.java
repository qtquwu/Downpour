package me.qtq.downpour.mixin;

import me.qtq.downpour.IBrightness;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(MobEntity.class)
public class MobEntityMixin {
    @Redirect(method = "isAffectedByDaylight", at = @At(value = "INVOKE", target = "net/minecraft/world/World.isDay ()Z"))
    public boolean affectByDaylight(World instance) {
        MobEntity mobEntity = (MobEntity)(Object)this;

        return !instance.getDimension().hasFixedTime() && ((IBrightness)instance).calculateLocalAmbientDarkness(mobEntity.getBlockPos()) < 4;
    }
}
