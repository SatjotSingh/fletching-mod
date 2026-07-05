package net.daplow.daplowfletch.item;

import net.daplow.daplowfletch.item.arrow.SpawnOpArrow;
import net.daplow.daplowfletch.item.arrow.feather.*;
import net.daplow.daplowfletch.item.arrow.stem.*;
import net.daplow.daplowfletch.item.custom.*;
import net.daplow.daplowfletch.other.recipes.FeatherRecipe;
import net.daplow.daplowfletch.other.recipes.FeatherRecipeManager;
import net.daplow.daplowfletch.other.recipes.StemRecipe;
import net.daplow.daplowfletch.other.recipes.StemRecipeManager;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.fabricmc.fabric.impl.item.group.ItemGroupExtensions;
import net.minecraft.block.DispenserBlock;
import net.minecraft.block.dispenser.ProjectileDispenserBehavior;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.potion.Potion;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Position;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;

import java.util.function.Supplier;

import static net.daplow.daplowfletch.DaplowFletch.MOD_ID;

public class ModItems {

    public static final ItemGroup DAPLOW_MOB_REVAMP = FabricItemGroupBuilder.create(
                    new Identifier(MOD_ID, "daplow_fletch_tab"))
            .icon(() -> SpawnOpArrow.getStackfromItem("minecraft:healing", Items.IRON_NUGGET, Items.ENDER_PEARL,1)) // icon for the tab
            .build();

    public static final Item OP_ARROW = new OpArrowItem(new Item.Settings().maxCount(64).group(null));;

    public static void registerStem(String id, Supplier<ArrowStem> factory) {
        ArrowStemRegistry.register(new Identifier(MOD_ID, id), factory);
    }
    public static void registerFeather(String id, Supplier<ArrowFeather> factory) {
        ArrowFeatherRegistry.register(new Identifier(MOD_ID, id), factory);
    }

    public static void register() {

        Registry.register(Registry.ITEM, new Identifier(MOD_ID, "op_arrow"), OP_ARROW);

        DispenserBlock.registerBehavior(
                ModItems.OP_ARROW,
                new ProjectileDispenserBehavior() {
                    @Override
                    protected ProjectileEntity createProjectile(World world, Position pos, ItemStack stack) {
                        OpArrowItemEntity arrow = new OpArrowItemEntity(world, pos.getX(),pos.getY(),pos.getZ(), stack, Items.BOW.getDefaultStack());

                        OpArrowFactory.applyBehavior(arrow, stack);
                        return arrow;
                    }
                }
        );

        // --- Registering Feathers ---
        registerFeather("tnt_feather", TntFeather::new);
        registerFeather("prismarine_feather", PrismarineFeather::new);
        registerFeather("flame_feather", FlameArrow::new);
        registerFeather("teleport_feather", WarpingArrow::new);
        registerFeather("homing_feather", HomingArrow::new);


        // --- Registering Stems ---
        registerStem("gold_stem", GoldStem::new);
        registerStem("base_stem", BaseStem::new);
        registerStem("iron_stem", IronStem::new);
        registerStem("lucky_stem", LuckyStem::new);
        registerStem("obsidian_stem", ObsidianStem::new);
        registerStem("lapis_stem", LapisStem::new);


    }
}
