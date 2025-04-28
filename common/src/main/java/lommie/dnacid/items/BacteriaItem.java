package lommie.dnacid.items;

import lommie.dnacid.Dnacid;
import lommie.dnacid.items.components.BacteriaData;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;
import java.util.Objects;

public class BacteriaItem extends Item {
    public BacteriaItem(Properties properties) {
        super(properties.component(Dnacid.BACTERIA_DATA_COMPONENT.get(), new BacteriaData(false,List.of())));
    }

    @Override
    public boolean overrideOtherStackedOnMe(ItemStack bac, ItemStack other, Slot slot, ClickAction clickAction, Player player, SlotAccess slotAccess) {
        BacteriaData data = Objects.requireNonNull(bac.getComponents().get(Dnacid.BACTERIA_DATA_COMPONENT.get()));
        if (other.getItem().toString().equals("dnacid:petri_dish") && !data.petriDish()){
            bac.set(Dnacid.BACTERIA_DATA_COMPONENT.get(), new BacteriaData(true,data.effects()));
            other.shrink(1);
            return true;
        }

        return false;
    }

    @Override
    public void appendHoverText(ItemStack bac, TooltipContext context, List<Component> tooltip, TooltipFlag type) {
        if (type.isAdvanced() && ((Player) bac.getEntityRepresentation()).isCreative()){
            tooltip.add(Component.literal("Creative mode AND advanced tooltips?!"));
            tooltip.add(Component.literal("NO, I will not allow this"));
            tooltip.add(Component.literal("/ban ").withStyle(ChatFormatting.RED).append(bac.getEntityRepresentation().getDisplayName()));
            return;
        }

        BacteriaData data = bac.getComponents().get(Dnacid.BACTERIA_DATA_COMPONENT.get());
        tooltip.add(Component.literal("In petri dish: " + (data.petriDish()?"Yes":"No")).withStyle(data.petriDish()?ChatFormatting.GREEN:ChatFormatting.RED));
    }
}
