package lommie.dnacid.mixin;


import net.minecraft.core.Registry;
import net.minecraft.core.WritableRegistry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

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