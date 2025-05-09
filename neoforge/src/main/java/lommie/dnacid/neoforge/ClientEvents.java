package lommie.dnacid.neoforge;

import lommie.dnacid.screens.ModMenus;
import lommie.dnacid.screens.ProteinConstructorScreen;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientEvents {
    @SubscribeEvent
    public static void registerScreens(RegisterMenuScreensEvent event) {
        event.register(ModMenus.PROTEIN_CONSTRUCTOR_MENU.get(), ProteinConstructorScreen::new);
    }
}
