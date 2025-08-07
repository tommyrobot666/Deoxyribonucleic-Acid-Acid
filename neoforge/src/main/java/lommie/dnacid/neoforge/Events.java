package lommie.dnacid.neoforge;

import lommie.dnacid.Dnacid;
import lommie.dnacid.ModRegistries;
import lommie.dnacid.client.DnacidClient;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.registries.NewRegistryEvent;
import net.neoforged.neoforge.registries.RegisterEvent;

@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD)
public class Events {
    @SubscribeEvent
    static void onNewRegister(NewRegistryEvent event){
//        event.register(ModRegistries.MUTATION_EFFECT_TYPE_REGISTRY);
//        Dnacid.init();
//        DnacidClient.init();
    }

//    @SubscribeEvent
//    static void onFMLCommonSetupEvent(FMLCommonSetupEvent event){
//        Dnacid.init();
//        DnacidClient.init();
//    }
}
