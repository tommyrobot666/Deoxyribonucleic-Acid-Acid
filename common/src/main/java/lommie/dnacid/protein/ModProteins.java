package lommie.dnacid.protein;

import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import lommie.dnacid.ModRegistries;
import lommie.dnacid.mutation.ModMutations;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffects;

import java.util.List;

import static lommie.dnacid.Dnacid.MOD_ID;

public class ModProteins {
    public static final DeferredRegister<Protein> PROTEINS =
            DeferredRegister.create(MOD_ID, ModRegistries.PROTEINS_KEY);

    public static RegistrySupplier<Protein> register(String name, Protein protein){
        ResourceLocation id = ResourceLocation.tryBuild(MOD_ID,name);
        Protein newProtein = new Protein(protein.effects, protein.aminoAcids, id);
        return PROTEINS.register(id, () -> newProtein);
    }

    public static final RegistrySupplier<Protein> TEST =
            register("test", new Protein(
                    List.of(
                            ModMutations.TEST_MUTATION_EFFECT_TYPE.get().newEffectWith(10,null),
                            ModMutations.POTION_MUTATION_EFFECT_TYPES.get(MobEffects.GLOWING.value()).get().newEffectWith(100,null)
                    ),
                    "AAAAAAAAAAAAAAAAAAAAAAAAAA"));

    public static void register(){
        PROTEINS.register();
    }
}
