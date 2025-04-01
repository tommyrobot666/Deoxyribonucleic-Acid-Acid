package lommie.dnacid.screens.recipebook;

import com.mojang.authlib.minecraft.client.MinecraftClient;
import lommie.dnacid.Dnacid;
import lommie.dnacid.recipe.ProteinConstructorRecipe;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.recipebook.*;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.display.*;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;

public class ProteinConstructorRecipeBookPage extends RecipeBookPage {
    List<RecipeCollection> alwaysTheRcipeCollections = List.of(
            new RecipeCollection(List.of(
                    new RecipeDisplayEntry(new RecipeDisplayId(0),
                            new ProteinConstructorRecipe("","AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA","idk").toShapedRecipe().display().get(0),
                            OptionalInt.empty(),
                            Dnacid.PROTEIN_CONSTRUCTOR_RECIPE_CATEGORY.get(),
                            Optional.of(List.of(Ingredient.of(Dnacid.AMINO_ACIDS.get(0).get()))))
                    )));

    public ProteinConstructorRecipeBookPage(RecipeBookComponent<?> recipeBookComponent, SlotSelectTime slotSelectTime, boolean bl) {
        super(recipeBookComponent, slotSelectTime, bl);
        Dnacid.LOGGER.error("AAAAAAAAAAAA");
        try {
            Field recipeCollections = RecipeBookPage.class.getDeclaredField("recipeCollections");
            recipeCollections.setAccessible(true);
            recipeCollections.set(this,alwaysTheRcipeCollections);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void updateCollections(List<RecipeCollection> list, boolean bl, boolean bl2) {
        try {
            Field recipeCollections = RecipeBookPage.class.getDeclaredField("recipeCollections");
            Field isFiltering = RecipeBookPage.class.getDeclaredField("isFiltering");
            Field totalPages = RecipeBookPage.class.getDeclaredField("totalPages");
            Field currentPage = RecipeBookPage.class.getDeclaredField("currentPage");
            Method updateButtonsForPage = RecipeBookPage.class.getDeclaredMethod("updateButtonsForPage");
            isFiltering.setAccessible(true);
            currentPage.setAccessible(true);
            totalPages.setAccessible(true);
            recipeCollections.setAccessible(true);
            updateButtonsForPage.setAccessible(true);

            recipeCollections.set(this,alwaysTheRcipeCollections);
            isFiltering.set(this,bl2);

            totalPages.set(this,(int)Math.ceil((double)list.size() / 20.0));
            if ((int)totalPages.get(this) <= (int) currentPage.get(this) || bl) {
                currentPage.set(this,0);
            }

            updateButtonsForPage.invoke(this);
        } catch (NoSuchFieldException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void init(Minecraft minecraft, int i, int j) {
        super.init(minecraft, i, j);
/*
        try {
            Field buttons = RecipeBookPage.class.getDeclaredField("buttons");
            buttons.setAccessible(true);
            for (RecipeButton b : (List<RecipeButton>) buttons.get(this)){
                b.init(alwaysTheRcipeCollections.get(0),false,this, new ContextMap.Builder().create(new ContextKeySet.Builder().build()));
            }
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }*/
    }
}
