package me.qtq.downpour.mixin;

import net.minecraft.world.biome.Biome;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Accessor;
import net.minecraft.world.biome.Biome.Weather;

@Mixin(Biome.class)
public interface BiomeAccessor {
    @Accessor
    Weather getWeather();
}
