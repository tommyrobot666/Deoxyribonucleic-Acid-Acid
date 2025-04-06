package lommie.dnacid.mutation;

import lommie.dnacid.recipe.ProteinConstructorRecipe;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

import java.util.Optional;


public class MutationEffectType<E extends MutationEffect> {
    StreamCodec<RegistryFriendlyByteBuf ,E> codec;
    E defaultMutationEffect;
    Optional<String> aminoAcids;
    Optional<ProteinConstructorRecipe> generatedRecipe;
    Component name;

    public MutationEffectType(Component name, E defaultMutationEffect,StreamCodec<RegistryFriendlyByteBuf ,E> codec, Optional<String> aminoAcids, ResourceLocation recipeLocation){
        this.name = name;
        this.codec = codec;
        this.defaultMutationEffect = defaultMutationEffect;
        this.aminoAcids = aminoAcids;
        this.generatedRecipe = aminoAcids.isPresent() ? Optional.of(generateRecipe(aminoAcids,recipeLocation)) : Optional.empty();
    }

    private ProteinConstructorRecipe generateRecipe(Optional<String> aminoAcids, ResourceLocation recipeLocation) {
        return new ProteinConstructorRecipe(recipeLocation,recipeLocation.getPath(), aminoAcids.orElseThrow(), recipeLocation.getPath());
    }

    public MutationEffectType(Component name, E defaultMutationEffect,StreamCodec<RegistryFriendlyByteBuf ,E> codec){
        this(name,defaultMutationEffect,codec,Optional.empty(),ResourceLocation.withDefaultNamespace(""));
    }
}
