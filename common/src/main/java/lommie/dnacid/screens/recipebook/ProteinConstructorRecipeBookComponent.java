package lommie.dnacid.screens.recipebook;

import com.google.common.collect.Lists;
import lommie.dnacid.Dnacid;
import lommie.dnacid.items.ModItems;
import lommie.dnacid.notmixin.ILocalPlayerMixin;
import lommie.dnacid.recipe.ModRecipes;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.*;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.navigation.CommonInputs;
import net.minecraft.client.gui.navigation.ScreenAxis;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.recipebook.*;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.resources.language.LanguageInfo;
import net.minecraft.client.resources.language.LanguageManager;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ServerboundRecipeBookChangeSettingsPacket;
import net.minecraft.recipebook.PlaceRecipeHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.util.context.ContextMap;
import net.minecraft.world.entity.player.StackedItemContents;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.crafting.ExtendedRecipeBookCategory;
import net.minecraft.world.item.crafting.display.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

@Environment(EnvType.CLIENT)
public class ProteinConstructorRecipeBookComponent<T extends RecipeBookMenu> implements Renderable, GuiEventListener, NarratableEntry {
    protected static final ResourceLocation RECIPE_BOOK_LOCATION = ResourceLocation.withDefaultNamespace("textures/gui/recipe_book.png");
    private static final Component SEARCH_HINT;
    private static final Component ALL_RECIPES_TOOLTIP;
    private int xOffset;
    private int width;
    private int height;
    private float time;
    @Nullable
    private RecipeDisplayId lastPlacedRecipe;
    private final GhostSlots ghostSlots;
    private final List<RecipeBookTabButton> tabButtons = Lists.newArrayList();
    @Nullable
    private RecipeBookTabButton selectedTab;
    protected StateSwitchingButton filterButton;
    protected final T menu;
    protected Minecraft minecraft;
    @Nullable
    private EditBox searchBox;
    private String lastSearch = "";
    private final List<RecipeBookComponent.TabInfo> tabInfos;
    private ProteinConstructorClientRecipeBook book;
    @Nullable
    private RecipeDisplayId lastRecipe;
    @Nullable
    private RecipeCollection lastRecipeCollection;
    private final StackedItemContents stackedContents = new StackedItemContents();
    private int timesInventoryChanged;
    private boolean ignoreTextInput;
    private boolean visible;
    private boolean widthTooNarrow;
    @Nullable
    private ScreenRectangle magnifierIconPlacement;
    private static final WidgetSprites FILTER_BUTTON_SPRITES = new WidgetSprites(ResourceLocation.withDefaultNamespace("recipe_book/filter_enabled"), ResourceLocation.withDefaultNamespace("recipe_book/filter_disabled"), ResourceLocation.withDefaultNamespace("recipe_book/filter_enabled_highlighted"), ResourceLocation.withDefaultNamespace("recipe_book/filter_disabled_highlighted"));
    private final ProteinConstructorRecipeBookPage recipeBookPage;

    public ProteinConstructorRecipeBookComponent(T recipeBookMenu) {
        this.menu = recipeBookMenu;
        this.tabInfos = List.of(
                new RecipeBookComponent.TabInfo(ModItems.SOAP.get(), ModRecipes.PROTEIN_CONSTRUCTOR_RECIPE_CATEGORY.get())
        );
        SlotSelectTime slotSelectTime = () -> Mth.floor(this.time / 30.0F);
        this.ghostSlots = new GhostSlots(slotSelectTime);
        recipeBookPage = new ProteinConstructorRecipeBookPage(this,slotSelectTime,false);

    }

    protected boolean isCraftingSlot(Slot slot) {
        return true;
    }

    protected void selectMatchingRecipes(RecipeCollection recipeCollection, StackedItemContents stackedItemContents) {
        recipeCollection.selectRecipes(stackedItemContents, this::canDisplay);
    }

    private boolean canDisplay(RecipeDisplay recipeDisplay) {
        return true;
    }

    protected @NotNull Component getRecipeFilterName() {
        return Component.literal("Amino Acids");
    }

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
        Dnacid.LOGGER.error("ghost:{}",list.toString());
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

    public void init(int i, int j, Minecraft minecraft, boolean bl) {
        this.minecraft = minecraft;
        this.width = i;
        this.height = j;
        this.widthTooNarrow = bl;
        assert minecraft.player != null;
        this.book = ((ILocalPlayerMixin) minecraft.player).getProteinConstructorClientRecipeBook();
        this.timesInventoryChanged = minecraft.player.getInventory().getTimesChanged();
        this.visible = this.isVisibleAccordingToBookData();
        if (this.visible) {
            this.initVisuals();
        }

    }

