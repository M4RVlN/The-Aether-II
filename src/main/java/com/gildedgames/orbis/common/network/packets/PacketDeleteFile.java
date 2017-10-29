package com.gildedgames.orbis.common.network.packets;

import com.gildedgames.aether.api.io.NBTFunnel;
import com.gildedgames.aether.api.orbis.management.IProject;
import com.gildedgames.aether.api.orbis.management.IProjectIdentifier;
import com.gildedgames.aether.common.AetherCore;
import com.gildedgames.aether.common.network.MessageHandlerClient;
import com.gildedgames.aether.common.network.MessageHandlerServer;
import com.gildedgames.aether.common.network.NetworkingAether;
import com.gildedgames.aether.common.network.util.PacketMultipleParts;
import com.gildedgames.orbis.client.gui.GuiLoadBlueprint;
import com.gildedgames.orbis.client.gui.GuiViewProjects;
import com.gildedgames.orbis.common.OrbisCore;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

import java.io.File;

public class PacketDeleteFile extends PacketMultipleParts
{

	private IProjectIdentifier project;

	private String location;

	public PacketDeleteFile()
	{

	}

	private PacketDeleteFile(final byte[] data)
	{
		super(data);
	}

	public PacketDeleteFile(final IProjectIdentifier project, final String location)
	{
		this.project = project;
		this.location = location;
	}

	@Override
	public void read(final ByteBuf buf)
	{
		final NBTTagCompound tag = ByteBufUtils.readTag(buf);
		final NBTFunnel funnel = AetherCore.io().createFunnel(tag);

		this.project = funnel.get("project");
		this.location = tag.getString("location");
	}

	@Override
	public void write(final ByteBuf buf)
	{
		final NBTTagCompound tag = new NBTTagCompound();
		final NBTFunnel funnel = AetherCore.io().createFunnel(tag);

		funnel.set("project", this.project);
		tag.setString("location", this.location);

		ByteBufUtils.writeTag(buf, tag);
	}

	@Override
	public PacketMultipleParts createPart(final byte[] data)
	{
		return new PacketDeleteFile(data);
	}

	public static class HandlerClient extends MessageHandlerClient<PacketDeleteFile, IMessage>
	{
		@Override
		public IMessage onMessage(final PacketDeleteFile message, final EntityPlayer player)
		{
			if (player == null || player.world == null)
			{
				return null;
			}

			final IProject project = OrbisCore.getProjectManager().findProject(message.project);

			project.getCache().removeData(project.getCache().getDataId(message.location));

			final File file = new File(project.getLocation(), message.location);

			if (file.delete())
			{
				if (Minecraft.getMinecraft().currentScreen instanceof GuiViewProjects)
				{
					final GuiViewProjects viewProjects = (GuiViewProjects) Minecraft.getMinecraft().currentScreen;

					viewProjects.refreshNavigator();
				}

				if (Minecraft.getMinecraft().currentScreen instanceof GuiLoadBlueprint)
				{
					final GuiLoadBlueprint loadBlueprints = (GuiLoadBlueprint) Minecraft.getMinecraft().currentScreen;

					loadBlueprints.refreshNavigator();
				}
			}

			return null;
		}
	}

	public static class HandlerServer extends MessageHandlerServer<PacketDeleteFile, IMessage>
	{
		@Override
		public IMessage onMessage(final PacketDeleteFile message, final EntityPlayer player)
		{
			if (player == null || player.world == null)
			{
				return null;
			}

			final IProject project = OrbisCore.getProjectManager().findProject(message.project);

			project.getCache().removeData(project.getCache().getDataId(message.location));

			final File file = new File(project.getLocation(), message.location);

			if (file.delete())
			{
				NetworkingAether.sendPacketToPlayer(new PacketDeleteFile(message.project, message.location), (EntityPlayerMP) player);
			}

			return null;
		}
	}
}