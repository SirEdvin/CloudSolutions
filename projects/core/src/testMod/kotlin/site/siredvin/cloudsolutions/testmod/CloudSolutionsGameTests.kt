package site.siredvin.cloudsolutions.testmod

import net.minecraft.gametest.framework.GameTest
import net.minecraft.gametest.framework.GameTestHelper
import site.siredvin.testiarium.api.TestGroup
import site.siredvin.testiarium.cct.thenLua

@TestGroup("cloudsolutions")
class CloudSolutionsGameTests {
    @GameTest(template = "cloudsolutionsgametests.kvstorage", timeoutTicks = 1_200_000)
    fun kvStorage(helper: GameTestHelper) = helper.thenLua().thenSucceed()

    @GameTest(template = "cloudsolutionsgametests.crafka_broker", timeoutTicks = 1_200_000)
    fun crafkaBroker(helper: GameTestHelper) = helper.thenLua().thenSucceed()
}
