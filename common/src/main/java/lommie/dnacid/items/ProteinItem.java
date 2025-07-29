package lommie.dnacid.items;

import lommie.dnacid.items.components.ModComponents;
import lommie.dnacid.mixin.ServerPlayerMixin;
import lommie.dnacid.mutation.ModMutations;
import lommie.dnacid.mutation.MutationEffect;
import lommie.dnacid.mutation.MutationEffectContainer;
import lommie.dnacid.protein.Protein;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.contents.PlainTextContents;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;

import java.util.List;
import java.util.Objects;

public class ProteinItem extends Item {
    public ProteinItem(Properties properties, Protein protein) {
        super(properties.component(ModComponents.PROTEIN.get(),protein));
    }

    @Override
    public void appendHoverText(ItemStack itemStack, TooltipContext tooltipContext, List<Component> text, TooltipFlag tooltipFlag) {
        text.add(MutableComponent.create(PlainTextContents.create(itemStack.getOrDefault(ModComponents.AMINO_ACIDS_COMPONENT.get(),"[error]"))).setStyle(Style.EMPTY.withColor(ChatFormatting.GOLD)));
        text.add(MutableComponent.create(PlainTextContents.create(itemStack.get(ModComponents.PROTEIN.get()).aminoAcids)).withStyle(Style.EMPTY.withColor(ChatFormatting.AQUA)));

        if (tooltipFlag.isAdvanced()){
            text.add(MutableComponent.create(PlainTextContents.create("Advanced Tooltips Is On Right Now")).withStyle(ChatFormatting.UNDERLINE));
        }
    }

    @Override
    public void onUseTick(Level level, LivingEntity livingEntity, ItemStack itemStack, int i) {
        if (level.isClientSide()) return;

        Protein protein = itemStack.get(ModComponents.PROTEIN.get());
        for (MutationEffect mutationEffect : protein.effects) {
            ((MutationEffectContainer) livingEntity).addMutationEffect(mutationEffect);
        }

        itemStack.shrink(1);
    }
}
