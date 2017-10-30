package com.gildedgames.aether.client.renderer;

import com.gildedgames.aether.api.AetherCapabilities;
import com.gildedgames.aether.api.orbis.*;
import com.gildedgames.aether.api.orbis.region.IRegion;
import com.gildedgames.aether.api.orbis.region.Region;
import com.gildedgames.aether.api.orbis.shapes.IShape;
import com.gildedgames.aether.api.orbis.util.RegionHelp;
import com.gildedgames.aether.common.capabilities.entity.player.PlayerAether;
import com.gildedgames.aether.common.capabilities.entity.player.PlayerAetherObserver;
import com.gildedgames.aether.common.capabilities.world.chunk.ChunkAttachment;
import com.gildedgames.orbis.client.renderers.RenderShape;
import com.gildedgames.orbis.common.player.PlayerOrbisModule;
import com.google.common.collect.Lists;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;

import java.util.List;

public class ChunkRendererManager implements PlayerAetherObserver, IWorldObjectGroupObserver, IWorldObjectManagerObserver
{

	private final static Minecraft mc = Minecraft.getMinecraft();

	private final List<IWorldRenderer> allRenders = Lists.newArrayList();

	/**
	 * The active renderer for the player, for example a blueprint in its hand or a region he's selecting.
	 */
	private final List<IWorldRenderer> playerRenderers = Lists.newArrayList();

	private RenderShape activeSelectionRender;

	public static IChunkRendererCapability getChunkRenderer(final World world, final int chunkX, final int chunkZ)
	{
		final IChunkRendererCapability data = ChunkAttachment.get(world).getAttachment(new ChunkPos(chunkX, chunkZ), AetherCapabilities.CHUNK_RENDERER);

		return data;
	}

	public void unload()
	{
		this.activeSelectionRender = null;
		this.allRenders.clear();
		this.playerRenderers.clear();
	}

	public synchronized void render(final World world, final float partialTicks)
	{
		final int renderDistance = mc.gameSettings.renderDistanceChunks;

		final BlockPos playerPos = mc.player.getPosition();

		final int blockDist = renderDistance << 4;
		final BlockPos minPos = new BlockPos(playerPos.getX() - blockDist, 0, playerPos.getZ() - blockDist);
		final BlockPos maxPos = new BlockPos(playerPos.getX() + blockDist, 256, playerPos.getZ() + blockDist);

		final IRegion encompassing = new Region(minPos, maxPos);

		GlStateManager.pushMatrix();

		for (final IWorldRenderer renderer : this.allRenders)
		{
			if (RegionHelp.intersects(renderer.getBoundingBox(), encompassing))
			{
				renderer.doGlobalRendering(world, partialTicks);
				renderer.prepare(world);
			}
		}

		final int minChunkX = minPos.getX() >> 4;
		final int minChunkZ = minPos.getZ() >> 4;

		for (int x = minChunkX; x < minChunkX + 2 * renderDistance; x++)
		{
			for (int z = minChunkZ; z < minChunkZ + 2 * renderDistance; z++)
			{
				this.load(world, x, z);

				final IChunkRendererCapability chunk = getChunkRenderer(world, x, z);

				if (chunk != null)
				{
					chunk.render(world, partialTicks);
				}
			}
		}

		GlStateManager.popMatrix();
	}

	public void load(final World world, final int chunkX, final int chunkZ)
	{
		final IChunkRendererCapability chunk = getChunkRenderer(world, chunkX, chunkZ);

		if (chunk != null && !chunk.hasBeenLoaded())
		{
			for (final IWorldRenderer renderer : this.allRenders)
			{
				if (chunk.shouldHave(renderer))
				{
					chunk.addRenderer(renderer);
				}
			}

			chunk.load();
		}
	}

	public synchronized void addRenderer(final World world, final IWorldRenderer renderer)
	{
		this.allRenders.add(renderer);

		final IRegion region = renderer.getBoundingBox();

		final BlockPos min = region.getMin();
		final BlockPos max = region.getMax();

		final int minChunkX = min.getX() >> 4;
		final int minChunkZ = min.getZ() >> 4;

		final int maxChunkX = max.getX() >> 4;
		final int maxChunkZ = max.getZ() >> 4;

		for (int x = minChunkX; x <= maxChunkX; x++)
		{
			for (int z = minChunkZ; z <= maxChunkZ; z++)
			{
				final IChunkRendererCapability chunk = getChunkRenderer(world, x, z);

				if (chunk != null)
				{
					chunk.addRenderer(renderer);
				}
			}
		}
	}

