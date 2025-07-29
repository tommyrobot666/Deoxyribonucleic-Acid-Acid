package lommie.dnacid.mutation;

import lommie.dnacid.items.components.ModComponents;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;


public class MobEffectMutationEffectType extends MutationEffectType{
    final MobEffect mobEffect;

    public MobEffectMutationEffectType(Settings settings, MobEffect mobEffect) {
        super(settings);
        this.mobEffect = mobEffect;
    }

    @Override
    boolean entityMutationTick(MutationEffect effect, LivingEntity entity) {
        entity.addEffect(new MobEffectInstance(Holder.direct(mobEffect)));
        return false;
    }

    @Override
    boolean playerMutationTick(MutationEffect effect, ServerPlayer player) {
        return entityMutationTick(effect,player);
    }

    @Override
    boolean bacteriaMutationTick(MutationEffect effect, ItemStack bacteria) {
        DataComponentType<Integer> component = ModComponents.EFFECT_AMOUNT_COMPONENTS.get(mobEffect).get();
        if (bacteria.has(component)){
            bacteria.set(component, bacteria.get(component)+1);
        } else {
            bacteria.set(component,1);
        }
        return false;
    }
}
