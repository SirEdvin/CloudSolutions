package site.siredvin.cloudsolutions.data

import net.minecraft.data.PackOutput
import net.minecraft.data.recipes.FinishedRecipe
import net.minecraft.data.recipes.RecipeProvider
import net.minecraft.tags.ItemTags
import site.siredvin.broccolium.modules.data.recipe.TweakedShapedRecipeBuilder
import site.siredvin.cloudsolutions.common.setup.ModBlocks
import site.siredvin.cloudsolutions.xplat.ModRecipeIngredients
import java.util.function.Consumer

class ModRecipeProvider(output: PackOutput) : RecipeProvider(output) {
    override fun buildRecipes(consumer: Consumer<FinishedRecipe>) {
        TweakedShapedRecipeBuilder.shaped(ModBlocks.KV_STORAGE.get())
            .define('B', ModRecipeIngredients.get().bookshelf)
            .define('C', ItemTags.COALS)
            .define('I', ModRecipeIngredients.get().ironBlock)
            .pattern("CBC")
            .pattern("BIB")
            .pattern("CBC")
            .save(consumer)
        TweakedShapedRecipeBuilder.shaped(ModBlocks.STATSD_BRIDGE.get())
            .define('M', ModRecipeIngredients.get().wirelessModem)
            .define('B', ModRecipeIngredients.get().bookshelf)
            .define('C', ItemTags.COALS)
            .define('I', ModRecipeIngredients.get().ironBlock)
            .pattern("CMC")
            .pattern("BIB")
            .pattern("CBC")
            .save(consumer)
    }
}
