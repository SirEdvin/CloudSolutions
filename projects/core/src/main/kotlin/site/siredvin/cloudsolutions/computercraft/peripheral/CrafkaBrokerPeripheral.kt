package site.siredvin.cloudsolutions.computercraft.peripheral

import dan200.computercraft.api.lua.LuaException
import dan200.computercraft.api.lua.LuaFunction
import dan200.computercraft.api.peripheral.IComputerAccess
import net.minecraft.resources.ResourceLocation
import site.siredvin.cloudsolutions.CloudSolutionsCore
import site.siredvin.cloudsolutions.common.configuration.ModConfig
import site.siredvin.cloudsolutions.subsystems.SubsystemManager
import site.siredvin.tweakium.modules.peripheral.OwnedPeripheral
import site.siredvin.tweakium.modules.peripheral.api.IPeripheralOwner
import java.time.Instant
import java.util.Optional
import kotlin.jvm.optionals.getOrNull

class CrafkaBrokerPeripheral(owner: IPeripheralOwner) : OwnedPeripheral<IPeripheralOwner>(TYPE, owner) {
    companion object {
        const val TYPE = "crafka_broker"
        val ID = ResourceLocation(CloudSolutionsCore.MOD_ID, TYPE)
    }

    override val isEnabled: Boolean
        get() = ModConfig.enableCrafkaBroker

    override val peripheralConfiguration: MutableMap<String, Any>
        get() {
            val data = super.peripheralConfiguration
            return data
        }
}
