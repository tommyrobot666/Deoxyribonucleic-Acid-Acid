package lommie.dnacid.recipe;

import net.minecraft.world.item.crafting.RecipeType;

public class ProteinConstructorRecipeType implements RecipeType<ProteinConstructorRecipe> {
    public static final ProteinConstructorRecipeType INSTANCE = new ProteinConstructorRecipeType();
    private ProteinConstructorRecipeType() {}
    @Override
    public String toString() {
        return "dnacid:protein_constructor";
    }
}