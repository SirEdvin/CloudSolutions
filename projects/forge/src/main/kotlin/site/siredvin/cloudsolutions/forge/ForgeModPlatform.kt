package site.siredvin.cloudsolutions.forge

import dan200.computercraft.api.pocket.PocketUpgradeSerialiser
import dan200.computercraft.api.turtle.TurtleUpgradeSerialiser
import net.minecraft.world.item.CreativeModeTab
import net.minecraft.world.item.Item
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraftforge.registries.DeferredRegister
import site.siredvin.cloudsolutions.CloudSolutionsCore
import site.siredvin.cloudsolutions.ForgeCloudSolutions
import site.siredvin.peripheralium.forge.ForgeBaseInnerPlatform

object ForgeModPlatform : ForgeBaseInnerPlatform() {
    override val itemsRegistry: DeferredRegister<Item>
        get() = ForgeCloudSolutions.itemsRegistry
    override val modID: String
        get() = CloudSolutionsCore.MOD_ID

    override val blocksRegistry: DeferredRegister<Block>
        get() = ForgeCloudSolutions.blocksRegistry

    override val blockEntityTypesRegistry: DeferredRegister<BlockEntityType<*>>?
        get() = ForgeCloudSolutions.blockEntityTypesRegistry

    override val turtleSerializers: DeferredRegister<TurtleUpgradeSerialiser<*>>
        get() = ForgeCloudSolutions.turtleSerializers
    override val pocketSerializers: DeferredRegister<PocketUpgradeSerialiser<*>>
        get() = ForgeCloudSolutions.pocketSerializers

    override val creativeTabRegistry: DeferredRegister<CreativeModeTab>
        get() = ForgeCloudSolutions.creativeTabRegistry
}
