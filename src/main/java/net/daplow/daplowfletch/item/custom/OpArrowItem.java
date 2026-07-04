package net.daplow.daplowfletch.item.custom;

import net.daplow.daplowfletch.item.ModItems;
import net.daplow.daplowfletch.item.arrow.SpawnOpArrow;
import net.daplow.daplowfletch.other.recipes.FeatherRecipe;
import net.daplow.daplowfletch.other.recipes.FeatherRecipeManager;
import net.daplow.daplowfletch.other.recipes.StemRecipe;
import net.daplow.daplowfletch.other.recipes.StemRecipeManager;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.item.*;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionUtil;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Supplier;

import static net.daplow.daplowfletch.DaplowFletch.MOD_ID;

public class OpArrowItem extends ArrowItem {


    public OpArrowItem(Settings settings) {
        super(settings);
    }

    public static void registerStem(String id, Supplier<ArrowStem> factory) {
        ArrowStemRegistry.register(new Identifier(MOD_ID, id), factory);
    }
    public static void registerFeather(String id, Supplier<ArrowFeather> factory) {
        ArrowFeatherRegistry.register(new Identifier(MOD_ID, id), factory);
    }


    @Override
    public ItemStack getDefaultStack() {
        ItemStack stack = new ItemStack(this);

        NbtCompound nbt = stack.getOrCreateNbt();
        nbt.putInt("index", 0);

        return stack;
    }

    @Override
    public void appendStacks(ItemGroup group, DefaultedList<ItemStack> stacks) {
        super.appendStacks(group, stacks);

        if (group != ModItems.DAPLOW_MOB_REVAMP) {
            return;
        }
        for (StemRecipe stem : StemRecipeManager.getRecipes()) {

            for (FeatherRecipe feather : FeatherRecipeManager.getRecipes()) {

                for (Potion potion : Registry.POTION) {

                    Identifier potionId = Registry.POTION.getId(potion);
                    String potionIdString = potionId.toString();

                    ItemStack arrow = SpawnOpArrow.getStackfromItem(
                            potionIdString,
                            stem.getItem(),
                            feather.getItem(),
                            1
                    );

                    stacks.add(arrow);
                }
            }
        }
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        super.inventoryTick(stack, world, entity, slot, selected);


        // Use net.fabricmc.fabric.api.event.client.player.ClientPickBlockApplyCallback to detect
        if (!stack.hasNbt()) {
            stack.getOrCreateNbt().putInt("index", 0);
            stack.setCustomName(Text.of("Arrow").copy().styled(style -> style.withItalic(false)));
            var displayTag = stack.getOrCreateSubNbt("display");
            NbtList loreList = new NbtList();

            displayTag.put("Lore", loreList);

        }

        super.inventoryTick(stack, world, entity, slot, selected);

    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        //tooltip.add(Text.literal("Please use: /daplw give op_arrow")
        //        .formatted(Formatting.RED));

        super.appendTooltip(stack, world, tooltip, context);
    }

    @Override
    public ArrowEntity createArrow(World world, ItemStack stack, LivingEntity shooter) {
        OpArrowItemEntity arrow = new OpArrowItemEntity(world, shooter, stack);
        if (stack.hasNbt() && stack.getNbt().contains("Potion")) {
            ItemStack potionStack = new ItemStack(Items.POTION);
            potionStack.getOrCreateNbt().putString(
                    "Potion",
                    stack.getNbt().getString("Potion")
            );

            arrow.setPotionColor(PotionUtil.getColor(potionStack));
        }
        OpArrowFactory.applyBehavior(arrow, stack);

        return arrow;
    }


}
