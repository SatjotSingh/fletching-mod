package net.daplow.daplowfletch.item.custom;

import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.item.ArrowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.text.Text;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class OpArrowItem extends ArrowItem {


    public OpArrowItem(Settings settings) {
        super(settings);
    }


    @Override
    public ItemStack getDefaultStack() {
        ItemStack stack = new ItemStack(this);

        NbtCompound nbt = stack.getOrCreateNbt();
        nbt.putInt("index", 0);

        return stack;
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
        NewOpArrowItemEntity arrow = new NewOpArrowItemEntity(world, shooter, stack);

        //master


        return arrow;
    }
}
