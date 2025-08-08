package lommie.dnacid.items;

import lommie.dnacid.items.components.ModComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;
import java.util.Objects;

public class PlasmidItem extends Item {
    public PlasmidItem(Properties properties) {
        super(properties);
    }

    @Override
    public void appendHoverText(ItemStack itemStack, TooltipContext tooltipContext, List<Component> toolTip, TooltipFlag tooltipFlag) {
        if (itemStack.has(ModComponents.PROTEIN.get())) {
            toolTip.add(Component.literal(Objects.requireNonNull(itemStack.get(ModComponents.PROTEIN.get())).id.toString()));
        }
    }
}
