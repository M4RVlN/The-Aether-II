package com.gildedgames.aether.common.world.aether.island;

import com.gildedgames.aether.api.world.IAetherChunkColumnInfo;
import com.gildedgames.aether.api.world.islands.IIslandChunkColumnInfo;
import com.gildedgames.aether.api.world.islands.IIslandData;
import com.gildedgames.aether.common.util.helpers.IslandHelper;
import com.gildedgames.aether.common.world.aether.WorldProviderAether;
import com.gildedgames.aether.common.world.aether.prep.AetherChunkColumnInfo;
import com.gildedgames.orbis_api.core.PlacedBlueprint;
import com.gildedgames.orbis_api.data.region.IRegion;
import com.gildedgames.orbis_api.data.region.Region;
import com.gildedgames.orbis_api.preparation.IChunkMaskTransformer;
import com.gildedgames.orbis_api.preparation.impl.ChunkDataContainer;
import com.gildedgames.orbis_api.preparation.impl.ChunkSegmentMask;
import com.gildedgames.orbis_api.processing.BlockAccessChunkDataContainer;
import com.gildedgames.orbis_api.processing.DataPrimer;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.terraingen.PopulateChunkEvent;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;

public class ChunkGeneratorAether implements IChunkGenerator
{

	private final World world;

	private final Random rand;

	private final WorldPreparationAether preparation;

	public ChunkGeneratorAether(final World world, final long seed)
	{
		this.world = world;

		if (!this.world.isRemote)
		{
			this.world.setSeaLevel(255);
		}

		this.rand = new Random(seed);
		this.preparation = new WorldPreparationAether(this.world, WorldProviderAether.get(world).getNoise());
	}

	public WorldPreparationAether getPreparation()
	{
		return this.preparation;
	}

	@Override
	public Chunk generateChunk(final int chunkX, final int chunkZ)
	{
		this.rand.setSeed((long) chunkX * 341873128712L + (long) chunkZ * 132897987541L);

		IIslandData islandData = IslandHelper.get(this.world, chunkX, chunkZ);

		if (islandData == null)
		{
			return new Chunk(this.world, chunkX, chunkZ);
		}

		final Biome[] biomes = this.world.getBiomeProvider().getBiomesForGeneration(null, chunkX * 16, chunkZ * 16, 16, 16);

		AetherChunkColumnInfo info = new AetherChunkColumnInfo(1);
		info.setIslandData(0, this.generateChunkColumnInfo(biomes, islandData, chunkX, chunkZ));

		ChunkSegmentMask[] masks = new ChunkSegmentMask[16];

		for (int chunkY = 0; chunkY < 16; chunkY++)
		{
			masks[chunkY] = new ChunkSegmentMask(chunkX, chunkY, chunkZ);

			this.preparation.generateFull(info, biomes, masks[chunkY], islandData, chunkX, chunkY, chunkZ, world.getSeed());
		}

		IChunkMaskTransformer transformer = islandData.getGenerator().createMaskTransformer(islandData, chunkX, chunkZ);

		final ChunkDataContainer data = ChunkDataContainer.createFromChunkSegmentMasks(masks, transformer, chunkX, chunkZ);

		final BlockAccessChunkDataContainer blockAccess = new BlockAccessChunkDataContainer(this.world, data);

		final DataPrimer dataPrimer = new DataPrimer(blockAccess);

		IRegion region = new Region(new BlockPos(chunkX * 16, 0, chunkZ * 16),
				new BlockPos(chunkX * 16, 255, chunkZ * 16).add(15, 15, 15));

		// Prime placed templates
		for (final PlacedBlueprint instance : islandData.getPlacedBlueprintsInChunk(chunkX, chunkZ))
		{
			dataPrimer.place(instance, region);
		}

		final Chunk chunk = data.createChunk(this.world, chunkX, chunkZ);
		chunk.generateSkylightMap();

		return chunk;
	}

	@Override
	public void populate(final int chunkX, final int chunkZ)
	{
		IIslandData island = IslandHelper.get(this.world, chunkX, chunkZ);

		if (island == null)
		{
			return;
		}

		MinecraftForge.EVENT_BUS.post(new PopulateChunkEvent.Pre(this, this.world, this.rand, chunkX, chunkZ, false));

		final int x = chunkX * 16;
		final int z = chunkZ * 16;

		final BlockPos pos = new BlockPos(x, 0, z);

		final Biome biome = this.world.getBiome(pos.add(16, 0, 16));

		this.rand.setSeed(this.world.getSeed());

		final long seedX = this.rand.nextLong() / 2L * 2L + 1L;
		final long seedZ = this.rand.nextLong() / 2L * 2L + 1L;

		this.rand.setSeed(chunkX * seedX + chunkZ * seedZ ^ this.world.getSeed());

		biome.decorate(this.world, this.rand, pos);
	}

	@Override
	public boolean generateStructures(final Chunk chunkIn, final int x, final int z)
	{
		return false;
	}

	@Override
	public void recreateStructures(final Chunk chunk, final int chunkX, final int chunkZ)
	{

	}

	@Override
	public boolean isInsideStructure(final World worldIn, final String structureName, final BlockPos pos)
	{
		return false;
	}

	@Override
	public List<Biome.SpawnListEntry> getPossibleCreatures(final EnumCreatureType creatureType, final BlockPos pos)
	{
		final Biome biomegenbase = this.world.getBiome(pos);

		if (biomegenbase == null)
		{
			return null;
		}
		else
		{
			return biomegenbase.getSpawnableList(creatureType);
		}
	}

	@Nullable
	@Override
	public BlockPos getNearestStructurePos(final World worldIn, final String structureName, final BlockPos position, final boolean findUnexplored)
	{
		return null;
	}

	public void generateBaseTerrain(IAetherChunkColumnInfo info, Biome[] biomes, ChunkSegmentMask mask, IIslandData islandData, int x, int y, int z)
	{
		this.preparation.generateBaseTerrain(info, biomes, mask, islandData, x, y, z, world.getSeed());
	}

	public IIslandChunkColumnInfo generateChunkColumnInfo(Biome[] biomes, IIslandData islandData, int chunkX, int chunkZ)
	{
		return this.preparation.generateChunkColumnInfo(biomes, islandData, chunkX, chunkZ);
	}
}
