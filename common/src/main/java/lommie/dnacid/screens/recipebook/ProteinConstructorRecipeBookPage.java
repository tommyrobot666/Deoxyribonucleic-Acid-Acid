package lommie.dnacid.screens.recipebook;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import lommie.dnacid.Dnacid;
import lommie.dnacid.notmixin.ILocalPlayerMixin;
import lommie.dnacid.recipe.ProteinConstructorRecipe;
import net.minecraft.client.ClientRecipeBook;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.StateSwitchingButton;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.gui.screens.recipebook.OverlayRecipeComponent;
import net.minecraft.client.gui.screens.recipebook.RecipeCollection;
import net.minecraft.client.gui.screens.recipebook.SlotSelectTime;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.context.ContextMap;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.display.RecipeDisplayEntry;
import net.minecraft.world.item.crafting.display.RecipeDisplayId;
import net.minecraft.world.item.crafting.display.SlotDisplayContext;
import org.jetbrains.annotations.Nullable;

import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.function.Consumer;

public class ProteinConstructorRecipeBookPage {
    List<RecipeCollection> alwaysTheRcipeCollections = List.of(
            new RecipeCollection(List.of(
                    new RecipeDisplayEntry(new RecipeDisplayId(0),
                            new ProteinConstructorRecipe("", "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA", "idk").toShapedRecipe().display().getFirst(),
                            OptionalInt.empty(),
                            Dnacid.PROTEIN_CONSTRUCTOR_RECIPE_CATEGORY.get(),
                            Optional.of(List.of(Ingredient.of(Dnacid.AMINO_ACIDS.getFirst().get()))))
            )));
    private static final WidgetSprites PAGE_FORWARD_SPRITES = new WidgetSprites(ResourceLocation.withDefaultNamespace("recipe_book/page_forward"), ResourceLocation.withDefaultNamespace("recipe_book/page_forward_highlighted"));
    private static final WidgetSprites PAGE_BACKWARD_SPRITES = new WidgetSprites(ResourceLocation.withDefaultNamespace("recipe_book/page_backward"), ResourceLocation.withDefaultNamespace("recipe_book/page_backward_highlighted"));
    private final List<ProteinConstructorRecipeButton> buttons = Lists.newArrayListWithCapacity(20);
    @Nullable
    private ProteinConstructorRecipeButton hoveredButton;
    private final OverlayRecipeComponent overlay;
    private Minecraft minecraft;
    private final ProteinConstructorRecipeBookComponent<?> parent;
    private List<RecipeCollection> recipeCollections = ImmutableList.of();
    private StateSwitchingButton forwardButton;
    private StateSwitchingButton backButton;
    private int totalPages;
    private int currentPage;
    private ProteinConstructorClientRecipeBook recipeBook;
    @Nullable
    private RecipeDisplayId lastClickedRecipe;
    @Nullable
    private RecipeCollection lastClickedRecipeCollection;
    private boolean isFiltering;

    public ProteinConstructorRecipeBookPage(ProteinConstructorRecipeBookComponent<?> recipeBookComponent, SlotSelectTime slotSelectTime, boolean bl) {
        this.parent = recipeBookComponent;
        this.overlay = new OverlayRecipeComponent(slotSelectTime, bl);

        for (int i = 0; i < 20; ++i) {
            this.buttons.add(new ProteinConstructorRecipeButton(slotSelectTime));
        }

        this.recipeCollections = calculateRecipeCollections();
    }

    private List<RecipeCollection> calculateRecipeCollections() {
        return alwaysTheRcipeCollections;
    }

    public void init(Minecraft minecraft, int i, int j) {
        this.minecraft = minecraft;
        assert minecraft.player != null;
        this.recipeBook = ((ILocalPlayerMixin) minecraft.player).getProteinConstructorClientRecipeBook();

        for (int k = 0; k < this.buttons.size(); ++k) {
            this.buttons.get(k).setPosition(i + 11 + 25 * (k % 5), j + 31 + 25 * (k / 5));
        }

        this.forwardButton = new StateSwitchingButton(i + 93, j + 137, 12, 17, false);
        this.forwardButton.initTextureValues(PAGE_FORWARD_SPRITES);
        this.backButton = new StateSwitchingButton(i + 38, j + 137, 12, 17, true);
        this.backButton.initTextureValues(PAGE_BACKWARD_SPRITES);
    }

    public void updateCollections(List<RecipeCollection> list, boolean bl, boolean bl2) {
        //this.recipeCollections = list;
        this.isFiltering = bl2;
        this.totalPages = (int) Math.ceil((double) list.size() / 20.0);
        if (this.totalPages <= this.currentPage || bl) {
            this.currentPage = 0;
        }

        this.updateButtonsForPage();
    }

