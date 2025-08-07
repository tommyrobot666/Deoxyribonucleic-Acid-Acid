package lommie.dnacid.items.components;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import lommie.dnacid.mutation.MutationEffect;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public record BacteriaData(boolean petriDish, List<MutationEffect> effects, Map<ResourceLocation,Float> metabolicOutputs) {
    public static final @Nullable Codec<BacteriaData> CODEC = RecordCodecBuilder.create((instance) ->
            instance.group(
                    Codec.BOOL.fieldOf("petriDish").forGetter(BacteriaData::petriDish),
                    MutationEffect.CODEC.listOf().fieldOf("effects").forGetter(BacteriaData::effects),
                    Codec.unboundedMap(ResourceLocation.CODEC,Codec.FLOAT).fieldOf("metabolicOutputs").forGetter(BacteriaData::metabolicOutputs)
            ).apply(instance, BacteriaData::new));
    public static final StreamCodec<RegistryFriendlyByteBuf,BacteriaData> STREAM_CODEC = StreamCodec.of(BacteriaData::encodeStream,BacteriaData::decodeStream);

    private static BacteriaData decodeStream(RegistryFriendlyByteBuf buf) {
        return new BacteriaData(buf.readBoolean(),buf.readList((b) -> MutationEffect.decode(((RegistryFriendlyByteBuf) b))),buf.readMap(
                FriendlyByteBuf::readResourceLocation,
                FriendlyByteBuf::readFloat
        ));
    }

    private static void encodeStream(RegistryFriendlyByteBuf buf, BacteriaData bacteriaData) {
        buf.writeBoolean(bacteriaData.petriDish());
        buf.writeCollection(bacteriaData.effects(),(b,e) -> e.encode((RegistryFriendlyByteBuf) b));
        buf.writeMap(bacteriaData.metabolicOutputs(), ResourceLocation.STREAM_CODEC::encode, FriendlyByteBuf::writeFloat);
    }

    public BacteriaData addEffect(MutationEffect effect){
        ArrayList<MutationEffect> newEffects = new ArrayList<>(effects());
        newEffects.add(effect);
       return new BacteriaData(petriDish(), newEffects, metabolicOutputs());
    }

    public BacteriaData setEffects(List<MutationEffect> effects){
        return new BacteriaData(petriDish(), effects, metabolicOutputs());
    }

    public BacteriaData setPetriDish(boolean petriDish){
        return new BacteriaData(petriDish, effects(), metabolicOutputs());
    }

    public BacteriaData addMetabolicOutput(ResourceLocation metabolicOutput, float amount){
        HashMap<ResourceLocation,Float> newMetabolicOutputs = new HashMap<>(metabolicOutputs());
        newMetabolicOutputs.putIfAbsent(metabolicOutput,metabolicOutputs().getOrDefault(metabolicOutput,0f) + amount);
        return new BacteriaData(petriDish(), effects(), newMetabolicOutputs);
    }

    public boolean hasMetabolicOutputs(Map<ResourceLocation,Float> havingMetabolicOutputs){
        for (ResourceLocation metabolicOutput : havingMetabolicOutputs.keySet()){
            if (metabolicOutputs().getOrDefault(metabolicOutput,0f) < havingMetabolicOutputs.get(metabolicOutput)){
                return false;
            }
        }
        return true;
    }

    public BacteriaData takeMetabolicOutputs(Map<ResourceLocation,Float> havingMetabolicOutputs){
        HashMap<ResourceLocation,Float> newMetabolicOutputs = new HashMap<>(metabolicOutputs());
        for (ResourceLocation metabolicOutput : havingMetabolicOutputs.keySet()){
            newMetabolicOutputs.putIfAbsent(metabolicOutput,metabolicOutputs().getOrDefault(metabolicOutput,0f)-havingMetabolicOutputs.get(metabolicOutput));
        }
        return new BacteriaData(petriDish(),effects(),newMetabolicOutputs);
    }
}
