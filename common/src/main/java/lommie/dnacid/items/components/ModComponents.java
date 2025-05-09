package lommie.dnacid.items.components;

import com.mojang.serialization.Codec;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import lommie.dnacid.mutation.MutationEffect;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

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
                    return null;
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

    public static void register(){
        COMPONENT_TYPES.register();
    }
}
