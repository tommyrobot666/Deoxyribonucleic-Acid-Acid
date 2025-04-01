package lommie.dnacid.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeSerializer;
import org.jetbrains.annotations.NotNull;

public class ProteinConstructorRecipeSerializer implements RecipeSerializer<ProteinConstructorRecipe> {
    public static final ProteinConstructorRecipeSerializer INSTANCE = new ProteinConstructorRecipeSerializer();

    @Override
    public @NotNull
    com.mojang.serialization.MapCodec<ProteinConstructorRecipe> codec() {
        // Note: the recipe ID is not in the JSON itself—it is provided externally.
        // Here we decode the group, pattern, and result.
        return RecordCodecBuilder.mapCodec(instance -> instance.group(
                Codec.STRING.optionalFieldOf("group", "").forGetter(recipe -> recipe.group)
        ).and(
                Codec.STRING.fieldOf("pattern").forGetter(recipe -> recipe.patternString)
        ).and(
                Codec.STRING.fieldOf("result").forGetter(recipe -> recipe.output)
        ).apply(instance, ProteinConstructorRecipe::new)
        );
    }

    @Override
    public @NotNull StreamCodec<RegistryFriendlyByteBuf, ProteinConstructorRecipe> streamCodec() {
        return new StreamCodec<>() {
            @Override
            public void encode(RegistryFriendlyByteBuf buf, ProteinConstructorRecipe recipe) {
                buf.writeUtf(recipe.id.toString());
                buf.writeUtf(recipe.group);
                buf.writeUtf(recipe.patternString);
                buf.writeUtf(recipe.output);
            }

            @Override
            public @NotNull ProteinConstructorRecipe decode(RegistryFriendlyByteBuf buf) {
                String id = buf.readUtf();
                String group = buf.readUtf();
                String pattern = buf.readUtf();
                String result = buf.readUtf();
                // Again, use a dummy ID; the proper ID is provided externally.
                return new ProteinConstructorRecipe(ResourceLocation.parse(id), group, pattern, result);
            }
        };
    }
}
