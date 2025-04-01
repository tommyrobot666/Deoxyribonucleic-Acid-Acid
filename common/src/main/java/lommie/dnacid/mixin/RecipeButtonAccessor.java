package lommie.dnacid.mixin;

import net.minecraft.client.gui.screens.recipebook.RecipeButton;
import net.minecraft.client.gui.screens.recipebook.SlotSelectTime;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@Mixin(RecipeButton.class)
public interface RecipeButtonAccessor {
    @Accessor("slotSelectTime")
    SlotSelectTime getSlotSelectTime();

    @Accessor("selectedEntries")
    List<?> getSelectedEntries();
}
