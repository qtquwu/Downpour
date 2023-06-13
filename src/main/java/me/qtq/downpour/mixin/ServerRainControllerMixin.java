package me.qtq.downpour.mixin;

import me.qtq.downpour.Downpour;
import net.minecraft.network.packet.s2c.play.GameStateChangeS2CPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.level.ServerWorldProperties;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;
import net.minecraft.util.math.MathHelper;

@Mixin(ServerWorld.class)
public abstract class ServerRainControllerMixin extends RainControllerMixin {
    @Shadow @Final private MinecraftServer server;
    private float rainStrength;

    @Accessor
     abstract ServerWorldProperties getWorldProperties();

    @Override
    public float getRainStrength() {
        Integer rainTimer = this.getWorldProperties().getRainTime();
        this.rainStrength = (float) rainTimer / 24000.0f;
        return this.rainStrength;
    }

    @Override
    protected void scaleWithBiome(BlockPos pos, CallbackInfoReturnable<Boolean> info) {
        getRainStrength();
        info.setReturnValue(Downpour.isRainingAtPos((World)(Object)this, pos));
    }
    // These functions are a little dangerous; they change the rain and clear timer but unfortunately if additional
    // UniformIntProviders are added, there's risk of it causing incorrect behaviors.
    @ModifyArgs(method = "<clinit>", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/intprovider/UniformIntProvider;create(II)Lnet/minecraft/util/math/intprovider/UniformIntProvider;", ordinal = 0))
    private static void changeClearWeatherTimer(Args args) {
        // Set the minimum clear time to 3000 ticks, maximum to 7320 ticks. This seems arbitrary, but makes sure the average rain cycle is precisely 1/2 a day.
        args.set(0, 3000);
        args.set(1, 7320);
    }
    /*
    // Changing from changing a uniform distribution to going Gamma
    @ModifyArgs(method = "<clinit>", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/intprovider/UniformIntProvider;create(II)Lnet/minecraft/util/math/intprovider/UniformIntProvider;", ordinal = 1))
    private static void changeRainWeatherTimer(Args args) {
        args.set(0, 100);
        args.set(1, 24000);
    }
    */

    // Send the rainStrength as a second argument in the rain_started packet so that the client knows where to make it rain this cycle
    // If the client is vanilla, this value is simply discarded. So, it does not cause problems.
    @ModifyArgs(method = "tickWeather()V", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/packet/s2c/play/GameStateChangeS2CPacket;<init>(Lnet/minecraft/network/packet/s2c/play/GameStateChangeS2CPacket$Reason;F)V"))
    private void sendRainStrength(Args args) {

        GameStateChangeS2CPacket.Reason reason = args.get(0);
        float f = args.get(1);
        if (reason == GameStateChangeS2CPacket.RAIN_STARTED) {
            this.rainStrength = (float) getWorldProperties().getRainTime() / 24000.0f;
            System.out.println("Rain Strength Sent: " + rainStrength);
            args.set(1, rainStrength);
        }
    }

    @Redirect(method = "tickWeather()V", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/ServerWorldProperties;setRainTime(I)V"))
    private void changeRainTimer(ServerWorldProperties properties, int originalTimer) {
        // Make sure that the timer was changed by replicating the original filtering conditions
        if (properties.getClearWeatherTime() > 0) {
            //Downpour.LOGGER.info("Rain timer not intercepted because getClearWeatherTime is not zero");
            return;
        }
        if (properties.getRainTime() > 0) {
            //Downpour.LOGGER.info("Rain timer not intercepted because it is already nonzero");
            return;
        }
        if (!properties.isRaining()) {
            //Downpour.LOGGER.info("Rain timer not intercepted because it is already raining");
            return;
        }
        int rainTimer = Downpour.getNewRainTime();
        Downpour.LOGGER.info("Rain Timer intercepted, set to " + rainTimer);
        properties.setRainTime(rainTimer);
    }

    // When setWeather is called (i.e. when /weather is used), catch the rain strength and send it to the clients
    @Inject(method = "setWeather", at = @At("HEAD"))
    private void updateRainStrength(int clearDuration, int rainDuration, boolean raining, boolean thundering, CallbackInfo ci) {
        rainStrength = MathHelper.clamp((float) rainDuration / 24000.0f, 0.0f, 1.0f);
        if (raining & rainDuration > 0) {
            this.server.getPlayerManager().sendToAll(new GameStateChangeS2CPacket(GameStateChangeS2CPacket.RAIN_STARTED, rainStrength));
        }
    }


}
