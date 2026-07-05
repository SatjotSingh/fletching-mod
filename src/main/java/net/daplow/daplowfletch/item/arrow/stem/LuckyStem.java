package net.daplow.daplowfletch.item.arrow.stem;

import net.daplow.daplowfletch.item.custom.ArrowStem;
import net.daplow.daplowfletch.item.custom.OpArrowItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

public class LuckyStem implements ArrowStem {
    @Override
    public void onEntityHit(OpArrowItemEntity arrow, EntityHitResult hit) {

        // Make Lucky Effect

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
                    ParticleTypes.TOTEM_OF_UNDYING,
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
