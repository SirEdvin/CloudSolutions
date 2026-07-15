package site.siredvin.cloudsolutions.data

import dan200.computercraft.api.turtle.ITurtleUpgrade
import net.minecraft.core.RegistrySetBuilder
import net.minecraft.data.worldgen.BootstrapContext
import net.minecraft.world.item.ItemStack
import site.siredvin.cloudsolutions.common.setup.ModBlocks
import site.siredvin.cloudsolutions.common.setup.TurtleUpgradeSerializers

object ModTurtleUpgradeDataProvider : RegistrySetBuilder.RegistryBootstrap<ITurtleUpgrade> {
    override fun run(context: BootstrapContext<ITurtleUpgrade>) {
        context.register(
            ITurtleUpgrade.createKey(TurtleUpgradeSerializers.STATSD_BRIDGE.id),
            TurtleUpgradeSerializers.STATSD_BRIDGE.createUpgrade(ItemStack(ModBlocks.STATSD_BRIDGE.get())),
        )
        context.register(
            ITurtleUpgrade.createKey(TurtleUpgradeSerializers.KV_STORAGE.id),
            TurtleUpgradeSerializers.KV_STORAGE.createUpgrade(ItemStack(ModBlocks.KV_STORAGE.get())),
        )
        context.register(
            ITurtleUpgrade.createKey(TurtleUpgradeSerializers.CRAFKA_BROKER.id),
            TurtleUpgradeSerializers.CRAFKA_BROKER.createUpgrade(ItemStack(ModBlocks.CRAFKA_BROKER.get())),
        )
    }
}
