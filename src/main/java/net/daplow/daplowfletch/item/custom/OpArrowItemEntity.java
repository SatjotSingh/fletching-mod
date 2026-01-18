package net.daplow.daplowfletch.item.custom;

import net.daplow.daplowfletch.DaplowFletch;
import net.daplow.daplowfletch.block.ModBlocks;
import net.daplow.daplowfletch.entity.ModEntities;
import net.daplow.daplowfletch.item.ModItems;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionUtil;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;

import java.util.List;
import java.util.Objects;

public class OpArrowItemEntity extends ArrowEntity {

    public String potionId = "";
    public String stemVariant = "";
    public String featherType = "";


    private NbtCompound customNbt;
    private ItemStack originalStack;

    private float gravityMultiplier = 1.0f;

    private BlockPos poweredPos;



    // Required for entity loading
    public OpArrowItemEntity(EntityType<? extends ArrowEntity> type, World world) {
        super(type, world);
    }

    // Used when shooting the arrow
    public OpArrowItemEntity(World world, LivingEntity owner, ItemStack stack) {
        super((EntityType<? extends ArrowEntity>) ModEntities.OP_ARROW_ENTITY, world);
        this.setOwner(owner);
        this.setPosition(owner.getX(), owner.getEyeY() - 0.1, owner.getZ());

        this.originalStack = stack.copy();
        this.originalStack.setCount(1);


        customNbt = stack.getNbt();
        stemVariant = customNbt.getString("stem_variant");
        potionId =  customNbt.getString("Potion");
        featherType =  customNbt.getString("feather");

    }



    @Override
    protected ItemStack asItemStack() {
        ItemStack stack = originalStack.copy();
        stack.setCount(1);

        return stack;
    }

    @Override
    public boolean hasNoGravity() {
        // Gold arrows ignore gravity
        return ("gold".equals(this.stemVariant));
    }

    @Override
    public void tick() {
        super.tick();

        if (!this.world.isClient) {
            Vec3d velocity = this.getVelocity();

            // Custom gravity modifier: less gravity for gold stem arrows
            float gravity = 0f; // vanilla gravity
            if ("gold".equals(this.stemVariant)) gravity = 0.03f; // reduce gravity to 30%
            if ("iron".equals(this.stemVariant)) gravity = 0.06f; // reduce gravity to 30%

            // Apply reduced gravity
            this.setVelocity(velocity.x, velocity.y - gravity, velocity.z);

            if (!potionId.isEmpty()) {
                // Spawn particles at arrow position
                for (int i = 0; i < 2; i++) {
                    this.world.addParticle(ParticleTypes.ENTITY_EFFECT,
                            this.getX() + (this.random.nextDouble() - 0.5) * 0.2,
                            this.getY() + (this.random.nextDouble() - 0.5) * 0.2,
                            this.getZ() + (this.random.nextDouble() - 0.5) * 0.2,
                            0, 0, 0);
                }
            }

            if ("prismarine".equals(this.featherType) && this.isTouchingWater()) {
                removeWaterDrag();
            }

            if (poweredPos != null && !this.isRemoved()) {
                BlockState state = world.getBlockState(poweredPos);
                world.updateNeighborsAlways(poweredPos, state.getBlock());
            }
            if ("endereye".equals(this.featherType)) {
                LivingEntity target = findNearestVisibleEnemy(10); // 10-block search radius
                if (target != null) {
                    Vec3d toTarget = target.getPos().subtract(this.getPos());
                    double distance = toTarget.length();
                    if (distance > 0) {
                        // Adjust velocity toward target
                        Vec3d newVelocity = this.getVelocity().multiply(0.9).add(toTarget.normalize().multiply(0.5));
                        this.setVelocity(newVelocity);
                        this.velocityDirty = true; // notify the client about the change
                    }
                }
            }
        }
    }

    private LivingEntity findNearestVisibleEnemy(double radius) {
        List<LivingEntity> entities = world.getEntitiesByClass(LivingEntity.class, this.getBoundingBox().expand(radius), e ->
                e != getOwner() && e.isAlive() && !e.isInvulnerable()
        );

        LivingEntity closest = null;
        double closestDist = Double.MAX_VALUE;

        for (LivingEntity entity : entities) {
            // Raycast from arrow to entity
            Vec3d arrowPos = this.getPos();
            Vec3d entityEyePos = entity.getEyePos();
            var hitResult = world.raycast(new net.minecraft.world.RaycastContext(
                    arrowPos,
                    entityEyePos,
                    net.minecraft.world.RaycastContext.ShapeType.COLLIDER,
                    net.minecraft.world.RaycastContext.FluidHandling.NONE,
                    this
            ));

            if (hitResult.getType() == net.minecraft.util.hit.HitResult.Type.MISS) {
                double dist = this.squaredDistanceTo(entity);
                if (dist < closestDist) {
                    closestDist = dist;
                    closest = entity;
                }
            }
        }

        return closest;
    }

