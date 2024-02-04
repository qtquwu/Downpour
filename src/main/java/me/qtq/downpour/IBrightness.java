package me.qtq.downpour;

import net.minecraft.util.math.BlockPos;

public interface IBrightness {
    public float getLocalBrightness(BlockPos pos);
    public int calculateLocalAmbientDarkness(BlockPos pos);
}
