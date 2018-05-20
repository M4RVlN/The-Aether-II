package com.gildedgames.aether.api.registry;

import com.gildedgames.aether.api.dialog.IDialogManager;
import com.gildedgames.aether.api.patron.PatronRewardRegistry;
import com.gildedgames.aether.api.recipes.altar.IAltarRecipeRegistry;
import com.gildedgames.aether.api.recipes.simple.ISimpleCraftingRegistry;
import com.gildedgames.aether.api.registry.recipes.IRecipeIndexRegistry;
import com.gildedgames.aether.api.registry.tab.ITabRegistry;
import com.gildedgames.aether.api.shop.ICurrencyRegistry;
import com.gildedgames.aether.api.shop.IShopManager;
import com.gildedgames.aether.api.world.generation.ITemplateRegistry;

/**
 * Provider for Aether content interfaces.
 */
public interface IContentRegistry
{
	/**
	 * @return The {@link IShopManager} provider for the Aether.
	 */
	IShopManager shop();

	/**
	 * @return The {@link ICurrencyRegistry} provider for the Aether.
	 */
	ICurrencyRegistry currency();

	/**
	 * @return The {@link ITemplateRegistry} provider for the Aether.
	 */
	ITemplateRegistry templates();

	/**
	 * @return The {@link IAltarRecipeRegistry} provider for the Aether.
	 */
	IAltarRecipeRegistry altar();

	/**
	 * @return The {@link ITabRegistry} provider for the Aether.
	 */
	ITabRegistry tabs();

	/**
	 * @return The {@link IItemPropertiesRegistry} provider for the Aether.
	 */
	IItemPropertiesRegistry items();

	/**
	 * @return The {@link IEffectRegistry} provider for the Aether.
	 */
	IEffectRegistry effects();

	/**
	 * @return The {@link IDialogManager} provider for the Aether.
	 */
	IDialogManager dialog();

	/**
	 * @return The {@link IRecipeIndexRegistry} provider for the Aether.
	 */
	IRecipeIndexRegistry craftable();

	ISimpleCraftingRegistry masonry();

	PatronRewardRegistry patronRewards();
}
