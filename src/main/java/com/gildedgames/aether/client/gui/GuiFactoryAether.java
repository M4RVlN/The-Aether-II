package com.gildedgames.aether.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.fml.client.IModGuiFactory;

import java.util.Set;

public class GuiFactoryAether implements IModGuiFactory
{

	@Override
	public void initialize(final Minecraft minecraftInstance)
	{

	}

	@Override
	public boolean hasConfigGui()
	{
		return false;
	}

	@Override
	public GuiScreen createConfigGui(final GuiScreen parentScreen)
	{
		//TODO: Create gui screen for config?
		return null;
	}

	@Override
	public Set<RuntimeOptionCategoryElement> runtimeGuiCategories()
	{
		return null;
	}

}
