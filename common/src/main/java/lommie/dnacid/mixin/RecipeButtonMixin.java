package lommie.dnacid.mixin;

import net.minecraft.client.gui.screens.recipebook.RecipeButton;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

@Mixin(RecipeButton.class)
public abstract class RecipeButtonMixin {
    /**
     * @author Me
     * @reason Div by 0 errors are fucked up
     */
    @Overwrite
    public ItemStack getDisplayStack() {
        RecipeButton self = (RecipeButton) (Object) this;
        int i = ((RecipeButtonAccessor) self).getSlotSelectTime().currentIndex();
        int j = ((RecipeButtonAccessor) self).getSelectedEntries().size();
        int k = 0;
        try {
            k = i / j;
        } catch (Exception ignored) {
            return new ItemStack(Items.DIRT_PATH);
        }
        int l = i - j * k;
        try {
            Method selectItem = RecipeButton.class.getDeclaredClasses()[0].getDeclaredMethod("selectItem",int.class);
            selectItem.setAccessible(true);
            return (ItemStack) selectItem.invoke((((RecipeButtonAccessor) self).getSelectedEntries().get(l)),k);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
