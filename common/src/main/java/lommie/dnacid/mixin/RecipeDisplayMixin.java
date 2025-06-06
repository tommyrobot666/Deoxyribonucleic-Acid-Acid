package lommie.dnacid.mixin;

import lommie.dnacid.Dnacid;
import lommie.dnacid.items.ModItems;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.item.crafting.display.RecipeDisplay;
import net.minecraft.world.item.crafting.display.SlotDisplay;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(RecipeDisplay.class)
public abstract interface RecipeDisplayMixin {

    @Shadow public abstract SlotDisplay result();

    @Inject(method = "isEnabled", at = @At("HEAD"), cancellable = true)
    private void proteinConstructorRecipeAlwaysEnabled(FeatureFlagSet featureFlagSet, CallbackInfoReturnable<Boolean> cir){
        try {
            if (((SlotDisplay.ItemStackSlotDisplay) this.result()).stack().is(ModItems.PROTEIN.get())) {
                Dnacid.LOGGER.error("AAH THE 5*40!");
                cir.setReturnValue(true);
            }
        } catch (Exception e) {
            Dnacid.LOGGER.info("error:{}",e.toString());
        }
    }
}
