package me.qtq.downpour.mixin.client;

import me.qtq.downpour.IRainable;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.network.packet.s2c.play.GameStateChangeS2CPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayNetworkHandler.class)
public class ClientCatchRainStrength {
	@Shadow
	private ClientWorld world;

	@Inject(at = @At("HEAD"), method = "onGameStateChange")
	private void catchRainStrength(GameStateChangeS2CPacket packet, CallbackInfo info) {
		if (world == null)
			return;
		if (packet.getReason() == GameStateChangeS2CPacket.RAIN_STARTED) {
			// if the packet value is equal to zero, the server is unmodded. Assume maximum strength.
			if (packet.getValue() != 0.0f) {
				((IRainable) world).setRainStrength(packet.getValue());
			} else {
				((IRainable) world).setRainStrength(1.0f);
			}
		} else if (packet.getReason() == GameStateChangeS2CPacket.RAIN_STOPPED) {
			((IRainable) world).setRainStrength(0.0f);
		} else if (packet.getReason() == GameStateChangeS2CPacket.RAIN_GRADIENT_CHANGED) {
			world.setRainGradient(packet.getValue());
		}
	}
}