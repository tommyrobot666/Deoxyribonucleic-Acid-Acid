package lommie.dnacid.screens.recipebook;

import lommie.dnacid.Dnacid;
import net.minecraft.client.ClientRecipeBook;
import net.minecraft.client.gui.screens.recipebook.RecipeCollection;
import net.minecraft.world.item.crafting.ExtendedRecipeBookCategory;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ProteinConstructorClientRecipeBook extends ClientRecipeBook {

    @Override
    public @NotNull List<RecipeCollection> getCollections() {
        //return Dnacid.idk
        return super.getCollections();
    }

    @Override
    public @NotNull List<RecipeCollection> getCollection(ExtendedRecipeBookCategory extendedRecipeBookCategory){
        return getCollections();
    }

    @Override
    public void rebuildCollections() {
        Dnacid.LOGGER.error("RebC");
        //super.rebuildCollections();
    }
}
