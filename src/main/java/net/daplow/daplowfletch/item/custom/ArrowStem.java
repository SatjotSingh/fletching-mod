package net.daplow.daplowfletch.item.custom;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;

public interface ArrowStem {

    void onEntityHit(OpArrowItemEntity arrow,
                     EntityHitResult hit);

    void onBlockHit(OpArrowItemEntity arrow,
                    BlockHitResult hit);

    void tick(OpArrowItemEntity arrow);

    void writeNbt(NbtCompound nbt);

    void readNbt(NbtCompound nbt);

    void onRemove();

    void onHit(LivingEntity target);

    void onPlayerCollision(PlayerEntity player);

    boolean hasNoGravity();

}