	public synchronized void removeRenderer(final World world, final IWorldRenderer renderer)
	{
		renderer.onRemoved();

		this.allRenders.remove(renderer);

		final BlockPos min = renderer.getBoundingBox().getMin();
		final BlockPos max = renderer.getBoundingBox().getMax();

		final int minChunkX = min.getX() >> 4;
		final int minChunkZ = min.getZ() >> 4;

		final int maxChunkX = max.getX() >> 4;
		final int maxChunkZ = max.getZ() >> 4;

		for (int x = minChunkX; x <= maxChunkX; x++)
		{
			for (int z = minChunkZ; z <= maxChunkZ; z++)
			{
				final IChunkRendererCapability chunk = getChunkRenderer(world, x, z);

				if (chunk != null)
				{
					chunk.removeRenderer(renderer);
				}
			}
		}
	}

	public void updateRenderer(final World world, final IWorldRenderer renderer)
	{
		this.removeRenderer(world, renderer);
		this.addRenderer(world, renderer);
	}

	@Override
	public void onUpdate(final PlayerAether playerAether)
	{
		final PlayerOrbisModule module = playerAether.getOrbisModule();
		final IShape activeSelection = playerAether.getSelectionModule().getActiveSelection();

		if (this.activeSelectionRender == null)
		{
			this.activeSelectionRender = new RenderShape(activeSelection);

			this.activeSelectionRender.useCustomColors = true;

			this.addPlayerRenderer(playerAether.getEntity().world, this.activeSelectionRender);
		}
		else if (activeSelection != null)
		{
			this.activeSelectionRender.colorBorder = module.powers().getCurrentPower().getClientHandler().getShapeColor(module);
			this.activeSelectionRender.colorGrid = module.powers().getCurrentPower().getClientHandler().getShapeColor(module);

			this.activeSelectionRender.setShape(activeSelection);
		}
		else
		{
			this.activeSelectionRender.setShape(null);
		}

		final List<IWorldRenderer> activeRenderers = playerAether.getOrbisModule().getActiveRenderers();
		final List<IWorldRenderer> renderersToRemove = Lists.newArrayList();

		for (final IWorldRenderer playerRenderer : this.playerRenderers)
		{
			if (!activeRenderers.contains(playerRenderer) && playerRenderer != this.activeSelectionRender)
			{
				renderersToRemove.add(playerRenderer);
			}
		}

		for (final IWorldRenderer renderer : renderersToRemove)
		{
			this.removeRenderer(playerAether.getEntity().world, renderer);
			this.playerRenderers.remove(renderer);
		}

		if (activeRenderers != null && !activeRenderers.isEmpty())
		{
			for (final IWorldRenderer renderer : activeRenderers)
			{
				if (!this.playerRenderers.contains(renderer))
				{
					this.addPlayerRenderer(playerAether.getEntity().world, renderer);
				}
			}
		}
	}

	private void addPlayerRenderer(final World world, final IWorldRenderer renderer)
	{
		this.playerRenderers.add(renderer);
		this.addRenderer(world, renderer);
	}

	private void addShapeRenderer(final World world, final IShape shape)
	{
		final RenderShape renderRegion = new RenderShape(shape);//TODO: It used to copy the region, dunno why

		renderRegion.useCustomColors = true;

		this.addPlayerRenderer(world, renderRegion);
	}

	@Override
	public void onObjectAdded(final IWorldObjectGroup group, final IWorldObject object)
	{
		if (object instanceof IShape)
		{
			final IShape shape = (IShape) object;

			final RenderShape renderRegion = new RenderShape(shape);

			renderRegion.useCustomColors = true;

			final PlayerOrbisModule module = PlayerOrbisModule.get(Minecraft.getMinecraft().player);

			final int color = module.powers().getCurrentPower().getClientHandler().getShapeColor(module);

			renderRegion.colorBorder = color;
			renderRegion.colorGrid = color;

			renderRegion.boxAlpha = 0.1F;

			this.addRenderer(object.getWorld(), renderRegion);
		}
	}

	@Override
	public void onObjectRemoved(final IWorldObjectGroup group, final IWorldObject object)
	{
		final List<IWorldRenderer> renderersToRemove = Lists.newArrayList();

		if (object instanceof IShape)
		{
			for (final IWorldRenderer renderer : this.allRenders)
			{
				if (object == renderer.getRenderedObject())
				{
					renderersToRemove.add(renderer);
				}
			}
		}

		this.allRenders.removeAll(renderersToRemove);
	}

	@Override
	public void onReloaded(final IWorldObjectGroup group)
	{
		for (final IWorldObject object : group.getObjects())
		{
			this.onObjectAdded(group, object);
		}
	}

	@Override
	public void onGroupAdded(final IWorldObjectManager manager, final IWorldObjectGroup group)
	{
		if (!group.containsObserver(this))
		{
			group.addObserver(this);
		}
	}

	@Override
	public void onGroupRemoved(final IWorldObjectManager manager, final IWorldObjectGroup group)
	{
		for (final IWorldObject object : group.getObjects())
		{
			this.onObjectRemoved(group, object);
		}
	}

	@Override
	public void onReloaded(final IWorldObjectManager manager)
	{
		for (final IWorldObjectGroup group : manager.getGroups())
		{
			this.onGroupAdded(manager, group);
		}
	}
}
