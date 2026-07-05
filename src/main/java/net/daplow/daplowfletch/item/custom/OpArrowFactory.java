package net.daplow.daplowfletch.item.custom;

import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.potion.PotionUtil;
import net.minecraft.util.Identifier;

public class OpArrowFactory {

    public static void applyBehavior(OpArrowItemEntity arrow, ItemStack stack) {
        if (stack.hasNbt()) {
            assert stack.getNbt() != null;
            if (stack.getNbt().contains("Potion")) {
                ItemStack potionStack = new ItemStack(Items.POTION);
                potionStack.getOrCreateNbt().putString(
                        "Potion",
                        stack.getNbt().getString("Potion")
                );

                arrow.setPotionColor(PotionUtil.getColor(potionStack));
            }
        }

        NbtCompound nbt = stack.getOrCreateNbt();

        ArrowStem stem = ArrowStemRegistry.create(
                new Identifier(nbt.getString("Stem"))
        );

        ArrowFeather feather = ArrowFeatherRegistry.create(
                new Identifier(nbt.getString("Feather"))
        );

        arrow.setStem(stem);
        arrow.setFeather(feather);

    }

}
