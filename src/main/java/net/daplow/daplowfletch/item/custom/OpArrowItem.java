package net.daplow.daplowfletch.item.custom;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.item.ArrowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.world.World;

public class OpArrowItem extends ArrowItem {

    private final int addon1;
    private final int addon2;
    private final int addon3;

    public OpArrowItem(Settings settings, int addon1, int addon2, int addon3) {
        super(settings);
        this.addon1 = addon1;
        this.addon2 = addon2;
        this.addon3 = addon3;
    }

    public int getAddon1() { return addon1; }
    public int getAddon2() { return addon2; }
    public int getAddon3() { return addon3; }

    @Override
    public ArrowEntity createArrow(World world, ItemStack stack, LivingEntity shooter) {
        OpArrowItemEntity arrow = new OpArrowItemEntity(world, shooter, stack);

        /*
        NbtCompound stackNbt = stack.getNbt();
        if (stackNbt != null) {
            if (stackNbt.contains("Potion")) {
                arrow.potionId = stackNbt.getString("Potion");
                arrow.hasPotion = stackNbt.getBoolean("potion");
            }
            if (stackNbt.contains("stem")) {
                arrow.stemVariant = stackNbt.getString("stem_variant");
                arrow.hasStem = stackNbt.getBoolean("stem");
            }
            if (stackNbt.contains("hasFeatherLayer")) {
                arrow.hasFeatherLayer = stackNbt.getBoolean("hasFeatherLayer");
            }
            if (stackNbt.contains("potion_stem")) {
                arrow.hasPotionStem = stackNbt.getBoolean("potion_stem");
            }
            if (stackNbt.contains("index")) {
                arrow.index = stackNbt.getInt("index");
            }
        }
        */
        return arrow;
    }
}
