package net.daplow.daplowfletch.item.custom;

import net.daplow.daplowfletch.other.entity.ModEntities;
import net.minecraft.enchantment.Enchantment;
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
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.Map;

public class OpArrowItemEntity extends ArrowEntity {

    private ArrowStem stem;
    private ArrowFeather feather;


    private ItemStack originalStack;
    private NbtCompound nbtData;
    private final Map<Enchantment, Integer> enchantments = new HashMap<>();


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

    public int getPotionColor() {
        return this.dataTracker.get(POTION_COLOR);
    }

    public OpArrowItemEntity(EntityType<? extends ArrowEntity> entityType, World world) {
        super(entityType, world);
    }
    public OpArrowItemEntity(World world, double x, double y, double z, ItemStack stack, ItemStack bowStack) {
        super(ModEntities.OP_ARROW_ENTITY, world);
        this.setPosition(x, y, z);
        this.originalStack = stack.copy();
        this.originalStack.setCount(1);
        this.pickupType = PickupPermission.ALLOWED;
        this.nbtData = stack.getNbt();
    }

    public OpArrowItemEntity(World world, LivingEntity owner, ItemStack stack, ItemStack bowStack) {
        super(ModEntities.OP_ARROW_ENTITY, world);
        this.setOwner(owner);
        this.setPosition(owner.getX(), owner.getEyeY() - 0.1, owner.getZ());
        this.originalStack = stack.copy();
        this.originalStack.setCount(1);
        this.nbtData = stack.getNbt();

/*
        Map<Enchantment, Integer> enchants =
                EnchantmentHelper.get(bow);

        for (Map.Entry<Enchantment, Integer> entry : enchants.entrySet()) {
            stack.setEnchantment(
                    entry.getKey(),
                    entry.getValue() + 1
            );
        }

 */

    }

    public NbtCompound getStemData() {
        return originalStack.getOrCreateNbt().getCompound("StemData");
    }

    public NbtCompound getFeatherData() {
        return originalStack.getOrCreateNbt().getCompound("FeatherData");
    }



    @Override
    protected ItemStack asItemStack() {
        // When item is picked up again.
        ItemStack stack = originalStack.copy();
        stack.setCount(1);
        return stack;
    }

    public void setStem(ArrowStem stem) {
        this.stem = stem;
    }

    public void setFeather(ArrowFeather feather) {
        this.feather = feather;
    }


    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);

        if(stem != null) {
            stem.writeNbt(nbt);
        }

        if(feather != null) {
            feather.writeNbt(nbt);
        }

    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);

        if(stem != null) {
            stem.readNbt(nbt);
        }

        if(feather != null) {
            feather.readNbt(nbt);
        }

    }

    @Override
    public void tick() {
        super.tick();

        if(stem != null) {
            stem.tick(this);
        }

        if(feather != null) {
            feather.tick(this);
        }


        if (this.getWorld().isClient) {
            if (this.age % 2 == 0) {
                int color = this.getPotionColor();

                if (color != 0) {
                    float fr = ((color >> 16) & 0xFF) / 255f;
                    float fg = ((color >> 8) & 0xFF) / 255f;
                    float fb = (color & 0xFF) / 255f;

                    this.getWorld().addParticle(
                            ParticleTypes.ENTITY_EFFECT,
                            this.getParticleX(0.5),
                            this.getRandomBodyY(),
                            this.getParticleZ(0.5),
                            fr, fg, fb
                    );
                }
            }
        }


    }


    @Override
    protected void onBlockHit(BlockHitResult blockHitResult) {
        super.onBlockHit(blockHitResult);


        if(stem != null) {
            stem.onBlockHit(this, blockHitResult);
        }

        if(feather != null) {
            feather.onBlockHit(this, blockHitResult);
        }
    }

    @Override
    protected void onEntityHit(EntityHitResult entityHitResult) {
        super.onEntityHit(entityHitResult);


        if(stem != null) {
            stem.onEntityHit(this,entityHitResult);
        }

        if(feather != null) {
            feather.onEntityHit(this, entityHitResult);
        }

        if (!this.world.isClient && entityHitResult.getEntity() instanceof LivingEntity target) {

            LivingEntity shooter = this.getOwner() instanceof LivingEntity le ? le : null;
/*
            float baseDamage = 2.0F;
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
*/

        if (this.nbtData != null && this.nbtData.contains("Potion")) {
            ItemStack potionStack = new ItemStack(Items.POTION);
            potionStack.getOrCreateNbt().putString("Potion", this.nbtData.getString("Potion"));
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


        }
    }

    @Override
    public void onRemoved() {
        super.onRemoved();

        if(stem != null) {
            stem.onRemove();
        }

        if(feather != null) {
            feather.onRemove();
        }
    }

    @Override
    protected void onHit(LivingEntity target) {
        super.onHit(target);

        if(stem != null) {
            stem.onHit(target);
        }

        if(feather != null) {
            feather.onHit(target);
        }

    }

    @Override
    public void onPlayerCollision(PlayerEntity player) {
        super.onPlayerCollision(player);

        if(stem != null) {
            stem.onPlayerCollision(player);
        }

        if(feather != null) {
            feather.onPlayerCollision(player);
        }

    }

    @Override
    public boolean hasNoGravity() {
        // Gold arrows ignore gravity
        boolean hasNoGravity = false;

        if(stem!= null && stem.hasNoGravity()) {
            hasNoGravity = true;
        }

        if(feather!= null && feather.hasNoGravity()) {
            hasNoGravity = true;
        }


        return hasNoGravity;

    }

}