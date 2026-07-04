package net.daplow.daplowfletch.item.arrow.feather;

import net.daplow.daplowfletch.item.custom.ArrowFeather;
import net.daplow.daplowfletch.item.custom.OpArrowItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.Vec3d;

public class PrismarineFeather implements ArrowFeather {
    @Override
    public void onEntityHit(OpArrowItemEntity arrow, EntityHitResult hit) {

    }

    @Override
    public void onBlockHit(OpArrowItemEntity arrow, BlockHitResult hit) {

    }

    @Override
    public void tick(OpArrowItemEntity arrow) {
        if (!arrow.world.isClient && arrow.isTouchingWater()) {
            removeWaterDrag(arrow);
        }
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

    private void removeWaterDrag(OpArrowItemEntity arrow) {
        Vec3d velocity = arrow.getVelocity();
        arrow.setVelocity(
                velocity.x / 0.6,
                velocity.y / 0.9,
                velocity.z / 0.6
        );
    }
}
