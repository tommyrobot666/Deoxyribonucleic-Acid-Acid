package lommie.dnacid.mutation;

import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.GameType;

import java.util.function.Supplier;

import static lommie.dnacid.Dnacid.MOD_ID;
import static lommie.dnacid.Dnacid.MUTATION_EFFECT_TYPE_KEY;

public class ModMutations {
    public static final DeferredRegister<MutationEffectType<?>> MUTATION_EFFECT_TYPES =
            DeferredRegister.create(MOD_ID, MUTATION_EFFECT_TYPE_KEY);

//    public static RegistrySupplier<MutationEffectType<?>> register(String name, Function<MutationEffectType.Settings, MutationEffectType<?>> factory, MutationEffectType.Settings settings){
//        ResourceKey<MutationEffectType<?>> key = ResourceKey.create(MUTATION_EFFECT_TYPE_KEY, ResourceLocation.tryBuild(MOD_ID,name));
//        return MUTATION_EFFECT_TYPES.register(key.location(), () -> factory.apply(settings.setId(key)));
//    }

    public static RegistrySupplier<MutationEffectType<?>> otherRegister(String name, Supplier<MutationEffectType<?>> effect){
        return MUTATION_EFFECT_TYPES.register(ResourceLocation.tryBuild(MOD_ID,name), effect);
    }

    public static final RegistrySupplier<MutationEffectType<?>> TEST_MUTATION_EFFECT_TYPE = MUTATION_EFFECT_TYPES.register(
            "test",
            () -> new MutationEffectType<>(
                    Component.literal("Test"),
                    () -> new TestMutationEffect(-1, GameType.SPECTATOR),
                    TestMutationEffect.STREAM_CODEC,
                    ResourceKey.create(MUTATION_EFFECT_TYPE_KEY, ResourceLocation.tryBuild(MOD_ID,"test")))
    );

    public static void register(){
        MUTATION_EFFECT_TYPES.register();
    }
}
