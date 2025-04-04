package lommie.dnacid.network;

import dev.architectury.networking.NetworkManager;/*
import dev.architectury.networking.simple.BaseS2CMessage;
import dev.architectury.networking.simple.MessageType;*/
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamDecoder;
import net.minecraft.world.item.crafting.display.RecipeDisplayEntry;

import java.util.List;
import java.util.function.Supplier;

public class ProteinConstructorRecipeDisplayEntriesMessage {//extends BaseS2CMessage {
    private List<RecipeDisplayEntry> proteinConstructorRecipeDisplayEntries;

    public ProteinConstructorRecipeDisplayEntriesMessage(FriendlyByteBuf buf){
        //this.proteinConstructorRecipeDisplayEntries = buf.readCollection()
    }

    public ProteinConstructorRecipeDisplayEntriesMessage(List<RecipeDisplayEntry> proteinConstructorRecipeDisplayEntries){
        this.proteinConstructorRecipeDisplayEntries = proteinConstructorRecipeDisplayEntries;
    }

    /*@Override
    public MessageType getType() {
        return null;
    }*/

    //@Override
    public void write(RegistryFriendlyByteBuf buf) {
        //buf.writeCollection();
    }

    //@Override
    public void handle(NetworkManager.PacketContext packetContext) {

    }
}
