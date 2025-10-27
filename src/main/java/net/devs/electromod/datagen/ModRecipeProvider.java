package net.devs.electromod.datagen;

import net.devs.electromod.block.ModBlocks;
import net.devs.electromod.item.ModItems;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.block.Blocks;
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
        // magnetic items
        ShapedRecipeJsonBuilder.create(RecipeCategory.REDSTONE, ModItems.MAGNET_ITEM)
                .pattern("R")
                .pattern("#")
                .pattern("B")
                .input('R', Items.RED_DYE)
                .input('#', Items.IRON_INGOT)
                .input('B', Items.BLUE_DYE)
                .criterion(hasItem(Items.IRON_INGOT), conditionsFromItem(Items.IRON_INGOT)) // important!
                .offerTo(recipeExporter);

        // magnetic blocks
        ShapedRecipeJsonBuilder.create(RecipeCategory.REDSTONE, ModBlocks.IRON_COIL, 2)
                .pattern("#*#")
                .pattern("# #")
                .pattern("# #")
                .input('#', Items.IRON_INGOT)
                .input('*', Items.REDSTONE)
                .criterion(hasItem(Items.IRON_INGOT), conditionsFromItem(NumberRange.IntRange.atLeast(6), Items.IRON_INGOT))
                .offerTo(recipeExporter);

        ShapedRecipeJsonBuilder.create(RecipeCategory.REDSTONE, ModBlocks.GOLDEN_COIL, 2)
                .pattern("#*#")
                .pattern("# #")
                .pattern("# #")
                .input('#', Items.GOLD_INGOT)
                .input('*', Items.REDSTONE)
                .criterion(hasItem(Items.GOLD_INGOT), conditionsFromItem(NumberRange.IntRange.atLeast(6), Items.GOLD_INGOT))
                .offerTo(recipeExporter);

        ShapedRecipeJsonBuilder.create(RecipeCategory.REDSTONE, ModBlocks.COPPER_COIL, 2)
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

        ShapedRecipeJsonBuilder.create(RecipeCategory.REDSTONE, ModBlocks.MAGNETIC_DETECTOR)
                .pattern("===")
                .pattern("=C=")
                .pattern("#*#")
                .input('=', Items.COPPER_INGOT)
                .input('*', Items.NETHER_STAR)
                .input('C', Items.COMPASS)
                .input('#', Items.POLISHED_DEEPSLATE_SLAB)
                .criterion(hasItem(Items.NETHER_STAR), conditionsFromItem(Items.NETHER_STAR))
                .offerTo(recipeExporter);

        // electro items
        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, ModItems.ELECTRO_ITEM)
                .pattern("N=N")
                .pattern("=*=")
                .pattern("N=N")
                .input('*', Items.NETHER_STAR)
                .input('=', Items.BLAZE_POWDER)
                .input('N', Items.NETHERITE_INGOT)
                .criterion(hasItem(Items.NETHER_STAR), conditionsFromItem(Items.NETHER_STAR))
                .offerTo(recipeExporter);

        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, ModItems.RUBBER_GLOVES, 2)
                .pattern("***")
                .pattern("*=*")
                .pattern("***")
                .input('=', Items.PINK_DYE)
                .input('*', Items.SLIME_BALL)
                .criterion(hasItem(Items.SLIME_BALL), conditionsFromItem(Items.SLIME_BALL))
                .offerTo(recipeExporter);

        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, ModItems.ELECTRO_STAFF)
                .pattern("*")
                .pattern("|")
                .pattern("|")
                .input('|', Items.BLAZE_ROD)
                .input('*', ModItems.ELECTRO_ITEM)
                .criterion(hasItem(ModItems.ELECTRO_ITEM), conditionsFromItem(ModItems.ELECTRO_ITEM))
                .offerTo(recipeExporter);

        // electro blocks
        ShapedRecipeJsonBuilder.create(RecipeCategory.REDSTONE, ModBlocks.COPPER_WIRE, 3)
                .pattern("===")
                .input('=', Items.COPPER_INGOT)
                .criterion(hasItem(Items.COPPER_INGOT), conditionsFromItem(Items.COPPER_INGOT))
                .offerTo(recipeExporter);

        ShapedRecipeJsonBuilder.create(RecipeCategory.REDSTONE, ModBlocks.WIRE, 3)
                .pattern("===")
                .input('=', Items.IRON_INGOT)
                .criterion(hasItem(Items.IRON_INGOT), conditionsFromItem(Items.IRON_INGOT))
                .offerTo(recipeExporter);

        ShapedRecipeJsonBuilder.create(RecipeCategory.REDSTONE, ModBlocks.GOLDEN_WIRE, 3)
                .pattern("===")
                .input('=', Items.GOLD_INGOT)
                .criterion(hasItem(Items.GOLD_INGOT), conditionsFromItem(Items.GOLD_INGOT))
                .offerTo(recipeExporter);

        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, ModBlocks.ACDC_CONVERTER)
                .pattern("P=P")
                .pattern("=*=")
                .pattern("P=P")
                .input('=', Items.CHAIN)
                .input('*', ModItems.ELECTRO_ITEM)
                .input('P', ModBlocks.PN_DIODE)
                .criterion(hasItem(ModItems.ELECTRO_ITEM), conditionsFromItem(ModItems.ELECTRO_ITEM))
                .offerTo(recipeExporter);

        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, ModBlocks.ELECTRO_DECTOR)
                .pattern("N=N")
                .pattern("=*=")
                .pattern("N=N")
                .input('*', Items.NETHER_STAR)
                .input('=', ModItems.ELECTRO_ITEM)
                .input('N', Items.IRON_INGOT)
                .criterion(hasItem(Items.NETHER_STAR), conditionsFromItem(Items.NETHER_STAR))
                .offerTo(recipeExporter);


        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, ModBlocks.PN_DIODE)
                .pattern("=*=")
                .input('*', Items.NETHER_STAR)
                .input('=', ModItems.ELECTRO_ITEM)
                .criterion(hasItem(Items.NETHER_STAR), conditionsFromItem(Items.NETHER_STAR))
                .offerTo(recipeExporter);

        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, ModBlocks.BATTERY, 3)
                .pattern("NNN")
                .pattern("=*=")
                .pattern("WWW")
                .input('*', ModItems.ELECTRO_ITEM)
                .input('=', Items.NETHER_STAR)
                .input('W', Blocks.IRON_BLOCK)
                .input('N', Blocks.NETHERITE_BLOCK)
                .criterion(hasItem(Items.NETHER_STAR), conditionsFromItem(Items.NETHER_STAR))
                .offerTo(recipeExporter);
    }
}
