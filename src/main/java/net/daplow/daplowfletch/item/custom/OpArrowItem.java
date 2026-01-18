package net.daplow.daplowfletch.item.custom;

import net.daplow.daplowfletch.DaplowFletch;
import net.daplow.daplowfletch.entity.ModEntities;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.item.ArrowItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;

public class OpArrowItem extends ArrowItem {


    public OpArrowItem(Settings settings, int a1, int a2, int a3) {
        super(settings);

    }

    public ArrowEntity createArrow(World world, ItemStack stack, LivingEntity shooter) {
        OpArrowItemEntity arrow = new OpArrowItemEntity(world, shooter);

        // Copy the NBT from the ItemStack to the entity
        NbtCompound stackNbt = stack.getNbt();
        if (stackNbt != null) {
            // Copy only the fields your entity actually needs
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
        }

        return arrow;
    }

}
