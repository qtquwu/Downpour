package me.qtq.downpour.config.client;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import net.fabricmc.loader.api.FabricLoader;

public class DownpourModMenu implements ModMenuApi {

    DownpourConfigScreen configScreen = new DownpourConfigScreen();

    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        if (FabricLoader.getInstance().isModLoaded("cloth-config"))
            return parent -> configScreen.getConfigScreen(parent);
        return null;
    }

}