    private void removeWaterDrag() {
        Vec3d velocity = this.getVelocity();

        // Vanilla water drag is ~0.6
        // We restore most of the lost speed
        this.setVelocity(
                velocity.x / 0.6,
                velocity.y / 0.6,
                velocity.z / 0.6
        );
    }
    private void teleportPlayer() {
        if (!"enderpearl".equals(this.featherType)) return;

        if (getOwner() instanceof PlayerEntity player) {
            player.teleport(this.getX(), this.getY(), this.getZ());
            player.fallDistance = 0; // prevent fall damage
        }

        // Remove the arrow after teleport
        this.remove(RemovalReason.KILLED);
    }


    @Override
    protected void onBlockHit(BlockHitResult blockHitResult) {
        super.onBlockHit(blockHitResult);

        if (world.isClient) return;
        if ("tnt".equals(this.featherType)) {
            world.createExplosion(
                    this,                      // entity source
                    this.getX(),
                    this.getY(),
                    this.getZ(),
                    1.5f,                      // power (TNT = 4.0f)
                    Explosion.DestructionType.BREAK
            );
            this.discard();
        }

        if ("redstone".equals(this.featherType)) {
            BlockPos currentPos = this.getBlockPos();

            // Remove previous block if moved
            if (poweredPos != null && !poweredPos.equals(currentPos)) {
                if (world.getBlockState(poweredPos).getBlock() == ModBlocks.ARROW_POWER_BLOCK) {
                    world.setBlockState(poweredPos, Blocks.AIR.getDefaultState(), 3);
                }
            }

            BlockState currentState = world.getBlockState(currentPos);

            // Only place the arrow power block if the space is empty
            if (currentState.isAir()) {
                world.setBlockState(currentPos, ModBlocks.ARROW_POWER_BLOCK.getDefaultState(), 3);
                poweredPos = currentPos;
            } else {
                poweredPos = null; // skip placement to avoid overwriting existing blocks
            }


        }

        teleportPlayer();
    }

    @Override
    protected void onHit(LivingEntity target) {
        super.onHit(target);

        if (!this.world.isClient) {



            //target.setOnFireFor(5);
        }
    }

    @Override
    protected void onEntityHit(EntityHitResult entityHitResult) {
        super.onEntityHit(entityHitResult);

        if (!this.world.isClient && entityHitResult.getEntity() instanceof LivingEntity target) {

            if ("fire".equals(this.featherType)) target.setOnFireFor(5);

            // --- 1. Base damage ---
            float damage = 5.0F;
            if ("gold".equals(this.stemVariant)) damage += 2.0F;
            if ("iron".equals(this.stemVariant)) damage += 1.0F;

            target.damage(DamageSource.arrow(this, this.getOwner()), damage);

            // --- 2. Apply potion effects ---
            if (!potionId.isEmpty()) {
                ItemStack potionStack = new ItemStack(Items.POTION);
                potionStack.getOrCreateNbt().putString("Potion", potionId);

                Potion potion = PotionUtil.getPotion(potionStack);

                for (StatusEffectInstance effect : potion.getEffects()) {
                    int duration = effect.getDuration();

                    // Vanilla tipped arrow scaling
                    int arrowDuration = Math.max(duration / 8, 20); // minimum 1 second

                    StatusEffectInstance arrowEffect = new StatusEffectInstance(
                            effect.getEffectType(),
                            arrowDuration,
                            effect.getAmplifier(),
                            effect.isAmbient(),
                            effect.shouldShowParticles(),
                            effect.shouldShowIcon()
                    );

                    target.addStatusEffect(arrowEffect);
                }
            }
            teleportPlayer();

            if ("tnt".equals(this.featherType)) {
                world.createExplosion(
                        this,                      // entity source
                        this.getX(),
                        this.getY(),
                        this.getZ(),
                        1.5f,                      // power (TNT = 4.0f)
                        Explosion.DestructionType.BREAK
                );
                this.discard();
            }
        }

    }
    @Override
    public void remove(RemovalReason reason) {
        super.remove(reason);

        if (!world.isClient && poweredPos != null) {
            if (world.getBlockState(poweredPos).getBlock() == ModBlocks.ARROW_POWER_BLOCK) {
                world.setBlockState(poweredPos, Blocks.AIR.getDefaultState());

            }
            poweredPos = null;
        }
    }
}