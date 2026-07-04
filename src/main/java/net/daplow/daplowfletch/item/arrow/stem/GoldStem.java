package net.daplow.daplowfletch.item.arrow.stem;

import net.daplow.daplowfletch.item.custom.ArrowStem;
import net.daplow.daplowfletch.item.custom.OpArrowItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class GoldStem implements ArrowStem {
    @Override
    public void onEntityHit(OpArrowItemEntity arrow, EntityHitResult hit) {
        if (!arrow.world.isClient && hit.getEntity() instanceof LivingEntity target) {

            target.damage(DamageSource.arrow(arrow, arrow.getOwner()), 1);

        }
    }

    @Override
    public void onBlockHit(OpArrowItemEntity arrow, BlockHitResult hit) {

    }

    @Override
    public void tick(OpArrowItemEntity arrow) {

        World world = arrow.getWorld();

        if (world.isClient()) return;

        Vec3d velocity = arrow.getVelocity();
        float gravity = 0f;
        gravity = 0.03f; // reduce gravity to 30%
        arrow.setVelocity(velocity.x, velocity.y - gravity, velocity.z);

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
        return true;
    }
}
