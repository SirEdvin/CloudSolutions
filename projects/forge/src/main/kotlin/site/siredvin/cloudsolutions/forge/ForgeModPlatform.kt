package site.siredvin.cloudsolutions.forge

import dan200.computercraft.api.pocket.IPocketUpgrade
import dan200.computercraft.api.turtle.ITurtleUpgrade
import dan200.computercraft.api.upgrades.UpgradeType
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.CreativeModeTab
import net.minecraft.world.item.Item
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.entity.BlockEntityType
import net.neoforged.neoforge.registries.DeferredRegister
import site.siredvin.cloudsolutions.CloudSolutionsCore
import site.siredvin.cloudsolutions.ForgeCloudSolutions
import site.siredvin.tweakium.modules.platform.ForgeInnerComputerBasePlatform
import java.util.function.Supplier

object ForgeModPlatform : ForgeInnerComputerBasePlatform() {
    override val itemsRegistry: DeferredRegister<Item>
        get() = ForgeCloudSolutions.itemsRegistry
    override val modID: String
        get() = CloudSolutionsCore.MOD_ID

    override val blocksRegistry: DeferredRegister<Block>
        get() = ForgeCloudSolutions.blocksRegistry

    override val blockEntityTypesRegistry: DeferredRegister<BlockEntityType<*>>?
        get() = ForgeCloudSolutions.blockEntityTypesRegistry

    override val creativeTabRegistry: DeferredRegister<CreativeModeTab>
        get() = ForgeCloudSolutions.creativeTabRegistry

    override fun <V : ITurtleUpgrade> registerTurtleUpgrade(key: ResourceLocation, upgrade: UpgradeType<V>): Supplier<UpgradeType<V>> {
        @Suppress("UNCHECKED_CAST")
        return ForgeCloudSolutions.turtleUpgradeTypes.register(key.path, Supplier { upgrade }) as Supplier<UpgradeType<V>>
    }

    override fun <V : IPocketUpgrade> registerPocketUpgrade(key: ResourceLocation, upgrade: UpgradeType<V>): Supplier<UpgradeType<V>> {
        @Suppress("UNCHECKED_CAST")
        return ForgeCloudSolutions.pocketUpgradeTypes.register(key.path, Supplier { upgrade }) as Supplier<UpgradeType<V>>
    }
}
