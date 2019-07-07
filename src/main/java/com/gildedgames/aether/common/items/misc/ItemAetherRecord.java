package com.gildedgames.aether.common.items.misc;

import com.gildedgames.aether.common.init.CreativeTabsAether;
import net.minecraft.item.ItemRecord;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemAetherRecord extends ItemRecord
{
	public ItemAetherRecord(final String recordName, final SoundEvent sound)
	{
		super(recordName, sound);

		this.setCreativeTab(CreativeTabsAether.TAB_MISCELLANEOUS);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public String getRecordNameLocal()
	{
		return new TextComponentTranslation(this.getTranslationKey() + ".desc").getFormattedText();
	}

	//TODO: Reimplement for 1.12.2?
	/*@Override
	public ResourceLocation getRecordResource(String name)
	{
		return AetherCore.getResource(name);
	}*/
}
