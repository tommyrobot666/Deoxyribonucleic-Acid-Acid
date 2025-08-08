package lommie.dnacid.items;

import lommie.dnacid.items.components.BacteriaData;
import lommie.dnacid.items.components.ModComponents;
import lommie.dnacid.mutation.MutationEffect;
import lommie.dnacid.mutation.MutationEffectContainer;
import lommie.dnacid.protein.Protein;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import java.util.*;

public class BacteriaItem extends Item {
    public BacteriaItem(Properties properties) {
        super(properties.component(ModComponents.BACTERIA_DATA_COMPONENT.get(), new BacteriaData(false, List.of(), Map.of(), Map.of())));
    }

    @Override
    public boolean overrideOtherStackedOnMe(ItemStack bac, ItemStack other, Slot slot, ClickAction clickAction, Player player, SlotAccess slotAccess) {
        BacteriaData data = Objects.requireNonNull(bac.getComponents().get(ModComponents.BACTERIA_DATA_COMPONENT.get()));
        if (other.getItem().toString().equals("dnacid:petri_dish") && !data.petriDish()){
            setData(bac, data.setPetriDish(true));
            other.shrink(1);
            return true;
        } else if (other.has(ModComponents.PROTEIN.get())) {
            Protein protein = other.get(ModComponents.PROTEIN.get());
            int amount = other.getCount();
            setData(bac, data.addProtein(protein,amount));
            other.shrink(amount);
            return true;
        }

        return false;
    }

    @Override
    public void inventoryTick(ItemStack bac, Level level, Entity entity, int i, boolean bl) {
        BacteriaData data = Objects.requireNonNull(bac.getComponents().get(ModComponents.BACTERIA_DATA_COMPONENT.get()));
        HashMap<Protein,Integer> proteins = new HashMap<>(data.proteins());
        for (Protein protein : proteins.keySet()){
            data = data.addEffects(protein.effects);
            if (proteins.get(protein) < 2){
                proteins.remove(protein);
            } else {
                proteins.put(protein,proteins.get(protein)-1);
            }
        }
        data.setProteins(proteins);
        setData(bac,data);
        ArrayList<MutationEffect> effects = new ArrayList<>(data.effects());
        int j = 0;
        while (j < effects.size()) {
            if (effects.get(j).getType().mutationTick(effects.get(j), (MutationEffectContainer) (Object) bac)) {
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

        for (ResourceLocation metabolicOutput : data.metabolicOutputs().keySet()){
            tooltip.add(Component.literal(metabolicOutput+"<"+data.metabolicOutputs().get(metabolicOutput)));
        }
    }

    void setData(ItemStack bac ,BacteriaData data){
        bac.set(ModComponents.BACTERIA_DATA_COMPONENT.get(),data);
    }
}
