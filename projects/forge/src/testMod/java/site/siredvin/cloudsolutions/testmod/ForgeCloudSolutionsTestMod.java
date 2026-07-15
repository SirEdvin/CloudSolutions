package site.siredvin.cloudsolutions.testmod;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.fml.common.Mod;
import site.siredvin.testiarium.ForgeTestiarium;
import site.siredvin.testiarium.Testiarium;
import site.siredvin.testiarium.cct.CctComputers;
import site.siredvin.testiarium.cct.CctFixtureCommands;

@Mod("cloudsolutions_testmod")
public final class ForgeCloudSolutionsTestMod {
    public ForgeCloudSolutionsTestMod() {
        CctComputers.INSTANCE.initialize();
        MinecraftForge.EVENT_BUS.addListener((ServerStartingEvent event) -> {
            CctComputers.INSTANCE.reset();
            CctFixtureCommands.INSTANCE.importFiles(event.getServer());
        });
        Testiarium.register(CloudSolutionsGameTests.class);
        ForgeTestiarium.registerTests();
    }
}
