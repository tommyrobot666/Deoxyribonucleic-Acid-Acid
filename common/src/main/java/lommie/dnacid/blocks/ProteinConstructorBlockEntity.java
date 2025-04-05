package lommie.dnacid.blocks;

import lommie.dnacid.Dnacid;
import lommie.dnacid.recipe.ProteinConstructorRecipe;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.StackedItemContents;
import net.minecraft.world.inventory.RecipeCraftingHolder;
import net.minecraft.world.inventory.StackedContentsCompatible;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.SimpleContainer;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class ProteinConstructorBlockEntity extends BlockEntity implements RecipeCraftingHolder, StackedContentsCompatible {
    private final SimpleContainer container = new SimpleContainer(201); // 200 input slots + 1 output slot

    public ProteinConstructorBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(Dnacid.PROTEIN_CONSTRUCTOR_ENTITY.get(), blockPos, blockState);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, ProteinConstructorBlockEntity te) {
        if (level == null || level.isClientSide()) return;

        // Create a temporary container with just the input slots (indices 0-199)
        SimpleContainer inputContainer = new SimpleContainer(200);
        for (int i = 0; i < 200; i++) {
            inputContainer.setItem(i, te.container.getItem(i));
        }

        CraftingInput input = CraftingInput.of(5,40,inputContainer.getItems());

        // Query the recipe manager for a matching recipe of our custom type.
        Optional<RecipeHolder<ProteinConstructorRecipe>> recipeHolderOpt =
                ((ServerLevel) level).recipeAccess().getRecipeFor(Dnacid.PROTEIN_CONSTRUCTOR_RECIPE_TYPE.get(), input, level);

        if (recipeHolderOpt.isPresent()) {
            RecipeHolder<ProteinConstructorRecipe> recipeHolder = recipeHolderOpt.get();
            ProteinConstructorRecipe recipe = recipeHolder.value();
            // Assemble the result (ensure assemble() returns a new copy)
            ItemStack result = recipe.assemble(input, null);
            te.container.setItem(200, result);
        } else {
            // No valid recipe found; clear the output slot.
            te.container.setItem(200, ItemStack.EMPTY);
        }
    }

    @Override
    protected void saveAdditional(CompoundTag compoundTag, HolderLookup.Provider provider) {
        super.saveAdditional(compoundTag, provider);
        ContainerHelper.saveAllItems(compoundTag, this.container.getItems(), provider);
    }

    @Override
    protected void loadAdditional(CompoundTag compoundTag, HolderLookup.Provider provider) {
        super.loadAdditional(compoundTag, provider);
        ContainerHelper.loadAllItems(compoundTag, this.container.getItems(), provider);
    }

    public SimpleContainer getContainer() {
        return container;
    }


    @Override
    public void setRecipeUsed(@Nullable RecipeHolder<?> recipeHolder) {

    }

    @Override
    public @Nullable RecipeHolder<?> getRecipeUsed() {
        return null;
    }

    @Override
    public void fillStackedContents(StackedItemContents stackedItemContents) {
        for (ItemStack stack : this.container.getItems()){
            stackedItemContents.accountStack(stack);
        }
    }


}
