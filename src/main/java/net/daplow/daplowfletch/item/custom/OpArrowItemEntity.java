package net.daplow.daplowfletch.item.custom;

import net.daplow.daplowfletch.DaplowFletch;
import net.daplow.daplowfletch.entity.ModEntities;
import net.daplow.daplowfletch.item.ModItems;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionUtil;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.Objects;

public class OpArrowItemEntity extends ArrowEntity {

    public String potionId = "";
    public String stemVariant = "";

    boolean hasFeatherLayer = false; // default to false
    boolean hasPotion = false;
    boolean hasStem = false;
    boolean hasPotionStem = false;


    private float gravityMultiplier = 1.0f; // default vanilla gravity


    // Required for entity loading
    public OpArrowItemEntity(EntityType<? extends ArrowEntity> type, World world) {
        super(type, world);
    }

    // Used when shooting the arrow
    public OpArrowItemEntity(World world, LivingEntity owner) {
        super((EntityType<? extends ArrowEntity>) ModEntities.OP_ARROW_ENTITY, world);
        this.setOwner(owner);
        this.setPosition(owner.getX(), owner.getEyeY() - 0.1, owner.getZ());

    // owner is the player who made the arrow



    }



    @Override
    protected ItemStack asItemStack() {
        ItemStack stack = new ItemStack(ModItems.OP_ARROW);

        NbtCompound nbt = new NbtCompound();


        if (this.hasPotion) {
            nbt.putString("Potion", this.potionId);
            nbt.putBoolean("potion", true);
        }

        if (this.hasStem) {
            nbt.putString("stem_variant", this.stemVariant);
            nbt.putBoolean("stem", true);
        }
        if (this.hasFeatherLayer) {
            nbt.putBoolean("hasFeatherLayer", true);
        }
        if (this.hasPotionStem) {
            nbt.putBoolean("potion_stem", true);
        }

        stack.setNbt(nbt);

        // Return a stack of your custom arrow item
        if (this.hasCustomName()) {
            stack.setCustomName(this.getCustomName());
        }

        return stack;
    }

    @Override
    public boolean hasNoGravity() {
        // Gold arrows ignore gravity
        return "gold".equals(this.stemVariant);
    }

    @Override
    public void tick() {
        super.tick();

        if (!this.world.isClient) {
            Vec3d velocity = this.getVelocity();

            // Custom gravity modifier: less gravity for gold stem arrows
            float gravity = 0.05f; // vanilla gravity
            if ("gold".equals(this.stemVariant)) gravity = 0.03f; // reduce gravity to 30%
            if ("iron".equals(this.stemVariant)) gravity = 0.06f; // reduce gravity to 30%

            // Apply reduced gravity
            this.setVelocity(velocity.x, velocity.y - gravity, velocity.z);

            if (hasPotion && !potionId.isEmpty()) {
                // Spawn particles at arrow position
                for (int i = 0; i < 2; i++) {
                    this.world.addParticle(ParticleTypes.ENTITY_EFFECT,
                            this.getX() + (this.random.nextDouble() - 0.5) * 0.2,
                            this.getY() + (this.random.nextDouble() - 0.5) * 0.2,
                            this.getZ() + (this.random.nextDouble() - 0.5) * 0.2,
                            0, 0, 0);
                }
            }
        }
    }



    @Override
    protected void onBlockHit(BlockHitResult blockHitResult) {
        super.onBlockHit(blockHitResult);

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

            // --- 1. Base damage ---
            float damage = 5.0F;
            if ("gold".equals(this.stemVariant)) damage += 2.0F;
            if ("iron".equals(this.stemVariant)) damage += 1.0F;

            target.damage(DamageSource.arrow(this, this.getOwner()), damage);

            // --- 2. Apply potion effects ---
            if (hasPotion && !potionId.isEmpty()) {
                // Create a temporary ItemStack of the potion
                ItemStack potionStack = new ItemStack(Items.POTION);
                NbtCompound nbt = potionStack.getOrCreateNbt();
                nbt.putString("Potion", potionId);

                // Get all effects from this potion
                Potion potion = PotionUtil.getPotion(potionStack);
                for (StatusEffectInstance effect : potion.getEffects()) {
                    // Apply each effect to the target
                    target.addStatusEffect(new StatusEffectInstance(effect));

                }
            }
        }

    }
}