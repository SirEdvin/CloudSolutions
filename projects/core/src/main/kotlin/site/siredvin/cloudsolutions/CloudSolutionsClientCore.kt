package site.siredvin.cloudsolutions

import dan200.computercraft.api.client.turtle.TurtleUpgradeModeller
import dan200.computercraft.api.turtle.ITurtleUpgrade
import dan200.computercraft.api.upgrades.UpgradeType
import net.minecraft.resources.ResourceLocation
import site.siredvin.cloudsolutions.common.setup.TurtleUpgradeSerializers
import site.siredvin.cloudsolutions.computercraft.peripheral.CrafkaBrokerPeripheral
import site.siredvin.cloudsolutions.computercraft.peripheral.KVStoragePeripheral
import site.siredvin.cloudsolutions.computercraft.peripheral.StatsDBridgePeripheral
import java.util.function.BiConsumer
import java.util.function.Consumer

object CloudSolutionsClientCore {
    val EXTRA_MODELS = emptyArray<String>()

    fun registerExtraModels(register: Consumer<ResourceLocation>) {
        EXTRA_MODELS.forEach { register.accept(ResourceLocation.fromNamespaceAndPath(CloudSolutionsCore.MOD_ID, it)) }
    }

    fun onModelRegister(consumer: BiConsumer<UpgradeType<*>, TurtleUpgradeModeller<ITurtleUpgrade>>) {
        consumer.accept(
            TurtleUpgradeSerializers.STATSD_BRIDGE.get(),
            TurtleUpgradeModeller.sided(
                ResourceLocation.fromNamespaceAndPath(CloudSolutionsCore.MOD_ID, "turtle/${StatsDBridgePeripheral.ID.path}_left"),
                ResourceLocation.fromNamespaceAndPath(CloudSolutionsCore.MOD_ID, "turtle/${StatsDBridgePeripheral.ID.path}_right"),
            ),
        )
        consumer.accept(
            TurtleUpgradeSerializers.KV_STORAGE.get(),
            TurtleUpgradeModeller.sided(
                ResourceLocation.fromNamespaceAndPath(CloudSolutionsCore.MOD_ID, "turtle/${KVStoragePeripheral.ID.path}_left"),
                ResourceLocation.fromNamespaceAndPath(CloudSolutionsCore.MOD_ID, "turtle/${KVStoragePeripheral.ID.path}_right"),
            ),
        )
        consumer.accept(
            TurtleUpgradeSerializers.CRAFKA_BROKER.get(),
            TurtleUpgradeModeller.sided(
                ResourceLocation.fromNamespaceAndPath(CloudSolutionsCore.MOD_ID, "turtle/${CrafkaBrokerPeripheral.ID.path}_left"),
                ResourceLocation.fromNamespaceAndPath(CloudSolutionsCore.MOD_ID, "turtle/${CrafkaBrokerPeripheral.ID.path}_right"),
            ),
        )
    }

    fun onInit() {
    }
}
