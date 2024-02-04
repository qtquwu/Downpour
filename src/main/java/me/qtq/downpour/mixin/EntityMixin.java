package me.qtq.downpour.mixin;

import me.qtq.downpour.Downpour;
import me.qtq.downpour.IBrightness;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public class EntityMixin {

    @Inject(method = "getBrightnessAtEyes", at = @At("RETURN"), cancellable = true)
    private void getLocalBrightnessAtEyes(CallbackInfoReturnable<Float> info) {
        Vec3d eyesPos = ((Entity) (Object) this).getEyePos();
        World world = ((Entity) (Object) this).getWorld();
        if (world.isChunkLoaded(BlockPos.ofFloored(eyesPos.x, eyesPos.y, eyesPos.z))) {
            // Check if the entity is in the overworld or an overworld-like dimension
            if (world.getDimension().hasSkyLight() && !world.isClient()) {
                float brightness = ((IBrightness) world).getLocalBrightness(BlockPos.ofFloored(eyesPos.x, eyesPos.y, eyesPos.z));

                info.setReturnValue(brightness);
            }
        }
    }
}
