package lommie.dnacid.mutation;

import com.mojang.serialization.Codec;
import lommie.dnacid.Dnacid;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;

public class MutationEffect {
    public Codec<MutationEffect> codec;
    int timeLeft;
    MutationEffectType<?> type;

    MutationEffect(int timeLeft, MutationEffectType<?> type){
        this.timeLeft = timeLeft;
        this.type = type;
    }

    MutationEffect(MutationEffectType<?> type){
        this(0,type);
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
        buf.writeResourceKey(this.type.key());
        buf.writeInt(this.timeLeft);

        // Now encode using the specific codec of the effect type
        if (this instanceof TestMutationEffect) {
            // Handle encoding for TestMutationEffect
            TestMutationEffect effect = (TestMutationEffect) this;
            TestMutationEffect.STREAM_CODEC.encode(buf, effect);
        } else {
            // Add more else-if conditions for other MutationEffect subclasses
            throw new IllegalStateException("Unknown MutationEffect subclass: " + this.getClass().getName());
        }
    }

    public static MutationEffect decode(RegistryFriendlyByteBuf buf) {
        // First read the type
        var type = buf.readRegistryKey(); // You can lookup your MutationEffectType from the registry
        MutationEffectType<?> effectType = Dnacid.MUTATION_EFFECT_TYPE_REGISTRY.getValue((ResourceKey<MutationEffectType<?>>) type); // We'll write this next
        int timeLeft = buf.readInt();
        // Then decode the data
        return effectType.streamCodec().decode(buf).withTimeLeft(timeLeft);
    }
}
