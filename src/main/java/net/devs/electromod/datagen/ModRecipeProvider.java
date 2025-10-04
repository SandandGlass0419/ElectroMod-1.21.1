package net.devs.electromod.datagen;

import net.devs.electromod.block.ModBlocks;
import net.devs.electromod.item.ModItems;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.data.server.recipe.RecipeExporter;
import net.minecraft.data.server.recipe.ShapedRecipeJsonBuilder;
import net.minecraft.item.Items;
import net.minecraft.predicate.NumberRange;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.registry.RegistryWrapper;

import java.util.concurrent.CompletableFuture;

public class ModRecipeProvider extends FabricRecipeProvider
{
    public ModRecipeProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture)
    {
        super(output, registriesFuture);
    }

    // generates json files for crafting/smelting recipes.
    // add ALL recipies using method.

    @Override
    public void generate(RecipeExporter recipeExporter)
    {
        // magnetic item
        ShapedRecipeJsonBuilder.create(RecipeCategory.REDSTONE, ModItems.MAGNET_ITEM)
                .pattern("R")
                .pattern("#")
                .pattern("B")
                .input('R', Items.RED_DYE)
                .input('#', Items.IRON_INGOT)
                .input('B', Items.BLUE_DYE)
                .criterion(hasItem(Items.IRON_INGOT), conditionsFromItem(Items.IRON_INGOT)) // important!
                .offerTo(recipeExporter);

        // magnetic block
        ShapedRecipeJsonBuilder.create(RecipeCategory.REDSTONE, ModBlocks.IRON_COIL)
                .pattern("#*#")
                .pattern("# #")
                .pattern("# #")
                .input('#', Items.IRON_INGOT)
                .input('*', Items.REDSTONE)
                .criterion(hasItem(Items.IRON_INGOT), conditionsFromItem(NumberRange.IntRange.atLeast(6), Items.IRON_INGOT))
                .offerTo(recipeExporter);

        ShapedRecipeJsonBuilder.create(RecipeCategory.REDSTONE, ModBlocks.GOLDEN_COIL)
                .pattern("#*#")
                .pattern("# #")
                .pattern("# #")
                .input('#', Items.GOLD_INGOT)
                .input('*', Items.REDSTONE)
                .criterion(hasItem(Items.GOLD_INGOT), conditionsFromItem(NumberRange.IntRange.atLeast(6), Items.GOLD_INGOT))
                .offerTo(recipeExporter);

        ShapedRecipeJsonBuilder.create(RecipeCategory.REDSTONE, ModBlocks.COPPER_COIL)
                .pattern("#*#")
                .pattern("# #")
                .pattern("# #")
                .input('#', Items.COPPER_INGOT)
                .input('*', Items.REDSTONE)
                .criterion(hasItem(Items.COPPER_INGOT), conditionsFromItem(NumberRange.IntRange.atLeast(6), Items.COPPER_INGOT))
                .offerTo(recipeExporter);

        ShapedRecipeJsonBuilder.create(RecipeCategory.REDSTONE, ModBlocks.MAGNET_BLOCK)
                .pattern("R")
                .pattern("#")
                .pattern("B")
                .input('R', Items.RED_DYE)
                .input('#', Items.LODESTONE)
                .input('B', Items.BLUE_DYE)
                .criterion(hasItem(Items.LODESTONE), conditionsFromItem(Items.LODESTONE))
                .offerTo(recipeExporter);
    }
}
