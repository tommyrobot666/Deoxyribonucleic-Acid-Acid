package lommie.dnacid.mutation;

import lommie.dnacid.items.components.ModComponents;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.Potion;


public class PotionMutationEffectType extends MutationEffectType{
    final Potion potion;

    public PotionMutationEffectType(Settings settings, Potion potion) {
        super(settings);
        this.potion = potion;
    }

    @Override
    boolean entityMutationTick(MutationEffect effect, LivingEntity entity) {
        for (MobEffectInstance e : potion.getEffects()){
            entity.addEffect(e);
        }
        return false;
    }

    @Override
    boolean playerMutationTick(MutationEffect effect, ServerPlayer player) {
        return entityMutationTick(effect,player);
    }

    @Override
    boolean bacteriaMutationTick(MutationEffect effect, ItemStack bacteria) {
        DataComponentType<Integer> component = ModComponents.POTION_COMPONENTS.get(potion).get();
        if (bacteria.has(component)){
            bacteria.set(component, bacteria.get(component)+1);
        } else {
            bacteria.set(component,1);
        }
        return false;
    }
}
