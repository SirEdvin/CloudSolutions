package site.siredvin.cloudsolutions.data

import net.minecraft.core.HolderLookup
import net.minecraft.data.PackOutput
import net.minecraft.data.recipes.RecipeOutput
import net.minecraft.data.recipes.RecipeProvider
import net.minecraft.tags.ItemTags
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import site.siredvin.broccolium.modules.data.recipe.TweakedShapedRecipeBuilder
import site.siredvin.cloudsolutions.common.setup.ModBlocks
import site.siredvin.cloudsolutions.xplat.ModRecipeIngredients
import java.util.concurrent.CompletableFuture

class ModRecipeProvider(output: PackOutput, registries: CompletableFuture<HolderLookup.Provider>) : RecipeProvider(output, registries) {
    override fun buildRecipes(consumer: RecipeOutput) {
        TweakedShapedRecipeBuilder(ItemStack(ModBlocks.KV_STORAGE.get()))
            .define('B', ModRecipeIngredients.get().bookshelf)
            .define('C', ItemTags.COALS)
            .define('I', ModRecipeIngredients.get().ironBlock)
            .pattern("CBC")
            .pattern("BIB")
            .pattern("CBC")
            .save(consumer)
        TweakedShapedRecipeBuilder(ItemStack(ModBlocks.STATSD_BRIDGE.get()))
            .define('M', ModRecipeIngredients.get().wirelessModem)
            .define('B', ModRecipeIngredients.get().bookshelf)
            .define('C', ItemTags.COALS)
            .define('I', ModRecipeIngredients.get().ironBlock)
            .pattern("CMC")
            .pattern("BIB")
            .pattern("CBC")
            .save(consumer)
        TweakedShapedRecipeBuilder(ItemStack(ModBlocks.CRAFKA_BROKER.get()))
            .define('I', ModRecipeIngredients.get().ironBlock)
            .define('C', Items.LIGHTNING_ROD)
            .define('R', Items.IRON_BARS)
            .define('D', ItemTags.WOOL)
            .pattern("DCD")
            .pattern("RIR")
            .pattern("RDR")
            .save(consumer)
    }
}
