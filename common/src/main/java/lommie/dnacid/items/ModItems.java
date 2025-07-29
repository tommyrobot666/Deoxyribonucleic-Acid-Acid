package lommie.dnacid.items;

import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import lommie.dnacid.Dnacid;
import lommie.dnacid.items.components.ModComponents;
import lommie.dnacid.mutation.ModMutations;
import lommie.dnacid.mutation.MutationEffect;
import lommie.dnacid.protein.Protein;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.*;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import static lommie.dnacid.Dnacid.MOD_ID;
import static lommie.dnacid.items.ModTabs.*;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(MOD_ID, Registries.ITEM);

    public static RegistrySupplier<Item> register(String name, Function<Item.Properties, Item> factory, Item.Properties settings){
        ResourceKey<Item> key = ResourceKey.create(Registries.ITEM, ResourceLocation.tryBuild(MOD_ID,name));
        return ITEMS.register(key.location(), () -> factory.apply(settings.setId(key)));
    }

    public static final RegistrySupplier<Item> SOAP = register("soap", Item::new,
            new Item.Properties()
                    .durability(128)
                    .stacksTo(1)
                    .food(new FoodProperties(-1,0,true))
                    .arch$tab(THE_TAB)
                    .setId(ResourceKey.create(Registries.ITEM, ResourceLocation.tryBuild(MOD_ID,"soap")))
    );

    public static final RegistrySupplier<Item> BACTERIA = register("bacteria", BacteriaItem::new ,
            new Item.Properties()
                    .food(new FoodProperties(1,1,false))
                    .stacksTo(1)
                    .arch$tab(THE_TAB)
                    .setId(ResourceKey.create(Registries.ITEM, ResourceLocation.tryBuild(MOD_ID,"bacteria")))


    );

    public static final RegistrySupplier<Item> PETRI_DISH = register("petri_dish", Item::new,
            new Item.Properties()
                    .arch$tab(THE_TAB)
                    .setId(ResourceKey.create(Registries.ITEM, ResourceLocation.tryBuild(MOD_ID,"petri_dish")))

    );

    public static final RegistrySupplier<Item> PLASMID = register("plasmid", PlasmidItem::new,
            new Item.Properties()
                    .component(ModComponents.MUTATION_EFFECT_COMPONENT.get(),new MutationEffect(ModMutations.TEST_MUTATION_EFFECT_TYPE::get,100))
                    .arch$tab(PLASMIDS_TAB)
                    .setId(ResourceKey.create(Registries.ITEM, ResourceLocation.tryBuild(MOD_ID,"plasmid")))

    );

    public static final RegistrySupplier<Item> PROTEIN = register("protein", (s) -> new ProteinItem(s,new Protein(List.of(ModMutations.POTION_MUTATION_EFFECT_TYPES.get(MobEffects.ABSORPTION.value()).get().defaultEffect().get()),"MVHLTPEEKSAVTALWGKVNVDEVGGEALGRLLVVYPWTQRFFESFGDLSTPDAVMGNPKVKAHGKKVLGAFSDGLAHLDNLKGTFATLSELHCDKLHVDPENFRLLGNVLVCVLAHHFGKEFTPPVQAAYQKVVAGVANALAHKYH")),
            new Item.Properties()
                    .food(new FoodProperties(1,0.2f,false))
                    .arch$tab(THE_TAB)
                    .component(ModComponents.AMINO_ACIDS_COMPONENT.get(), "MVHLTPEEKSAVTALWGKVNVDEVGGEALGRLLVVYPWTQRFFESFGDLSTPDAVMGNPKVKAHGKKVLGAFSDGLAHLDNLKGTFATLSELHCDKLHVDPENFRLLGNVLVCVLAHHFGKEFTPPVQAAYQKVVAGVANALAHKYH")
                    .setId(ResourceKey.create(Registries.ITEM, ResourceLocation.tryBuild(MOD_ID,"protein")))

    );

    public static final List<RegistrySupplier<Item>> AMINO_ACIDS = register_amino_acids();

    static List<RegistrySupplier<Item>> register_amino_acids(){
        ArrayList<RegistrySupplier<Item>> acids = new ArrayList<>();
        //StringBuilder transMsg = new StringBuilder();
        for (Character c : Dnacid.AMINO_ACID_CHARS){
            acids.add(
                    register("amino_acid_"+String.valueOf(c).toLowerCase(),Item::new,
                            new Item.Properties()
                                    .food(new FoodProperties(0,0.5f,false))
                                    .arch$tab(AMINO_ACIDS_TAB)
                                    .component(DataComponents.RARITY, Rarity.EPIC)
                                    .setId(ResourceKey.create(Registries.ITEM, ResourceLocation.tryBuild(MOD_ID,"amino_acid_"+String.valueOf(c).toLowerCase())))
                            )

            );
            //transMsg.append("\"item." + MOD_ID + ".amino_acid_").append(String.valueOf(c).toLowerCase()).append("\": \"name\"\n");
        }
        //LOGGER.warn(transMsg.toString());
        return acids;
    }

    public static void register(){
        ITEMS.register();
    }
}
