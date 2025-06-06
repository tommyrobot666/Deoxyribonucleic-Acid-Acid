package lommie.dnacid.recipe;

import com.google.common.collect.ImmutableMap;
import lommie.dnacid.Dnacid;
import lommie.dnacid.items.ModItems;
import lommie.dnacid.items.components.ModComponents;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.item.crafting.display.RecipeDisplay;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class ProteinConstructorRecipe implements Recipe<RecipeInput> {
    final ResourceLocation id;
    final String group;
    final String patternString;
    final String output;

    private static Map<Character, Ingredient> generateKeyMapping() {
        HashMap<Character,Ingredient> map = new HashMap<>(Dnacid.AMINO_ACID_CHARS.size());
        for (int i = 0; i < Dnacid.AMINO_ACID_CHARS.size(); i++) {
            map.put(Dnacid.AMINO_ACID_CHARS.get(i), Ingredient.of(ModItems.AMINO_ACIDS.get(i).get()));
        }
        return ImmutableMap.copyOf(map);
    }


    public ProteinConstructorRecipe(ResourceLocation id, String group, String patternString, String output) {
        this.id = id;
        this.group = group;
        this.patternString = patternString;
        this.output = output;
    }

    public ProteinConstructorRecipe(String group, String patternString, String output) {
        this(ResourceLocation.withDefaultNamespace("idk"),group,patternString,output);
    }

    @Override
    public boolean matches(RecipeInput recipeInput, Level level) {
        return this.toShapedRecipe().matches((CraftingInput) recipeInput,level);
    }

    @Override
    public @NotNull ItemStack assemble(RecipeInput recipeInput, HolderLookup.Provider provider) {
        return this.toShapedRecipe().assemble((CraftingInput) recipeInput,provider);
    }

    @Override
    public @NotNull RecipeSerializer<? extends Recipe<RecipeInput>> getSerializer() {
        return ProteinConstructorRecipeSerializer.INSTANCE;
    }

    @Override
    public @NotNull RecipeType<? extends Recipe<RecipeInput>> getType() {
        return ProteinConstructorRecipeType.INSTANCE;
    }

    @Override
    public List<RecipeDisplay> display() {
        return toShapedRecipe().display();
    }

    @Override
    public @NotNull PlacementInfo placementInfo() {
        return this.toShapedRecipe().placementInfo();
    }

    @Override
    public @NotNull RecipeBookCategory recipeBookCategory() {
        return ModRecipes.PROTEIN_CONSTRUCTOR_RECIPE_CATEGORY.get();
    }

    public ShapedRecipe toShapedRecipe() {
        int rowLength = 40;
        int numRows = 5;
        int totalLength = rowLength * numRows;

// Pad the patternString with spaces to ensure it's long enough.
        String paddedPatternString = String.format("%-" + totalLength + "s", patternString);
        //Dnacid.LOGGER.warn(paddedPatternString);
        List<String> rows = new ArrayList<>(numRows);
        for (int row = 0; row < numRows; row++) {
            int start = row * rowLength;
            int end = start + rowLength;
            rows.add(paddedPatternString.substring(start, end));
        }
        // Use the built-in factory method to create a ShapedRecipePattern.
        ShapedRecipePattern shapedPattern = new ShapedRecipePattern(5,40,thatList(generateKeyMapping(),paddedPatternString),Optional.of(new ShapedRecipePattern.Data(generateKeyMapping(),rows)));
        // Create and return a new ShapedRecipe.
        ItemStack out = new ItemStack(ModItems.PROTEIN.get());
        out.applyComponents(DataComponentPatch.builder()
                .set(ModComponents.AMINO_ACIDS_COMPONENT.get(),patternString)
                .set(DataComponents.ITEM_NAME, Component.literal(this.output))
                .set(DataComponents.RARITY, Rarity.UNCOMMON).build());

        return new ShapedRecipe(this.group,CraftingBookCategory.MISC,shapedPattern,out);
    }

    private static List<Optional<Ingredient>> thatList(Map<Character, Ingredient> key, String pattern) {
        List<Optional<Ingredient>> list = new ArrayList<>(5 * 20);

        for (int q = 0; q < 5 * 40; ++q) {
            if (q < pattern.length()) {
                char c = pattern.charAt(q);
                Ingredient ingredient = (Ingredient) key.get(c);
                if (ingredient == null){
                    ingredient = Ingredient.of(ModItems.AMINO_ACIDS.get(0).get());
                }
                list.add(Optional.of(ingredient));
            } else {
                list.add(Optional.empty());
            }
        }
        return list;
    }

    @Override
    public boolean isSpecial() {
        return false;
    }
}
