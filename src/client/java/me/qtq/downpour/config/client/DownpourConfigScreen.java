package me.qtq.downpour.config.client;

import me.qtq.downpour.Downpour;
import me.qtq.downpour.config.DownpourConfig;
import me.shedaniel.clothconfig2.api.AbstractConfigListEntry;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;


public class DownpourConfigScreen {

    DownpourConfig config = Downpour.config;

    public Screen getConfigScreen(Screen parent) {
        ConfigBuilder builder = ConfigBuilder.create()
                .setParentScreen(parent)
                .setTitle(Text.translatable("downpour.config.title"));

        builder.setSavingRunnable(() -> {
            Downpour.saveConfig();
        });

        ConfigCategory general = builder.getOrCreateCategory(Text.translatable("downpour.config.category.general"));
        ConfigCategory distribution = builder.getOrCreateCategory(Text.translatable("downpour.config.category.distribution"));

        ConfigEntryBuilder entryBuilder = builder.entryBuilder();

        AbstractConfigListEntry minRainTimeEntry = entryBuilder
                .startFloatField(Text.translatable("downpour.config.min_rain_time"),
                        config.minRainTime)
                .setDefaultValue(0.0f)
                .setSaveConsumer(newValue -> config.minRainTime = newValue)
                .build();

        AbstractConfigListEntry maxRainTimeEntry = entryBuilder
                .startFloatField(Text.translatable("downpour.config.max_rain_time"),
                        config.maxRainTime)
                .setDefaultValue(24000.0f)
                .setSaveConsumer(newValue -> config.maxRainTime = newValue)
                .build();

        AbstractConfigListEntry disclaimer = entryBuilder
                .startTextDescription(Text.translatable("downpour.config.disclaimer"))
                .build();

        AbstractConfigListEntry distributionEntry = entryBuilder
                .startEnumSelector(Text.translatable("downpour.config.distribution"),
                        DownpourConfig.RandomDistribution.class,
                        config.rainDistribution)
                .setDefaultValue(DownpourConfig.RandomDistribution.BETA)
                .setTooltip(Text.translatable("downpour.config.distribution.tooltip"))
                .setSaveConsumer(newValue -> config.rainDistribution = newValue)
                .build();

        AbstractConfigListEntry distributionArg1Entry = entryBuilder
                .startFloatField(Text.translatable("downpour.config.argument1"),
                        config.distributionArg1)
                .setDefaultValue(0.5f)
                .setTooltip(Text.translatable("downpour.config.argument1.tooltip"))
                .setSaveConsumer(newValue -> config.distributionArg1 = newValue)
                .build();

        AbstractConfigListEntry distributionArg2Entry = entryBuilder
                .startFloatField(Text.translatable("downpour.config.argument2"),
                        config.distributionArg2)
                .setDefaultValue(2.0f)
                .setTooltip(Text.translatable("downpour.config.argument2.tooltip"))
                .setSaveConsumer(newValue -> config.distributionArg2 = newValue)
                .build();

        general.addEntry(minRainTimeEntry);
        general.addEntry(maxRainTimeEntry);

        distribution.addEntry(disclaimer);
        distribution.addEntry(distributionEntry);
        distribution.addEntry(distributionArg1Entry);
        distribution.addEntry(distributionArg2Entry);

        return builder.build();
    }

}
