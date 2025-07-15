package fr.blackbalrog.oneblock.world.generator;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.generator.BiomeProvider;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.generator.WorldInfo;

import java.util.List;
import java.util.Random;

public class VoidWorldGenerator extends ChunkGenerator
{
	@Override
	public ChunkData generateChunkData(World world, Random random, int chunkX, int chunkZ, BiomeGrid biome)
	{
		ChunkData chunk = createChunkData(world);
		for (int x = 0; x < 16; x++)
		{
			for (int z = 0; z < 16; z++)
			{
				biome.setBiome(x, z, Biome.PLAINS);
			}
		}
		return chunk;
	}
	
	@Override
	public BiomeProvider getDefaultBiomeProvider(WorldInfo worldInfo)
	{
		return new BiomeProvider()
		{
			@Override
			public Biome getBiome(WorldInfo info, int x, int y, int z)
			{
				return Biome.PLAINS;
			}
			@Override
			public List<Biome> getBiomes(WorldInfo info)
			{
				return List.of(Biome.PLAINS);
			}
		};
	}
	
	@Override
	public Location getFixedSpawnLocation(World world, Random random)
	{
		return new Location(world, 0, 100, 0);
	}
}