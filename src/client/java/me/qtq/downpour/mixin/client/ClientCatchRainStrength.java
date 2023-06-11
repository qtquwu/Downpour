package me.qtq.downpour.mixin.client;

import me.qtq.downpour.IRainStrength;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.network.packet.s2c.play.GameStateChangeS2CPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayNetworkHandler.class)
public class ClientCatchRainStrength {
	@Inject(at = @At("HEAD"), method = "onGameStateChange")
	private void catchRainStrength(GameStateChangeS2CPacket packet, CallbackInfo info) {
		if (packet.getReason() == GameStateChangeS2CPacket.RAIN_STARTED) {
			// if the packet value is equal to zero, the server is unmodded. Assume maximum strength.
			ClientWorld world = ((ClientPlayNetworkHandler)(Object)this).getWorld();
			if (packet.getValue() != 0.0f) {
				((IRainStrength)world).setRainStrength(packet.getValue());
			}
		}
	}
}