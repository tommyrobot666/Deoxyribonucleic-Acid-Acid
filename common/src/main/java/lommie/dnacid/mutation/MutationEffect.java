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

import static lommie.dnacid.ModRegistries.MUTATION_EFFECT_TYPE_KEY;
import static lommie.dnacid.ModRegistries.MUTATION_EFFECT_TYPE_REGISTRY;

public class MutationEffect implements DataComponentHolder {
    public static Codec<MutationEffect> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ResourceKey.codec(MUTATION_EFFECT_TYPE_KEY).fieldOf("id").forGetter(e -> e.getType().getId()),
            Codec.INT.fieldOf("time").forGetter(e -> e.timeLeft),
            DataComponentMap.CODEC.fieldOf("components").forGetter(MutationEffect::getComponents))
            .apply(instance, (y, t, c) -> new MutationEffect(MUTATION_EFFECT_TYPE_REGISTRY.get(y).get()::value,t,(c))));
    public static StreamCodec<RegistryFriendlyByteBuf, MutationEffect> STREAM_CODEC = StreamCodec.of((b,e) -> e.encode(b),MutationEffect::decode);
    public int timeLeft;
    PatchedDataComponentMap components;
    @NotNull
    public Supplier<MutationEffectType> type;

    public MutationEffect(@NotNull Supplier<MutationEffectType> type,int timeLeft,DataComponentMap components){
        this.timeLeft = timeLeft;
        this.type = type;
        this.components = new PatchedDataComponentMap(components);
    }

    public MutationEffect(@NotNull Supplier<MutationEffectType> type,int timeLeft){
        this(type,timeLeft, DataComponentMap.EMPTY);
    }

    public MutationEffect(@NotNull ResourceLocation location, int timeLeft, DataComponentMap components){
        this(MUTATION_EFFECT_TYPE_REGISTRY.get(location).get()::value,timeLeft,components);
    }

    public MutationEffect(@NotNull ResourceLocation location,int timeLeft){
        this(MUTATION_EFFECT_TYPE_REGISTRY.get(location).get()::value,timeLeft);
    }

    public MutationEffect(@NotNull MutationEffectType type){
        this(type,-1);
    }
    public MutationEffect(@NotNull MutationEffectType type, int timeLeft){
        this(type,timeLeft,DataComponentMap.EMPTY);
    }

    public MutationEffect(@NotNull MutationEffectType type, int timeLeft, DataComponentMap components){
        this(() -> type,timeLeft,components);
    }

    @NotNull
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
        ResourceKey<MutationEffectType> id = buf.readResourceKey(MUTATION_EFFECT_TYPE_KEY); // You can lookup your MutationEffectType from the registry
        MutationEffectType effectType = MUTATION_EFFECT_TYPE_REGISTRY.getValue(id); // We'll write this next
        if (effectType == null){
            Dnacid.LOGGER.error("Unknown mutation effect type: " + id.location());
            effectType = MUTATION_EFFECT_TYPE_REGISTRY.getValue(MUTATION_EFFECT_TYPE_REGISTRY.getDefaultKey());
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
