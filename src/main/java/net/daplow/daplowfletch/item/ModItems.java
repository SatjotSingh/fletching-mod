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

    public static final Item OP_ARROW = new OpArrowItem(new Item.Settings().maxCount(64));

    // Base arrow

    // Stem (render-only, doesn’t need to exist in inventory if you want)
    public static final Item OP_ARROW_STEM = new Item(new FabricItemSettings().group(ItemGroup.COMBAT));

    // Tip (potion tip)
    public static final Item OP_ARROW_TIP = new Item(new FabricItemSettings().group(ItemGroup.COMBAT));

    // Addons: example for TNT, Diamond, etc.
    public static final Item ADDON_TNT = new Item(new FabricItemSettings().group(ItemGroup.COMBAT));

    // Optional helper to get an addon by string
    public static Item getAddonItem(String payload) {
        return switch(payload) {
            case "tnt" -> ADDON_TNT;
            default -> null; // or OP_ARROW as fallback
        };
    }
    public static void registerItems() {
        Registry.register(Registry.ITEM, new Identifier(DaplowFletch.MOD_ID, "op_arrow"), OP_ARROW);
        Registry.register(Registry.ITEM, new Identifier("daplow-fletch", "op_arrow_stem"), OP_ARROW_STEM);
        Registry.register(Registry.ITEM, new Identifier("daplow-fletch", "op_arrow_potion"), OP_ARROW_TIP);
        Registry.register(Registry.ITEM, new Identifier("daplow-fletch", "op_arrow_addon_tnt"), ADDON_TNT);


        ModelPredicateProviderRegistry.register(ModItems.OP_ARROW, new Identifier("stem"), (stack, world, entity, seed) ->
                stack.getOrCreateNbt().getBoolean("stem") ? 1f : 0f
        );

        ModelPredicateProviderRegistry.register(ModItems.OP_ARROW, new Identifier("potion"), (stack, world, entity, seed) ->
                stack.getOrCreateNbt().getBoolean("potion") ? 1f : 0f
        );

    }
}
