package lommie.dnacid.mixin;

import lommie.dnacid.items.ModItems;
import lommie.dnacid.items.components.BacteriaData;
import lommie.dnacid.items.components.ModComponents;
import lommie.dnacid.mutation.MutationEffect;
import lommie.dnacid.mutation.MutationEffectContainer;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.ArrayList;
import java.util.Objects;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin implements MutationEffectContainer {
    @Shadow public abstract Item getItem();

    @Shadow public abstract boolean is(Item arg);

    @Shadow public abstract DataComponentMap getComponents();

    @Shadow @Nullable
    public abstract <T> T set(DataComponentType<? super T> arg, T object);

    @Override
    public void addMutationEffect(MutationEffect effect) {
        BacteriaData data = Objects.requireNonNull(getComponents().get(ModComponents.BACTERIA_DATA_COMPONENT.get()));
        set(ModComponents.BACTERIA_DATA_COMPONENT.get(),data.addEffect(effect));
    }

    @Override
    public void removeMutationEffectAt(int i){
        BacteriaData data = Objects.requireNonNull(getComponents().get(ModComponents.BACTERIA_DATA_COMPONENT.get()));
        ArrayList<MutationEffect> effects = new ArrayList<>(data.effects());
        effects.remove(i);
        set(ModComponents.BACTERIA_DATA_COMPONENT.get(),new BacteriaData(data.petriDish(), effects, data.metabolicOutputs()));
    }

    @Override
    public ArrayList<MutationEffect> getMutationEffects() {
        return new ArrayList<>(Objects.requireNonNull(getComponents().get(ModComponents.BACTERIA_DATA_COMPONENT.get())).effects());
    }

    @Override
    public creatureType getMutationCreatureType() {
        if (is(ModItems.BACTERIA.get())) {
            return creatureType.BACTERIA;
        } else {
            throw new IllegalStateException("Unexpected value: " + getItem());
        }
    }
}
