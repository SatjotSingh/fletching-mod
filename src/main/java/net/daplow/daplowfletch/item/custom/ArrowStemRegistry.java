package net.daplow.daplowfletch.item.custom;

import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class ArrowStemRegistry {
    private static final Map<Identifier, Supplier<ArrowStem>> STEMS =
            new HashMap<>();

    public static void register(Identifier id,
                                Supplier<ArrowStem> factory) {

        STEMS.put(id, factory);
    }

    public static ArrowStem create(Identifier id) {
        Supplier<ArrowStem> supplier = STEMS.get(id);
        return supplier == null ? null : supplier.get();
    }
}
