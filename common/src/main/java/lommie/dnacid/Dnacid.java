package lommie.dnacid;


import com.mojang.serialization.Lifecycle;
import dev.architectury.event.events.common.LifecycleEvent;
import dev.architectury.event.events.common.PlayerEvent;
import dev.architectury.networking.NetworkManager;
import dev.architectury.networking.transformers.PacketSink;
import lommie.dnacid.blocks.ModBlocks;
import lommie.dnacid.items.ModItems;
import lommie.dnacid.items.ModTabs;
import lommie.dnacid.items.components.ModComponents;
import lommie.dnacid.mixin.RecipeManagerAccessor;
import lommie.dnacid.mutation.ModMutations;
import lommie.dnacid.mutation.MutationEffectType;
import lommie.dnacid.network.ProteinConstructorRecipeDisplayEntriesPacket;
import lommie.dnacid.recipe.ModRecipes;
import lommie.dnacid.recipe.ProteinConstructorRecipe;
import lommie.dnacid.screens.*;
import net.minecraft.core.DefaultedMappedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.WritableRegistry;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.item.crafting.display.RecipeDisplayEntry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;


public final class Dnacid {
    public static final String MOD_ID = "dnacid";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static final List<Character> AMINO_ACID_CHARS = List.of('A','R','N','D','C','Q','E','G','H','I','L','K','M','F','P','S','T','W','Y','V');
    public static List<RecipeDisplayEntry> proteinConstructorRecipeDisplayEntries = List.of();
    public void setProteinConstructorRecipeDisplayEntries(List<RecipeDisplayEntry> ls){
        proteinConstructorRecipeDisplayEntries = ls;
    }

    public static final ResourceKey<Registry<MutationEffectType<?>>> MUTATION_EFFECT_TYPE_KEY =
            ResourceKey.createRegistryKey(ResourceLocation.tryBuild(MOD_ID, "mutation_effects"));

    public static final DefaultedMappedRegistry<MutationEffectType<?>> MUTATION_EFFECT_TYPE_REGISTRY =
            new DefaultedMappedRegistry<>(MOD_ID+":test", MUTATION_EFFECT_TYPE_KEY, Lifecycle.stable(),false);


    public static void init() {
        registerMutationEffectTypeRegistry();
        ModTabs.register();
        ModComponents.register();
        ModItems.register();
        ModBlocks.register();
        ModMutations.register();
        ModRecipes.register();
        ModMenus.register();


        LifecycleEvent.SERVER_STARTED.register((server) -> {
            ArrayList<ResourceKey<Recipe<?>>> keys = new ArrayList<>();
            server.getRecipeManager().getRecipes().forEach((i) -> {
                if (i.value() instanceof ProteinConstructorRecipe) {
                    LOGGER.error("Loaded Recipe: {}", i.id());
                    keys.add(i.id());
                }
                    }
            );
            ArrayList<RecipeDisplayEntry> recipeDisplayEntries = new ArrayList<>();
            ((RecipeManagerAccessor) server.getRecipeManager()).getAllDisplays().stream().forEach((sdi) -> {
                if (keys.contains(sdi.parent().id())) {
                    recipeDisplayEntries.add(sdi.display());
                }
            });
            Dnacid.proteinConstructorRecipeDisplayEntries = recipeDisplayEntries;
            LOGGER.error("server ent:");
            recipeDisplayEntries.forEach(i -> LOGGER.error("{}", i));
        });

        PlayerEvent.PLAYER_JOIN.register((e) -> {
            ArrayList<ResourceKey<Recipe<?>>> proteinConstructorRecipeIds = new ArrayList<>();
            for (RecipeDisplayEntry entry : proteinConstructorRecipeDisplayEntries) {
                ((RecipeManagerAccessor) e.getServer().getRecipeManager()).getAllDisplays().forEach(i -> {
                    if (i.display() == entry) {
                        proteinConstructorRecipeIds.add(i.parent().id());
                    }
                });
            }
            proteinConstructorRecipeIds.forEach(i -> {
                if (!e.getRecipeBook().contains(i)) {
                    e.getRecipeBook().add(i);
                }
            });
            LOGGER.warn("on join:");
            proteinConstructorRecipeDisplayEntries.forEach(i -> LOGGER.warn(i.toString()));
            NetworkManager.collectPackets(PacketSink.ofPlayer(e), NetworkManager.serverToClient(), new ProteinConstructorRecipeDisplayEntriesPacket.PacketPayload(proteinConstructorRecipeDisplayEntries), e.registryAccess());
        });
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