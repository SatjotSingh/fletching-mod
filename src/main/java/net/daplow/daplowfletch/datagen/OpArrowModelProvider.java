package net.daplow.daplowfletch.datagen;

import net.daplow.daplowfletch.item.ModItems;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;
import net.minecraft.data.client.*;
import net.minecraft.util.Identifier;

public class OpArrowModelProvider extends FabricModelProvider {

    public OpArrowModelProvider(FabricDataGenerator generator) {
        super(generator);
    }

    @Override
    public void generateBlockStateModels(BlockStateModelGenerator blockStateModelGenerator) {

    }
    public class ModTextureKeys {
        public static final TextureKey LAYER1 = TextureKey.of("layer1");
        public static final TextureKey LAYER2 = TextureKey.of("layer2");
        public static final TextureKey LAYER3 = TextureKey.of("layer3");
    }


    @Override
    public void generateItemModels(ItemModelGenerator gen) {
        int totalVariants = 2 * 2 * 10; // addon1 0-1, addon2 0-1, addon3 0-9
        int index = 0;

        for (int addon1 = 0; addon1 <= 1; addon1++) {
            for (int addon2 = 0; addon2 <= 1; addon2++) {
                for (int addon3 = 0; addon3 < 10; addon3++) {

                    // Name matches registration
                    String itemName = "op_arrow_" + index;

                    // Prepare texture map
                    TextureMap textures = new TextureMap()
                            .put(TextureKey.LAYER0, new Identifier("daplow-fletch", "item/op_arrow"))
                            .put(TextureKey.of("layer1"), addon1 == 1
                                    ? new Identifier("daplow-fletch", "item/op_arrow_stem")
                                    : new Identifier("daplow-fletch", "item/no_addon"))
                            .put(TextureKey.of("layer3"), addon2 == 1
                                    ? new Identifier("daplow-fletch", "item/tipped_arrow_head")
                                    : new Identifier("daplow-fletch", "item/no_addon"))
                            .put(TextureKey.of("layer2"), new Identifier("daplow-fletch", "item/op_arrow_addon_" + addon3));

                    // Upload the model
                    Models.GENERATED.upload(
                            new Identifier("daplow-fletch", "item/" + itemName),
                            textures,
                            gen.writer
                    );

                    index++;
                }
            }
        }
    }

}