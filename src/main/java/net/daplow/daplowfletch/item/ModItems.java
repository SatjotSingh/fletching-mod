package net.daplow.daplowfletch.item;

import net.daplow.daplowfletch.DaplowFletch;
import net.daplow.daplowfletch.item.custom.OpArrowItem;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.client.item.ModelPredicateProviderRegistry;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class ModItems {

    public static final Item[] OP_ARROW = new Item[40];

    public static void registerItems() {
        // First: create all OP_ARROW items
        int index = 0;
        for (int a1 = 0; a1 <= 1; a1++) {
            for (int a2 = 0; a2 <= 1; a2++) {
                for (int a3 = 0; a3 < 10; a3++) {

                    OP_ARROW[index] = new OpArrowItem(new Item.Settings().maxCount(64), a1, a2, a3);
                    index++;
                }
            }
        }

        // Now register all items safely
        for (int i = 0; i < OP_ARROW.length; i++) {
            Registry.register(Registry.ITEM, new Identifier("daplow-fletch", "op_arrow_" + i), OP_ARROW[i]);
        }




    }
}
