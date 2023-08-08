package me.qtq.downpour.mixin.client;

import me.qtq.downpour.Downpour;
import me.qtq.downpour.IRainable;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.network.packet.s2c.play.GameStateChangeS2CPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayNetworkHandler.class)
public class ClientCatchRainStrength {
	@Shadow
	private ClientWorld world;

	@Inject(at = @At("TAIL"), method = "onGameStateChange")
	private void catchRainStrength(GameStateChangeS2CPacket packet, CallbackInfo info) {
		if (world == null)
			return;
		if (packet.getReason() == GameStateChangeS2CPacket.RAIN_STARTED) {
			// If it is already raining, the game is adjusting its strength rather than starting a new storm
			if (world.isRaining() || world.getRainGradient(1.0f) > 0.0f) {
				world.setRainGradient(1.0f);
			}
			// if the packet value is equal to zero, the server is unmodded. Assume maximum strength.
			if (packet.getValue() == 0.0f) {
				Downpour.LOGGER.info("Rain strength received as 0.0 - assuming vanilla server");
			}
			((IRainable) world).setRainStrength(packet.getValue() != 0.0f ? packet.getValue() : 1.0f);
		} else if (packet.getReason() == GameStateChangeS2CPacket.RAIN_STOPPED) {
			((IRainable) world).setRainStrength(0.0f);
		} else if (packet.getReason() == GameStateChangeS2CPacket.RAIN_GRADIENT_CHANGED) {
			world.setRainGradient(packet.getValue());
		}
	}
	@Redirect(method = "onGameStateChange", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/world/ClientWorld;setRainGradient(F)V"))
	private void cancelSetRainGradient(ClientWorld instance, float v) {
	}
}