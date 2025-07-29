package lommie.dnacid.blocks;

import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import lommie.dnacid.items.ModItems;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;

import java.lang.reflect.Constructor;
import java.util.Set;
import java.util.function.Function;

import static lommie.dnacid.Dnacid.MOD_ID;
import static lommie.dnacid.items.ModTabs.THE_TAB;

public class ModBlocks {
    public static final DeferredRegister<Block> BLOCKS =
            DeferredRegister.create(MOD_ID, Registries.BLOCK);
    public static final DeferredRegister<BlockEntityType<?>> BLOCKS_ENTITY_TYPES =
            DeferredRegister.create(MOD_ID, Registries.BLOCK_ENTITY_TYPE);

    static RegistrySupplier<Block> register(String name, Function<Block.Properties, Block> factory, Block.Properties settings, boolean andItem, Item.Properties itemSettings){
        ResourceKey<Block> key = ResourceKey.create(Registries.BLOCK, ResourceLocation.tryBuild(MOD_ID,name));
        Block realBlock = factory.apply(settings.setId(key));
        RegistrySupplier<Block> block = BLOCKS.register(key.location(), () -> realBlock);
        if (andItem) {
            ModItems.register(name, (s) -> new BlockItem(realBlock,s), itemSettings);
        }
        return block;
    }

    static RegistrySupplier<Block> register(String name, Function<Block.Properties, Block> factory, Block.Properties settings){
        return register(name,factory,settings,false,null);
    }

    public static void register(){
        BLOCKS.register();
        BLOCKS_ENTITY_TYPES.register();
    }
}
