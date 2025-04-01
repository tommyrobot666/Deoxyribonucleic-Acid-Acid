package lommie.dnacid;


import com.mojang.serialization.Codec;
import dev.architectury.platform.Platform;
import dev.architectury.registry.CreativeTabRegistry;
import dev.architectury.registry.menu.MenuRegistry;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import lommie.dnacid.blocks.ProteinConstructorBlock;
import lommie.dnacid.blocks.ProteinConstructorBlockEntity;
import lommie.dnacid.items.AminoAcidContainingItem;
import lommie.dnacid.recipe.ProteinConstructorRecipe;
import lommie.dnacid.recipe.ProteinConstructorRecipeSerializer;
import lommie.dnacid.recipe.ProteinConstructorRecipeType;
import lommie.dnacid.screens.*;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.RecipeBookCategory;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;


public final class Dnacid {
    public static final String MOD_ID = "dnacid";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static final List<Character> AMINO_ACID_CHARS = List.of('A','R','N','D','C','Q','E','G','H','I','L','K','M','F','P','S','T','W','Y','V');

    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(MOD_ID, Registries.ITEM);
    public static final DeferredRegister<Block> BLOCKS =
            DeferredRegister.create(MOD_ID, Registries.BLOCK);
    public static final DeferredRegister<BlockEntityType<?>> BLOCKS_ENTITY_TYPES =
            DeferredRegister.create(MOD_ID, Registries.BLOCK_ENTITY_TYPE);
    public static final DeferredRegister<DataComponentType<?>> COMPONENT_TYPES =
            DeferredRegister.create(MOD_ID, Registries.DATA_COMPONENT_TYPE);
    public static final DeferredRegister<CreativeModeTab> TABS =
            DeferredRegister.create(MOD_ID, Registries.CREATIVE_MODE_TAB);
    public static final DeferredRegister<MenuType<?>> MENUS =
            DeferredRegister.create(MOD_ID, Registries.MENU);
    public static final DeferredRegister<RecipeBookCategory> RECIPE_BOOK_CATEGORIES =
            DeferredRegister.create(MOD_ID, Registries.RECIPE_BOOK_CATEGORY);
    public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS =
            DeferredRegister.create(MOD_ID, Registries.RECIPE_SERIALIZER);
    public static final DeferredRegister<RecipeType<?>> RECIPE_TYPES =
            DeferredRegister.create(MOD_ID, Registries.RECIPE_TYPE);
/*
    public static final DeferredRegister<MutationEffect> MUTATION_EFFECTS =
            DeferredRegister.create(MOD_ID, ResourceKey.createRegistryKey(ResourceLocation.tryBuild(MOD_ID,"mutation_effects")));


    public static final RegistrySupplier<MutationEffect> TEST_MUTATION_EFFECT = MUTATION_EFFECTS.register(
            "test",
            TestMutationEffect::new
    );
*/

    public static final RegistrySupplier<RecipeType<ProteinConstructorRecipe>> PROTEIN_CONSTRUCTOR_RECIPE_TYPE =
            RECIPE_TYPES.register("protein_constructor", () -> ProteinConstructorRecipeType.INSTANCE);

    public static final RegistrySupplier<RecipeSerializer<ProteinConstructorRecipe>> PROTEIN_CONSTRUCTOR_RECIPE_SERIALIZER =
            RECIPE_SERIALIZERS.register("protein_constructor", () -> ProteinConstructorRecipeSerializer.INSTANCE);

    public static final RegistrySupplier<CreativeModeTab> THE_TAB = TABS.register(
            "the_tab", // Tab ID
            () -> CreativeTabRegistry.create(
                    Component.translatable("category."+MOD_ID), // Tab Name
                    () -> new ItemStack(Items.GLOW_INK_SAC) // Icon
            )
    );

    public static final RegistrySupplier<DataComponentType<String>> AMINO_ACIDS_COMPONENT = COMPONENT_TYPES.register(
            "amino_acids",
            () -> new DataComponentType<>() {
                @Override
                public Codec<String> codec() {
                    return Codec.STRING;
                }

                @Override
                public @NotNull StreamCodec<? super RegistryFriendlyByteBuf, String> streamCodec() {
                    return ByteBufCodecs.STRING_UTF8;
                }
            }
    );



    public static final RegistrySupplier<Block> PROTEIN_CONSTRUCTOR = BLOCKS.register(
            "protein_constructor",
            () -> new ProteinConstructorBlock(BlockBehaviour.Properties.of()
                    .destroyTime(0.1f)
                    .mapColor(MapColor.TERRACOTTA_GRAY)
                    .setId(ResourceKey.create(Registries.BLOCK, ResourceLocation.tryBuild(MOD_ID,"protein_constructor"))
    )));