    private void initVisuals() {
        boolean bl = this.isFiltering();
        this.xOffset = this.widthTooNarrow ? 0 : 86;
        int i = this.getXOrigin();
        int j = this.getYOrigin();
        this.stackedContents.clear();
        assert this.minecraft.player != null;
        this.minecraft.player.getInventory().fillStackedContents(this.stackedContents);
        this.menu.fillCraftSlotsStackedContents(this.stackedContents);
        String string = this.searchBox != null ? this.searchBox.getValue() : "";
        Font var10003 = this.minecraft.font;
        int var10004 = i + 25;
        int var10005 = j + 13;
        Objects.requireNonNull(this.minecraft.font);
        this.searchBox = new EditBox(var10003, var10004, var10005, 81, 9 + 5, Component.translatable("itemGroup.search"));
        this.searchBox.setMaxLength(50);
        this.searchBox.setVisible(true);
        this.searchBox.setTextColor(16777215);
        this.searchBox.setValue(string);
        this.searchBox.setHint(SEARCH_HINT);
        this.magnifierIconPlacement = ScreenRectangle.of(ScreenAxis.HORIZONTAL, i + 8, this.searchBox.getY(), this.searchBox.getX() - this.getXOrigin(), this.searchBox.getHeight());
        this.recipeBookPage.init(this.minecraft, i, j);
        this.filterButton = new StateSwitchingButton(i + 110, j + 12, 26, 16, bl);
        this.updateFilterButtonTooltip();
        this.initFilterButtonTextures();
        this.tabButtons.clear();

        for (RecipeBookComponent.TabInfo tabInfo : this.tabInfos) {
            this.tabButtons.add(new RecipeBookTabButton(tabInfo));
        }

        if (this.selectedTab != null) {
            this.selectedTab = this.tabButtons.stream().filter((recipeBookTabButton) -> recipeBookTabButton.getCategory().equals(this.selectedTab.getCategory())).findFirst().orElse(null);
        }

        if (this.selectedTab == null) {
            this.selectedTab = this.tabButtons.getFirst();
        }

        this.selectedTab.setStateTriggered(true);
        this.selectMatchingRecipes();
        this.updateTabs(bl);
        this.updateCollections(false, bl);
    }

    protected void initFilterButtonTextures() {
        this.filterButton.initTextureValues(FILTER_BUTTON_SPRITES);
    }

    private int getYOrigin() {
        return (this.height - 166) / 2;
    }

    private int getXOrigin() {
        return (this.width - 147) / 2 - this.xOffset;
    }

    private void updateFilterButtonTooltip() {
        this.filterButton.setTooltip(this.filterButton.isStateTriggered() ? Tooltip.create(this.getRecipeFilterName()) : Tooltip.create(ALL_RECIPES_TOOLTIP));
    }

    public int updateScreenPosition(int i, int j) {
        int k;
        if (this.isVisible() && !this.widthTooNarrow) {
            k = 177 + (i - j - 200) / 2;
        } else {
            k = (i - j) / 2;
        }

        return k;
    }

    public void toggleVisibility() {
        this.setVisible(!this.isVisible());
    }

    public boolean isVisible() {
        return this.visible;
    }

    private boolean isVisibleAccordingToBookData() {
        return this.book.isOpen(this.menu.getRecipeBookType());
    }

    protected void setVisible(boolean bl) {
        if (bl) {
            this.initVisuals();
        }

        this.visible = bl;
        this.book.setOpen(this.menu.getRecipeBookType(), bl);
        if (!bl) {
            this.recipeBookPage.setInvisible();
        }

        this.sendUpdateSettings();
    }

    public void slotClicked(@Nullable Slot slot) {
        if (slot != null && this.isCraftingSlot(slot)) {
            this.lastPlacedRecipe = null;
            this.ghostSlots.clear();
            if (this.isVisible()) {
                this.updateStackedContents();
            }
        }

    }

    private void selectMatchingRecipes() {

        for (RecipeBookComponent.TabInfo tabInfo : this.tabInfos) {

            for (RecipeCollection recipeCollection : this.book.getCollection(tabInfo.category())) {
                this.selectMatchingRecipes(recipeCollection, this.stackedContents);
            }
        }

    }

