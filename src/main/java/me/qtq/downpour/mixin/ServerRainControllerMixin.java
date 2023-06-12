package me.qtq.downpour.mixin;

import me.qtq.downpour.Downpour;
import net.minecraft.network.packet.s2c.play.GameStateChangeS2CPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.intprovider.IntProvider;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.level.ServerWorldProperties;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(ServerWorld.class)
public abstract class ServerRainControllerMixin extends RainControllerMixin {
    @Shadow @Final private MinecraftServer server;
    private float rainStrength;

    @Accessor
     abstract ServerWorldProperties getWorldProperties();

    @Accessor("CLEAR_WEATHER_DURATION_PROVIDER")
    public static void setClearWeatherDurationProvider(IntProvider provider) {
        throw new AssertionError();
    }

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
    @ModifyArgs(method = "<clinit>", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/intprovider/UniformIntProvider;create(II)Lnet/minecraft/util/math/intprovider/UniformIntProvider;", ordinal = 0))
    private static void changeClearWeatherTimer(Args args) {
        args.set(0, 3000);
        args.set(1, 72000);
    }
    @ModifyArgs(method = "<clinit>", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/intprovider/UniformIntProvider;create(II)Lnet/minecraft/util/math/intprovider/UniformIntProvider;", ordinal = 1))
    private static void changeRainWeatherTimer(Args args) {
        args.set(0, 100);
        args.set(1, 24000);
    }
    // Send the rainStrength as a second argument in the rain_started packet so that the client knows where to make it rain this cycle
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
    // When setWeather is called (i.e. when /weather is used), catch the rain strength and send it to the clients
    @Inject(method = "setWeather", at = @At("HEAD"))
    private void updateRainStrength(int clearDuration, int rainDuration, boolean raining, boolean thundering, CallbackInfo ci) {
        rainStrength = (float) rainDuration / 24000.0f;
        if (raining & rainDuration > 0) {
            this.server.getPlayerManager().sendToAll(new GameStateChangeS2CPacket(GameStateChangeS2CPacket.RAIN_STARTED, rainStrength));
        }
    }


}
