package net.daplow.daplowfletch.item.custom;

import net.daplow.daplowfletch.entity.ModEntities;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class NewOpArrowItemEntity extends ArrowEntity {
    public NewOpArrowItemEntity(EntityType<? extends ArrowEntity> entityType, World world) {
        super(entityType, world);
    }

    public NewOpArrowItemEntity(World world, LivingEntity owner, ItemStack stack) {
        super((EntityType<? extends ArrowEntity>) ModEntities.OP_ARROW_ENTITY, world);
        this.setOwner(owner);
        this.setPosition(owner.getX(), owner.getEyeY() - 0.1, owner.getZ());


    }
}