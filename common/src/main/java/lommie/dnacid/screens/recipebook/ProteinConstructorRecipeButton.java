package lommie.dnacid.screens.recipebook;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.ClientRecipeBook;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.recipebook.RecipeButton;
import net.minecraft.client.gui.screens.recipebook.RecipeCollection;
import net.minecraft.client.gui.screens.recipebook.SlotSelectTime;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.context.ContextMap;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.display.RecipeDisplayEntry;
import net.minecraft.world.item.crafting.display.RecipeDisplayId;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

public class ProteinConstructorRecipeButton extends RecipeButton {
    private static final ResourceLocation SLOT_MANY_CRAFTABLE_SPRITE = ResourceLocation.withDefaultNamespace("recipe_book/slot_many_craftable");
    private static final ResourceLocation SLOT_CRAFTABLE_SPRITE = ResourceLocation.withDefaultNamespace("recipe_book/slot_craftable");
    private static final ResourceLocation SLOT_MANY_UNCRAFTABLE_SPRITE = ResourceLocation.withDefaultNamespace("recipe_book/slot_many_uncraftable");
    private static final ResourceLocation SLOT_UNCRAFTABLE_SPRITE = ResourceLocation.withDefaultNamespace("recipe_book/slot_uncraftable");
    private static final float ANIMATION_TIME = 15.0F;
    private static final int BACKGROUND_SIZE = 25;
    private static final Component MORE_RECIPES_TOOLTIP = Component.translatable("gui.recipebook.moreRecipes");
    private RecipeCollection collection;
    private List<ResolvedEntry> selectedEntries;
    private boolean allRecipesHaveSameResultDisplay;
    private final SlotSelectTime slotSelectTime;
    private float animationTime;

    public ProteinConstructorRecipeButton(SlotSelectTime slotSelectTime) {
        super(slotSelectTime);
        this.slotSelectTime = slotSelectTime;
    }
    
    public void init(RecipeCollection recipeCollection, boolean bl, ProteinConstructorRecipeBookPage recipeBookPage, ContextMap contextMap) {
        this.collection = recipeCollection;
        List<RecipeDisplayEntry> list = recipeCollection.getSelectedRecipes(bl ? RecipeCollection.CraftableStatus.CRAFTABLE : RecipeCollection.CraftableStatus.ANY);
        this.selectedEntries = list.stream().map((recipeDisplayEntry) -> new ResolvedEntry(recipeDisplayEntry.id(), recipeDisplayEntry.resultItems(contextMap))).toList();
        this.allRecipesHaveSameResultDisplay = allRecipesHaveSameResultDisplay(this.selectedEntries);
        Stream<RecipeDisplayId> var10000 = list.stream().map(RecipeDisplayEntry::id);
        ClientRecipeBook var10001 = recipeBookPage.getRecipeBook();
        Objects.requireNonNull(var10001);
        List<RecipeDisplayId> list2 = var10000.filter(var10001::willHighlight).toList();
        if (!list2.isEmpty()) {
            Objects.requireNonNull(recipeBookPage);
            list2.forEach(recipeBookPage::recipeShown);
            this.animationTime = 15.0F;
        }

    }

    private static boolean allRecipesHaveSameResultDisplay(List<ResolvedEntry> list) {
        Iterator<ItemStack> iterator = list.stream().flatMap((resolvedEntry) -> {
            return resolvedEntry.displayItems().stream();
        }).iterator();
        if (!iterator.hasNext()) {
            return true;
        } else {
            ItemStack itemStack = (ItemStack)iterator.next();

            ItemStack itemStack2;
            do {
                if (!iterator.hasNext()) {
                    return true;
                }

                itemStack2 = (ItemStack)iterator.next();
            } while(ItemStack.isSameItemSameComponents(itemStack, itemStack2));

            return false;
        }
    }

    public @NotNull RecipeCollection getCollection() {
        return this.collection;
    }

