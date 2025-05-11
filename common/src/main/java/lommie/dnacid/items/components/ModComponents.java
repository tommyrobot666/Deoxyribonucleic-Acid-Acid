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

    public static Map<Potion, RegistrySupplier<DataComponentType<Integer>>> POTION_COMPONENTS;

    private static Map<Potion, RegistrySupplier<DataComponentType<Integer>>> registerPotionComponents() {
        if (POTION_COMPONENTS != null){
            return POTION_COMPONENTS;
        }

        Map<Potion, RegistrySupplier<DataComponentType<Integer>>> out = new HashMap<>();
        HashSet<String> imGoingCrazy = new HashSet<>();
        for (Map.Entry<ResourceKey<Potion>, Potion> potionEntry : BuiltInRegistries.POTION.entrySet()) {
            Potion potion = potionEntry.getValue();
            ResourceKey<Potion> key = potionEntry.getKey();
            String name = key.location().toString().replace(':', '_');//key.location().getNamespace()+"_"+potion.name();//+key.location().getPath();
            LOGGER.error(name);
            if (imGoingCrazy.contains(potion.name().substring(0,4))){
                continue;
            }
            imGoingCrazy.add(potion.name().substring(0,4));

            // Skip duplicates by registry key
//            if (out.keySet().stream().anyMatch(p -> p.name().equals(key.location().getNamespace()+"_"+key.location().getPath()))) {continue;}
            if (COMPONENT_TYPES.getRegistrar().contains(ResourceLocation.tryBuild(MOD_ID,name))) {continue;}

            // Create the codec for Pair<Integer, Holder<Potion>>
//            Codec<Pair<Integer, Holder<Potion>>> codec = Codec.INT.xmap(
//                    i -> new ImmutablePair<>(i, Holder.direct(potion)),
//                    Pair::getLeft
//            );

            // Define the StreamCodec with explicit type parameters
//            StreamCodec<FriendlyByteBuf, Pair<Integer, Holder<Potion>>> streamCodec = StreamCodec.of(
//                    // Encoder
//                    (FriendlyByteBuf buf, Pair<Integer, Holder<Potion>> pair) -> {
//                        buf.writeInt(pair.getLeft());  // Write the Integer part of the Pair
//                        buf.writeResourceLocation(BuiltInRegistries.POTION.getKey(pair.getRight().value()));  // Write the ResourceLocation
//                    },
//                    // Decoder
//                    buf -> {
//                        int amount = buf.readInt();  // Read the Integer part
//                        ResourceLocation id = buf.readResourceLocation();  // Read the ResourceLocation
//                        Optional<Holder.Reference<Potion>> readPotion = BuiltInRegistries.POTION.get(id);
//                        if (readPotion.isPresent()) {
//                            return new ImmutablePair<>(amount, readPotion.get());  // Return the Pair
//                        } else {
//                            throw new IllegalStateException("Potion not found: " + id);  // Handle the case where the potion doesn't exist
//                        }
//                    }
//            );

            // Register the components
            out.put(potion, register(
                    name,
                    Codec.INT,
                    StreamCodec.of(FriendlyByteBuf::writeInt,FriendlyByteBuf::readInt)
            ));
        }

        return out;
    }

    public static void register(){
        POTION_COMPONENTS = registerPotionComponents();
        COMPONENT_TYPES.register();
    }
}