    private void updateButtonsForPage() {
        int i = 20 * this.currentPage;
        assert this.minecraft.level != null;
        ContextMap contextMap = SlotDisplayContext.fromLevel(this.minecraft.level);

        for (int j = 0; j < this.buttons.size(); ++j) {
            ProteinConstructorRecipeButton proteinConstructorRecipeButton = this.buttons.get(j);
            if (i + j < this.recipeCollections.size()) {
                RecipeCollection recipeCollection = this.recipeCollections.get(i + j);
                proteinConstructorRecipeButton.init(recipeCollection, this.isFiltering, this, contextMap);
                proteinConstructorRecipeButton.visible = true;
            } else {
                proteinConstructorRecipeButton.visible = false;
            }
        }

        this.updateArrowButtons();
    }

    private void updateArrowButtons() {
        this.forwardButton.visible = this.totalPages > 1 && this.currentPage < this.totalPages - 1;
        this.backButton.visible = this.totalPages > 1 && this.currentPage > 0;
    }

    public void render(GuiGraphics guiGraphics, int i, int j, int k, int l, float f) {
        if (this.totalPages > 1) {
            Component component = Component.translatable("gui.recipebook.page", this.currentPage + 1, this.totalPages);
            int m = this.minecraft.font.width(component);
            guiGraphics.drawString(this.minecraft.font, component, i - m / 2 + 73, j + 141, -1);
        }

        this.hoveredButton = null;

        for (ProteinConstructorRecipeButton proteinConstructorRecipeButton : this.buttons) {
            proteinConstructorRecipeButton.render(guiGraphics, k, l, f);
            if (proteinConstructorRecipeButton.visible && proteinConstructorRecipeButton.isHoveredOrFocused()) {
                this.hoveredButton = proteinConstructorRecipeButton;
            }
        }

        this.backButton.render(guiGraphics, k, l, f);
        this.forwardButton.render(guiGraphics, k, l, f);
        this.overlay.render(guiGraphics, k, l, f);
    }

    public void renderTooltip(GuiGraphics guiGraphics, int i, int j) {
        if (this.minecraft.screen != null && this.hoveredButton != null && !this.overlay.isVisible()) {
            ItemStack itemStack = this.hoveredButton.getDisplayStack();
            ResourceLocation resourceLocation = itemStack.get(DataComponents.TOOLTIP_STYLE);
            guiGraphics.renderComponentTooltip(this.minecraft.font, this.hoveredButton.getTooltipText(itemStack), i, j, resourceLocation);
        }

    }

    @Nullable
    public RecipeDisplayId getLastClickedRecipe() {
        return this.lastClickedRecipe;
    }

    @Nullable
    public RecipeCollection getLastClickedRecipeCollection() {
        return this.lastClickedRecipeCollection;
    }

    public void setInvisible() {
        this.overlay.setVisible(false);
    }

    public boolean mouseClicked(double d, double e, int i, int j, int k, int l, int m) {
        this.lastClickedRecipe = null;
        this.lastClickedRecipeCollection = null;
        if (this.overlay.isVisible()) {
            if (this.overlay.mouseClicked(d, e, i)) {
                this.lastClickedRecipe = this.overlay.getLastRecipeClicked();
                this.lastClickedRecipeCollection = this.overlay.getRecipeCollection();
            } else {
                this.overlay.setVisible(false);
            }

            return true;
        } else if (this.forwardButton.mouseClicked(d, e, i)) {
            ++this.currentPage;
            this.updateButtonsForPage();
            return true;
        } else if (this.backButton.mouseClicked(d, e, i)) {
            --this.currentPage;
            this.updateButtonsForPage();
            return true;
        } else {
            assert this.minecraft.level != null;
            ContextMap contextMap = SlotDisplayContext.fromLevel(this.minecraft.level);
            Iterator<ProteinConstructorRecipeButton> buttonIterator = this.buttons.iterator();

            ProteinConstructorRecipeButton proteinConstructorRecipeButton;
            do {
                if (!buttonIterator.hasNext()) {
                    return false;
                }

                proteinConstructorRecipeButton = buttonIterator.next();
            } while (!proteinConstructorRecipeButton.mouseClicked(d, e, i));

            if (i == 0) {
                this.lastClickedRecipe = proteinConstructorRecipeButton.getCurrentRecipe();
                this.lastClickedRecipeCollection = proteinConstructorRecipeButton.getCollection();
            } else if (i == 1 && !this.overlay.isVisible() && !proteinConstructorRecipeButton.isOnlyOption()) {
                this.overlay.init(proteinConstructorRecipeButton.getCollection(), contextMap, this.isFiltering, proteinConstructorRecipeButton.getX(), proteinConstructorRecipeButton.getY(), j + l / 2, k + 13 + m / 2, (float) proteinConstructorRecipeButton.getWidth());
            }

            return true;
        }
    }

    public void recipeShown(RecipeDisplayId recipeDisplayId) {
        this.parent.recipeShown(recipeDisplayId);
    }

    public ClientRecipeBook getRecipeBook() {
        return this.recipeBook;
    }

    protected void listButtons(Consumer<AbstractWidget> consumer) {
        consumer.accept(this.forwardButton);
        consumer.accept(this.backButton);
        this.buttons.forEach(consumer);
    }
}


