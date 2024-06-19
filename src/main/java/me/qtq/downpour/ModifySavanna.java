package me.qtq.downpour;

import net.fabricmc.fabric.api.biome.v1.*;
import net.minecraft.registry.tag.BiomeTags;
import net.minecraft.util.Identifier;


/*
    The goal of this class is to make it rain in Savannas
    This actually works sort of! The problem is that it only works server-side
    Which is fine except that I need to tell whether it is raining on the client as well
        I could potentially make this work anyway, but it would be an atrocious hack
        so I'm not gonna!
    TODO: FIgure out a way to make this work
*/
public class ModifySavanna {
    public static void modify() {
        BiomeModifications.create(Identifier.of("downpour", "biome_modifier"))
                .add(ModificationPhase.ADDITIONS,
                        BiomeSelectors.tag(BiomeTags.IS_SAVANNA),
                        (selectionContext, context) -> modifyDownfall(selectionContext, context));
    }

    private static void modifyDownfall(BiomeSelectionContext selectionContext, BiomeModificationContext context) {
        BiomeModificationContext.WeatherContext weatherContext = context.getWeather();
        weatherContext.setPrecipitation(true);
        weatherContext.setDownfall(0.1f);
    }
}
