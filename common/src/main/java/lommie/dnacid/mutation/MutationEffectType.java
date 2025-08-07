package lommie.dnacid.mutation;

import lommie.dnacid.items.components.BacteriaData;
import lommie.dnacid.items.components.ModComponents;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Style;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.function.Supplier;

public class MutationEffectType {
    Settings settings;

    public MutationEffectType(Settings settings){
        if (settings.id == null) {
            throw new IllegalStateException("MutationEffectType must have an ID");
        }
        this.settings = settings;
    }

    /**
     * @return returns whether effect should be removed
     * */
    public boolean mutationTick(MutationEffect effect, MutationEffectContainer mutationEffectContainer){
        if (effect.timeLeft == 0){
            return true;
        }else if (effect.timeLeft > 0) {
            effect.timeLeft--;
        }
        switch (mutationEffectContainer.getMutationCreatureType()){
            case PLAYER -> {
                return effect.getType().playerMutationTick(effect, (ServerPlayer) mutationEffectContainer);
            }
            case ENTITY -> {
                return effect.getType().entityMutationTick(effect, (LivingEntity) mutationEffectContainer);
            }
            case PLANT_BLOCK -> {
                return effect.getType().plantBlockMutationTick(effect, (BlockState) mutationEffectContainer);
            }
            case BACTERIA -> {
                if ((Object) mutationEffectContainer instanceof ItemStack stack) {
                    return effect.getType().bacteriaMutationTick(effect, stack);
                } else {
                    throw new IllegalStateException("Expected ItemStack for BACTERIA but got: " + mutationEffectContainer.getClass());
                }
            }
        }

        return false;
    }

    /**
     * @return returns whether effect should be removed
     * */
    boolean playerMutationTick(MutationEffect effect, ServerPlayer player){
        throw new IllegalStateException("Mutation Effect \""+getName().getString()+"\" does nothing when applied to a player");
    };

    /**
     * @return returns whether effect should be removed
     * */
    boolean entityMutationTick(MutationEffect effect, LivingEntity entity){
        throw new IllegalStateException("Mutation Effect \""+getName().getString()+"\" does nothing when applied to a entity");
    };

    /**
     * @return returns whether effect should be removed
     * */
    boolean plantBlockMutationTick(MutationEffect effect, BlockState state){
        throw new IllegalStateException("Mutation Effect \""+getName().getString()+"\" does nothing when applied to a plant");
    };

    /**
     * @return returns whether effect should be removed
     * */
    boolean bacteriaMutationTick(MutationEffect effect, ItemStack bacteria){
        throw new IllegalStateException("Mutation Effect \""+getName().getString()+"\" does nothing when applied to a bacteria");
    }

    void setBacteriaData(ItemStack bac , BacteriaData data){
        bac.set(ModComponents.BACTERIA_DATA_COMPONENT.get(),data);
    }

    BacteriaData getBacteriaData(ItemStack bac){
        return bac.get(ModComponents.BACTERIA_DATA_COMPONENT.get());
    }

    public MutationEffect decode(RegistryFriendlyByteBuf buf, MutationEffect effect) {return effect;}

    public void encode(RegistryFriendlyByteBuf buf) {}

    public ResourceKey<MutationEffectType> getId() {
        return settings.id;
    }

    public StreamCodec<RegistryFriendlyByteBuf, MutationEffect> getStreamCodec() {
        return settings.codec;
    }

    public Component getName(){
        return settings.name;
    }

    public Optional<MutationEffect> defaultEffect(){
        return settings.defaultEffect.get();
    }

    public static class Settings{
        StreamCodec<RegistryFriendlyByteBuf, MutationEffect> codec;
        public Supplier<Optional<MutationEffect>> defaultEffect;
        public Component name;
        @Nullable
        private ResourceKey<MutationEffectType> id;

        public Settings() {}

        public Settings defaultEffect(Supplier<Optional<MutationEffect>> defaultEffect) {
            this.defaultEffect = defaultEffect;
            return this;
        }

        public Settings codec(StreamCodec<RegistryFriendlyByteBuf, MutationEffect> codec) {
            this.codec = codec;
            return this;
        }


        public Settings name(Component name){
            this.name = name;
            return this;
        }

        public Settings name(String name){
            return name(Component.literal(name));
        }

        public Settings nameStyle(Style style){
            this.name = name.copy().setStyle(style);
            return this;
        }

        public Settings translationName(){
            this.name = Component.translatable("mutationEffect."+this.id.location().getNamespace()+"."+this.id.location().getPath());
            return this;
        }

        public Settings setId(ResourceKey<MutationEffectType> id) {
            this.id = id;
            return this;
        }
    }
}
