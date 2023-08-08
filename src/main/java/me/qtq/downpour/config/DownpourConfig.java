package me.qtq.downpour.config;

public class DownpourConfig {

    public enum RandomDistribution {
        UNIFORM, NORMAL, BETA, GAMMA;
    }

    public float minRainTime = 0.0f;
    public float maxRainTime = 24000.0f;

    public RandomDistribution rainDistribution = RandomDistribution.BETA;
    public float distributionArg1 = 0.5f;
    public float distributionArg2 = 2f;

}