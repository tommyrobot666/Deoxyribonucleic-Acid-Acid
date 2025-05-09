package lommie.dnacid.mutation;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import lommie.dnacid.Dnacid;
import net.minecraft.core.component.DataComponentHolder;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.PatchedDataComponentMap;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

public class MutationEffect implements DataComponentHolder {
    public static Codec<MutationEffect> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ResourceKey.codec(Dnacid.MUTATION_EFFECT_TYPE_KEY).fieldOf("id").forGetter(e -> e.getType().getId()),
            Codec.INT.fieldOf("time").forGetter(e -> e.timeLeft),
            PatchedDataComponentMap.CODEC.fieldOf("components").forGetter(MutationEffect::getComponents))
            .apply(instance, (y, t, c) -> new MutationEffect(Dnacid.MUTATION_EFFECT_TYPE_REGISTRY.get(y).get()::value,t,((PatchedDataComponentMap) c))));
    public static StreamCodec<RegistryFriendlyByteBuf, MutationEffect> STREAM_CODEC = StreamCodec.of((b,e) -> e.encode(b),MutationEffect::decode);
    public int timeLeft;
    PatchedDataComponentMap components;
    @NotNull
    public Supplier<MutationEffectType> type;

    public MutationEffect(@NotNull Supplier<MutationEffectType> type,int timeLeft,PatchedDataComponentMap components){
        this.timeLeft = timeLeft;
        this.type = type;
        this.components = components;
    }

    public MutationEffect(@NotNull Supplier<MutationEffectType> type,int timeLeft){
        this(type,timeLeft, new PatchedDataComponentMap(DataComponentMap.EMPTY));
    }

    public MutationEffect(@NotNull ResourceLocation location, int timeLeft, PatchedDataComponentMap components){
        this(Dnacid.MUTATION_EFFECT_TYPE_REGISTRY.get(location).get()::value,timeLeft,components);
    }

    public MutationEffect(@NotNull ResourceLocation location,int timeLeft){
        this(Dnacid.MUTATION_EFFECT_TYPE_REGISTRY.get(location).get()::value,timeLeft);
    }

    public MutationEffect(@NotNull MutationEffectType type){
        this(type,-1);
    }
    public MutationEffect(@NotNull MutationEffectType type, int timeLeft){
        this(type,timeLeft,new PatchedDataComponentMap(DataComponentMap.EMPTY));
    }

    public MutationEffect(@NotNull MutationEffectType type, int timeLeft, PatchedDataComponentMap components){
        this(() -> type,timeLeft,components);
    }

    public MutationEffectType getType(){
        return type.get();
    }

    public MutationEffect withTimeLeft(int timeLeft){
        this.timeLeft = timeLeft;
        return this;
    }

    public void encode(RegistryFriendlyByteBuf buf) {
        buf.writeResourceKey(this.getType().getId());
        buf.writeInt(this.timeLeft);
        getType().encode(buf);
    }

    public static MutationEffect decode(RegistryFriendlyByteBuf buf) {
        ResourceKey<MutationEffectType> id = buf.readResourceKey(Dnacid.MUTATION_EFFECT_TYPE_KEY); // You can lookup your MutationEffectType from the registry
        MutationEffectType effectType = Dnacid.MUTATION_EFFECT_TYPE_REGISTRY.getValue(id); // We'll write this next
        if (effectType == null){
            Dnacid.LOGGER.error("Unknown mutation effect type: " + id.location());
            effectType = Dnacid.MUTATION_EFFECT_TYPE_REGISTRY.getValue(Dnacid.MUTATION_EFFECT_TYPE_REGISTRY.getDefaultKey());
        }
        int timeLeft = buf.readInt();
        MutationEffect effect = new MutationEffect(effectType,timeLeft);
        return effectType.decode(buf,effect);
    }

    @Override
    public @NotNull DataComponentMap getComponents() {
        return components;
    }

    @Override
    public @Nullable <T> T get(DataComponentType<? extends T> dataComponentType) {
        return components.get(dataComponentType);
    }

    @Override
    public <T> @NotNull T getOrDefault(DataComponentType<? extends T> dataComponentType, T object) {
        return components.getOrDefault(dataComponentType,object);
    }

    @Override
    public boolean has(DataComponentType<?> dataComponentType) {
        return components.has(dataComponentType);
    }

    public <T> void set(DataComponentType<T> dataComponentType, @Nullable T object) {
        this.components.set(dataComponentType, object);
    }
}