    public static final RegistrySupplier<BlockEntityType<ProteinConstructorBlockEntity>> PROTEIN_CONSTRUCTOR_ENTITY = BLOCKS_ENTITY_TYPES.register("protein_constructor",
            () -> {
                Constructor<BlockEntityType> constructor;
                BlockEntityType<ProteinConstructorBlockEntity> type;
                try {
                    constructor = BlockEntityType.class.getDeclaredConstructor(BlockEntityType.BlockEntitySupplier.class, Set.class);
                    constructor.setAccessible(true);
                    type = constructor.newInstance((BlockEntityType.BlockEntitySupplier<BlockEntity>) ProteinConstructorBlockEntity::new, Set.of(PROTEIN_CONSTRUCTOR.get()));
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }

                return type;
            });

    public static final RegistrySupplier<MenuType<ProteinConstructorMenu>> PROTEIN_CONSTRUCTOR_MENU = MENUS.register("protein_constructor", () -> MenuRegistry.ofExtended(ProteinConstructorMenu::new));

    public static final RegistrySupplier<Item> PROTEIN_CONSTRUCTOR_ITEM = ITEMS.register(
            "protein_constructor",
            () -> new BlockItem(PROTEIN_CONSTRUCTOR.get(),
                    new Item.Properties()
                            .arch$tab(THE_TAB)
                            .setId(ResourceKey.create(Registries.ITEM, ResourceLocation.tryBuild(MOD_ID,"protein_constructor"))
    )));

    public static final RegistrySupplier<Item> SOAP = ITEMS.register("soap",
            () -> new Item(new Item.Properties()
                    .durability(128)
                    .stacksTo(1)
                    .food(new FoodProperties(-1,0,true))
                    .arch$tab(THE_TAB)
                    .setId(ResourceKey.create(Registries.ITEM, ResourceLocation.tryBuild(MOD_ID,"soap")))
            )
    );

    public static final RegistrySupplier<Item> PROTEIN = ITEMS.register("protein",
            () -> new AminoAcidContainingItem(new Item.Properties()
                    .food(new FoodProperties(1,0.2f,false))
                    .arch$tab(THE_TAB)
                    .component(AMINO_ACIDS_COMPONENT.get(), "MVHLTPEEKSAVTALWGKVNVDEVGGEALGRLLVVYPWTQRFFESFGDLSTPDAVMGNPKVKAHGKKVLGAFSDGLAHLDNLKGTFATLSELHCDKLHVDPENFRLLGNVLVCVLAHHFGKEFTPPVQAAYQKVVAGVANALAHKYH")
                    .setId(ResourceKey.create(Registries.ITEM, ResourceLocation.tryBuild(MOD_ID,"protein")))
            )
    );

    public static final List<RegistrySupplier<Item>> AMINO_ACIDS = register_amino_acids();

    static List<RegistrySupplier<Item>> register_amino_acids(){
        ArrayList<RegistrySupplier<Item>> acids = new ArrayList<>();
        StringBuilder transMsg = new StringBuilder();
        for (Character c : AMINO_ACID_CHARS){
            acids.add(
            ITEMS.register("amino_acid_"+String.valueOf(c).toLowerCase(),
                    () -> new Item(new Item.Properties()
                            .food(new FoodProperties(0,0.5f,false))
                            .arch$tab(THE_TAB)
                            .component(DataComponents.RARITY, Rarity.EPIC)
                            .setId(ResourceKey.create(Registries.ITEM, ResourceLocation.tryBuild(MOD_ID,"amino_acid_"+String.valueOf(c).toLowerCase())))
                    )
            )
            );
            transMsg.append("\"item." + MOD_ID + ".amino_acid_").append(String.valueOf(c).toLowerCase()).append("\": \"name\"\n");
        }
        LOGGER.warn(transMsg.toString());
        return acids;
    }

    public static final RegistrySupplier<RecipeBookCategory> PROTEIN_CONSTRUCTOR_RECIPE_CATEGORY = RECIPE_BOOK_CATEGORIES.register("protein_constructor",
            RecipeBookCategory::new
    );

    public static void init() {
        LOGGER.error(AMINO_ACIDS.toString());

        RECIPE_SERIALIZERS.register();
        RECIPE_TYPES.register();
        RECIPE_BOOK_CATEGORIES.register();
        TABS.register();
        COMPONENT_TYPES.register();
        BLOCKS.register();
        BLOCKS_ENTITY_TYPES.register();
        MENUS.register();
        if (Platform.isFabric()){
            MenuRegistry.registerScreenFactory(Dnacid.PROTEIN_CONSTRUCTOR_MENU.get(), ProteinConstructorScreen::new);
        }
        ITEMS.register();

        //MUTATION_EFFECTS.register();
    }
}