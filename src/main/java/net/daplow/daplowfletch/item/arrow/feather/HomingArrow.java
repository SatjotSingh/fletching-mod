package net.daplow.daplowfletch.item.arrow.feather;

import net.daplow.daplowfletch.item.custom.ArrowFeather;
import net.daplow.daplowfletch.item.custom.OpArrowItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.List;

public class HomingArrow implements ArrowFeather {
    @Override
    public void onEntityHit(OpArrowItemEntity arrow, EntityHitResult hit) {

    }

    @Override
    public void onBlockHit(OpArrowItemEntity arrow, BlockHitResult hit) {

    }

    @Override
    public void tick(OpArrowItemEntity arrow) {
        World world = arrow.getWorld();
        if (world.isClient) return;
        int radius = arrow.getFeatherData().getInt("radius");
        LivingEntity target = findNearestVisibleEnemy(arrow, world, radius);
        if (target != null) {

            Vec3d currentVel = arrow.getVelocity();
            double speed = currentVel.length();

            Vec3d toTarget = target.getEyePos().subtract(arrow.getPos());
            if (toTarget.lengthSquared() > 0.0001) {

                Vec3d desiredDir = toTarget.normalize();

                // turn rate
                double turnStrength = 0.55;

                Vec3d newDir = currentVel.normalize()
                        .lerp(desiredDir, turnStrength)
                        .normalize();

                // slow the arrow slightly
                double newSpeed = speed * 0.81;

                newSpeed = Math.max(newSpeed, 0.35);

                arrow.setVelocity(newDir.multiply(newSpeed));
                arrow.velocityDirty = true;
            }
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

    private LivingEntity findNearestVisibleEnemy(OpArrowItemEntity arrow, World world, double radius) {

        List<LivingEntity> entities = world.getEntitiesByClass(LivingEntity.class, arrow.getBoundingBox().expand(radius), e ->
                e != arrow.getOwner() && e.isAlive() && !e.isInvulnerable()
        );
        LivingEntity closest = null;
        double closestDist = Double.MAX_VALUE;
        for (LivingEntity entity : entities) {
            // Raycast
            Vec3d arrowPos = arrow.getPos();
            Vec3d entityEyePos = entity.getEyePos();
            var hitResult = world.raycast(new net.minecraft.world.RaycastContext(
                    arrowPos,
                    entityEyePos,
                    net.minecraft.world.RaycastContext.ShapeType.COLLIDER,
                    net.minecraft.world.RaycastContext.FluidHandling.NONE,
                    arrow
            ));
            if (hitResult.getType() == net.minecraft.util.hit.HitResult.Type.MISS) {
                double dist = arrow.squaredDistanceTo(entity);
                if (dist < closestDist) {
                    closestDist = dist;
                    closest = entity;
                }
            }
        }
        return closest;
    }
}
