package net.daplow.daplowfletch.item.custom;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.item.ArrowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class OpArrowItem extends ArrowItem {


    public OpArrowItem(Settings settings, int addon1, int addon2, int addon3) {
        super(settings);
    }


    @Override
    public ArrowEntity createArrow(World world, ItemStack stack, LivingEntity shooter) {
        OpArrowItemEntity arrow = new OpArrowItemEntity(world, shooter, stack);


        return arrow;
    }
}
