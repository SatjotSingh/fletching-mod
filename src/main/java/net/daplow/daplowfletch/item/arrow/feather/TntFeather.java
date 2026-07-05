package net.daplow.daplowfletch.item.arrow.feather;

import net.daplow.daplowfletch.item.custom.ArrowFeather;
import net.daplow.daplowfletch.item.custom.OpArrowItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;

public class TntFeather implements ArrowFeather {

    @Override
    public void onEntityHit(OpArrowItemEntity arrow, EntityHitResult hit) {

        World world = arrow.getWorld();

        if (world.isClient()) return;

        double x = arrow.getX();
        double y = arrow.getY();
        double z = arrow.getZ();

        world.createExplosion(
                arrow,
                x,
                y,
                z,
                1.5f,
                Explosion.DestructionType.BREAK
        );
        arrow.discard();

    }

    @Override
    public void onBlockHit(OpArrowItemEntity arrow, BlockHitResult hit) {

        World world = arrow.getWorld();

        if (world.isClient()) return;

        int amplifier = arrow.getFeatherData().getInt("amplifier");

        if (amplifier == 0) {
            amplifier = 1;
        }

        System.out.println(amplifier);

        double x = arrow.getX();
        double y = arrow.getY();
        double z = arrow.getZ();

        world.createExplosion(
                arrow,
                x,
                y,
                z,
                1.5f *amplifier,
                Explosion.DestructionType.BREAK
        );
        arrow.discard();

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
