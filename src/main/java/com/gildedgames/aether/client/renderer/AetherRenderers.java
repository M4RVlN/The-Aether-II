package com.gildedgames.aether.client.renderer;

import com.gildedgames.aether.client.renderer.entities.RenderFloatingBlock;
import com.gildedgames.aether.client.renderer.entities.RenderMovingBlock;
import com.gildedgames.aether.client.renderer.entities.attachments.RenderParachute;
import com.gildedgames.aether.client.renderer.entities.companions.*;
import com.gildedgames.aether.client.renderer.entities.living.*;
import com.gildedgames.aether.client.renderer.entities.projectiles.RenderBolt;
import com.gildedgames.aether.client.renderer.entities.projectiles.RenderDaggerfrostSnowball;
import com.gildedgames.aether.client.renderer.entities.projectiles.RenderDart;
import com.gildedgames.aether.client.renderer.items.ItemMoaEggColorHandler;
import com.gildedgames.aether.client.renderer.items.MoaFeatherColorHandler;
import com.gildedgames.aether.client.renderer.items.WrappingPaperColorHandler;
import com.gildedgames.aether.client.renderer.tile_entities.*;
import com.gildedgames.aether.common.entities.blocks.EntityFloatingBlock;
import com.gildedgames.aether.common.entities.blocks.EntityMovingBlock;
import com.gildedgames.aether.common.entities.blocks.EntityParachute;
import com.gildedgames.aether.common.entities.living.companions.*;
import com.gildedgames.aether.common.entities.living.mobs.*;
import com.gildedgames.aether.common.entities.living.mounts.EntityMoa;
import com.gildedgames.aether.common.entities.living.npc.EntityEdison;
import com.gildedgames.aether.common.entities.living.passive.*;
import com.gildedgames.aether.common.entities.projectiles.EntityBolt;
import com.gildedgames.aether.common.entities.projectiles.EntityDaggerfrostSnowball;
import com.gildedgames.aether.common.entities.projectiles.EntityDart;
import com.gildedgames.aether.common.entities.tiles.*;
import com.gildedgames.aether.common.entities.tiles.builder.TileEntityStructureBuilder;
import com.gildedgames.aether.common.items.ItemsAether;
import com.gildedgames.orbis.client.renderers.tiles.TileEntityBlockDataContainerRenderer;
import com.gildedgames.orbis.client.renderers.tiles.TileEntityBlockPaletteRenderer;
import com.gildedgames.orbis.client.renderers.tiles.TileEntityBlueprintRenderer;
import com.gildedgames.orbis.common.tiles.TileEntityBlockDataContainer;
import com.gildedgames.orbis.common.tiles.TileEntityBlockPalette;
import com.gildedgames.orbis.common.tiles.TileEntityBlueprint;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.color.ItemColors;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.client.registry.RenderingRegistry;

public class AetherRenderers
{
	public static void preInit()
	{
		registerEntityRenderers();
	}

	public static void init()
	{
		registerTESRs();

		final ItemColors itemColors = Minecraft.getMinecraft().getItemColors();
		itemColors.registerItemColorHandler(new ItemMoaEggColorHandler(), ItemsAether.moa_egg);
		itemColors.registerItemColorHandler(new WrappingPaperColorHandler(), ItemsAether.wrapping_paper);
		itemColors.registerItemColorHandler(new MoaFeatherColorHandler(), ItemsAether.moa_feather);
	}

	private static void registerEntityRenderers()
	{
		RenderingRegistry.registerEntityRenderingHandler(EntityFloatingBlock.class, RenderFloatingBlock::new);
		RenderingRegistry.registerEntityRenderingHandler(EntityMovingBlock.class, RenderMovingBlock::new);
		RenderingRegistry.registerEntityRenderingHandler(EntityDart.class, RenderDart::new);
		RenderingRegistry.registerEntityRenderingHandler(EntityBolt.class, RenderBolt::new);
		RenderingRegistry.registerEntityRenderingHandler(EntityDaggerfrostSnowball.class, RenderDaggerfrostSnowball::new);
		RenderingRegistry.registerEntityRenderingHandler(EntityParachute.class, RenderParachute::new);

		RenderingRegistry.registerEntityRenderingHandler(EntityAechorPlant.class, RenderAechorPlant::new);
		RenderingRegistry.registerEntityRenderingHandler(EntityAerbunny.class, RenderAerbunny::new);
		RenderingRegistry.registerEntityRenderingHandler(EntityCarrionSprout.class, RenderCarrionSprout::new);

		RenderingRegistry.registerEntityRenderingHandler(EntityFrostpineTotem.class, RenderFrostpineTotem::new);
		RenderingRegistry.registerEntityRenderingHandler(EntityKraisith.class, RenderKraisith::new);
		RenderingRegistry.registerEntityRenderingHandler(EntityShadeOfArkenzus.class, RenderShadeOfArkenzus::new);
		RenderingRegistry.registerEntityRenderingHandler(EntityEtherealWisp.class, RenderEtherealWisp::new);
		RenderingRegistry.registerEntityRenderingHandler(EntityFleetingWisp.class, RenderFleetingWisp::new);
		RenderingRegistry.registerEntityRenderingHandler(EntitySoaringWisp.class, RenderSoaringWisp::new);
		RenderingRegistry.registerEntityRenderingHandler(EntityFangrin.class, RenderFangrin::new);
		RenderingRegistry.registerEntityRenderingHandler(EntityNexSpirit.class, RenderNexSpirit::new);
		RenderingRegistry.registerEntityRenderingHandler(EntityCockatrice.class, RenderCockatrice::new);
		RenderingRegistry.registerEntityRenderingHandler(EntityKirrid.class, RenderKirrid::new);
		RenderingRegistry.registerEntityRenderingHandler(EntityMoa.class, RenderMoa::new);
		RenderingRegistry.registerEntityRenderingHandler(EntityAerwhale.class, RenderAerwhale::new);
		RenderingRegistry.registerEntityRenderingHandler(EntityZephyr.class, RenderZephyr::new);
		RenderingRegistry.registerEntityRenderingHandler(EntityTempest.class, RenderTempest::new);
		RenderingRegistry.registerEntityRenderingHandler(EntitySwet.class, RenderSwet::new);
		RenderingRegistry.registerEntityRenderingHandler(EntityPinkBabySwet.class, RenderPinkBabySwet::new);
		RenderingRegistry.registerEntityRenderingHandler(EntityGlitterwing.class, RenderGlitterwing::new);
		RenderingRegistry.registerEntityRenderingHandler(EntityEdison.class, RenderEdisonSitting::new);
		RenderingRegistry.registerEntityRenderingHandler(EntityTaegore.class, RenderTaegore::new);
		RenderingRegistry.registerEntityRenderingHandler(EntityBurrukai.class, RenderBurrukai::new);
	}

	private static void registerTESRs()
	{
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityBlockDataContainer.class, new TileEntityBlockDataContainerRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityBlockPalette.class, new TileEntityBlockPaletteRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityBlueprint.class, new TileEntityBlueprintRenderer());

		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityAltar.class, new TileEntityAltarRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntitySkyrootChest.class, new TileEntitySkyrootChestRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntitySkyrootSign.class, new TileEntitySkyrootSignRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityMoaEgg.class, new TileEntityMoaEggRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityPresent.class, new TileEntityPresentRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityIcestoneCooler.class, new TileEntityIcestoneCoolerRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityMasonryBench.class, new TileEntityMasonryBenchRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityOutpostCampfire.class, new TileEntityOutpostCampfireRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityStructureBuilder.class, new TileEntityStructureBuilderRenderer());
	}
}
