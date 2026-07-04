package net.daplow.daplowfletch.item.custom;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;

public class OpArrowFactory {

    public static void applyBehavior(OpArrowItemEntity arrow, ItemStack stack) {
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
