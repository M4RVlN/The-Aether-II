package com.gildedgames.aether.common.blocks.containers;

import com.gildedgames.aether.client.ClientEventHandler;
import com.gildedgames.aether.common.capabilities.entity.player.PlayerAether;
import com.gildedgames.aether.common.entities.tiles.TileEntityTeleporter;
import com.gildedgames.aether.common.registry.content.DimensionsAether;
import com.gildedgames.aether.common.registry.content.InstancesAether;
import com.gildedgames.aether.common.world.necromancer_tower.NecromancerTowerInstance;
import com.gildedgames.aether.common.world.necromancer_tower.NecromancerTowerInstanceHandler;
import com.gildedgames.orbis.api.OrbisAPI;
import com.gildedgames.orbis.api.util.TeleporterGeneric;
import com.gildedgames.orbis.api.util.mc.BlockPosDimension;
import com.gildedgames.orbis.api.world.instances.IInstance;
import com.gildedgames.orbis.api.world.instances.IPlayerInstances;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.PlayerList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Teleporter;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;

import javax.annotation.Nullable;

public class BlockAetherTeleporter extends Block implements ITileEntityProvider
{

	public static final PropertyDirection PROPERTY_FACING = PropertyDirection.create("facing", EnumFacing.Plane.HORIZONTAL);

	public BlockAetherTeleporter()
	{
		super(Material.ROCK);

		this.setHardness(2.5f);
		this.setSoundType(SoundType.STONE);
		this.setDefaultState(this.getBlockState().getBaseState().withProperty(PROPERTY_FACING, EnumFacing.NORTH));
	}

	@Override
	public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta,
			EntityLivingBase placer)
	{
		return this.getDefaultState().withProperty(PROPERTY_FACING, placer.getHorizontalFacing());
	}

	@Override
	public IBlockState getStateFromMeta(int meta)
	{
		return this.getDefaultState().withProperty(PROPERTY_FACING, EnumFacing.getHorizontal(meta));
	}

	@Override
	public int getMetaFromState(IBlockState state)
	{
		return state.getValue(PROPERTY_FACING).getIndex();
	}

	@Override
	public boolean isFullCube(IBlockState state)
	{
		return false;
	}

	@Override
	public boolean isOpaqueCube(IBlockState p_isOpaqueCube_1_)
	{
		return false;
	}


	@Override
	public boolean onBlockActivated(
			final World world, final BlockPos pos, final IBlockState state, final EntityPlayer player, final EnumHand hand, final EnumFacing facing,
			final float hitX, final float hitY, final float hitZ)
	{
		if (player.world.provider.getDimensionType() == DimensionsAether.AETHER)
		{
			return false;
		}

		if (world.isRemote)
		{
			final PlayerAether playerAether = PlayerAether.getPlayer(player);

			if (!playerAether.getTeleportingModule().hasPlayedIntro())
			{
				ClientEventHandler.DRAW_BLACK_SCREEN = true;
			}

			return true;
		}
		else
		{
			final NecromancerTowerInstanceHandler handler = InstancesAether.NECROMANCER_TOWER_HANDLER;

			final PlayerAether playerAether = PlayerAether.getPlayer(player);
			final IPlayerInstances hook = OrbisAPI.instances().getPlayer(player);

			if (playerAether.getTeleportingModule().getAetherPos() != null)
			{
				if (hook.getInstance() != null)
				{
					final IInstance instance = hook.getInstance();

					instance.onLeave(player);
				}

				final EntityPlayerMP playerMP = (EntityPlayerMP) player;
				final BlockPosDimension p = playerAether.getTeleportingModule().getAetherPos();

				final MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();

				final Teleporter teleporter = new TeleporterGeneric(server.getWorld(player.dimension));
				final PlayerList playerList = server.getPlayerList();
				playerList.transferPlayerToDimension(playerMP, p.getDim(), teleporter);
				player.timeUntilPortal = player.getPortalCooldown();

				playerMP.connection.setPlayerLocation(p.getX(), p.getY(), p.getZ(), 225, 0);
			}
			else if (hook.getInstance() != null)
			{
				final IInstance instance = hook.getInstance();

				if (player.dimension == instance.getDimensionId())
				{
					handler.teleportBack((EntityPlayerMP) player);
				}
			}
			else
			{
				final NecromancerTowerInstance inst = handler.get(new BlockPosDimension(pos, world.provider.getDimension()));

				playerAether.getTeleportingModule()
						.setNonAetherPos(new BlockPosDimension((int) player.posX, (int) player.posY, (int) player.posZ, player.dimension));

				handler.teleportToInst((EntityPlayerMP) player, inst);
			}

			return true;
		}
	}

	@Nullable
	@Override
	public TileEntity createNewTileEntity(World world, int i)
	{
		return new TileEntityTeleporter();
	}

	@Override
	protected BlockStateContainer createBlockState()
	{
		return new BlockStateContainer(this, PROPERTY_FACING);
	}
}