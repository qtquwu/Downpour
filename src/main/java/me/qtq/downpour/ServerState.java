package me.qtq.downpour;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.PersistentState;
import net.minecraft.world.PersistentStateManager;

public class ServerState extends PersistentState {

    float rainStrength;

    public void setRainStrength(float strength) {
        rainStrength = strength;
        this.markDirty();
    }
    public float getRainStrength() {
        return rainStrength;
    }

    private static Type<ServerState> type = new Type<>(
            ServerState::new,
            ServerState::createFromNbt,
            null
            );

    public static ServerState getServerState(ServerWorld world) {
        PersistentStateManager manager = world.getPersistentStateManager();
        ServerState serverState = manager.getOrCreate(
                type,
                "downpour"
        );
        serverState.markDirty();
        return serverState;
    }

    @Override
    public NbtCompound writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup wrapper) {
        nbt.putFloat("rainStrength", rainStrength);
        return nbt;
    }
    public static ServerState createFromNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup wrapper) {
        ServerState serverState = new ServerState();
        serverState.rainStrength = nbt.getFloat("rainStrength");
        return serverState;
    }
}
