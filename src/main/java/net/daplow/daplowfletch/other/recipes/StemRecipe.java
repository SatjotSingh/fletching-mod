package net.daplow.daplowfletch.other.recipes;

import com.google.gson.JsonObject;
import net.minecraft.item.Item;
import net.minecraft.nbt.NbtElement;
import net.minecraft.util.Identifier;

public class StemRecipe {

    private final Item item;
    private final Identifier stemId;
    private final int color;
    private final JsonObject name;
    private final JsonObject data;



    public StemRecipe(Item item, Identifier stemId, int color, JsonObject name, JsonObject data) {
        this.item = item;
        this.stemId = stemId;
        this.color = color;
        this.name = name;
        this.data = data;
    }

    public Item getItem() {
        return item;
    }

    public Identifier getStemId() {
        return stemId;
    }

    public int getColor() {return color;}

    public JsonObject getName() {return name;}

    public JsonObject getData() {return data;}


}
