package net.daplow.daplowfletch.item.custom;

import net.daplow.daplowfletch.block.ModBlocks;
import net.daplow.daplowfletch.entity.ModEntities;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionUtil;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;

import java.util.List;

public class OpArrowItemEntity extends ArrowEntity {

    public String potionId = "";
    public String stemVariant = "";
    public String featherType = "";


    private NbtCompound customNbt;
    private ItemStack originalStack;

    private BlockPos poweredPos;

    private static final TrackedData<Integer> POTION_COLOR =
            DataTracker.registerData(OpArrowItemEntity.class, TrackedDataHandlerRegistry.INTEGER);

    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(POTION_COLOR, 0); // 0 = no potion
    }

    public void setPotionColor(int color) {
        this.dataTracker.set(POTION_COLOR, color);
    }

    // Required for entity loading
    public OpArrowItemEntity(EntityType<? extends ArrowEntity> type, World world) {
        super(type, world);
    }

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
        // When item is picked up again.
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
        // Tick Script
        if (!this.world.isClient) {

            Vec3d velocity = this.getVelocity();
            float gravity = 0f;
            if ("gold".equals(this.stemVariant)) gravity = 0.03f; // reduce gravity to 30%
            if ("iron".equals(this.stemVariant)) gravity = 0.06f; // reduce gravity to 30%
            this.setVelocity(velocity.x, velocity.y - gravity, velocity.z);

            if ("prismarine".equals(this.featherType) && this.isTouchingWater()) {
                removeWaterDrag();
            }

            if (poweredPos != null && !this.isRemoved()) {  // This is now useless because now Redstone Arrow
                BlockState state = world.getBlockState(poweredPos);
                world.updateNeighborsAlways(poweredPos, state.getBlock());
            }

            if ("endereye".equals(this.featherType)) {
                LivingEntity target = findNearestVisibleEnemy(16);
                if (target != null) {

                    Vec3d currentVel = this.getVelocity();
                    double speed = currentVel.length();

                    Vec3d toTarget = target.getEyePos().subtract(this.getPos());
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

                        this.setVelocity(newDir.multiply(newSpeed));
                        this.velocityDirty = true;
                    }
                }
            }

        }
        else {  // CLIENT SIDE

            int color = this.dataTracker.get(POTION_COLOR);
            if (color != 0) {
                float fr = ((color >> 16) & 0xFF) / 255f;
                float fg = ((color >> 8) & 0xFF) / 255f;
                float fb = (color & 0xFF) / 255f;
                for (int i = 0; i < 1; i++) {
                    this.world.addParticle(ParticleTypes.ENTITY_EFFECT, this.getParticleX(0.5), this.getRandomBodyY(), this.getParticleZ(0.5), fr, fg, fb);
                }
            }
        }
    }

    @Override
    protected void onBlockHit(BlockHitResult blockHitResult) {
        super.onBlockHit(blockHitResult);

        if (world.isClient) return;
        if ("tnt".equals(this.featherType)) {
            world.createExplosion(
                    this,
                    this.getX(),
                    this.getY(),
                    this.getZ(),
                    1.5f,
                    Explosion.DestructionType.BREAK
            );
            this.discard();
        }

        if ("redstone".equals(this.featherType)) {  // This is now useless since this arrow doesn't exist...
            BlockPos currentPos = this.getBlockPos();
            if (poweredPos != null && !poweredPos.equals(currentPos)) {
                if (world.getBlockState(poweredPos).getBlock() == ModBlocks.ARROW_POWER_BLOCK) {
                    world.setBlockState(poweredPos, Blocks.AIR.getDefaultState(), 3);
                }
            }
            BlockState currentState = world.getBlockState(currentPos);
            if (currentState.isAir()) {
                world.setBlockState(currentPos, ModBlocks.ARROW_POWER_BLOCK.getDefaultState(), 3);
                poweredPos = currentPos;
            } else {
                poweredPos = null;
            }


        }
        teleportPlayer();  // The check for Enderpearl is in the function
    }


    @Override
    protected void onEntityHit(EntityHitResult entityHitResult) {
        super.onEntityHit(entityHitResult);

        if (!this.world.isClient && entityHitResult.getEntity() instanceof LivingEntity target) {

            LivingEntity shooter = this.getOwner() instanceof LivingEntity le ? le : null;


            if ("fire".equals(this.featherType)) target.setOnFireFor(10);

            float baseDamage = 2.0F;
            if ("gold".equals(this.stemVariant)) baseDamage += 2.0F;
            if ("iron".equals(this.stemVariant)) baseDamage += 1.0F;
            if (this.isCritical()) {
                baseDamage += this.random.nextFloat() * baseDamage * 0.5F + 0.5F;
            }
            if (shooter != null) {
                int powerLevel = EnchantmentHelper.getLevel(Enchantments.POWER, shooter.getMainHandStack());
                if (powerLevel > 0) {
                    baseDamage += (powerLevel * 0.5F + 0.5F);
                }

                // Apply Punch (knockback)
                int punchLevel = EnchantmentHelper.getLevel(Enchantments.PUNCH, shooter.getMainHandStack());
                if (punchLevel > 0) {
                    target.takeKnockback(0.5F * punchLevel, MathHelper.sin(this.getYaw() * ((float)Math.PI / 180)), -MathHelper.cos(this.getYaw() * ((float)Math.PI / 180)));
                }

                // Flame enchantment
                if (EnchantmentHelper.getLevel(Enchantments.FLAME, shooter.getMainHandStack()) > 0) {
                    target.setOnFireFor(5);
                }
            }
            target.damage(DamageSource.arrow(this, this.getOwner()), baseDamage);


            if (!potionId.isEmpty()) {
                ItemStack potionStack = new ItemStack(Items.POTION);
                potionStack.getOrCreateNbt().putString("Potion", potionId);
                for (StatusEffectInstance effect : PotionUtil.getPotionEffects(potionStack)) {

                    if (effect.getEffectType().isInstant()) {
                        effect.getEffectType().applyInstantEffect(
                                this.getOwner(), // shooter
                                this.getOwner(), // indirect source
                                target,
                                effect.getAmplifier(),
                                1.0f // multiplier
                        );
                    } else {
                        int arrowDuration = Math.max(effect.getDuration() / 8, 20);
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
            }
            teleportPlayer();  // The check for Enderpearl is in the function

            if ("tnt".equals(this.featherType)) {
                world.createExplosion(
                        this,
                        this.getX(),
                        this.getY(),
                        this.getZ(),
                        2f,
                        Explosion.DestructionType.BREAK
                );
                this.discard();
            }
        }

    }

    @Override
    public void remove(RemovalReason reason) {
        super.remove(reason);

        if (!world.isClient && poweredPos != null) {  // This is useless since redstone arrow doesn't exist.
            if (world.getBlockState(poweredPos).getBlock() == ModBlocks.ARROW_POWER_BLOCK) {
                world.setBlockState(poweredPos, Blocks.AIR.getDefaultState());

            }
            poweredPos = null;
        }
    }

    //  *** CUSTOM FUNCTIONS *** //

    private LivingEntity findNearestVisibleEnemy(double radius) {
        List<LivingEntity> entities = world.getEntitiesByClass(LivingEntity.class, this.getBoundingBox().expand(radius), e ->
                e != getOwner() && e.isAlive() && !e.isInvulnerable()
        );
        LivingEntity closest = null;
        double closestDist = Double.MAX_VALUE;
        for (LivingEntity entity : entities) {
            // Raycast
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
        this.setVelocity(
                velocity.x / 0.6,
                velocity.y / 0.9,
                velocity.z / 0.6
        );
    }

    private void teleportPlayer() {
        if (!"enderpearl".equals(this.featherType)) return;
        if (getOwner() instanceof PlayerEntity player) {
            player.teleport(this.getX(), this.getY(), this.getZ());
            player.fallDistance = 0;
        }
        this.remove(RemovalReason.KILLED);
    }
}