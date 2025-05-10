package lommie.dnacid.mutation;

import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import lommie.dnacid.items.components.ModComponents;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.level.GameType;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

import static lommie.dnacid.Dnacid.MOD_ID;
import static lommie.dnacid.Dnacid.MUTATION_EFFECT_TYPE_KEY;

public class ModMutations {
    public static final DeferredRegister<MutationEffectType> MUTATION_EFFECT_TYPES =
            DeferredRegister.create(MOD_ID, MUTATION_EFFECT_TYPE_KEY);

    public static RegistrySupplier<MutationEffectType> register(String name, Function<MutationEffectType.Settings, MutationEffectType> factory, MutationEffectType.Settings settings){
        ResourceKey<MutationEffectType> key = ResourceKey.create(MUTATION_EFFECT_TYPE_KEY, ResourceLocation.tryBuild(MOD_ID,name));
        return MUTATION_EFFECT_TYPES.register(key.location(), () -> factory.apply(settings.setId(key)));
    }

    public static final RegistrySupplier<MutationEffectType> TEST_MUTATION_EFFECT_TYPE = register(
            "test",
            (s) -> new TestMutationEffectType(s,GameType.SPECTATOR),
            new MutationEffectType.Settings()
                    .name("Test")
                    .defaultEffect(() -> new MutationEffect(Objects.requireNonNull(ResourceLocation.tryBuild(MOD_ID, "test")),-1))
    );

    public static Map<Potion, RegistrySupplier<MutationEffectType>> POTION_MUTATION_EFFECT_TYPES;

    private static Map<Potion, RegistrySupplier<MutationEffectType>> registerPotionEffectTypes() {
        HashMap<Potion, RegistrySupplier<MutationEffectType>> out = new HashMap<>();
        for(Potion potion : ModComponents.POTION_COMPONENTS.keySet()){
            out.put(potion,register(potion.name(),
                    (s) -> new PotionMutationEffectType(s, potion),
                    new MutationEffectType.Settings()));
        }
        return out;
    }

    public static void register(){
        POTION_MUTATION_EFFECT_TYPES = registerPotionEffectTypes();
        MUTATION_EFFECT_TYPES.register();
    }
}
