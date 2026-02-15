package net.daplow.daplowfletch.item;

import net.daplow.daplowfletch.DaplowFletch;
import net.daplow.daplowfletch.item.custom.OpArrowItem;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.client.item.ModelPredicateProviderRegistry;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import static net.daplow.daplowfletch.DaplowFletch.MOD_ID;

public class ModItems {

    public static final Item OP_ARROW = new OpArrowItem(new Item.Settings().maxCount(64).group(null));;

    public static void register() {
        // First: create all OP_ARROW items
        /*
        int index = 0;
        for (int a1 = 0; a1 <= 1; a1++) {
            for (int a2 = 0; a2 <= 1; a2++) {
                for (int a3 = 0; a3 < 10; a3++) {

                    //OP_ARROW[index] = new OpArrowItem(new Item.Settings().maxCount(64).group(null), a1, a2, a3);
                    index++;
                }
            }
        }

        // Now register all items safely
        for (int i = 0; i < OP_ARROW.length; i++) {
            Registry.register(Registry.ITEM, new Identifier("daplow-fletch", "op_arrow_" + i), OP_ARROW[i]);
        }
*/

        Registry.register(Registry.ITEM, new Identifier(MOD_ID, "op_arrow"), OP_ARROW);


    }
}
