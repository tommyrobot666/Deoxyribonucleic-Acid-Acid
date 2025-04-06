package lommie.dnacid.fabric.datageneration;

import lommie.dnacid.Dnacid;
import net.fabricmc.fabric.api.client.datagen.v1.provider.FabricModelProvider;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.model.ModelTemplates;

public class DataGenerator implements DataGeneratorEntrypoint {
    @Override
    public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
        FabricDataGenerator.Pack p = fabricDataGenerator.createPack();
        p.addProvider(ModelProvider::new);
    }

    static class ModelProvider extends FabricModelProvider{

        public ModelProvider(FabricDataOutput output) {
            super(output);
        }

        @Override
        public void generateBlockStateModels(BlockModelGenerators g) {
            g.createTrivialCube(Dnacid.PROTEIN_CONSTRUCTOR.get());
        }

        @Override
        public void generateItemModels(ItemModelGenerators g) {
            g.createFlatItemModel(Dnacid.SOAP.get(), ModelTemplates.FLAT_ITEM);
        }
    }
}
