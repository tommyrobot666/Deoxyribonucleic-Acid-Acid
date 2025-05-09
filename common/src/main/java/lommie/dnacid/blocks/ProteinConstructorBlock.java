package lommie.dnacid.blocks;

import com.mojang.serialization.MapCodec;
import dev.architectury.registry.menu.ExtendedMenuProvider;
import dev.architectury.registry.menu.MenuRegistry;
import lommie.dnacid.screens.ProteinConstructorMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.contents.PlainTextContents;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class ProteinConstructorBlock extends BaseEntityBlock {
    public ProteinConstructorBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected @NotNull MapCodec<? extends BaseEntityBlock> codec() {
        return simpleCodec(ProteinConstructorBlock::new);
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new ProteinConstructorBlockEntity(blockPos, blockState);
    }

    @Override
    protected @NotNull InteractionResult useWithoutItem(BlockState blockState, Level level, BlockPos blockPos, Player player, BlockHitResult blockHitResult) {
        if (level.isClientSide) {
            return InteractionResult.PASS;
        }

        // Retrieve the block entity at the clicked position
        BlockEntity entity = level.getBlockEntity(blockPos);
        if (!(entity instanceof ProteinConstructorBlockEntity proteinEntity)) {
            return InteractionResult.PASS;
        }

        MenuRegistry.openExtendedMenu((ServerPlayer) player, new ExtendedMenuProvider() {
            @Override
            public void saveExtraData(FriendlyByteBuf friendlyByteBuf) {
                return;
            }

            @Override
            public @NotNull Component getDisplayName() {
                return MutableComponent.create(PlainTextContents.create("Protein Constructor"));
            }

            @Override
            public @NotNull AbstractContainerMenu createMenu(int containerId, Inventory inventory, Player player) {
                // Pass the block entity's container to the menu so that changes persist
                return new ProteinConstructorMenu(containerId, inventory, proteinEntity.getContainer());
            }
        });
        return InteractionResult.SUCCESS;
    }

    @Override
    protected void onRemove(BlockState blockState, Level level, BlockPos blockPos, BlockState blockState2, boolean bl) {
        Containers.dropContents(level,blockPos,((ProteinConstructorBlockEntity) Objects.requireNonNull(level.getBlockEntity(blockPos))).getContainer());
        level.addFreshEntity(new ItemEntity(level,blockPos.getX(),blockPos.getY(),blockPos.getZ(),new ItemStack(ModBlocks.PROTEIN_CONSTRUCTOR.get().asItem())));
        super.onRemove(blockState, level, blockPos, blockState2, bl);
    }

    @Override
    public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState blockState, BlockEntityType<T> blockEntityType) {
        // Use a proper ticker that calls tick() on the block entity on the server side.
        return level.isClientSide ? null : createTickerHelper(blockEntityType, ModBlocks.PROTEIN_CONSTRUCTOR_ENTITY.get(), (lvl, pos, state, te) -> {
            ProteinConstructorBlockEntity.tick(lvl, pos, state, te);
        });
    }
}
