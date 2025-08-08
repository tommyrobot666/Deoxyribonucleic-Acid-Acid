package lommie.dnacid;

import com.mojang.serialization.Lifecycle;
import dev.architectury.platform.Platform;
import lommie.dnacid.mutation.MutationEffectType;
import lommie.dnacid.protein.Protein;
import net.minecraft.core.DefaultedMappedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.WritableRegistry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static lommie.dnacid.Dnacid.MOD_ID;

public class ModRegistries {
    private static <T> void registerRegistry(ResourceKey<Registry<T>> key, WritableRegistry<T> reg) {
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
            internalRegister.invoke(null,key,reg,null);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    public static final ResourceKey<Registry<MutationEffectType>> MUTATION_EFFECT_TYPE_KEY =
            ResourceKey.createRegistryKey(ResourceLocation.tryBuild(MOD_ID, "mutation_effects"));

    public static final DefaultedMappedRegistry<MutationEffectType> MUTATION_EFFECT_TYPE_REGISTRY =
            new DefaultedMappedRegistry<>(MOD_ID+":test", MUTATION_EFFECT_TYPE_KEY, Lifecycle.stable(),false);


    public static final ResourceKey<Registry<Protein>> PROTEINS_KEY =
            ResourceKey.createRegistryKey(ResourceLocation.tryBuild(MOD_ID,"proteins"));

    public static final DefaultedMappedRegistry<Protein> PROTEINS_REGISTRY =
            new DefaultedMappedRegistry<>(MOD_ID+":test",PROTEINS_KEY,Lifecycle.stable(),false);

    public static void register(){
        registerRegistry(MUTATION_EFFECT_TYPE_KEY,MUTATION_EFFECT_TYPE_REGISTRY);
        registerRegistry(PROTEINS_KEY,PROTEINS_REGISTRY);
    }
}
