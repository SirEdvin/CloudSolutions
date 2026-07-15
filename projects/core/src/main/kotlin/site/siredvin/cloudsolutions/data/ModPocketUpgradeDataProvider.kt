package site.siredvin.cloudsolutions.data

import dan200.computercraft.api.pocket.IPocketUpgrade
import net.minecraft.core.RegistrySetBuilder
import net.minecraft.data.worldgen.BootstrapContext
import net.minecraft.resources.ResourceKey
import net.minecraft.world.item.ItemStack
import site.siredvin.cloudsolutions.common.setup.ModBlocks
import site.siredvin.cloudsolutions.common.setup.PocketUpgradeSerializers

object ModPocketUpgradeDataProvider : RegistrySetBuilder.RegistryBootstrap<IPocketUpgrade> {
    override fun run(context: BootstrapContext<IPocketUpgrade>) {
        context.register(
            ResourceKey.create(IPocketUpgrade.REGISTRY, PocketUpgradeSerializers.STATSD_BRIDGE.id),
            PocketUpgradeSerializers.STATSD_BRIDGE.createUpgrade(ItemStack(ModBlocks.STATSD_BRIDGE.get())),
        )
        context.register(
            ResourceKey.create(IPocketUpgrade.REGISTRY, PocketUpgradeSerializers.KV_STORAGE.id),
            PocketUpgradeSerializers.KV_STORAGE.createUpgrade(ItemStack(ModBlocks.KV_STORAGE.get())),
        )
        context.register(
            ResourceKey.create(IPocketUpgrade.REGISTRY, PocketUpgradeSerializers.CRAFKA_BROKER.id),
            PocketUpgradeSerializers.CRAFKA_BROKER.createUpgrade(ItemStack(ModBlocks.CRAFKA_BROKER.get())),
        )
    }
}
