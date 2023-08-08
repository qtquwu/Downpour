package me.qtq.downpour.mixin;

import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biome.Weather;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Biome.class)
public interface BiomeAccessor {
    @Accessor
    Weather getWeather();
}
