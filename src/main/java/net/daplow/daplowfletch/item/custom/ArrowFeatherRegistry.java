package net.daplow.daplowfletch.item.custom;

import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class ArrowFeatherRegistry {
    private static final Map<Identifier, Supplier<ArrowFeather>> FEATHERS =
            new HashMap<>();

    public static void register(Identifier id,
                                Supplier<ArrowFeather> factory) {

        FEATHERS.put(id, factory);
    }

    public static ArrowFeather create(Identifier id) {
        Supplier<ArrowFeather> supplier = FEATHERS.get(id);
        return supplier == null ? null : supplier.get();
    }
}
