package lommie.dnacid.screens.recipebook;

import com.mojang.authlib.minecraft.client.MinecraftClient;
import lommie.dnacid.Dnacid;
import net.fabricmc.loader.impl.game.GameProvider;
import net.fabricmc.loader.impl.game.minecraft.MinecraftGameProvider;
import net.minecraft.client.ClientRecipeBook;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.recipebook.RecipeCollection;
import net.minecraft.server.MinecraftServer;
import net.minecraft.stats.ServerRecipeBook;
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
