package lommie.dnacid.network;

import dev.architectury.networking.NetworkManager;/*
import dev.architectury.networking.simple.BaseS2CMessage;
import dev.architectury.networking.simple.MessageType;*/
import lommie.dnacid.Dnacid;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.display.RecipeDisplayEntry;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class ProteinConstructorRecipeDisplayEntriesPacket {//extends BaseS2CMessage {
    public static final ResourceLocation PACKET_ID = ResourceLocation.tryBuild(Dnacid.MOD_ID, "protein_constructor_recipe_display_entries_packet");
    public static final CustomPacketPayload.Type<PacketPayload> PACKET_TYPE;
    public static final StreamCodec<RegistryFriendlyByteBuf, PacketPayload> PACKET_CODEC;


    static {
        PACKET_TYPE = new CustomPacketPayload.Type(PACKET_ID);
        PACKET_CODEC = CustomPacketPayload.codec(PacketPayload::write, PacketPayload::new);
    }

    public record PacketPayload(List<RecipeDisplayEntry> data) implements CustomPacketPayload {

        public PacketPayload(List<RecipeDisplayEntry> data) {
            this.data = data;
        }

        public PacketPayload(RegistryFriendlyByteBuf buf) {
            this(readEntries(buf));
        }

        private static List<RecipeDisplayEntry> readEntries(RegistryFriendlyByteBuf buf) {
            ArrayList<RecipeDisplayEntry> list = new ArrayList<>();
            for (int i = 0; i < buf.readShort(); i++) {
                list.add(RecipeDisplayEntry.STREAM_CODEC.decode(buf));
            }
            return list.stream().toList();
        }


        @Override
        public @NotNull Type<? extends CustomPacketPayload> type() {
            return PACKET_TYPE;
        }


        /**
         * Made by ChatGPT because I used all of my brainpower on the read function
         *
         * @param buf is buffer
         */
        public void write(RegistryFriendlyByteBuf buf) {
            // Write the number of recipe entries as a short.
            buf.writeShort(this.data.size());
            for (RecipeDisplayEntry entry : this.data) {
                RecipeDisplayEntry.STREAM_CODEC.encode(buf,entry);
            }
        }
    }

    @Environment(EnvType.CLIENT)
    public static void receive(PacketPayload payload, NetworkManager.PacketContext context) {
        Dnacid.proteinConstructorRecipeDisplayEntries = payload.data;
    }
}