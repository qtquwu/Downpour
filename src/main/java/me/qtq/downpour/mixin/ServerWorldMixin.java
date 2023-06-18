package me.qtq.downpour.mixin;

import me.qtq.downpour.Downpour;
import net.minecraft.network.packet.s2c.play.GameStateChangeS2CPacket;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.WorldGenerationProgressListener;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.RandomSequencesState;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionOptions;
import net.minecraft.world.level.ServerWorldProperties;
import net.minecraft.world.level.storage.LevelStorage;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;
import net.minecraft.util.math.MathHelper;

import java.util.List;
import java.util.concurrent.Executor;

@Mixin(ServerWorld.class)
public abstract class ServerWorldMixin extends RainControllerMixin {
    private boolean lastRaining = false;
    private float lastRainStrength = 0.0f;
    private float rainStrength;

    @Shadow @Final private MinecraftServer server;

    @Accessor
     abstract ServerWorldProperties getWorldProperties();


    @Override
    public float getRainStrength() {
        if (this.rainStrength != lastRainStrength) {
            Downpour.LOGGER.info("Rain Strength changed to " + this.rainStrength);
        }
        lastRainStrength = rainStrength;
        return this.rainStrength;
    }

    public void beginRain(float strength) {
        this.getWorldProperties().setRaining(true);
        rainStrength = strength;
    }
    public void stopRaining() {
        this.getWorldProperties().setRaining(false);
        rainStrength = 0.0f;
    }
    public void changeRainState(boolean isRaining) {
        lastRaining = this.getWorldProperties().isRaining();
        if (lastRaining != isRaining)
            Downpour.LOGGER.info("Rain State changed on server to " + isRaining);
        if (isRaining)
            beginRain(this.getWorldProperties().getRainTime() / Downpour.MAX_RAIN_TIME);
        else
            stopRaining();
    }

    @Override
    protected void posHasRain(BlockPos pos, CallbackInfoReturnable<Boolean> info) {
        getRainStrength();
        info.setReturnValue(Downpour.isRainingAtPos((World)(Object)this, pos));
    }
    // This function is a little dangerous; it changes the clear timer but unfortunately if additional
    // UniformIntProviders are added, there's risk of it causing incorrect behaviors.
    @ModifyArgs(method = "<clinit>", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/intprovider/UniformIntProvider;create(II)Lnet/minecraft/util/math/intprovider/UniformIntProvider;", ordinal = 0))
    private static void changeClearWeatherTimer(Args args) {
        // Set the minimum clear time to 3000 ticks, maximum to 7320 ticks. This seems arbitrary, but makes sure the average rain cycle is precisely 1/2 of a day.
        args.set(0, 3000);
        args.set(1, 7320);
    }

    @Inject(method = "<init>", at = @At(value = "RETURN"))
    private void initializeRainStrength(MinecraftServer server, Executor workerExecutor, LevelStorage.Session session, ServerWorldProperties properties, RegistryKey worldKey, DimensionOptions dimensionOptions, WorldGenerationProgressListener worldGenerationProgressListener, boolean debugWorld, long seed, List spawners, boolean shouldTickTime, RandomSequencesState randomSequencesState, CallbackInfo ci) {
        if (properties == null)
            return;
        changeRainState(properties.isRaining());
    }

    // Send the rainStrength as a second argument in the rain_started packet so that the client knows where to make it rain this cycle
    // If the client is vanilla, this value is simply discarded. So, it does not cause problems.
    @ModifyArgs(method = "tickWeather()V", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/packet/s2c/play/GameStateChangeS2CPacket;<init>(Lnet/minecraft/network/packet/s2c/play/GameStateChangeS2CPacket$Reason;F)V"))
    private void sendRainStrength(Args args) {

        GameStateChangeS2CPacket.Reason reason = args.get(0);
        float f = args.get(1);
        if (reason == GameStateChangeS2CPacket.RAIN_STARTED) {
            this.rainStrength = (float) getWorldProperties().getRainTime() / Downpour.MAX_RAIN_TIME;
            System.out.println("Rain Strength Sent: " + rainStrength);
            args.set(1, rainStrength);
        }
    }

    @Redirect(method = "tickWeather()V", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/ServerWorldProperties;setRaining(Z)V"))
    private void changeRainTimer(ServerWorldProperties properties, boolean b) {
        // Make sure that the timer was changed by replicating the original filtering conditions
        if (properties.getClearWeatherTime() > 0 || properties.getRainTime() > 0 || properties.isRaining()) {
            properties.setRaining(b);
            return;
        }
        int rainTimer = Downpour.getNewRainTime();
        Downpour.LOGGER.info("Rain Timer intercepted, set to " + rainTimer);
        properties.setRainTime(rainTimer);
        changeRainState(true);
    }

    // When setWeather is called (i.e. when /weather is used), catch the rain strength and send it to the clients
    @Inject(method = "setWeather", at = @At("HEAD"))
    private void updateRainStrength(int clearDuration, int rainDuration, boolean raining, boolean thundering, CallbackInfo ci) {
        rainStrength = MathHelper.clamp((float) rainDuration / Downpour.MAX_RAIN_TIME, 0.0f, 1.0f);
        if (raining & rainDuration > 0) {
            this.server.getPlayerManager().sendToAll(new GameStateChangeS2CPacket(GameStateChangeS2CPacket.RAIN_STARTED, rainStrength));
        }
    }


}
