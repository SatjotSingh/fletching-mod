package net.daplow.daplowfletch.item.custom;

import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.item.ArrowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class OpArrowItem extends ArrowItem {


    public OpArrowItem(Settings settings) {
        super(settings);
    }


    @Override
    public ItemStack getDefaultStack() {
        ItemStack stack = super.getDefaultStack();
        NbtCompound nbt = stack.getOrCreateNbt();
        nbt.putInt("index", 0); // always present
        return stack;
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



        return arrow;
    }
}
