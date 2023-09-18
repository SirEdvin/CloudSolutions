package site.siredvin.datafortress.data

import net.minecraft.data.loot.LootTableProvider
import net.minecraft.data.loot.LootTableSubProvider
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.level.storage.loot.LootTable
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets
import site.siredvin.datafortress.common.setup.Blocks
import site.siredvin.datafortress.xplat.ModPlatform
import site.siredvin.peripheralium.data.blocks.LootTableHelper
import java.util.function.BiConsumer

object ModLootTableProvider {
    fun getTables(): List<LootTableProvider.SubProviderEntry> {
        return listOf(
            LootTableProvider.SubProviderEntry({
                LootTableSubProvider {
                    registerBlocks(it)
                }
            }, LootContextParamSets.BLOCK),
        )
    }

    fun registerBlocks(consumer: BiConsumer<ResourceLocation, LootTable.Builder>) {
        val lootTable = LootTableHelper(ModPlatform.holder)
        lootTable.dropSelf(consumer, Blocks.DATA_STORAGE)
        lootTable.dropSelf(consumer, Blocks.TSDB_STORAGE)
        lootTable.validate()
    }
}
