package lommie.dnacid.mixin;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;
import java.util.Map;

@Mixin(RecipeManager.class)
public interface RecipeManagerAccessor {
    @Accessor(value = "allDisplays")
    List<RecipeManager.ServerDisplayInfo> getAllDisplays();

    @Accessor(value = "recipeToDisplay")
    Map<ResourceKey<Recipe<?>>, List<RecipeManager.ServerDisplayInfo>> getRecipeToDisplay();
}
