package lommie.dnacid.client;

import dev.architectury.networking.NetworkManager;
import dev.architectury.platform.Platform;
import dev.architectury.registry.menu.MenuRegistry;
import lommie.dnacid.network.ProteinConstructorRecipeDisplayEntriesPacket;
import lommie.dnacid.screens.ModMenus;
import lommie.dnacid.screens.ProteinConstructorScreen;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class DnacidClient {
    public static void init(){
        //NetworkManager.registerReceiver(NetworkManager.s2c(), ProteinConstructorRecipeDisplayEntriesPacket.PACKET_TYPE, ProteinConstructorRecipeDisplayEntriesPacket.PACKET_CODEC, ProteinConstructorRecipeDisplayEntriesPacket::receive);
        /*NetworkManager.registerS2CPayloadType(
                ProteinConstructorRecipeDisplayEntriesPacket.PACKET_TYPE,
                ProteinConstructorRecipeDisplayEntriesPacket.PACKET_CODEC
        );*/
        NetworkManager.registerReceiver(
                NetworkManager.Side.S2C,
                ProteinConstructorRecipeDisplayEntriesPacket.PACKET_TYPE,
                ProteinConstructorRecipeDisplayEntriesPacket.PACKET_CODEC,
                ProteinConstructorRecipeDisplayEntriesPacket::receive
        );
        if (Platform.isFabric()){
            MenuRegistry.registerScreenFactory(ModMenus.PROTEIN_CONSTRUCTOR_MENU.get(), ProteinConstructorScreen::new);
        }
    }
}
