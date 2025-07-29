package lommie.dnacid.screens;

import dev.architectury.registry.menu.MenuRegistry;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.MenuType;

import static lommie.dnacid.Dnacid.MOD_ID;

public class ModMenus {
    public static final DeferredRegister<MenuType<?>> MENUS =
            DeferredRegister.create(MOD_ID, Registries.MENU);

    public static void register(){
        MENUS.register();
    }
}
