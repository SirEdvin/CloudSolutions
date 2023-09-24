package site.siredvin.datafortress.forge

import dan200.computercraft.api.pocket.PocketUpgradeSerialiser
import dan200.computercraft.api.turtle.TurtleUpgradeSerialiser
import net.minecraft.world.item.CreativeModeTab
import net.minecraft.world.item.Item
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraftforge.registries.DeferredRegister
import site.siredvin.datafortress.DataFortressCore
import site.siredvin.datafortress.ForgeDataFortress
import site.siredvin.peripheralium.forge.ForgeBaseInnerPlatform

object ForgeModPlatform : ForgeBaseInnerPlatform() {
    override val itemsRegistry: DeferredRegister<Item>
        get() = ForgeDataFortress.itemsRegistry
    override val modID: String
        get() = DataFortressCore.MOD_ID

    override val blocksRegistry: DeferredRegister<Block>
        get() = ForgeDataFortress.blocksRegistry

    override val blockEntityTypesRegistry: DeferredRegister<BlockEntityType<*>>?
        get() = ForgeDataFortress.blockEntityTypesRegistry

    override val turtleSerializers: DeferredRegister<TurtleUpgradeSerialiser<*>>
        get() = ForgeDataFortress.turtleSerializers
    override val pocketSerializers: DeferredRegister<PocketUpgradeSerialiser<*>>
        get() = ForgeDataFortress.pocketSerializers

    override val creativeTabRegistry: DeferredRegister<CreativeModeTab>
        get() = ForgeDataFortress.creativeTabRegistry
}
