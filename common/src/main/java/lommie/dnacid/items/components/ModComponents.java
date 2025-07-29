package lommie.dnacid.items.components;

import com.mojang.serialization.Codec;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import lommie.dnacid.mutation.MutationEffect;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.item.alchemy.Potion;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.function.Supplier;

import static lommie.dnacid.Dnacid.LOGGER;
import static lommie.dnacid.Dnacid.MOD_ID;

public class ModComponents {
    public static final DeferredRegister<DataComponentType<?>> COMPONENT_TYPES =
            DeferredRegister.create(MOD_ID, Registries.DATA_COMPONENT_TYPE);

    static <T> RegistrySupplier<DataComponentType<T>> register(String name, Codec<T> codec, StreamCodec<? super RegistryFriendlyByteBuf, T> streamCodec){
        Supplier<DataComponentType<T>> factory = () -> new DataComponentType<>() {
            @Override
            public @Nullable Codec<T> codec() {
                return codec;
            }

            @Override
            public @NotNull StreamCodec<? super RegistryFriendlyByteBuf, T> streamCodec() {
                return streamCodec;
            }
        };
        return COMPONENT_TYPES.register(ResourceLocation.tryBuild(MOD_ID,name), factory);
    }

    public static final RegistrySupplier<DataComponentType<String>> AMINO_ACIDS_COMPONENT = COMPONENT_TYPES.register(
            "amino_acids",
            () -> new DataComponentType<>() {
                @Override
                public Codec<String> codec() {
                    return Codec.STRING;
                }

                @Override
                public @NotNull StreamCodec<? super RegistryFriendlyByteBuf, String> streamCodec() {
                    return ByteBufCodecs.STRING_UTF8;
                }
            }
    );

    public static final RegistrySupplier<DataComponentType<MutationEffect>> MUTATION_EFFECT_COMPONENT = COMPONENT_TYPES.register(
            "mutation_effect",
            () -> new DataComponentType<>() {
                @Override
                public Codec<MutationEffect> codec() {
                    return MutationEffect.CODEC;
                }

                @Override
                public @NotNull StreamCodec<? super RegistryFriendlyByteBuf, MutationEffect> streamCodec() {
                    return StreamCodec.of((b,e) -> e.encode(b),MutationEffect::decode);
                }
            }
    );

    public static final RegistrySupplier<DataComponentType<BacteriaData>> BACTERIA_DATA_COMPONENT = COMPONENT_TYPES.register(
            "bacteria_data",
            BacteriaDataComponentType::new
    );

    public static final RegistrySupplier<DataComponentType<Integer>> GAME_MODE_COMPONENT = register(
            "game_mode",
            Codec.INT,
            ByteBufCodecs.INT
    );

    public static Map<MobEffect, RegistrySupplier<DataComponentType<Integer>>> EFFECT_AMOUNT_COMPONENTS;

    private static Map<MobEffect, RegistrySupplier<DataComponentType<Integer>>> registerEffectAmountComponents() {
        if (EFFECT_AMOUNT_COMPONENTS != null){
            throw new RuntimeException("Why is this function being run twice?");
        }

        Map<MobEffect, RegistrySupplier<DataComponentType<Integer>>> out = new HashMap<>();
        for (Map.Entry<ResourceKey<MobEffect>, MobEffect> mobEffectEntry : BuiltInRegistries.MOB_EFFECT.entrySet()) {
            MobEffect mobEffect = mobEffectEntry.getValue();
            ResourceKey<MobEffect> key = mobEffectEntry.getKey();
            String name = "effect_amount_" + key.location().toString().replace(':', '_');
            LOGGER.error(name);
            out.put(mobEffect, register(
                    name,
                    Codec.INT,
                    StreamCodec.of(FriendlyByteBuf::writeInt,FriendlyByteBuf::readInt)
            ));
        }

        return out;
    }

    public static void register(){
        EFFECT_AMOUNT_COMPONENTS = registerEffectAmountComponents();
        COMPONENT_TYPES.register();
    }
}
