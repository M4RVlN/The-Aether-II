package com.gildedgames.aether.client.renderer.tiles;

import com.gildedgames.aether.client.models.entities.tile.ModelTeleporter;
import com.gildedgames.aether.common.AetherCore;
import com.gildedgames.aether.common.entities.tiles.TileEntityTeleporter;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.util.ResourceLocation;

public class TileEntityTeleporterRenderer extends TileEntityRenderer<TileEntityTeleporter>
{
	private final ModelTeleporter model = new ModelTeleporter();

	private final ResourceLocation texture = AetherCore.getResource("textures/tile_entities/teleporter/pedestal.png");

	@Override
	public void render(final TileEntityTeleporter teleporter, final double x, final double y, final double z, final float partialTicks, final int destroyStage)
	{
		GlStateManager.pushMatrix();
		GlStateManager.enableRescaleNormal();

		GlStateManager.translatef((float) x + 0.5F, (float) y + 1.5F, (float) z + 0.5F);
		GlStateManager.rotatef(180f, 1f, 0f, 1f);

		this.bindTexture(this.texture);

		if (teleporter != null)
		{
			switch (teleporter.getFacing())
			{
				case NORTH:
					GlStateManager.rotatef(270.0f, 0.0f, 1.0f, 0.0f);
					break;
				case WEST:
					GlStateManager.rotatef(180.0f, 0.0f, 1.0f, 0.0f);
					break;
				case SOUTH:
					GlStateManager.rotatef(90.0f, 0.0f, 1.0f, 0.0f);
					break;
				case EAST:
					GlStateManager.rotatef(0.0f, 0.0f, 1.0f, 0.0f);
					break;
			}

			this.model.render(0.0625F, teleporter.animationTicks + ((double) (teleporter.animationTicks - teleporter.prevAnimationTicks) * partialTicks));
		}
		else
		{
			this.model.render(0.0625F, 0);
		}

		GlStateManager.disableRescaleNormal();

		GlStateManager.popMatrix();
	}

}
