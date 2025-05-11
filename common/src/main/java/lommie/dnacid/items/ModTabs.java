package lommie.dnacid.items;

import dev.architectury.registry.CreativeTabRegistry;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import lommie.dnacid.Dnacid;
import lommie.dnacid.items.components.ModComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

import static lommie.dnacid.Dnacid.MOD_ID;

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
                            /*.displayItems((p, o) -> p.holders().get(Dnacid.MUTATION_EFFECT_TYPE_KEY).ifPresent((r) -> {
                                for (Map.Entry<ResourceKey<MutationEffectType>, MutationEffectType> mutationEffectTypeEntry : r.value().entrySet()){
                                    ItemStack stack = new ItemStack(ModItems.PLASMID.get());
                                    stack.set(ModComponents.MUTATION_EFFECT_COMPONENT.get(),mutationEffectTypeEntry.getValue().defaultEffect());
                                    o.accept(stack);
                                }}))*/
                            .displayItems((p,o) ->
                                    Dnacid.MUTATION_EFFECT_TYPE_REGISTRY.entrySet().forEach((e) ->{
                                        ItemStack stack = new ItemStack(ModItems.PLASMID.get());
                                        stack.set(ModComponents.MUTATION_EFFECT_COMPONENT.get(),e.getValue().defaultEffect());
                                        o.accept(stack);
                                    })).build()
            )
    );

    public static void register(){
        TABS.register();
    }
}
