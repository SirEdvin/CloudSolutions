package site.siredvin.cloudsolutions.data

import net.minecraft.data.loot.LootTableProvider
import net.minecraft.data.loot.LootTableSubProvider
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.level.storage.loot.LootTable
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets
import site.siredvin.broccolium.modules.data.loot.LootTableHelper
import site.siredvin.cloudsolutions.common.setup.ModBlocks
import site.siredvin.cloudsolutions.xplat.ModPlatform
import java.util.function.BiConsumer

object ModLootTableProvider {
    fun getTables(): List<LootTableProvider.SubProviderEntry> = listOf(
        LootTableProvider.SubProviderEntry({
            LootTableSubProvider {
                registerBlocks(it)
            }
        }, LootContextParamSets.BLOCK),
    )

    fun registerBlocks(consumer: BiConsumer<ResourceLocation, LootTable.Builder>) {
        val lootTable = LootTableHelper(ModPlatform.holder)
        lootTable.dropSelf(consumer, ModBlocks.KV_STORAGE)
        lootTable.dropSelf(consumer, ModBlocks.TSDB_STORAGE)
        lootTable.dropSelf(consumer, ModBlocks.STATSD_BRIDGE)
        lootTable.dropSelf(consumer, ModBlocks.CRAFKA_BROKER)
        lootTable.validate()
    }
}
