package lommie.dnacid.mixin;


import net.minecraft.core.registries.BuiltInRegistries;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(BuiltInRegistries.class)
public interface RegistriesAccessor {
    /*@Accessor("internalRegister")
    <T, R extends WritableRegistry<T>> R getInternalRegister(
            ResourceKey<? extends Registry<T>> resourceKey,
            R writableRegistry,
            BuiltInRegistries.RegistryBootstrap<T> registryBootstrap);*/

    /*@Accessor("registryBootstrap")
    static RegistryBootstrap<?> getRegistryBootstrap() {
        throw new UnsupportedOperationException("Mixin method");
    }*/
}