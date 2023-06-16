package me.qtq.downpour.mixin;

import me.qtq.downpour.Downpour;
import net.minecraft.registry.RegistryEntryLookup;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.OverworldBiomeCreator;
import net.minecraft.world.biome.SpawnSettings;
import net.minecraft.world.gen.carver.ConfiguredCarver;
import net.minecraft.world.gen.feature.PlacedFeature;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(OverworldBiomeCreator.class)
public abstract class BiomeCreatorMixin {

    @Inject(method = "createSavanna", at = @At("HEAD"))
    private static void ping(RegistryEntryLookup<PlacedFeature> featureLookup, RegistryEntryLookup<ConfiguredCarver<?>> carverLookup, boolean windswept, boolean plateau, CallbackInfoReturnable<Biome> cir) {
        Downpour.LOGGER.info("Create Savanna Entered");
    }

    /*
    // Make savannas have rare but nonzero downfall
    @ModifyArg(method = "createSavanna", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/biome/OverworldBiomeCreator;createBiome(ZFFLnet/minecraft/world/biome/SpawnSettings$Builder;Lnet/minecraft/world/biome/GenerationSettings$LookupBackedBuilder;Lnet/minecraft/sound/MusicSound;)Lnet/minecraft/world/biome/Biome;"), index = 0)
    private static boolean changeSavannaPrecipitation(boolean precipitation) {
        System.out.println("Savanna modified");
        return true;
    }
    @ModifyArg(method = "createSavanna", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/biome/OverworldBiomeCreator;createBiome(ZFFLnet/minecraft/world/biome/SpawnSettings$Builder;Lnet/minecraft/world/biome/GenerationSettings$LookupBackedBuilder;Lnet/minecraft/sound/MusicSound;)Lnet/minecraft/world/biome/Biome;"), index = 2)
    private static float changeSavannaDownfall(float downfall) {
        System.out.println("Savanna modified");
        return 0.1f;
    }
     */
}
