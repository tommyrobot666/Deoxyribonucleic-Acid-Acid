package lommie.dnacid.screens;

import lommie.dnacid.Dnacid;
import lommie.dnacid.screens.recipebook.ProteinConstructorRecipeBookComponent;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.navigation.ScreenPosition;
import net.minecraft.client.gui.screens.inventory.AbstractRecipeBookScreen;
import net.minecraft.client.gui.screens.recipebook.CraftingRecipeBookComponent;
import net.minecraft.client.gui.screens.recipebook.RecipeBookComponent;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.crafting.display.RecipeDisplay;
import org.jetbrains.annotations.NotNull;

public class ProteinConstructorScreen extends AbstractRecipeBookScreen<ProteinConstructorMenu> {
    private static final ResourceLocation BACKGROUND_TEXTURE = ResourceLocation.tryBuild(Dnacid.MOD_ID, "textures/gui/protein_constructor.png");
    private final ProteinConstructorRecipeBookComponent<ProteinConstructorMenu> betterRecipeBookComponent;

    public ProteinConstructorScreen(ProteinConstructorMenu menu, Inventory playerInventory, Component title) {
        super(menu, new CraftingRecipeBookComponent(menu) ,playerInventory, title);
        this.betterRecipeBookComponent = new ProteinConstructorRecipeBookComponent<>(menu);
    }

    @Override
    protected @NotNull ScreenPosition getRecipeBookButtonPosition() {
        return new ScreenPosition(999,999);
    }

    protected @NotNull ScreenPosition getRealRecipeBookButtonPosition() {
        return new ScreenPosition(this.leftPos +180, this.height / 2 + 29);
    }

    private void initButton() {
        ScreenPosition screenPosition = this.getRealRecipeBookButtonPosition();
        this.addRenderableWidget(new ImageButton(screenPosition.x(), screenPosition.y(), 20, 18, RecipeBookComponent.RECIPE_BUTTON_SPRITES, (button) -> {
            this.betterRecipeBookComponent.toggleVisibility();
            this.leftPos = this.betterRecipeBookComponent.updateScreenPosition(this.width, this.imageWidth);
            button.setPosition(screenPosition.x(), screenPosition.y());
            this.onRecipeBookButtonClick();
        }));
        this.addWidget(this.betterRecipeBookComponent);
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float f, int i, int j) {
        guiGraphics.blit(
                RenderType::guiOpaqueTexturedBackground,// function mapping texture to render type
                BACKGROUND_TEXTURE,              // the ResourceLocation for your texture
                this.leftPos, this.topPos,       // screen position (x,y)
                0f, 0f,                          // starting u and v in the texture
                this.imageWidth, this.imageHeight, // size to draw on screen
                this.imageWidth, this.imageHeight, // region width & height (the portion of the texture to use)
                256, 256                         // texture’s full width & height (for UV mapping)
        );
    }

    @Override
    protected boolean hasClickedOutside(double d, double e, int i, int j, int k) {
        return false;
    }

    @Override
    protected void init() {
        super.init();
        this.betterRecipeBookComponent.init(this.width, this.height, this.minecraft, false);
        initButton();
    }

    @Override
    public void render(GuiGraphics guiGraphics, int i, int j, float f) {
        super.render(guiGraphics, i, j, f);
        betterRecipeBookComponent.render(guiGraphics, i, j, f);
        betterRecipeBookComponent.renderTooltip(guiGraphics, i, j, this.hoveredSlot);
    }

    @Override
    protected void renderSlots(GuiGraphics guiGraphics) {
        super.renderSlots(guiGraphics);
        this.betterRecipeBookComponent.renderGhostRecipe(guiGraphics, this.isBiggerResultSlot());
    }

    @Override
    public boolean charTyped(char c, int i) {
        return this.betterRecipeBookComponent.charTyped(c, i) || super.charTyped(c, i);
    }

    @Override
    public boolean keyPressed(int i, int j, int k) {
        return this.betterRecipeBookComponent.keyPressed(i, j, k) || super.keyPressed(i, j, k);
    }

    @Override
    public boolean mouseClicked(double d, double e, int i) {
        if (this.betterRecipeBookComponent.mouseClicked(d, e, i)) {
            this.setFocused(this.betterRecipeBookComponent);
            return true;
        } else {
            return super.mouseClicked(d, e, i);
        }
    }

    @Override
    protected boolean isHovering(int i, int j, int k, int l, double d, double e) {
        return super.isHovering(i, j, k, l, d, e);
    }

    @Override
    protected void slotClicked(Slot slot, int i, int j, ClickType clickType) {
        super.slotClicked(slot, i, j, clickType);
        this.betterRecipeBookComponent.slotClicked(slot);
    }

    @Override
    public void containerTick() {
        super.containerTick();
        this.betterRecipeBookComponent.tick();
    }

    @Override
    public void recipesUpdated() {
        this.betterRecipeBookComponent.recipesUpdated();
    }

    @Override
    public void fillGhostRecipe(RecipeDisplay recipeDisplay) {
        this.betterRecipeBookComponent.fillGhostRecipe(recipeDisplay);
    }
}