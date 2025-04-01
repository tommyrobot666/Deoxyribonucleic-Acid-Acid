package lommie.dnacid.mixin;

import lommie.dnacid.notmixin.ILocalPlayerMixin;
import lommie.dnacid.screens.recipebook.ProteinConstructorClientRecipeBook;
import net.minecraft.client.player.LocalPlayer;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(LocalPlayer.class)
public class LocalPlayerMixin implements ILocalPlayerMixin {
    private ProteinConstructorClientRecipeBook proteinConstructorClientRecipeBook = new ProteinConstructorClientRecipeBook();

    @Override
    public ProteinConstructorClientRecipeBook getProteinConstructorClientRecipeBook() {
        return proteinConstructorClientRecipeBook;
    }
}
