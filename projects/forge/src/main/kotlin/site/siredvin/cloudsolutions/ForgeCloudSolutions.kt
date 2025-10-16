package site.siredvin.cloudsolutions

import dan200.computercraft.api.ForgeComputerCraftAPI
import dan200.computercraft.api.pocket.PocketUpgradeSerialiser
import dan200.computercraft.api.turtle.TurtleUpgradeSerialiser
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.world.item.CreativeModeTab
import net.minecraft.world.item.Item
import net.minecraft.world.level.block.Block
import net.minecraftforge.common.util.LazyOptional
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.config.ModConfig
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent
import net.minecraftforge.registries.DeferredRegister
import net.minecraftforge.registries.ForgeRegistries
import site.siredvin.cloudsolutions.common.configuration.ConfigHolder
import site.siredvin.cloudsolutions.forge.ForgeModPlatform
import site.siredvin.cloudsolutions.forge.ForgeModRecipeIngredients
import site.siredvin.cloudsolutions.xplat.ModCommonHooks
import site.siredvin.tweakium.ForgeTweakium
import site.siredvin.tweakium.modules.peripheral.api.IPeripheralProvider
import thedarkcolour.kotlinforforge.forge.LOADING_CONTEXT
import thedarkcolour.kotlinforforge.forge.MOD_CONTEXT

@Mod(CloudSolutionsCore.MOD_ID)
@Mod.EventBusSubscriber(modid = CloudSolutionsCore.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
object ForgeCloudSolutions {

    val blocksRegistry: DeferredRegister<Block> =
        DeferredRegister.create(ForgeRegistries.BLOCKS, CloudSolutionsCore.MOD_ID)
    val itemsRegistry: DeferredRegister<Item> =
        DeferredRegister.create(ForgeRegistries.ITEMS, CloudSolutionsCore.MOD_ID)
    val blockEntityTypesRegistry = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, CloudSolutionsCore.MOD_ID)
    val creativeTabRegistry: DeferredRegister<CreativeModeTab> =
        DeferredRegister.create(BuiltInRegistries.CREATIVE_MODE_TAB.key(), CloudSolutionsCore.MOD_ID)
    val turtleSerializers = DeferredRegister.create(
        TurtleUpgradeSerialiser.registryId(),
        CloudSolutionsCore.MOD_ID,
    )
    val pocketSerializers = DeferredRegister.create(
        PocketUpgradeSerialiser.registryId(),
        CloudSolutionsCore.MOD_ID,
    )

    init {
        ForgeTweakium.sayHi()
        // Configure configuration
        LOADING_CONTEXT.registerConfig(ModConfig.Type.COMMON, ConfigHolder.commonSpec, "${CloudSolutionsCore.MOD_ID}.toml")
        LOADING_CONTEXT.registerConfig(ModConfig.Type.SERVER, ConfigHolder.serverSpec, "${CloudSolutionsCore.MOD_ID}_server.toml")
        CloudSolutionsCore.configure(ForgeModPlatform, ForgeModRecipeIngredients)
        val eventBus = MOD_CONTEXT.getKEventBus()
        eventBus.addListener(this::commonSetup)
        // Register items and blocks
        ModCommonHooks.onRegister()
        blocksRegistry.register(eventBus)
        itemsRegistry.register(eventBus)
        blockEntityTypesRegistry.register(eventBus)
        creativeTabRegistry.register(eventBus)
        turtleSerializers.register(eventBus)
        pocketSerializers.register(eventBus)
    }

    @Suppress("UNUSED_PARAMETER")
    fun commonSetup(event: FMLCommonSetupEvent) {
        // Register peripheral provider
        ForgeComputerCraftAPI.registerPeripheralProvider { world, pos, side ->
            val entity = world.getBlockEntity(pos)
            if (entity is IPeripheralProvider<*>) {
                val foundPeripheral = entity.getPeripheral(side)
                if (foundPeripheral != null) {
                    return@registerPeripheralProvider LazyOptional.of { foundPeripheral }
                }
            }
            return@registerPeripheralProvider LazyOptional.empty()
        }
    }
}
