package net.daplow.daplowfletch.item;

import net.daplow.daplowfletch.DaplowFletch;
import net.daplow.daplowfletch.item.custom.OpArrowItem;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class ModItems {

    public static final Item OP_ARROW = new OpArrowItem(new Item.Settings().maxCount(64));

    public static void registerItems() {
        Registry.register(Registry.ITEM, new Identifier(DaplowFletch.MOD_ID, "op_arrow"), OP_ARROW);



    }
}
