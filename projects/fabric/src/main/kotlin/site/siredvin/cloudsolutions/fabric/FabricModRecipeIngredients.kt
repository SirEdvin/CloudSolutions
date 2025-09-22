package site.siredvin.cloudsolutions.fabric

import dan200.computercraft.shared.ModRegistry.Items
import net.fabricmc.fabric.api.tag.convention.v1.ConventionalItemTags
import net.minecraft.world.item.crafting.Ingredient
import net.minecraft.world.level.block.Blocks
import site.siredvin.cloudsolutions.xplat.ModRecipeIngredients

object FabricModRecipeIngredients : ModRecipeIngredients {
    override val wirelessModem: Ingredient
        get() = Ingredient.of(Items.WIRELESS_MODEM_NORMAL.get())
    override val bookshelf: Ingredient
        get() = Ingredient.of(ConventionalItemTags.BOOKSHELVES)
    override val ironBlock: Ingredient
        get() = Ingredient.of(Blocks.IRON_BLOCK)
}
