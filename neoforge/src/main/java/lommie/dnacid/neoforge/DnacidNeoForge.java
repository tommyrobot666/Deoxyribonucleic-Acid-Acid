package lommie.dnacid.neoforge;

import lommie.dnacid.Dnacid;
import lommie.dnacid.ModRegistries;
import lommie.dnacid.client.DnacidClient;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.neoforged.fml.common.Mod;

import java.lang.reflect.Field;


@Mod(Dnacid.MOD_ID)
public final class DnacidNeoForge {
    public DnacidNeoForge() {
        // Run our common setup.
        Field writableRegistryField;
        try {
            writableRegistryField = BuiltInRegistries.class.getDeclaredField("WRITABLE_REGISTRY");
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
        writableRegistryField.setAccessible(true);
        Registry<?> writableRegistry = null;
        try {
            writableRegistry = (Registry<?>) writableRegistryField.get(null);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        forceUnfreezeRegistry(writableRegistry);
        ModRegistries.register();
        forceFreezeRegistry(writableRegistry);
        Dnacid.init();
        DnacidClient.init();
    }

    public static <T> void forceUnfreezeRegistry(Registry<T> registry) {
        try {
            Field frozenField = MappedRegistry.class.getDeclaredField("frozen");
            frozenField.setAccessible(true);
            frozenField.setBoolean(registry, false);
        } catch (Exception e) {
            throw new RuntimeException("Failed to unfreeze registry", e);
        }
    }

    public static <T> void forceFreezeRegistry(Registry<T> registry) {
        try {
            Field frozenField = MappedRegistry.class.getDeclaredField("frozen");
            frozenField.setAccessible(true);
            frozenField.setBoolean(registry, true);
        } catch (Exception e) {
            throw new RuntimeException("Failed to unfreeze registry", e);
        }
    }
}
