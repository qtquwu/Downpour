package me.qtq.downpour;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.PersistentState;
import net.minecraft.world.PersistentStateManager;
import net.minecraft.world.World;

public class ServerState extends PersistentState {

    float rainStrength;

    public void setRainStrength(float strength) {
        rainStrength = strength;
        this.markDirty();
    }
    public float getRainStrength() {
        return rainStrength;
    }

    public static ServerState getServerState(ServerWorld world) {
        PersistentStateManager manager = world.getPersistentStateManager();
        ServerState serverState = manager.getOrCreate(
                ServerState::createFromNbt,
                ServerState::new,
                "Downpour"
        );
        return serverState;
    }

    @Override
    public NbtCompound writeNbt(NbtCompound nbt) {
        nbt.putFloat("rainStrength", rainStrength);
        return nbt;
    }
    public static ServerState createFromNbt(NbtCompound nbt) {
        ServerState serverState = new ServerState();
        serverState.rainStrength = nbt.getFloat("rainStrength");
        return serverState;
    }
}
