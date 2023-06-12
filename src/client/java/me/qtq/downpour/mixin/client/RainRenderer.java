package me.qtq.downpour.mixin.client;

import me.qtq.downpour.Downpour;
import me.qtq.downpour.DownpourClient;
import me.qtq.downpour.IRainStrength;
import me.qtq.downpour.mixin.BiomeAccessor;
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
    ClientWorld world;

    @Redirect(method = "renderWeather", at = @At(value = "INVOKE", target = "net/minecraft/world/biome/Biome.hasPrecipitation ()Z"))
    private boolean getRenderingDownfall(Biome instance) {
        if (world == null)
            return false;
        if (DownpourClient.rainStrength < 0.0) {
            ((IRainStrength)world).setRainStrength(world.isRaining() ? 1.0f : 0.0f);
            DownpourClient .rainStrength = world.isRaining() ? 1.0f : 0.0f;
        }
        if (1.0 - ((BiomeAccessor)(Object)instance).getWeather().downfall() < ((IRainStrength)world).getRainStrength()) {
            return true;
        }
        return false;
    }
    @Redirect(method = "tickRainSplashing", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/biome/Biome;getPrecipitation(Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/world/biome/Biome$Precipitation;"))
    private Biome.Precipitation getPrecipitationAtTime(Biome instance, BlockPos pos) {
        if (world.getBiome(pos).value().getPrecipitation(pos) != Biome.Precipitation.RAIN) {
            return Biome.Precipitation.NONE;
        }
        if (1.0 - ((BiomeAccessor)(Object)instance).getWeather().downfall() < ((IRainStrength)world).getRainStrength()) {
            return Biome.Precipitation.RAIN;
        }
        return Biome.Precipitation.NONE;
    }
}
