package lommie.dnacid.network;

import dev.architectury.impl.NetworkAggregator;
import dev.architectury.networking.NetworkManager;/*
import dev.architectury.networking.simple.BaseS2CMessage;
import dev.architectury.networking.simple.MessageType;*/
import io.netty.buffer.*;
import lommie.dnacid.Dnacid;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.display.RecipeDisplay;
import net.minecraft.world.item.crafting.display.RecipeDisplayEntry;
import net.minecraft.world.item.crafting.display.RecipeDisplayId;
import net.minecraft.world.level.ItemLike;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.stream.Stream;

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
            /*ArrayList<RecipeDisplayEntry> list = new ArrayList<>();
            for (int i = 0; i < buf.readShort(); i++) {
                list.add(new RecipeDisplayEntry(
                        new RecipeDisplayId(buf.readInt()),
                        RecipeDisplay.STREAM_CODEC.decode(buf),
                        (buf.readBoolean() ? OptionalInt.of(buf.readInt()) : OptionalInt.empty()),
                        Dnacid.PROTEIN_CONSTRUCTOR_RECIPE_CATEGORY.get(),
                        readIngredientList(buf)
                ));
            }
            this(list.stream().toList());*/
            this(readEntries(buf));
        }

        private static List<RecipeDisplayEntry> readEntries(RegistryFriendlyByteBuf buf) {
            ArrayList<RecipeDisplayEntry> list = new ArrayList<>();
            for (int i = 0; i < buf.readShort(); i++) {
                list.add(RecipeDisplayEntry.STREAM_CODEC.decode(buf)/*new RecipeDisplayEntry(
                        new RecipeDisplayId(buf.readInt()),
                        RecipeDisplay.STREAM_CODEC.decode(buf),
                        (buf.readBoolean() ? OptionalInt.of(buf.readInt()) : OptionalInt.empty()),
                        Dnacid.PROTEIN_CONSTRUCTOR_RECIPE_CATEGORY.get(),
                        readIngredientList(buf)
                )*/);
            }
            return list.stream().toList();
        }

        /*private static Optional<List<Ingredient>> readIngredientList(RegistryFriendlyByteBuf buf) {
            if (!buf.readBoolean()) {
                return Optional.empty();
            }
            ArrayList<Ingredient> list = new ArrayList<>();
            for (int i = 0; i < buf.readShort(); i++) {
                list.add(Ingredient.CONTENTS_STREAM_CODEC.decode(buf));//.of(readItemlikeStream(buf)));
            }
            return Optional.of(list.stream().toList());
        }

        private static Stream<? extends ItemLike> readItemlikeStream(RegistryFriendlyByteBuf buf) {
            ArrayList<Item> list = new ArrayList<>();
            for (int i = 0; i < buf.readShort(); i++) {
                list.add(buf.registryAccess().get(ResourceKey.create(Registries.ITEM, ResourceLocation.STREAM_CODEC.decode(buf))).get().value());
            }
            return list.stream();
        }*/

        @Override
        public @NotNull Type<? extends CustomPacketPayload> type() {
            return PACKET_TYPE;
        }

        /*public NetworkAggregator.BufCustomPacketPayload toBufCustomPacketPayload(RegistryAccess registryAccess){
            RegistryFriendlyByteBuf buf = new RegistryFriendlyByteBuf(Unpooled.buffer(),registryAccess);
            write(buf);
            return new NetworkAggregator.BufCustomPacketPayload(NetworkAggregator.S2C_TYPE.get(ProteinConstructorRecipeDisplayEntriesPacket.PACKET_ID),buf.array());
        }*/

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
                /*// Write the RecipeDisplayId (assumed to be an int).
                buf.writeInt(entry.id().index());

                // Write the RecipeDisplay using its codec.
                RecipeDisplay.STREAM_CODEC.encode(buf, entry.display());

                // Write the optional int: first a boolean flag then, if present, the int value.
                if (entry.group().isPresent()) {
                    buf.writeBoolean(true);
                    buf.writeInt(entry.group().getAsInt());
                } else {
                    buf.writeBoolean(false);
                }

                // Write the ingredient list.
                writeIngredientList(buf, entry.craftingRequirements());*/
            }
        }/*

        private static void writeIngredientList(RegistryFriendlyByteBuf buf, Optional<List<Ingredient>> optionalIngredients) {
            // Write a flag indicating if an ingredient list is present.
            if (optionalIngredients.isEmpty()) {
                buf.writeBoolean(false);
            } else {
                buf.writeBoolean(true);
                List<Ingredient> ingredients = optionalIngredients.get();
                // Write the number of ingredients as a short.
                buf.writeShort(ingredients.size());
                for (Ingredient ingredient : ingredients) {
                    // Write each ingredient by writing its stream of ItemLike objects.
                    //writeItemlikeStream(buf, ingredient);
                    Ingredient.CONTENTS_STREAM_CODEC.encode(buf, ingredient);
                }
            }
        }

        private static void writeItemlikeStream(RegistryFriendlyByteBuf buf, Ingredient ingredient) {
            // Assume Ingredient#getItems() returns an array (or list) of ItemLike objects.
            // (Adjust this call if your Ingredient API differs.)
            Holder<Item>[] items = (Holder<Item>[]) ingredient.items().toArray();

            // Write the number of ItemLike entries as a short.
            buf.writeShort(items.length);
            for (Holder<Item> item : items) {
                // Write each ItemLike as its ResourceLocation.
                // Here we assume item.asItem() returns an Item that has a method getRegistryName()
                // which in turn returns the ResourceLocation. Adjust if needed.
                ResourceLocation loc = item.value().asItem().arch$registryName();
                ResourceLocation.STREAM_CODEC.encode(buf, loc);
            }
        }*/
    }

    @Environment(EnvType.CLIENT)
    public static void receive(PacketPayload payload, NetworkManager.PacketContext context) {
        Dnacid.LOGGER.info(Dnacid.proteinConstructorRecipeDisplayEntries.toString());
        Dnacid.proteinConstructorRecipeDisplayEntries = payload.data;
        Dnacid.LOGGER.info(Dnacid.proteinConstructorRecipeDisplayEntries.toString());
    }
}