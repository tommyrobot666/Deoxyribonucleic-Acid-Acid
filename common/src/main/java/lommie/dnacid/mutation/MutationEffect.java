package lommie.dnacid.mutation;

import com.mojang.serialization.Codec;
import lommie.dnacid.Dnacid;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.function.Supplier;

public class MutationEffect {
    public Codec<MutationEffect> codec;
    public int timeLeft;
    @NotNull
    public Supplier<MutationEffectType<?>> type;

    public MutationEffect(int timeLeft, @NotNull Supplier<MutationEffectType<?>> type){
        this.timeLeft = timeLeft;
        this.type = type;
    }

    /**
     * @return returns whether effect should be removed
     * */
    public boolean mutationTick(MutationEffectContainer mutationEffectContainer){
        if (timeLeft == 0){
            return false;
        }else if (this.timeLeft > 0) {
            this.timeLeft--;
        }
        switch (mutationEffectContainer.getMutationCreatureType()){
            case PLAYER -> {
                ServerPlayer player = (ServerPlayer) mutationEffectContainer;
                return playerMutationTick(player);
            }
            case ENTITY, BLOCK -> {
                return false;
            }
        }

        return false;
    }

    /**
     * @return returns whether effect should be removed
     * */
    boolean playerMutationTick(ServerPlayer player){
        throw new IllegalStateException("Mutation Effect \""+this+"\" does nothing when applied to a player");
    };

    public MutationEffect withTimeLeft(int timeLeft){
        this.timeLeft = timeLeft;
        return this;
    }

    public void encode(RegistryFriendlyByteBuf buf) {
        // Write the type key first
        buf.writeResourceKey(this.type.get().key());
        buf.writeInt(this.timeLeft);

        // Look up the correct subclass codec based on the resource key
        MutationEffectType<?> effectType = Dnacid.MUTATION_EFFECT_TYPE_REGISTRY.getValue(this.type.get().key());
        if (effectType == null) {
            throw new IllegalStateException("Unknown MutationEffect type: " + this.type.get().key());
        }

        // Get the class type of the effect
        Class<?> effectClass = this.getClass(); // Get the actual class of the effect (e.g., TestMutationEffect)

        try {
            // Use reflection to get the "streamCodec" method for the specific subclass type
            Method streamCodecMethod = effectType.getClass().getMethod("streamCodec");
            StreamCodec<RegistryFriendlyByteBuf, ? extends MutationEffect> effectCodec =
                    (StreamCodec<RegistryFriendlyByteBuf, ? extends MutationEffect>) streamCodecMethod.invoke(effectType);

            // Use reflection to invoke the "encode" method of the codec
            Method encodeMethod = Arrays.stream(effectCodec.getClass().getMethods()).filter((m) -> m.getName().equals("encode")).findFirst().get();    //.getMethod("encode", RegistryFriendlyByteBuf.class, effectClass);
            encodeMethod.invoke(effectCodec, buf, this);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error encoding MutationEffect: " + e.getMessage(), e);
        }
    }

    public static MutationEffect decode(RegistryFriendlyByteBuf buf) {
        // First read the type
        ResourceKey<MutationEffectType<?>> type = buf.readResourceKey(Dnacid.MUTATION_EFFECT_TYPE_KEY); // You can lookup your MutationEffectType from the registry
        MutationEffectType<?> effectType = Dnacid.MUTATION_EFFECT_TYPE_REGISTRY.getValue(type); // We'll write this next
        if (effectType == null){
            Dnacid.LOGGER.error("Unknown mutation effect type: " + type);
            effectType = Dnacid.MUTATION_EFFECT_TYPE_REGISTRY.get(0).get().value();
        }
        int timeLeft = buf.readInt();
        // Then decode the data
        return effectType.streamCodec().decode(buf).withTimeLeft(timeLeft);
    }
}
