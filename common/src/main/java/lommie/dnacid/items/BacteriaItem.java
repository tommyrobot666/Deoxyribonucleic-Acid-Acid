package lommie.dnacid.items;

import dev.architectury.registry.registries.RegistrySupplier;
import lommie.dnacid.items.components.BacteriaData;
import lommie.dnacid.items.components.ModComponents;
import lommie.dnacid.mutation.MutationEffect;
import lommie.dnacid.mutation.MutationEffectContainer;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class BacteriaItem extends Item {
    public BacteriaItem(Properties properties) {
        super(properties.component(ModComponents.BACTERIA_DATA_COMPONENT.get(), new BacteriaData(false,List.of())));
    }

    @Override
    public boolean overrideOtherStackedOnMe(ItemStack bac, ItemStack other, Slot slot, ClickAction clickAction, Player player, SlotAccess slotAccess) {
        BacteriaData data = Objects.requireNonNull(bac.getComponents().get(ModComponents.BACTERIA_DATA_COMPONENT.get()));
        if (other.getItem().toString().equals("dnacid:petri_dish") && !data.petriDish()){
            bac.set(ModComponents.BACTERIA_DATA_COMPONENT.get(), new BacteriaData(true,data.effects()));
            other.shrink(1);
            return true;
        } else if (other.has(ModComponents.MUTATION_EFFECT_COMPONENT.get())) {
            List<MutationEffect> effects = new ArrayList<>(data.effects());
            effects.add(other.get(ModComponents.MUTATION_EFFECT_COMPONENT.get()));
            bac.set(ModComponents.BACTERIA_DATA_COMPONENT.get(), new BacteriaData(data.petriDish(),effects));
            other.shrink(1);
            return true;
        }

        return false;
    }

    @Override
    public void inventoryTick(ItemStack bac, Level level, Entity entity, int i, boolean bl) {
        BacteriaData data = Objects.requireNonNull(bac.getComponents().get(ModComponents.BACTERIA_DATA_COMPONENT.get()));
        ArrayList<MutationEffect> effects = new ArrayList<>(data.effects());
        int j = 0;
        while (j < effects.size()) {
            if (effects.get(j).getType().mutationTick(effects.get(j),(MutationEffectContainer) ((Object) bac))) {
                effects.remove(j);
            } else {
                j++;
            }
        }
    }

    @Override
    public void appendHoverText(ItemStack bac, TooltipContext context, List<Component> tooltip, TooltipFlag type) {
        if (type.isAdvanced() && ((Player) bac.getEntityRepresentation()).isCreative()){
            tooltip.add(Component.literal("Creative mode AND advanced tooltips?!"));
            tooltip.add(Component.literal("NO, I will not allow this"));
            tooltip.add(Component.literal("/ban ").withStyle(ChatFormatting.RED).append(bac.getEntityRepresentation().getDisplayName()));
            return;
        }

        BacteriaData data = bac.getComponents().get(ModComponents.BACTERIA_DATA_COMPONENT.get());
        tooltip.add(Component.literal("In petri dish: " + (data.petriDish()?"Yes":"No")).withStyle(data.petriDish()?ChatFormatting.GREEN:ChatFormatting.RED));
        if (data.effects().size() > 0){
            tooltip.add(Component.literal("Effects:"));
            for (MutationEffect effect : data.effects()){
                if (effect.type.get() == null){
                    tooltip.add(Component.literal("❓ Unknown Effect"));
                } else {
                    tooltip.add(effect.getType().getName().copy().append(effect.timeLeft < 0 ? "" : " Time Left: " + effect.timeLeft));
                }
            }
        }

        for (Potion potion : ModComponents.POTION_COMPONENTS.keySet()){
            RegistrySupplier<DataComponentType<Integer>> component = ModComponents.POTION_COMPONENTS.get(potion);
            if (bac.has(component.get())){
                tooltip.add(Component.literal(potion.name()+"<"+bac.get(component.get())));
            }
        }
    }
}
