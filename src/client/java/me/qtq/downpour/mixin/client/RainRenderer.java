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
    private ClientWorld world;

    @Redirect(method = "renderWeather", at = @At(value = "INVOKE", target = "net/minecraft/world/biome/Biome.hasPrecipitation ()Z"))
    private boolean getRenderingDownfall(Biome instance) {
        if (world == null)
            return false;
        // We can initialize rain when client connects by setting value to negative to start
        // The problem is that if you disconnect + reconnect it'll default to the previous world's value
        // Is that a real problem?
        if (DownpourClient.rainStrength < 0.0) {
            ((IRainStrength)world).setRainStrength(world.isRaining() ? 1.0f : 0.0f);
            DownpourClient.rainStrength = world.isRaining() ? 1.0f : 0.0f;
        }
        return Downpour.isRainingByValue(((BiomeAccessor) (Object) instance).getWeather().downfall(), ((IRainStrength) world).getRainStrength());
    }
    // The target here is ONLY used for determining if the relevant precipitation is rain
    // So if it is not raining, we don't need to differentiate between SNOW and NONE
    @Redirect(method = "tickRainSplashing", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/biome/Biome;getPrecipitation(Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/world/biome/Biome$Precipitation;"))
    private Biome.Precipitation getPrecipitationAtTime(Biome instance, BlockPos pos) {
        if (world.getBiome(pos).value().getPrecipitation(pos) != Biome.Precipitation.RAIN) {
            return Biome.Precipitation.NONE;
        }
        if (Downpour.isRainingByValue(((BiomeAccessor)(Object)instance).getWeather().downfall(), ((IRainStrength)world).getRainStrength())) {
            return Biome.Precipitation.RAIN;
        }
        return Biome.Precipitation.NONE;
    }
}
