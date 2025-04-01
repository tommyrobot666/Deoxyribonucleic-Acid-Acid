package lommie.dnacid.screens;

import lommie.dnacid.Dnacid;
import lommie.dnacid.recipe.ProteinConstructorRecipe;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.contents.PlainTextContents;
import net.minecraft.recipebook.ServerPlaceRecipe;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Container;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.StackedItemContents;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;


public class ProteinConstructorMenu extends AbstractCraftingMenu implements MenuProvider {
    private final Container container;
    private final Player player;

    public static final int INPUT_SLOTS = 200;
    public static final int OUTPUT_SLOT_INDEX = INPUT_SLOTS; // 200th slot (index 200)

    public ProteinConstructorMenu(int containerId, Inventory playerInventory) {
        this(containerId, playerInventory, new SimpleContainer(INPUT_SLOTS + 1)); // 200 input + 1 output
    }

    public ProteinConstructorMenu(int containerId, Inventory playerInventory, Container container) {
        super(Dnacid.PROTEIN_CONSTRUCTOR_MENU.get(), containerId,40,5);
        this.player = playerInventory.player;
        this.container = container;

        // Add input slots (200 slots, 10 rows × 20 columns)
        int slotIndex = 0;
        for (int row = 0; row < 5; row++) {
            for (int col = 0; col < 40; col++) {
                if (slotIndex == OUTPUT_SLOT_INDEX){
                    break;
                }
                this.addSlot(new Slot(container, slotIndex++, 8 + col * 18 -(18*19), 18 + row * 18 -(18*2))
                {
                    @Override
                    public boolean mayPlace(ItemStack itemStack) {
                        return true;//itemStack.is(Dnacid.PROTEIN.get()) || Dnacid.AMINO_ACIDS.contains(itemStack.getItem());
                    }
                });
            }
        }

        // Add output slot
        this.addSlot(new Slot(container, OUTPUT_SLOT_INDEX, (18*-3), 18*6) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return false; // Prevent placing items in the output slot
            }
        });

        // Add player inventory slots (Standard 9x4 inventory layout)
        for (int row = 0; row < 3; ++row) {
            for (int col = 0; col < 9; ++col) {
                this.addSlot(new Slot(playerInventory, col + row * 9 + 9, 8 + col * 18, 140 + row * 18 -(18*3)));
            }
        }

        // Add player hotbar slots
        for (int col = 0; col < 9; ++col) {
            this.addSlot(new Slot(playerInventory, col, 8 + col * 18, 198 -(18*3)));
        }
    }

    public ProteinConstructorMenu(int id, Inventory inventory, FriendlyByteBuf buf) {
        this(id, inventory);

    }

    @Override
    public @NotNull ItemStack quickMoveStack(Player player, int index) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean stillValid(Player player) {
        return this.container.stillValid(player);
    }

    @Override
    public @NotNull Component getDisplayName() {
        return MutableComponent.create(PlainTextContents.create("Protein Constructor"));
    }

    @Override
    public @Nullable AbstractContainerMenu createMenu(int i, Inventory inventory, Player player) {
        return new ProteinConstructorMenu(i,inventory,container);
    }

    @Override
    public @NotNull PostPlaceAction handlePlacement(boolean placeAll, boolean placeOne, RecipeHolder<?> recipeHolder, ServerLevel serverLevel, Inventory inventory) {
        // Attempt to cast the recipe holder to our specific recipe type
        RecipeHolder<ProteinConstructorRecipe> proteinRecipeHolder;
        try {
            proteinRecipeHolder = (RecipeHolder<ProteinConstructorRecipe>) recipeHolder;
        } catch (ClassCastException e) {
            // If the recipe isn't a ProteinRecipe, return early.
            return PostPlaceAction.NOTHING;
        }

        // Collect input slots (indices 0 to 199)
        ArrayList<Slot> inputSlots = new ArrayList<>();
        for (int i = 0; i < INPUT_SLOTS; i++) {
            inputSlots.add(getSlot(i));
        }

        // Call ServerPlaceRecipe.placeRecipe to handle recipe placement.
        ServerPlaceRecipe.placeRecipe(
                new ServerPlaceRecipe.CraftingMenuAccess<>() {
                    @Override
                    public void fillCraftSlotsStackedContents(StackedItemContents stackedItemContents) {
                        // Forward the contents from your crafting grid (if needed)
                        getInputGridSlots().forEach(slot -> {
                            ItemStack stack = slot.getItem();
                            if (!stack.isEmpty()) {
                                stackedItemContents.accountStack(stack);
                            }
                        });
                    }

                    @Override
                    public void clearCraftingContent() {
                        // Optionally clear your input slots if the recipe is placed
                        for (Slot slot : getInputGridSlots()) {
                            slot.set(ItemStack.EMPTY);
                        }
                    }

                    @Override
                    public boolean recipeMatches(RecipeHolder<ProteinConstructorRecipe> recipeHolder) {
                        return recipeHolder.value().matches(craftSlots.asCraftInput(), owner().level());
                    }
                },
                40, // grid width (as passed to the super constructor)
                5,  // grid height
                new ArrayList<>(inputSlots), // crafting slots list
                new ArrayList<>(inputSlots), // available slots list (if different, adjust accordingly)
                inventory,
                proteinRecipeHolder,
                placeAll,
                placeOne
        );

        return PostPlaceAction.PLACE_GHOST_RECIPE;
    }

    @Override
    public @NotNull Slot getResultSlot() {
        return getSlot(OUTPUT_SLOT_INDEX);
    }

    @Override
    public @NotNull List<Slot> getInputGridSlots() {
        ArrayList<Slot> inputSlots = new ArrayList<>();
        for (int i = 0; i < 200; i++) {
            inputSlots.add(getSlot(i));
        }
        return inputSlots.stream().toList();
    }

    @Override
    protected @NotNull Player owner() {
        return player;
    }

    @Override
    public void fillCraftSlotsStackedContents(StackedItemContents stackedItemContents) {
        this.craftSlots.fillStackedContents(stackedItemContents);
    }

    @Override
    public @NotNull RecipeBookType getRecipeBookType() {
        return RecipeBookType.CRAFTING;
    }
}