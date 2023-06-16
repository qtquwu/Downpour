package me.qtq.downpour;

import net.minecraft.util.math.BlockPos;

public interface IRainable {
    float getRainStrength();
    void setRainStrength(float strength);
    boolean isRainingAtPos(BlockPos pos);
}
