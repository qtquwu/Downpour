package me.qtq.downpour;

import net.fabricmc.api.ClientModInitializer;

public class DownpourClient implements ClientModInitializer {


	@Override
	public void onInitializeClient() {
		Downpour.LOGGER.info("Downpour initialized (client-side)");
	}
}