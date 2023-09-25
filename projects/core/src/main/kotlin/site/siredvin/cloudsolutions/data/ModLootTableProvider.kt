package site.siredvin.cloudsolutions.data

import net.minecraft.data.loot.LootTableProvider
import net.minecraft.data.loot.LootTableSubProvider
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.level.storage.loot.LootTable
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets
import site.siredvin.cloudsolutions.common.setup.Blocks
import site.siredvin.cloudsolutions.xplat.ModPlatform
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
        lootTable.dropSelf(consumer, Blocks.KV_STORAGE)
        lootTable.dropSelf(consumer, Blocks.TSDB_STORAGE)
        lootTable.dropSelf(consumer, Blocks.STATSD_BRIDGE)
        lootTable.validate()
    }
}
