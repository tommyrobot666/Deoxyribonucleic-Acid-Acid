package lommie.dnacid;


import com.mojang.serialization.Lifecycle;
import lommie.dnacid.blocks.ModBlocks;
import lommie.dnacid.items.ModItems;
import lommie.dnacid.items.ModTabs;
import lommie.dnacid.items.components.ModComponents;
import lommie.dnacid.mutation.ModMutations;
import lommie.dnacid.mutation.MutationEffectType;
import lommie.dnacid.screens.*;
import net.minecraft.core.DefaultedMappedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.WritableRegistry;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;


public final class Dnacid {
    public static final String MOD_ID = "dnacid";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static final List<Character> AMINO_ACID_CHARS = List.of('A','R','N','D','C','Q','E','G','H','I','L','K','M','F','P','S','T','W','Y','V');

    public static final ResourceKey<Registry<MutationEffectType>> MUTATION_EFFECT_TYPE_KEY =
            ResourceKey.createRegistryKey(ResourceLocation.tryBuild(MOD_ID, "mutation_effects"));

    public static final DefaultedMappedRegistry<MutationEffectType> MUTATION_EFFECT_TYPE_REGISTRY =
            new DefaultedMappedRegistry<>(MOD_ID+":test", MUTATION_EFFECT_TYPE_KEY, Lifecycle.stable(),false);


    public static void init() {
        registerMutationEffectTypeRegistry();
        ModComponents.register();
        ModItems.register();
        ModBlocks.register();
        ModMutations.register();
        ModTabs.register();
        ModMenus.register();
    }

    private static void registerMutationEffectTypeRegistry() {
        Class<?> registryBootstrapClass;
        try {
            registryBootstrapClass = Class.forName("net.minecraft.core.registries.BuiltInRegistries$RegistryBootstrap");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        Method internalRegister;
        try {
            internalRegister = BuiltInRegistries.class.getDeclaredMethod("internalRegister", ResourceKey.class, WritableRegistry.class, registryBootstrapClass);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
        internalRegister.setAccessible(true);
        try {
            internalRegister.invoke(null,MUTATION_EFFECT_TYPE_KEY,MUTATION_EFFECT_TYPE_REGISTRY,null);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }
}