package me.qtq.downpour.mixin;

import me.qtq.downpour.Downpour;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.command.WeatherCommand;
import net.minecraft.util.math.intprovider.IntProvider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(WeatherCommand.class)
public class WeatherCommandMixin {
    @Inject(method = "processDuration", at = @At("RETURN"), cancellable = true)
    private static void sendGammaDuration(ServerCommandSource source, int duration, IntProvider provider, CallbackInfoReturnable<Integer> cir) {
        cir.setReturnValue(duration == -1 ? Downpour.getNewRainTime() : duration);
    }
}
