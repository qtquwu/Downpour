# Downpour
### Downpour is a fabric mod that aims to add a little more realism to Minecraft's weather system without feeling out of place in vanilla.

Primarily, the mod makes it so that it rains more in wetter biomes such as jungles than in dryer biomes like plains, but it also changes the typical duration of rain/clear cycles to account for its newfound locality.

There are no plans for a Forge version. Maybe a forge version already exists. I don't know, I don't care much for Forge. The MIT License allows any devs who want to port it to do so.

## Compatibility
Compatible with Iris and Sodium (the mod has to change when rain is rendered, so this could've been an issue).

In theory at least, any mod that adds biomes should include appropriate downfall values. If they do, then there shouldn't be compatibility issues.

Obviously, mods that modify how rain works or rely on weather may not be compatible depending on their implementation. Mix with caution.

Any combination of vanilla/modded server/client pairs work. However, a vanilla client with a modded server will not be able to see where it is raining and where it is not, but will feel its effects. A vanilla server with a modded client will act as though the client were not modded at all. As well, a word of caution - for whatever reason, the calculation to determine when to use riptide is done on the client, so VANILLA CLIENTS ON MODDED SERVERS MAY MOVE ILLEGALLY. It is thus HIGHLY recommended to avoid vanilla clients + modded servers.

For developers: if you need to determine if it is raining at a location in a world that uses this mod, there is a method for this - Downpour.isRainingAtPos(World world, BlockPos pos). If you do not have access to the relevant world object, you can also determine this by biome (using isRainingInBiome(Biome biome)) or by the relevant values (using isRainingByValue(float downfall, float rainStrength)). 
Mods that don't interface with this won't necessarily be strictly incompatible with this mod, but it will lead to odd behaviors as it may detect it is raining where it is not. The converse probably won't happen though.

### How it works (quite a bit technical)
In vanilla, when it starts raining, the server generates a random duration between 12,000-24,000 ticks, corresponding to 0.5 - 1.0 in-game days, during which it will rain. When it clears, it generates a random duration from 12,000-180,000 ticks, corresponding to 0.5 - 7.5 days, during which period it will be clear.

The mod changes this system a bit. Instead of generating a random rain duration of 12,000-24,000 ticks, it generates a random duration of 100-24,000 ticks, favoring shorter storms, enabling shorter "sprinkling" cycles. As well, the clear time is changed from 12,000-180,000 ticks to 3,000-7,320 ticks. The time seems arbitrary, but it makes it so that a full cycle lasts about half of a day on average.

The duration of rain then is compared to the total possible duration to generate a "rain strength" value, indicating how strong a particular rainstorm is. Each biome has an almost-unused value called "downfall," which in vanilla Minecraft is used only to determine if a biome is "humid" or not. The values range from 0.0 - 1.0, with values at or above 0.8 indicating that a biome is humid. In this mod, this value is compared to the rain strength value in each biome to determine if it is raining in this biome.

What this means is that strong rain storms last longer and affect more biomes. Weak rain storms are shorter and affect fewer. For instance, if a storm is generated that lasts only 1000 ticks (50 seconds), its rain strength value will be 1000 / 24000 = 0.0416. Any biome with a downfall value of 1.0 - 0.0416 = 0.9583 will experience this rainstorm - in vanilla, this is only the mushroom fields, but mods may add especially humid biomes.

What is the shortest/longest storm per biome? Keep in mind that the maximum values are just what is generated - you can use /weather to generate longer storms.

| Downfall | Minimum Storm Duration | Maximum Storm Duration |
|----------|------------------------|------------------------|
|    0.0   |     Not Applicable     |     Not Applicable     |
|    0.1   |      21600 ticks       |       24000 ticks      |
|    0.2   |      19200 ticks       |       24000 ticks      |
|    0.3   |      16800 ticks       |       24000 ticks      |
|    0.4   |      14400 ticks       |       24000 ticks      |
|    0.5   |      12000 ticks       |       24000 ticks      |
|    0.6   |       9600 ticks       |       24000 ticks      |
|    0.7   |       7200 ticks       |       24000 ticks      |
|    0.8   |       4800 ticks       |       24000 ticks      |
|    0.9   |       2400 ticks       |       24000 ticks      |
|    1.0   |        100 ticks       |       24000 ticks      |