    public void renderWidget(GuiGraphics guiGraphics, int i, int j, float f) {
        ResourceLocation resourceLocation;
        if (this.collection.hasCraftable()) {
            if (this.hasMultipleRecipes()) {
                resourceLocation = SLOT_MANY_CRAFTABLE_SPRITE;
            } else {
                resourceLocation = SLOT_CRAFTABLE_SPRITE;
            }
        } else if (this.hasMultipleRecipes()) {
            resourceLocation = SLOT_MANY_UNCRAFTABLE_SPRITE;
        } else {
            resourceLocation = SLOT_UNCRAFTABLE_SPRITE;
        }

        boolean bl = this.animationTime > 0.0F;
        if (bl) {
            float g = 1.0F + 0.1F * (float)Math.sin((double)(this.animationTime / 15.0F * 3.1415927F));
            guiGraphics.pose().pushPose();
            guiGraphics.pose().translate((float)(this.getX() + 8), (float)(this.getY() + 12), 0.0F);
            guiGraphics.pose().scale(g, g, 1.0F);
            guiGraphics.pose().translate((float)(-(this.getX() + 8)), (float)(-(this.getY() + 12)), 0.0F);
            this.animationTime -= f;
        }

        guiGraphics.blitSprite(RenderType::guiTextured, resourceLocation, this.getX(), this.getY(), this.width, this.height);
        ItemStack itemStack = this.getDisplayStack();
        int k = 4;
        if (this.hasMultipleRecipes() && this.allRecipesHaveSameResultDisplay) {
            guiGraphics.renderItem(itemStack, this.getX() + k + 1, this.getY() + k + 1, 0, 10);
            --k;
        }

        guiGraphics.renderFakeItem(itemStack, this.getX() + k, this.getY() + k);
        if (bl) {
            guiGraphics.pose().popPose();
        }

    }

    private boolean hasMultipleRecipes() {
        return this.selectedEntries.size() > 1;
    }

    public boolean isOnlyOption() {
        return this.selectedEntries.size() == 1;
    }

    public @NotNull RecipeDisplayId getCurrentRecipe() {
        int i = this.slotSelectTime.currentIndex() % this.selectedEntries.size();
        return ((ResolvedEntry)this.selectedEntries.get(i)).id;
    }

    public @NotNull ItemStack getDisplayStack() {
        int i = this.slotSelectTime.currentIndex();
        int j = this.selectedEntries.size();
        int k = i / j;
        int l = i - j * k;
        return ((ResolvedEntry)this.selectedEntries.get(l)).selectItem(k);
    }

    public @NotNull List<Component> getTooltipText(ItemStack itemStack) {
        List<Component> list = new ArrayList<>(Screen.getTooltipFromItem(Minecraft.getInstance(), itemStack));
        if (this.hasMultipleRecipes()) {
            list.add(MORE_RECIPES_TOOLTIP);
        }

        return list;
    }

    public void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {
        narrationElementOutput.add(NarratedElementType.TITLE, Component.translatable("narration.recipe", new Object[]{this.getDisplayStack().getHoverName()}));
        if (this.hasMultipleRecipes()) {
            narrationElementOutput.add(NarratedElementType.USAGE, new Component[]{Component.translatable("narration.button.usage.hovered"), Component.translatable("narration.recipe.usage.more")});
        } else {
            narrationElementOutput.add(NarratedElementType.USAGE, Component.translatable("narration.button.usage.hovered"));
        }

    }

    public int getWidth() {
        return 25;
    }

    protected boolean isValidClickButton(int i) {
        return i == 0 || i == 1;
    }

    @Environment(EnvType.CLIENT)
    record ResolvedEntry(RecipeDisplayId id, List<ItemStack> displayItems) {

        public ItemStack selectItem(int i) {
            if (this.displayItems.isEmpty()) {
                return ItemStack.EMPTY;
            } else {
                int j = i % this.displayItems.size();
                return this.displayItems.get(j);
            }
        }

        public RecipeDisplayId id() {
            return this.id;
        }

        public List<ItemStack> displayItems() {
            return this.displayItems;
        }
    }
}
