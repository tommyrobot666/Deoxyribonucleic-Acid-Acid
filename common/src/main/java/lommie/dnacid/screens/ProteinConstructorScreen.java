package lommie.dnacid.screens;

import lommie.dnacid.Dnacid;
import lommie.dnacid.screens.recipebook.ProteinConstructorRecipeBookComponent;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.navigation.ScreenPosition;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.recipebook.RecipeBookComponent;
import net.minecraft.client.gui.screens.recipebook.RecipeUpdateListener;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.crafting.display.RecipeDisplay;
import org.jetbrains.annotations.NotNull;

@Environment(EnvType.CLIENT)
public class ProteinConstructorScreen extends AbstractContainerScreen<ProteinConstructorMenu> implements RecipeUpdateListener {
    private static final ResourceLocation BACKGROUND_TEXTURE = ResourceLocation.tryBuild(Dnacid.MOD_ID, "textures/gui/protein_constructor.png");
    private final ProteinConstructorRecipeBookComponent<ProteinConstructorMenu> betterRecipeBookComponent;
    private boolean widthTooNarrow;

    public ProteinConstructorScreen(ProteinConstructorMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.betterRecipeBookComponent = new ProteinConstructorRecipeBookComponent<>(menu);
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
        this.widthTooNarrow = this.width < 379;
        this.betterRecipeBookComponent.init(this.width, this.height, this.minecraft, this.widthTooNarrow);
        this.leftPos = this.betterRecipeBookComponent.updateScreenPosition(this.width, this.imageWidth);
        this.betterRecipeBookComponent.init(this.width, this.height, this.minecraft, false);
        initButton();
    }

    @Override
    public void render(GuiGraphics guiGraphics, int i, int j, float f) {
        if (this.betterRecipeBookComponent.isVisible() && this.widthTooNarrow) {
            this.renderBackground(guiGraphics, i, j, f);
        } else {
            super.render(guiGraphics, i, j, f);
        }

        betterRecipeBookComponent.render(guiGraphics, i, j, f);
        this.renderTooltip(guiGraphics, i, j);
        betterRecipeBookComponent.renderTooltip(guiGraphics, i, j, this.hoveredSlot);
    }

    @Override
    protected void renderSlots(GuiGraphics guiGraphics) {
        super.renderSlots(guiGraphics);
        this.betterRecipeBookComponent.renderGhostRecipe(guiGraphics, true);//this.isBiggerResultSlot());
    }

    @Override
    public boolean charTyped(char c, int i) {
        return this.betterRecipeBookComponent.charTyped(c, i) || super.charTyped(c, i);
    }

    protected void onRecipeBookButtonClick() {
        Dnacid.LOGGER.warn("Book clicked");
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
            return this.widthTooNarrow && this.betterRecipeBookComponent.isVisible() || super.mouseClicked(d, e, i);
        }
    }

    @Override
    protected boolean isHovering(int i, int j, int k, int l, double d, double e) {
        return (!this.widthTooNarrow || !this.betterRecipeBookComponent.isVisible()) && super.isHovering(i, j, k, l, d, e);
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
        Dnacid.LOGGER.error("RU");
        this.betterRecipeBookComponent.recipesUpdated();
    }

    @Override
    public void fillGhostRecipe(RecipeDisplay recipeDisplay) {
        Dnacid.LOGGER.error("FGR");
        this.betterRecipeBookComponent.fillGhostRecipe(recipeDisplay);
    }
}