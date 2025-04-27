package lommie.dnacid.items.components;

import com.mojang.serialization.Codec;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BacteriaDataComponentType implements DataComponentType<BacteriaData> {
    @Override
    public @Nullable Codec<BacteriaData> codec() {
        return BacteriaData.CODEC;
    }

    @Override
    public @NotNull StreamCodec<RegistryFriendlyByteBuf,BacteriaData> streamCodec() {
        return BacteriaData.STREAM_CODEC;
    }
}
