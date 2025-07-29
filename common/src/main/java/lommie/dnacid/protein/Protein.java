package lommie.dnacid.protein;

import lommie.dnacid.mutation.MutationEffect;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

import java.util.List;

public class Protein {
    public static final StreamCodec<? super RegistryFriendlyByteBuf, Protein> STREAM_CODEC = StreamCodec.of(
            (b,p) -> {},
            (b) -> {return new Protein(List.of(),"");}
    );
    public final List<MutationEffect> effects;
    public final String aminoAcids;

    public Protein(List<MutationEffect> effects, String aminoAcids) {
        this.effects = effects;
        this.aminoAcids = aminoAcids;
    }
}
