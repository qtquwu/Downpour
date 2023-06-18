package me.qtq.downpour.mixin.client;

import me.qtq.downpour.Downpour;
import me.qtq.downpour.ILocalRainClient;
import me.qtq.downpour.IRainable;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(WorldRenderer.class)
public abstract class RainRenderer {
    @Shadow
    private ClientWorld world;

    @Redirect(method = "renderWeather", at = @At(value = "INVOKE", target = "net/minecraft/world/biome/Biome.hasPrecipitation ()Z"))
    private boolean getRenderingDownfall(Biome biome) {
        if (world == null)
            return false;
        return Downpour.isRainingInBiome(biome, ((IRainable) world).getRainStrength());
    }

    // The target here is ONLY used for determining if the relevant precipitation is rain at pos
    // So if it is not raining, we don't need to differentiate between SNOW and NONE
    @Redirect(method = "tickRainSplashing", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/biome/Biome;getPrecipitation(Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/world/biome/Biome$Precipitation;"))
    private Biome.Precipitation getPrecipitationAtPos(Biome biome, BlockPos pos) {
        if (world.getBiome(pos).value().getPrecipitation(pos) == Biome.Precipitation.RAIN
                && Downpour.isRainingAtPos(world, pos)) {
            return Biome.Precipitation.RAIN;
        }
        return Biome.Precipitation.NONE;
    }
    @Redirect(method = "tickRainSplashing", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/world/ClientWorld;getRainGradient(F)F"))
    private float splashWithGlobalRainGradient(ClientWorld world, float v) {
        return ((ILocalRainClient)world).getGlobalRainGradient();
    }
    @Redirect(method = "renderWeather", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/world/ClientWorld;getRainGradient(F)F"))
    private float renderGlobalRain(ClientWorld world, float v) {
        return ((ILocalRainClient)world).getGlobalRainGradient();
    }
}