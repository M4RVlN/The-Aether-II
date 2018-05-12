package com.gildedgames.aether.common.capabilities.entity.player.modules;

import com.gildedgames.aether.common.capabilities.entity.player.PlayerAether;
import com.gildedgames.aether.common.capabilities.entity.player.PlayerAetherModule;
import com.gildedgames.aether.common.network.packets.PacketSpecialMovement;
import com.gildedgames.aether.common.util.helpers.AetherHelper;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class PlayerConfigModule extends PlayerAetherModule
{
	private boolean skipIntro;

	public PlayerConfigModule(final PlayerAether playerAether)
	{
		super(playerAether);
	}

	public boolean skipIntro()
	{
		return this.skipIntro;
	}

	public void setSkipIntro(boolean flag)
	{
		this.skipIntro = flag;
	}

	@Override
	public void tickStart(TickEvent.PlayerTickEvent event)
	{

	}

	@Override
	public void tickEnd(TickEvent.PlayerTickEvent event)
	{

	}

	@Override
	public void onUpdate()
	{

	}

	@Override
	public void write(final NBTTagCompound compound)
	{

	}

	@Override
	public void read(final NBTTagCompound compound)
	{

	}

	@Override
	public void onDeath(final LivingDeathEvent event)
	{

	}

}