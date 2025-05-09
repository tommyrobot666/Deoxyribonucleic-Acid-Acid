package lommie.dnacid.fabric.client;

import lommie.dnacid.client.DnacidClient;
import net.fabricmc.api.ClientModInitializer;

public final class DnacidFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        // This entrypoint is suitable for setting up client-specific logic, such as rendering.
        //MenuRegistry.registerScreenFactory(Dnacid.PROTEIN_CONSTRUCTOR_MENU.get(), ProteinConstructorScreen::new);
        DnacidClient.init();
    }
}