    private void updateCollections(boolean bl, boolean bl2) {
        assert this.selectedTab != null;
        List<RecipeCollection> inList = this.book.getCollection(this.selectedTab.getCategory());
        List<RecipeCollection> outList = Lists.newArrayList(inList);
        //
        this.recipeBookPage.updateCollections(outList, bl, bl2);
        return;
        /*
        outList.removeIf((recipeCollection) -> !recipeCollection.hasAnySelected());
        assert this.searchBox != null;
        String string = this.searchBox.getValue();
        if (!string.isEmpty()) {
            ClientPacketListener clientPacketListener = this.minecraft.getConnection();
            if (clientPacketListener != null) {
                ObjectSet<RecipeCollection> objectSet = new ObjectLinkedOpenHashSet<>(clientPacketListener.searchTrees().recipes().search(string.toLowerCase(Locale.ROOT)));
                outList.removeIf((recipeCollection) -> !objectSet.contains(recipeCollection));
            }
        }

        if (bl2) {
            outList.removeIf((recipeCollection) -> !recipeCollection.hasCraftable());
        }

        this.recipeBookPage.updateCollections(outList, bl, bl2);*/
    }

    private void updateTabs(boolean bl) {
        int i = (this.width - 147) / 2 - this.xOffset - 30;
        int j = (this.height - 166) / 2 + 3;
        int l = 0;

        for (RecipeBookTabButton recipeBookTabButton : this.tabButtons) {
            ExtendedRecipeBookCategory extendedRecipeBookCategory = recipeBookTabButton.getCategory();
            if (extendedRecipeBookCategory instanceof SearchRecipeBookCategory) {
                recipeBookTabButton.visible = true;
                recipeBookTabButton.setPosition(i, j + 27 * l++);
            } else if (recipeBookTabButton.updateVisibility(this.book)) {
                recipeBookTabButton.setPosition(i, j + 27 * l++);
                recipeBookTabButton.startAnimation(this.book, bl);
            }
        }

    }

    public void tick() {
        boolean bl = this.isVisibleAccordingToBookData();
        if (this.isVisible() != bl) {
            this.setVisible(bl);
        }

        if (this.isVisible()) {
            assert this.minecraft.player != null;
            if (this.timesInventoryChanged != this.minecraft.player.getInventory().getTimesChanged()) {
                this.updateStackedContents();
                this.timesInventoryChanged = this.minecraft.player.getInventory().getTimesChanged();
            }

        }
    }

    private void updateStackedContents() {
        this.stackedContents.clear();
        assert this.minecraft.player != null;
        this.minecraft.player.getInventory().fillStackedContents(this.stackedContents);
        this.menu.fillCraftSlotsStackedContents(this.stackedContents);
        this.selectMatchingRecipes();
        this.updateCollections(false, this.isFiltering());
    }

    private boolean isFiltering() {
        return this.book.isFiltering(this.menu.getRecipeBookType());
    }

    public void render(GuiGraphics guiGraphics, int i, int j, float f) {
        if (this.isVisible()) {
            if (!Screen.hasControlDown()) {
                this.time += f;
            }

            guiGraphics.pose().pushPose();
            guiGraphics.pose().translate(0.0F, 0.0F, 100.0F);
            int k = this.getXOrigin();
            int l = this.getYOrigin();
            guiGraphics.blit(RenderType::guiTextured, RECIPE_BOOK_LOCATION, k, l, 1.0F, 1.0F, 147, 166, 256, 256);
            assert this.searchBox != null;
            this.searchBox.render(guiGraphics, i, j, f);

            for (RecipeBookTabButton recipeBookTabButton : this.tabButtons) {
                recipeBookTabButton.render(guiGraphics, i, j, f);
            }

            this.filterButton.render(guiGraphics, i, j, f);
            this.recipeBookPage.render(guiGraphics, k, l, i, j, f);
            guiGraphics.pose().popPose();
        }
    }

    public void renderTooltip(GuiGraphics guiGraphics, int i, int j, @Nullable Slot slot) {
        if (this.isVisible()) {
            this.recipeBookPage.renderTooltip(guiGraphics, i, j);
            this.ghostSlots.renderTooltip(guiGraphics, this.minecraft, i, j, slot);
        }
    }

    public void renderGhostRecipe(GuiGraphics guiGraphics, boolean bl) {
        this.ghostSlots.render(guiGraphics, this.minecraft, bl);
    }

