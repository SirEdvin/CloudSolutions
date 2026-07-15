package site.siredvin.cloudsolutions

import dan200.computercraft.api.pocket.IPocketUpgrade
import dan200.computercraft.api.turtle.ITurtleUpgrade
import dan200.computercraft.api.upgrades.UpgradeType
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.world.item.CreativeModeTab
import net.minecraft.world.item.Item
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.entity.BlockEntityType
import net.neoforged.bus.api.IEventBus
import net.neoforged.fml.ModContainer
import net.neoforged.fml.common.Mod
import net.neoforged.fml.config.ModConfig
import net.neoforged.neoforge.registries.DeferredRegister
import site.siredvin.cloudsolutions.common.configuration.ConfigHolder
import site.siredvin.cloudsolutions.forge.ForgeModPlatform
import site.siredvin.cloudsolutions.forge.ForgeModRecipeIngredients
import site.siredvin.cloudsolutions.xplat.ModCommonHooks
import site.siredvin.tweakium.ForgeTweakium

@Mod(CloudSolutionsCore.MOD_ID)
class ForgeCloudSolutions(modEventBus: IEventBus, modContainer: ModContainer) {

    companion object {
        val blocksRegistry: DeferredRegister<Block> =
            DeferredRegister.create(BuiltInRegistries.BLOCK, CloudSolutionsCore.MOD_ID)
        val itemsRegistry: DeferredRegister<Item> =
            DeferredRegister.create(BuiltInRegistries.ITEM, CloudSolutionsCore.MOD_ID)
        val blockEntityTypesRegistry: DeferredRegister<BlockEntityType<*>> =
            DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, CloudSolutionsCore.MOD_ID)
        val creativeTabRegistry: DeferredRegister<CreativeModeTab> =
            DeferredRegister.create(BuiltInRegistries.CREATIVE_MODE_TAB.key(), CloudSolutionsCore.MOD_ID)
        val turtleUpgradeTypes: DeferredRegister<UpgradeType<out ITurtleUpgrade>> =
            DeferredRegister.create(ITurtleUpgrade.typeRegistry(), CloudSolutionsCore.MOD_ID)
        val pocketUpgradeTypes: DeferredRegister<UpgradeType<out IPocketUpgrade>> =
            DeferredRegister.create(IPocketUpgrade.typeRegistry(), CloudSolutionsCore.MOD_ID)
    }

    init {
        ForgeTweakium.sayHi()
        modContainer.registerConfig(ModConfig.Type.COMMON, ConfigHolder.commonSpec, "${CloudSolutionsCore.MOD_ID}.toml")
        modContainer.registerConfig(ModConfig.Type.SERVER, ConfigHolder.serverSpec, "${CloudSolutionsCore.MOD_ID}_server.toml")
        CloudSolutionsCore.configure(ForgeModPlatform, ForgeModRecipeIngredients)
        ModCommonHooks.onRegister()
        blocksRegistry.register(modEventBus)
        itemsRegistry.register(modEventBus)
        blockEntityTypesRegistry.register(modEventBus)
        creativeTabRegistry.register(modEventBus)
        turtleUpgradeTypes.register(modEventBus)
        pocketUpgradeTypes.register(modEventBus)
    }
}
