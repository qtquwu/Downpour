package me.qtq.downpour.mixin.client;

import me.qtq.downpour.Downpour;
import me.qtq.downpour.ILocalRainClient;
import me.qtq.downpour.mixin.RainControllerMixin;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.function.BooleanSupplier;

@Mixin(ClientWorld.class)
public class ClientWorldMixin extends RainControllerMixin implements ILocalRainClient {
    @Shadow @Final private MinecraftClient client;

    private float rainStrength;
    private float playerRainGradient = 0.0f;
    private float globalRainGradient = 0.0f;

    @Inject(method = "tick", at = @At("HEAD"))
    public void updateRainGradient(BooleanSupplier shouldKeepTicking, CallbackInfo ci) {
        updateRainGradient(client.player.getBlockPos());
    }

    public float getGlobalRainGradient() {
        return globalRainGradient;
    }

    @Override
    public void getRainGradient(float delta, CallbackInfoReturnable<Float> info) {
        info.setReturnValue(playerRainGradient);
    }

    @Override
    public void setRainGradient(float value, CallbackInfo ci) {
        globalRainGradient = value;
    }

    public void updateRainGradient(BlockPos playerPos) {
        playerRainGradient += isRainingAtPos(playerPos) ? 0.01f : -0.01f;
        playerRainGradient = MathHelper.clamp(playerRainGradient, 0.0f, 1.0f);
    }

    @Override
    public float getRainStrength() {
        return rainStrength;
    }
    @Override
    public void setRainStrength(float strength) {
        rainStrength = strength;
    }
    @Override
    public boolean isRainingAtPos(BlockPos pos) {
        return client.player != null && Downpour.isRainingAtPos((ClientWorld)(Object)this, client.player.getBlockPos());
    }
}