    public boolean mouseClicked(double d, double e, int i) {
        if (this.isVisible() && !Objects.requireNonNull(this.minecraft.player).isSpectator()) {
            if (this.recipeBookPage.mouseClicked(d, e, i, this.getXOrigin(), this.getYOrigin(), 147, 166)) {
                RecipeDisplayId recipeDisplayId = this.recipeBookPage.getLastClickedRecipe();
                RecipeCollection recipeCollection = this.recipeBookPage.getLastClickedRecipeCollection();
                if (recipeDisplayId != null && recipeCollection != null) {
                    if (!this.tryPlaceRecipe(recipeCollection, recipeDisplayId)) {
                        return false;
                    }

                    this.lastRecipeCollection = recipeCollection;
                    this.lastRecipe = recipeDisplayId;
                    if (!this.isOffsetNextToMainGUI()) {
                        this.setVisible(false);
                    }
                }

            } else {
                boolean bl;
                if (this.searchBox != null) {
                    bl = this.magnifierIconPlacement != null && this.magnifierIconPlacement.containsPoint(Mth.floor(d), Mth.floor(e));
                    if (bl || this.searchBox.mouseClicked(d, e, i)) {
                        this.searchBox.setFocused(true);
                        return true;
                    }

                    this.searchBox.setFocused(false);
                }

                if (this.filterButton.mouseClicked(d, e, i)) {
                    bl = this.toggleFiltering();
                    this.filterButton.setStateTriggered(bl);
                    this.updateFilterButtonTooltip();
                    this.sendUpdateSettings();
                    this.updateCollections(false, bl);
                } else {
                    Iterator<RecipeBookTabButton> var8 = this.tabButtons.iterator();

                    RecipeBookTabButton recipeBookTabButton;
                    do {
                        if (!var8.hasNext()) {
                            return false;
                        }

                        recipeBookTabButton = var8.next();
                    } while(!recipeBookTabButton.mouseClicked(d, e, i));

                    if (this.selectedTab != recipeBookTabButton) {
                        if (this.selectedTab != null) {
                            this.selectedTab.setStateTriggered(false);
                        }

                        this.selectedTab = recipeBookTabButton;
                        this.selectedTab.setStateTriggered(true);
                        this.updateCollections(true, this.isFiltering());
                    }

                }
            }
            return true;
        } else {
            return false;
        }
    }

    private boolean tryPlaceRecipe(RecipeCollection recipeCollection, RecipeDisplayId recipeDisplayId) {
        if (!recipeCollection.isCraftable(recipeDisplayId) && recipeDisplayId.equals(this.lastPlacedRecipe)) {
            return false;
        } else {
            Dnacid.LOGGER.error("tryPlaceRecipe");
            this.lastPlacedRecipe = recipeDisplayId;
            this.ghostSlots.clear();
            this.minecraft.gameMode.handlePlaceRecipe(this.minecraft.player.containerMenu.containerId, recipeDisplayId, Screen.hasShiftDown());
            return true;
        }
    }

    private boolean toggleFiltering() {
        RecipeBookType recipeBookType = this.menu.getRecipeBookType();
        boolean bl = !this.book.isFiltering(recipeBookType);
        this.book.setFiltering(recipeBookType, bl);
        return bl;
    }

    public boolean hasClickedOutside(double d, double e, int i, int j, int k, int l, int m) {
        if (!this.isVisible()) {
            return true;
        } else {
            boolean bl = d < (double)i || e < (double)j || d >= (double)(i + k) || e >= (double)(j + l);
            boolean bl2 = (double)(i - 147) < d && d < (double)i && (double)j < e && e < (double)(j + l);
            if (!bl || bl2) return false;
            assert this.selectedTab != null;
            return !this.selectedTab.isHoveredOrFocused();
        }
    }

    public boolean keyPressed(int i, int j, int k) {
        this.ignoreTextInput = false;
        if (this.isVisible() && !Objects.requireNonNull(this.minecraft.player).isSpectator()) {
            if (i == 256 && !this.isOffsetNextToMainGUI()) {
                this.setVisible(false);
                return true;
            } else {
                assert this.searchBox != null;
                if (this.searchBox.keyPressed(i, j, k)) {
                    this.checkSearchStringUpdate();
                    return true;
                } else if (this.searchBox.isFocused() && this.searchBox.isVisible() && i != 256) {
                    return true;
                } else if (this.minecraft.options.keyChat.matches(i, j) && !this.searchBox.isFocused()) {
                    this.ignoreTextInput = true;
                    this.searchBox.setFocused(true);
                    return true;
                } else if (CommonInputs.selected(i) && this.lastRecipeCollection != null && this.lastRecipe != null) {
                    AbstractWidget.playButtonClickSound(Minecraft.getInstance().getSoundManager());
                    return this.tryPlaceRecipe(this.lastRecipeCollection, this.lastRecipe);
                } else {
                    return false;
                }
            }
        } else {
            return false;
        }
    }

