package com.gildedgames.aether.common.network;

import com.gildedgames.aether.common.AetherCore;
import com.gildedgames.aether.common.network.packets.*;
import com.gildedgames.aether.common.network.packets.dialog.*;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EntityTracker;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.management.PlayerList;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class NetworkingAether
{
	private static SimpleNetworkWrapper instance;

	private static int discriminant;

	public static void preInit()
	{
		instance = NetworkRegistry.INSTANCE.newSimpleChannel(AetherCore.MOD_ID);

		// S E R V E R
		instance.registerMessage(PacketSpecialMovement.HandlerServer.class, PacketSpecialMovement.class, discriminant++, Side.SERVER);
		instance.registerMessage(PacketOpenTab.HandlerServer.class, PacketOpenTab.class, discriminant++, Side.SERVER);
		instance.registerMessage(PacketCloseDialog.HandlerServer.class, PacketCloseDialog.class, discriminant++, Side.SERVER);
		instance.registerMessage(PacketSetPlayedIntro.HandlerServer.class, PacketSetPlayedIntro.class, discriminant++, Side.SERVER);
		instance.registerMessage(PacketActivateButton.HandlerServer.class, PacketActivateButton.class, discriminant++, Side.SERVER);
		instance.registerMessage(PacketMasonryRecipeChanged.HandlerServer.class, PacketMasonryRecipeChanged.class, discriminant++, Side.SERVER);
		instance.registerMessage(PacketMasonryInputCountChanged.HandlerServer.class, PacketMasonryInputCountChanged.class, discriminant++, Side.SERVER);
		instance.registerMessage(PacketCancelIntro.HandlerServer.class, PacketCancelIntro.class, discriminant++, Side.SERVER);
		instance.registerMessage(PacketSetShouldRespawnAtCampfire.HandlerServer.class, PacketSetShouldRespawnAtCampfire.class, discriminant++, Side.SERVER);
		instance.registerMessage(PacketSetPlayerConfig.HandlerServer.class, PacketSetPlayerConfig.class, discriminant++, Side.SERVER);
		instance.registerMessage(PacketAdvance.HandlerServer.class, PacketAdvance.class, discriminant++, Side.SERVER);
		instance.registerMessage(PacketShopSell.HandlerServer.class, PacketShopSell.class, discriminant++, Side.SERVER);
		instance.registerMessage(PacketShopBack.HandlerServer.class, PacketShopBack.class, discriminant++, Side.SERVER);
		instance.registerMessage(PacketShopBuy.HandlerServer.class, PacketShopBuy.class, discriminant++, Side.SERVER);

		// C L I E N T
		instance.registerMessage(PacketEquipment.HandlerClient.class, PacketEquipment.class, discriminant++, Side.CLIENT);
		instance.registerMessage(PacketMarkPlayerDeath.HandlerClient.class, PacketMarkPlayerDeath.class, discriminant++, Side.CLIENT);
		instance.registerMessage(PacketMoaJump.HandlerClient.class, PacketMoaJump.class, discriminant++, Side.CLIENT);
		instance.registerMessage(PacketOpenDialog.HandlerClient.class, PacketOpenDialog.class, discriminant++, Side.CLIENT);
		instance.registerMessage(PacketLatchSwet.HandlerClient.class, PacketLatchSwet.class, discriminant++, Side.CLIENT);
		instance.registerMessage(PacketDetachSwet.HandlerClient.class, PacketDetachSwet.class, discriminant++, Side.CLIENT);
		instance.registerMessage(PacketSetPlayedIntro.HandlerClient.class, PacketSetPlayedIntro.class, discriminant++, Side.CLIENT);
		instance.registerMessage(PacketSwitchToAetherInventory.HandlerClient.class, PacketSwitchToAetherInventory.class, discriminant++, Side.CLIENT);
		instance.registerMessage(PacketSwitchToMinecraftInventory.HandlerClient.class, PacketSwitchToMinecraftInventory.class, discriminant++, Side.CLIENT);
		instance.registerMessage(PacketCloseLoadingScreen.HandlerClient.class, PacketCloseLoadingScreen.class, discriminant++, Side.CLIENT);
		instance.registerMessage(PacketLoadingScreenPercent.HandlerClient.class, PacketLoadingScreenPercent.class, discriminant++, Side.CLIENT);
		instance.registerMessage(PacketCampfires.HandlerClient.class, PacketCampfires.class, discriminant++, Side.CLIENT);
		instance.registerMessage(PacketPreventDropsInventories.HandlerClient.class, PacketPreventDropsInventories.class, discriminant++, Side.CLIENT);
		instance.registerMessage(PacketSeparateInventoryModule.HandlerClient.class, PacketSeparateInventoryModule.class, discriminant++, Side.CLIENT);
		instance.registerMessage(PacketAerbunnySetRiding.HandlerClient.class, PacketAerbunnySetRiding.class, discriminant++, Side.CLIENT);
		instance.registerMessage(PacketRequestClientInfo.HandlerClient.class, PacketRequestClientInfo.class, discriminant++, Side.CLIENT);
		instance.registerMessage(PacketProgressModule.HandlerClient.class, PacketProgressModule.class, discriminant++, Side.CLIENT);
		instance.registerMessage(PacketTalkedTo.HandlerClient.class, PacketTalkedTo.class, discriminant++, Side.CLIENT);
		instance.registerMessage(PacketConditionsMetData.HandlerClient.class, PacketConditionsMetData.class, discriminant++, Side.CLIENT);
		instance.registerMessage(PacketActivateButton.HandlerClient.class, PacketActivateButton.class, discriminant++, Side.CLIENT);
		instance.registerMessage(PacketNavigateNode.HandlerClient.class, PacketNavigateNode.class, discriminant++, Side.CLIENT);
		instance.registerMessage(PacketNavigateBack.HandlerClient.class, PacketNavigateBack.class, discriminant++, Side.CLIENT);
		instance.registerMessage(PacketAdvance.HandlerClient.class, PacketAdvance.class, discriminant++, Side.CLIENT);
		instance.registerMessage(PacketCurrencyModule.HandlerClient.class, PacketCurrencyModule.class, discriminant++, Side.CLIENT);

		NetworkRegistry.INSTANCE.registerGuiHandler(AetherCore.INSTANCE, new AetherGuiHandler());
	}

	public static void sendPacketToDimension(final IMessage message, final int dimension)
	{
		NetworkingAether.instance.sendToDimension(message, dimension);
	}

	public static void sendPacketToAllPlayers(final IMessage message)
	{
		NetworkingAether.instance.sendToAll(message);
	}

	public static void sendPacketToAllPlayersExcept(final IMessage message, final EntityPlayerMP player)
	{
		final PlayerList playerList = FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList();

		for (final EntityPlayerMP p : playerList.getPlayers())
		{
			if (p != player)
			{
				NetworkingAether.instance.sendTo(message, p);
			}
		}
	}

	public static void sendPacketToPlayer(final IMessage message, final EntityPlayerMP player)
	{
		NetworkingAether.instance.sendTo(message, player);
	}

	public static void sendPacketToWatching(final IMessage message, final EntityLivingBase entity, final boolean includeSelf)
	{
		final WorldServer world = (WorldServer) entity.world;

		final EntityTracker tracker = world.getEntityTracker();

		for (final EntityPlayer player : tracker.getTrackingPlayers(entity))
		{
			NetworkingAether.sendPacketToPlayer(message, (EntityPlayerMP) player);
		}

		// Entities don't watch themselves, take special care here
		if (includeSelf && entity instanceof EntityPlayer)
		{
			NetworkingAether.sendPacketToPlayer(message, (EntityPlayerMP) entity);
		}
	}

	@SideOnly(Side.CLIENT)
	public static void sendPacketToServer(final IMessage message)
	{
		NetworkingAether.instance.sendToServer(message);
	}
}
