package net.daplow.daplowfletch.block;

import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class ModBlockEntities {

    public static BlockEntityType<FletchingTableBlockEntity> FLETCHING_TABLE_BE;

    public static void register() {
        FLETCHING_TABLE_BE = Registry.register(
                Registry.BLOCK_ENTITY_TYPE,
                new Identifier("daplowfletch", "fletching_table"),
                FabricBlockEntityTypeBuilder
                        .create(FletchingTableBlockEntity::new, Blocks.FLETCHING_TABLE)
                        .build()
        );
    }
}