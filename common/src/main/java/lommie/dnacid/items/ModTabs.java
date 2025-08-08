package lommie.dnacid.items;

import dev.architectury.registry.CreativeTabRegistry;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import lommie.dnacid.Dnacid;
import lommie.dnacid.ModRegistries;
import lommie.dnacid.items.components.ModComponents;
import lommie.dnacid.protein.Protein;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

import java.util.List;

import static lommie.dnacid.Dnacid.MOD_ID;
import static lommie.dnacid.ModRegistries.MUTATION_EFFECT_TYPE_REGISTRY;

public class ModTabs {
    public static final DeferredRegister<CreativeModeTab> TABS =
            DeferredRegister.create(MOD_ID, Registries.CREATIVE_MODE_TAB);

    public static final RegistrySupplier<CreativeModeTab> THE_TAB = TABS.register(
            "the_tab", // Tab ID
            () -> CreativeTabRegistry.create(
                    Component.translatable("category."+MOD_ID), // Tab Name
                    () -> new ItemStack(ModItems.PETRI_DISH.get()) // Icon
            )
    );

    public static final RegistrySupplier<CreativeModeTab> AMINO_ACIDS_TAB = TABS.register(
            "amino_acids", // Tab ID
            () -> CreativeTabRegistry.create(
                    Component.translatable("category."+MOD_ID+".amino_acids"), // Tab Name
                    () -> new ItemStack(ModItems.AMINO_ACIDS.getFirst().get()) // Icon
            )
    );

    public static final RegistrySupplier<CreativeModeTab> PLASMIDS_TAB = TABS.register(
            "plasmids", // Tab ID
            () -> CreativeTabRegistry.create(
                    (b) -> b.title(Component.translatable("category."+MOD_ID+".plasmids"))
                            .icon(() -> new ItemStack(ModItems.PLASMID.get()))
                            .displayItems((p,o) -> {
                                ModRegistries.PROTEINS_REGISTRY.entrySet().forEach((proteinEntry) -> {
                                    ItemStack stack = new ItemStack(ModItems.PLASMID.get());
                                    stack.set(ModComponents.PROTEIN.get(), proteinEntry.getValue());
                                    o.accept(stack);
                                });
                                MUTATION_EFFECT_TYPE_REGISTRY.entrySet().forEach((e) -> {
                                    if (e.getValue().defaultEffect().isEmpty()) return;
                                    ItemStack stack = new ItemStack(ModItems.PLASMID.get());
                                    stack.set(ModComponents.PROTEIN.get(), new Protein(List.of(e.getValue().defaultEffect().get()), "UUUUUUUUUUUUU"));
                                    o.accept(stack);
                                });
                            }).build()
            )
    );

    public static void register(){
        TABS.register();
    }
}
