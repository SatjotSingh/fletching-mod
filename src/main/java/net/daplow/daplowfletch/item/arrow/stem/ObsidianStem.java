package net.daplow.daplowfletch.item.arrow.stem;

import net.daplow.daplowfletch.item.custom.ArrowStem;
import net.daplow.daplowfletch.item.custom.OpArrowItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;

public class ObsidianStem implements ArrowStem {

    @Override
    public void onEntityHit(OpArrowItemEntity arrow, EntityHitResult hit) {
        // Make it Do Damage more through Armor

        // Make it Do Damage to Armor

    }

    @Override
    public void onBlockHit(OpArrowItemEntity arrow, BlockHitResult hit) {

    }

    @Override
    public void tick(OpArrowItemEntity arrow) {

    }

    @Override
    public void writeNbt(NbtCompound nbt) {

    }

    @Override
    public void readNbt(NbtCompound nbt) {

    }

    @Override
    public void onRemove() {

    }

    @Override
    public void onHit(LivingEntity target) {

    }

    @Override
    public void onPlayerCollision(PlayerEntity player) {

    }

    @Override
    public boolean hasNoGravity() {
        return false;
    }
}
