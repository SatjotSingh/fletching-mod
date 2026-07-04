package net.daplow.daplowfletch.other.recipes;

import com.google.gson.JsonObject;
import net.minecraft.item.Item;
import net.minecraft.nbt.NbtElement;
import net.minecraft.util.Identifier;

public class FeatherRecipe {

    private final Item item;
    private final Identifier featherId;
    private final JsonObject name;
    private final JsonObject data;

    public FeatherRecipe(Item item, Identifier featherId, JsonObject name, JsonObject data) {
        this.item = item;
        this.featherId = featherId;
        this.name = name;
        this.data = data;
    }

    public Item getItem() {
        return item;
    }

    public Identifier getFeatherId() {
        return featherId;
    }

    public JsonObject getName() {return name;}

    public JsonObject getData() {return data;}

}
