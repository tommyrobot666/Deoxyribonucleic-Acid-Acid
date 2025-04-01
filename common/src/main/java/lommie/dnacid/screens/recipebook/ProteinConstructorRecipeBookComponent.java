package lommie.dnacid.screens.recipebook;

import lommie.dnacid.Dnacid;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.gui.screens.recipebook.*;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.Component;
import net.minecraft.recipebook.PlaceRecipeHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.util.context.ContextMap;
import net.minecraft.world.entity.player.StackedItemContents;
import net.minecraft.world.inventory.AbstractCraftingMenu;
import net.minecraft.world.inventory.RecipeBookMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.crafting.display.*;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Objects;

public class ProteinConstructorRecipeBookComponent<T extends RecipeBookMenu> extends RecipeBookComponent<T> {
    private static final WidgetSprites FILTER_BUTTON_SPRITES = new WidgetSprites(ResourceLocation.withDefaultNamespace("recipe_book/filter_enabled"), ResourceLocation.withDefaultNamespace("recipe_book/filter_disabled"), ResourceLocation.withDefaultNamespace("recipe_book/filter_enabled_highlighted"), ResourceLocation.withDefaultNamespace("recipe_book/filter_disabled_highlighted"));

    public ProteinConstructorRecipeBookComponent(T recipeBookMenu) {
        super(recipeBookMenu, List.of(
                new RecipeBookComponent.TabInfo(Dnacid.SOAP.get(), Dnacid.PROTEIN_CONSTRUCTOR_RECIPE_CATEGORY.get())
        ));
        Dnacid.LOGGER.warn("Yes, The book is strange");

        SlotSelectTime slotSelectTime = () -> {
            try {
                Field time = RecipeBookComponent.class.getDeclaredField("time");
                time.setAccessible(true);
                return Mth.floor( (float)time.get(this) / 30.0F);
            } catch (NoSuchFieldException | IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        };

        try {
            Field recipeBookPage = RecipeBookComponent.class.getDeclaredField("recipeBookPage");
            recipeBookPage.setAccessible(true);
            recipeBookPage.set(this,new ProteinConstructorRecipeBookPage(this,slotSelectTime,false));
        } catch (IllegalAccessException | NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
        /*
        if (SearchRecipeBookCategory.CRAFTING.includedCategories().equals(List.of(RecipeBookCategories.CRAFTING_EQUIPMENT, RecipeBookCategories.CRAFTING_BUILDING_BLOCKS, RecipeBookCategories.CRAFTING_MISC, RecipeBookCategories.CRAFTING_REDSTONE))) {
            try {
                Field includedCategories = SearchRecipeBookCategory.class.getDeclaredField("includedCategories");
                includedCategories.setAccessible(true);
                includedCategories.set(SearchRecipeBookCategory.CRAFTING, List.of(RecipeBookCategories.CRAFTING_EQUIPMENT, RecipeBookCategories.CRAFTING_BUILDING_BLOCKS, RecipeBookCategories.CRAFTING_MISC, RecipeBookCategories.CRAFTING_REDSTONE, Dnacid.PROTEIN_CONSTRUCTOR_RECIPE_CATEGORY.get()));
                SearchRecipeBookCategory.CRAFTING.includedCategories().forEach((cat)->Dnacid.LOGGER.error(cat.toString()));
                Dnacid.LOGGER.error(Dnacid.PROTEIN_CONSTRUCTOR_RECIPE_CATEGORY.get().toString());
            } catch (NoSuchFieldException | IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }*/
    }

    @Override
    protected void initFilterButtonTextures() {
        this.filterButton.initTextureValues(FILTER_BUTTON_SPRITES);
    }

    @Override
    protected boolean isCraftingSlot(Slot slot) {
        return true;
    }

    @Override
    protected void selectMatchingRecipes(RecipeCollection recipeCollection, StackedItemContents stackedItemContents) {
        recipeCollection.selectRecipes(stackedItemContents, this::canDisplay);
    }

    private boolean canDisplay(RecipeDisplay recipeDisplay) {
        return true;
    }

    @Override
    protected @NotNull Component getRecipeFilterName() {
        return Component.literal("Amino Acids");
    }

    @Override
    protected void fillGhostRecipe(GhostSlots ghostSlots, RecipeDisplay recipeDisplay, ContextMap contextMap) {
        try {
            Method setResult = GhostSlots.class.getDeclaredMethod("setResult", Slot.class, ContextMap.class, SlotDisplay.class);
            setResult.setAccessible(true);
            setResult.invoke(ghostSlots,((AbstractCraftingMenu)this.menu).getResultSlot(), contextMap, recipeDisplay.result());
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
        Objects.requireNonNull(recipeDisplay);
        ShapedCraftingRecipeDisplay shapedCraftingRecipeDisplay = (ShapedCraftingRecipeDisplay)recipeDisplay;
        List<Slot> list = ((AbstractCraftingMenu)this.menu).getInputGridSlots();
        PlaceRecipeHelper.placeRecipe(((AbstractCraftingMenu)this.menu).getGridWidth(), ((AbstractCraftingMenu)this.menu).getGridHeight(), shapedCraftingRecipeDisplay.width(), shapedCraftingRecipeDisplay.height(), shapedCraftingRecipeDisplay.ingredients(), (slotDisplay, ix, jx, k) -> {
            Slot slot = list.get(ix);
            try {
                Method setInput =  GhostSlots.class.getDeclaredMethod("setInput", Slot.class, ContextMap.class, SlotDisplay.class);
                setInput.setAccessible(true);
                setInput.invoke(ghostSlots,slot, contextMap, slotDisplay);
            } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Override
    public void render(GuiGraphics guiGraphics, int i, int j, float f) {
        super.render(guiGraphics, i, j, f);
        guiGraphics.blit(RenderType::guiTextured, ResourceLocation.withDefaultNamespace("textures/item/book.png"), 0, 0, 1.0F, 1.0F, 147, 166, 256, 256);
    }
}
