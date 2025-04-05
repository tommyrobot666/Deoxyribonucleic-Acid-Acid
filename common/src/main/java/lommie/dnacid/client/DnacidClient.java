package lommie.dnacid.client;

import dev.architectury.networking.NetworkManager;
import lommie.dnacid.network.ProteinConstructorRecipeDisplayEntriesPacket;
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
    }
}
