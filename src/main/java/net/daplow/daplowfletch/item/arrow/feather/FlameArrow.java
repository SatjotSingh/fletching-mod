package net.daplow.daplowfletch.item.arrow.feather;

import net.daplow.daplowfletch.item.custom.ArrowFeather;
import net.daplow.daplowfletch.item.custom.OpArrowItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

public class FlameArrow implements ArrowFeather {
    @Override
    public void onEntityHit(OpArrowItemEntity arrow, EntityHitResult hit) {

        if (arrow.getWorld().isClient) return;

        int time = arrow.getFeatherData().getInt("timer");

        hit.getEntity().setOnFireFor(time);

    }

    @Override
    public void onBlockHit(OpArrowItemEntity arrow, BlockHitResult hit) {

    }

    @Override
    public void tick(OpArrowItemEntity arrow) {

        World world = arrow.getWorld();

        if (world.isClient) return;

        Random random = Random.create();

        if(random.nextBoolean()) {
            ((ServerWorld) world).spawnParticles(
                    ParticleTypes.FLAME,
                    arrow.getX(),
                    arrow.getY(),
                    arrow.getZ(),
                    1,
                    random.nextDouble() * 0.25,
                    random.nextDouble() * 0.25,
                    random.nextDouble() * 0.25,
                    0.005
            );
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
}
