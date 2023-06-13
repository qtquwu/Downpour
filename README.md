# Downpour
### Downpour is a mod that aims to add a little more realism to Minecraft's weather system without feeling out of place in vanilla.

Primarily, the mod makes it so that it rains more in wetter biomes such as jungles than in dryer biomes like plains, but it also changes the typical duration of rain/clear cycles to account for its newfound locality.

## Compatibility
Compatible with iris and sodium
Any combination of vanilla/modded server/client pairs work. However, a vanilla client with a modded server will not be able to see where it is raining and where it is not, but will feel its effects. A vanilla server with a modded client will act as though the client were not modded at all.
Obviously, mods that modify how rain works may not be compatible depending on their implementation. Mix with caution.

### How it works (a bit technical)
In vanilla, when it starts raining in Minecraft, the server generates a random duration between 12,000-24,000 ticks, corresponding to 0.5 - 1.0 in-game days. When it clears, it generates a random duration from 12,000-180,000 ticks, corresponding to 0.5 - 7.5 days, during which period it will be clear.

The mod changes this system a bit. Instead of generating a random rain duration of 12,000-24,000 ticks, it generates a random duration of 100-24,000 ticks, enabling shorter "sprinkling" cycles. As well, the clear time is changed from 12,000-180,000 ticks to 3000-72,000 ticks.

The duration of rain then is compared to the total possible duration to generate a "rain strength" value, indicating how strong a particular rainstorm is. Each biome has an almost-unused value called "downfall," which in vanilla Minecraft is used only to determine if a biome is "humid" or not. The values range from 0.0 - 1.0, with values at or above 0.8 indicating that a biome is humid. In this mod, this value is compared to the rain strength value in each biome to determine if it is raining in this biome.

What this means is that strong rain storms last longer and affect more biomes. Weak rain storms are shorter and affect fewer. For instance, if a storm is generated that lasts only 1000 ticks (50 seconds), its rain strength value will be 1000 / 24000 = 0.0416. Any biome with a downfall value of 1.0 - 0.0416 = 0.9583 will experience this rainstorm - in vanilla, this is only the mushroom fields, but mods may add especially humid biomes.

What is the shortest storm per biome? Suppose that a biome has a downfall value of d (usually a multiple of 0.1). Then, we must solve 1.0 - d < duration / 24000 -> duration > 24000 (1.0 - d). We can also determine the average storm and clear duration in a biome using a Markov Chain. I'm a math person but horrible at statistics so I make no promises that these values are 100% accurate. Here's a table of what this all looks like:

| Downfall | Minimum Storm Duration | Average Storm Duration | Maximum Storm Duration | Average Clear Duration|
|----------|------------------------|------------------------|------------------------|-----------------------|
|    0.0   |     Not Applicable     |     Not Applicable     |     Not Applicable     |        Always         |
|    0.1   |      21600 ticks       |      22800 ticks       |       24000 ticks      |      476700 ticks     |
|    0.2   |      19200 ticks       |      21600 ticks       |       24000 ticks      |      227900 ticks     |
|    0.3   |      16800 ticks       |      20400 ticks       |       24000 ticks      |      145760 ticks     |
|    0.4   |      14400 ticks       |      19200 ticks       |       24000 ticks      |      105300 ticks     |
|    0.5   |      12000 ticks       |      18000 ticks       |       24000 ticks      |       81500 ticks     |
|    0.6   |       9600 ticks       |      16800 ticks       |       24000 ticks      |       66030 ticks     |
|    0.7   |       7200 ticks       |      15600 ticks       |       24000 ticks      |       53530 ticks     |
|    0.8   |       4800 ticks       |      14400 ticks       |       24000 ticks      |       47600 ticks     |
|    0.9   |       2400 ticks       |      13200 ticks       |       24000 ticks      |       41860 ticks     |
|    1.0   |        100 ticks       |      12050 ticks       |       24000 ticks      |       37500 ticks     |

To convert to in-game days, divide by 24,000, and to convert to real-world seconds, divide by 20.
Keep in mind the average rain time in vanilla is 18000 ticks (0.75 days) and the average clear time is 96000 ticks (4 days).
These values are very subject to change! In fact, the entire formula may change to give what I feel are "realistic" rain and clear times.
