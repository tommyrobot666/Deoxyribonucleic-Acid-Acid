package lommie.dnacid.protein;

import com.mojang.serialization.Codec;
import lommie.dnacid.ModRegistries;
import lommie.dnacid.mutation.MutationEffect;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

public class Protein {
    public static final StreamCodec<RegistryFriendlyByteBuf, Protein> STREAM_CODEC =
            StreamCodec.of(
            (b,p) -> {
                if (!p.id.getPath().equals("no_id")){
                    b.writeBoolean(true);
                    b.writeResourceLocation(p.id);
                }
                b.writeBoolean(false);

                b.writeCollection(p.effects,(buff,e) -> e.encode((RegistryFriendlyByteBuf) buff));
                b.writeUtf(p.aminoAcids);
            },
            (b) -> {
                if (b.readBoolean()){
                    return b.registryAccess().lookupOrThrow(ModRegistries.PROTEINS_KEY).get(b.readResourceLocation()).get().value();
                }

                return new Protein(b.readList((buf) -> MutationEffect.decode(((RegistryFriendlyByteBuf) buf))),b.readUtf());
            }
    );
    public static final Codec<Protein> CODEC = null;
    public final List<MutationEffect> effects;
    public final String aminoAcids;
    public final ResourceLocation id;

    public Protein(List<MutationEffect> effects, String aminoAcids) {
        this.effects = effects;
        this.aminoAcids = aminoAcids;
        this.id = ResourceLocation.withDefaultNamespace("no_id");
    }

    public Protein(List<MutationEffect> effects, String aminoAcids, ResourceLocation id) {
        this.effects = effects;
        this.aminoAcids = aminoAcids;
        this.id = id;
    }
}
