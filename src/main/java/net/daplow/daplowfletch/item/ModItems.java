package net.daplow.daplowfletch.item;

import net.daplow.daplowfletch.item.arrow.SpawnOpArrow;
import net.daplow.daplowfletch.item.arrow.feather.*;
import net.daplow.daplowfletch.item.arrow.stem.BaseStem;
import net.daplow.daplowfletch.item.arrow.stem.GoldStem;
import net.daplow.daplowfletch.item.arrow.stem.IronStem;
import net.daplow.daplowfletch.item.custom.*;
import net.daplow.daplowfletch.other.recipes.FeatherRecipe;
import net.daplow.daplowfletch.other.recipes.FeatherRecipeManager;
import net.daplow.daplowfletch.other.recipes.StemRecipe;
import net.daplow.daplowfletch.other.recipes.StemRecipeManager;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.fabricmc.fabric.impl.item.group.ItemGroupExtensions;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.potion.Potion;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import static net.daplow.daplowfletch.DaplowFletch.MOD_ID;

public class ModItems {

    public static final ItemGroup DAPLOW_MOB_REVAMP = FabricItemGroupBuilder.create(
                    new Identifier(MOD_ID, "daplow_fletch_tab"))
            .icon(() -> SpawnOpArrow.getStackfromItem("minecraft:healing", Items.IRON_NUGGET, Items.ENDER_PEARL,1)) // icon for the tab
            .build();

    public static final Item OP_ARROW = new OpArrowItem(new Item.Settings().maxCount(64).group(null));;

    public static void register() {

        Registry.register(Registry.ITEM, new Identifier(MOD_ID, "op_arrow"), OP_ARROW);

        // --- Registering Feathers ---
        OpArrowItem.registerFeather("tnt_feather", TntFeather::new);
        OpArrowItem.registerFeather("prismarine_feather", PrismarineFeather::new);
        OpArrowItem.registerFeather("flame_feather", FlameArrow::new);
        OpArrowItem.registerFeather("teleport_feather", WarpingArrow::new);
        OpArrowItem.registerFeather("homing_feather", HomingArrow::new);


        // --- Registering Stems ---
        OpArrowItem.registerStem("gold_stem", GoldStem::new);
        OpArrowItem.registerStem("base_stem", BaseStem::new);
        OpArrowItem.registerStem("iron_stem", IronStem::new);


    }
}
