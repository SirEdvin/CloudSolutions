package site.siredvin.cloudsolutions.forge

import dan200.computercraft.shared.ModRegistry.Items
import net.minecraft.world.item.crafting.Ingredient
import net.minecraftforge.common.Tags
import site.siredvin.cloudsolutions.xplat.ModRecipeIngredients

object ForgeModRecipeIngredients : ModRecipeIngredients {
    override val wirelessModem: Ingredient
        get() = Ingredient.of(Items.WIRELESS_MODEM_NORMAL.get())
    override val bookshelf: Ingredient
        get() = Ingredient.of(Tags.Items.BOOKSHELVES)
    override val ironBlock: Ingredient
        get() = Ingredient.of(Tags.Items.STORAGE_BLOCKS_IRON)
}
