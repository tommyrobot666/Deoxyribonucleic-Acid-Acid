package lommie.dnacid.items.components;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import org.jetbrains.annotations.Nullable;

public record BacteriaData(boolean petriDish) {
    public static final @Nullable Codec<BacteriaData> CODEC = RecordCodecBuilder.create(
            (i) -> i.group(
                    Codec.BOOL.fieldOf("petriDish").forGetter(BacteriaData::petriDish)
            ).apply(i, BacteriaData::new)
    );
    public static final StreamCodec<RegistryFriendlyByteBuf,BacteriaData> STREAM_CODEC = StreamCodec.of(BacteriaData::encodeStream,BacteriaData::decodeStream);

    private static <T> DataResult<Pair<BacteriaData,T>> decode(DynamicOps<T> tDynamicOps, T t) {
        return DataResult.success(Pair.of(new BacteriaData(false),t));
    }

    private <T> DataResult<T> encode(DynamicOps<T> tDynamicOps, T t) {
        return DataResult.success(t);
    }

    private static BacteriaData decodeStream(RegistryFriendlyByteBuf buf) {
        return new BacteriaData(buf.readBoolean());
    }

    private static void encodeStream(RegistryFriendlyByteBuf buf, BacteriaData bacteriaData) {
        buf.writeBoolean(bacteriaData.petriDish());
    }

}
