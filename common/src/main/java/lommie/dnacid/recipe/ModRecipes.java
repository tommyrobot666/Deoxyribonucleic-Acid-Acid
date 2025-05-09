package lommie.dnacid.recipe;

import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.RecipeBookCategory;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;

import static lommie.dnacid.Dnacid.MOD_ID;

public class ModRecipes {
    public static final DeferredRegister<RecipeBookCategory> RECIPE_BOOK_CATEGORIES =
            DeferredRegister.create(MOD_ID, Registries.RECIPE_BOOK_CATEGORY);
    public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS =
            DeferredRegister.create(MOD_ID, Registries.RECIPE_SERIALIZER);
    public static final DeferredRegister<RecipeType<?>> RECIPE_TYPES =
            DeferredRegister.create(MOD_ID, Registries.RECIPE_TYPE);

    public static final RegistrySupplier<RecipeType<ProteinConstructorRecipe>> PROTEIN_CONSTRUCTOR_RECIPE_TYPE =
            RECIPE_TYPES.register("protein_constructor", () -> ProteinConstructorRecipeType.INSTANCE);

    public static final RegistrySupplier<RecipeSerializer<ProteinConstructorRecipe>> PROTEIN_CONSTRUCTOR_RECIPE_SERIALIZER =
            RECIPE_SERIALIZERS.register("protein_constructor", () -> ProteinConstructorRecipeSerializer.INSTANCE);

    public static final RegistrySupplier<RecipeBookCategory> PROTEIN_CONSTRUCTOR_RECIPE_CATEGORY = RECIPE_BOOK_CATEGORIES.register("protein_constructor",
            RecipeBookCategory::new
    );

    public static void register(){
        RECIPE_SERIALIZERS.register();
        RECIPE_TYPES.register();
        RECIPE_BOOK_CATEGORIES.register();
    }
}
