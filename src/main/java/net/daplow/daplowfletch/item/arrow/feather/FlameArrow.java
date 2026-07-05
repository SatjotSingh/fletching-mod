package net.daplow.daplowfletch.item.arrow.feather;

import net.daplow.daplowfletch.item.custom.ArrowFeather;
import net.daplow.daplowfletch.item.custom.OpArrowItemEntity;
import net.minecraft.block.*;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.registry.Registry;
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
        World world = arrow.getWorld();

        if (world.isClient()) return;
        BlockPos pos = hit.getBlockPos();
        BlockState state = world.getBlockState(pos);
        BlockPos firePos = hit.getBlockPos().offset(hit.getSide());

        if (state.getBlock() instanceof CampfireBlock && !state.get(CampfireBlock.LIT)) {
            world.setBlockState(pos, state.with(CampfireBlock.LIT, true));
            return;
        }

        if (state.getBlock() instanceof CandleBlock && !state.get(CandleBlock.LIT)) {
            world.setBlockState(pos, state.with(CandleBlock.LIT, true));
            return;
        }
        if (state.getBlock() instanceof CandleCakeBlock && !state.get(CandleCakeBlock.LIT)) {
            world.setBlockState(pos, state.with(CandleCakeBlock.LIT, true));
            return;
        }

        if (state.isOf(Blocks.TNT)) {
            TntBlock.primeTnt(world, pos);
            world.removeBlock(pos, false);
            return;
        }

        if (world.getBlockState(pos.up()).isOf(Blocks.SNOW) ) {
            world.removeBlock(pos.up(), false);
            world.playSound(
                    null,
                    pos.up(),
                    SoundEvents.BLOCK_FIRE_EXTINGUISH,
                    SoundCategory.BLOCKS,
                    0.5F,
                    1.0F
            );


            return;
        }

        if (state.isOf(Blocks.SNOW_BLOCK)) {
            world.breakBlock(pos, false);
            world.playSound(
                    null,
                    pos,
                    SoundEvents.BLOCK_FIRE_EXTINGUISH,
                    SoundCategory.BLOCKS,
                    0.75F,
                    1.0F
            );

            return;
        }

        if (AbstractFireBlock.canPlaceAt(world, firePos, hit.getSide())) {
            world.setBlockState(firePos, AbstractFireBlock.getState(world, firePos));
        }
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
