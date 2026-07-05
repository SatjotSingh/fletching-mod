package net.daplow.daplowfletch.item.arrow.feather;

import net.daplow.daplowfletch.item.custom.ArrowFeather;
import net.daplow.daplowfletch.item.custom.OpArrowItemEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;

public class WarpingArrow implements ArrowFeather {
    @Override
    public void onEntityHit(OpArrowItemEntity arrow, EntityHitResult hit) {

        if (arrow.getWorld().isClient) return;

        teleportPlayer(arrow);

    }

    @Override
    public void onBlockHit(OpArrowItemEntity arrow, BlockHitResult hit) {

        if (arrow.getWorld().isClient) return;

        teleportPlayer(arrow);

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

    private void teleportPlayer(OpArrowItemEntity arrow) {
        if (arrow.getOwner() instanceof PlayerEntity player) {
            player.teleport(arrow.getX(), arrow.getY(), arrow.getZ());
            player.fallDistance = 0;
            arrow.world.playSound(player, arrow.getBlockPos(), SoundEvents.ENTITY_GENERIC_SMALL_FALL, SoundCategory.PLAYERS,2,1);

        }

        arrow.remove(Entity.RemovalReason.KILLED);
    }
}