    public boolean keyReleased(int i, int j, int k) {
        this.ignoreTextInput = false;
        return false;//super.keyReleased(i, j, k);
    }

    public boolean charTyped(char c, int i) {
        if (this.ignoreTextInput) {
            return false;
        } else if (this.isVisible() && !Objects.requireNonNull(this.minecraft.player).isSpectator()) {
            assert this.searchBox != null;
            if (this.searchBox.charTyped(c, i)) {
                this.checkSearchStringUpdate();
                return true;
            } else {
                return false;//super.charTyped(c, i);
            }
        } else {
            return false;
        }
    }

    public boolean isMouseOver(double d, double e) {
        return false;
    }

    public void setFocused(boolean bl) {
    }

    public boolean isFocused() {
        return false;
    }

    private void checkSearchStringUpdate() {
        assert this.searchBox != null;
        String string = this.searchBox.getValue().toLowerCase(Locale.ROOT);
        this.pirateSpeechForThePeople(string);
        if (!string.equals(this.lastSearch)) {
            this.updateCollections(false, this.isFiltering());
            this.lastSearch = string;
        }

    }

    private void pirateSpeechForThePeople(String string) {
        if ("excitedze".equals(string)) {
            LanguageManager languageManager = this.minecraft.getLanguageManager();
            LanguageInfo languageInfo = languageManager.getLanguage("en_pt");
            if (languageInfo == null || languageManager.getSelected().equals("en_pt")) {
                return;
            }

            languageManager.setSelected("en_pt");
            this.minecraft.options.languageCode = "en_pt";
            this.minecraft.reloadResourcePacks();
            this.minecraft.options.save();
        }

    }

    private boolean isOffsetNextToMainGUI() {
        return this.xOffset == 86;
    }

    public void recipesUpdated() {
        this.selectMatchingRecipes();
        this.updateTabs(this.isFiltering());
        if (this.isVisible()) {
            this.updateCollections(false, this.isFiltering());
        }

    }

    public void recipeShown(RecipeDisplayId recipeDisplayId) {
        assert this.minecraft.player != null;
        this.minecraft.player.removeRecipeHighlight(recipeDisplayId);
    }

    public void fillGhostRecipe(RecipeDisplay recipeDisplay) {
        this.ghostSlots.clear();
        ContextMap contextMap = SlotDisplayContext.fromLevel(Objects.requireNonNull(this.minecraft.level));
        this.fillGhostRecipe(this.ghostSlots, recipeDisplay, contextMap);
    }

    protected void sendUpdateSettings() {
        if (this.minecraft.getConnection() != null) {
            RecipeBookType recipeBookType = this.menu.getRecipeBookType();
            boolean bl = this.book.getBookSettings().isOpen(recipeBookType);
            boolean bl2 = this.book.getBookSettings().isFiltering(recipeBookType);
            this.minecraft.getConnection().send(new ServerboundRecipeBookChangeSettingsPacket(recipeBookType, bl, bl2));
        }

    }

    public NarratableEntry.@NotNull NarrationPriority narrationPriority() {
        return this.visible ? NarrationPriority.HOVERED : NarrationPriority.NONE;
    }

    public void updateNarration(NarrationElementOutput narrationElementOutput) {
        List<NarratableEntry> list = Lists.newArrayList();
        this.recipeBookPage.listButtons((abstractWidget) -> {
            if (abstractWidget.isActive()) {
                list.add(abstractWidget);
            }

        });
        list.add(this.searchBox);
        list.add(this.filterButton);
        list.addAll(this.tabButtons);
        Screen.NarratableSearchResult narratableSearchResult = Screen.findNarratableWidget(list, null);
        if (narratableSearchResult != null) {
            narratableSearchResult.entry.updateNarration(narrationElementOutput.nest());
        }

    }

    static {
        SEARCH_HINT = Component.translatable("gui.recipebook.search_hint").withStyle(ChatFormatting.ITALIC).withStyle(ChatFormatting.GRAY);
        ALL_RECIPES_TOOLTIP = Component.translatable("gui.recipebook.toggleRecipes.all");
    }
}
