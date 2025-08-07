package lommie.dnacid.items;

import lommie.dnacid.items.components.ModComponents;
import lommie.dnacid.mutation.ModMutations;
import lommie.dnacid.mutation.MutationEffect;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;

public class PlasmidItem extends Item {
    public PlasmidItem(Properties properties) {
        super(properties.component(ModComponents.MUTATION_EFFECT_COMPONENT.get(),new MutationEffect(ModMutations.TEST_MUTATION_EFFECT_TYPE,100)));
    }

    @Override
    public void appendHoverText(ItemStack itemStack, TooltipContext tooltipContext, List<Component> toolTip, TooltipFlag tooltipFlag) {
        toolTip.add(itemStack.get(ModComponents.MUTATION_EFFECT_COMPONENT.get()).getType().getName());
    }
}
