package lommie.dnacid.items.components;

import com.mojang.serialization.Codec;
import lommie.dnacid.mutation.MutationEffect;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public record BacteriaData(boolean petriDish, List<MutationEffect> effects) {
    public static final @Nullable Codec<BacteriaData> CODEC = null;
    public static final StreamCodec<RegistryFriendlyByteBuf,BacteriaData> STREAM_CODEC = StreamCodec.of(BacteriaData::encodeStream,BacteriaData::decodeStream);

    private static BacteriaData decodeStream(RegistryFriendlyByteBuf buf) {
        return new BacteriaData(buf.readBoolean(),buf.readList((b) -> MutationEffect.decode(((RegistryFriendlyByteBuf) b))));
    }

    private static void encodeStream(RegistryFriendlyByteBuf buf, BacteriaData bacteriaData) {
        buf.writeBoolean(bacteriaData.petriDish());
        buf.writeCollection(bacteriaData.effects,(b,e) -> e.encode((RegistryFriendlyByteBuf) b));
    }

}
