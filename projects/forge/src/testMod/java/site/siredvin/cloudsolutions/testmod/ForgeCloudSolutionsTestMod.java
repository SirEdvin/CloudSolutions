package site.siredvin.cloudsolutions.testmod;

import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import site.siredvin.testiarium.Testiarium;
import site.siredvin.testiarium.cct.CctComputers;
import site.siredvin.testiarium.cct.CctFixtureCommands;

@Mod("cloudsolutions_testmod")
public final class ForgeCloudSolutionsTestMod {
    public ForgeCloudSolutionsTestMod() {
        CctComputers.INSTANCE.initialize();
        NeoForge.EVENT_BUS.addListener((ServerStartingEvent event) -> {
            CctComputers.INSTANCE.reset();
            CctFixtureCommands.INSTANCE.importFiles(event.getServer());
        });
        Testiarium.register(CloudSolutionsGameTests.class);
    }
}
