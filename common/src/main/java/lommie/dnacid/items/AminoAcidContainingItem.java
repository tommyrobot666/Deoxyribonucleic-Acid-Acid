package lommie.dnacid.items;

import lommie.dnacid.Dnacid;
import lommie.dnacid.mutation.MutationEffectContainer;
import lommie.dnacid.mutation.TestMutationEffect;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.contents.PlainTextContents;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.Blocks;

import java.util.List;

public class AminoAcidContainingItem extends Item {
    public AminoAcidContainingItem(Properties properties) {
        super(properties);
    }

    @Override
    public void appendHoverText(ItemStack itemStack, TooltipContext tooltipContext, List<Component> text, TooltipFlag tooltipFlag) {
        text.add(MutableComponent.create(PlainTextContents.create(itemStack.getOrDefault(Dnacid.AMINO_ACIDS_COMPONENT.get(),"[error]"))).setStyle(Style.EMPTY.withColor(ChatFormatting.GOLD)));

        if (tooltipFlag.isAdvanced()){
            text.add(MutableComponent.create(PlainTextContents.create("Advanced Tooltips Is On Right Now")).withStyle(ChatFormatting.UNDERLINE));
        }
    }

    @Override
    public InteractionResult useOn(UseOnContext useOnContext) {
        if (useOnContext.getLevel().isClientSide){
            return InteractionResult.PASS;
        }

        if (useOnContext.getLevel().getBlockState(useOnContext.getClickedPos()).is(Blocks.COPPER_BLOCK)){
            ((MutationEffectContainer) useOnContext.getPlayer()).addMutationEffect(new TestMutationEffect());
            return InteractionResult.SUCCESS_SERVER;
        }

        return InteractionResult.PASS;
    }
}
