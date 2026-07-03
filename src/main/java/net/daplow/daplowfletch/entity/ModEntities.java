package net.daplow.daplowfletch.entity;

import net.daplow.daplowfletch.DaplowFletch;
import net.daplow.daplowfletch.item.custom.OpArrowItemEntityOLD;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class ModEntities {

    // For Shooting the arrow...  The shot arrow is a entity
    public static final EntityType<OpArrowItemEntityOLD> OP_ARROW_ENTITY =
            Registry.register(
                    Registry.ENTITY_TYPE,
                    new Identifier(DaplowFletch.MOD_ID, "op_arrow"),
                    FabricEntityTypeBuilder
                            .<OpArrowItemEntityOLD>create(SpawnGroup.MISC, OpArrowItemEntityOLD::new)
                            .dimensions(EntityDimensions.fixed(0.5F, 0.5F)) // size of arrow
                            .trackRangeBlocks(64) // client tracking range
                            .trackedUpdateRate(10) // update rate
                            .build()

            );


    public static void register() {
        //System.out.println("Registered Mod Entities!");



    }
}