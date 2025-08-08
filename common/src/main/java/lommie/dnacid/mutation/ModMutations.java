package lommie.dnacid.mutation;

import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.level.GameType;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

import static lommie.dnacid.Dnacid.MOD_ID;
import static lommie.dnacid.ModRegistries.MUTATION_EFFECT_TYPE_KEY;

public class ModMutations {
    public static final DeferredRegister<MutationEffectType> MUTATION_EFFECT_TYPES =
            DeferredRegister.create(MOD_ID, MUTATION_EFFECT_TYPE_KEY);

    public static RegistrySupplier<MutationEffectType> register(@NotNull String name, Function<MutationEffectType.Settings, MutationEffectType> factory, MutationEffectType.Settings settings){
        ResourceKey<MutationEffectType> key = ResourceKey.create(MUTATION_EFFECT_TYPE_KEY, ResourceLocation.tryBuild(MOD_ID,name));
        // STOP GIVING ME A FUCKING WARNING, THE GAME HAS CRASHED BECAUSE IT CAN BE NULL
        if (key.location() == null){
            throw new RuntimeException(name + "made a null location, because of \"L+Ratio+Bozo\"");
        }
        return MUTATION_EFFECT_TYPES.register(key.location(), () -> factory.apply(settings.setId(key)));
    }

    public static final RegistrySupplier<MutationEffectType> TEST_MUTATION_EFFECT_TYPE = register(
            "test",
            (s) -> new TestMutationEffectType(s,GameType.SPECTATOR),
            new MutationEffectType.Settings()
                    .name("Test")
                    .defaultEffect(() -> java.util.Optional.of(new MutationEffect(Objects.requireNonNull(ResourceLocation.tryBuild(MOD_ID, "test")), -1)))
    );

    public static Map<MobEffect, RegistrySupplier<MutationEffectType>> POTION_MUTATION_EFFECT_TYPES;

    private static Map<MobEffect, RegistrySupplier<MutationEffectType>> registerMobEffectTypes() {
        HashMap<MobEffect, RegistrySupplier<MutationEffectType>> out = new HashMap<>();
        for(Map.Entry<ResourceKey<MobEffect>, MobEffect> mobEffectEntry : BuiltInRegistries.MOB_EFFECT.entrySet()){
            MobEffect mobEffect = mobEffectEntry.getValue();
            String name = mobEffectEntry.getKey().location().toString().replace(":","_"); // caused the "null" crash by making "tryBuild" fail
            out.put(mobEffect,register(name,
                    (s) -> new MobEffectMutationEffectType(s, mobEffect),
                    new MutationEffectType.Settings()
                            .defaultEffect(() -> java.util.Optional.of(new MutationEffect(() -> POTION_MUTATION_EFFECT_TYPES.get(mobEffect).get(), 100)))
                            .name(name)));
        }
        return out;
    }

    public static void register(){
        POTION_MUTATION_EFFECT_TYPES = registerMobEffectTypes();
        MUTATION_EFFECT_TYPES.register();
    }
}
