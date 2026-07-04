package net.daplow.daplowfletch.other.recipes;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.item.Item;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;

import static net.daplow.daplowfletch.DaplowFletch.MOD_ID;

public class FeatherRecipeLoader implements SimpleSynchronousResourceReloadListener {
    @Override
    public Identifier getFabricId() {
        return new Identifier(MOD_ID, "feather_recipe_loader");
    }

    @Override
    public void reload(ResourceManager manager) {
        Map<Identifier, Resource> resources =
                manager.findResources(
                        "fletching_table/feather_recipe",
                        id -> id.getPath().endsWith(".json")
                );

        for (Map.Entry<Identifier, Resource> entry : resources.entrySet()) {
            try (InputStream stream = entry.getValue().getInputStream()) {

                JsonObject json =
                        JsonParser.parseReader(
                                new InputStreamReader(stream)
                        ).getAsJsonObject();

                Item item =
                        Registry.ITEM.get(
                                new Identifier(json.get("item").getAsString()));

                Identifier effect =
                        new Identifier(json.get("effect").getAsString());
                JsonObject data;

                if (json.has("Data")) {
                    data = json.getAsJsonObject("Data");
                } else {
                    data = new JsonObject();
                }
                JsonObject name;

                if (json.has("Info")) {
                    name = json.getAsJsonObject("Info");
                } else {
                    name = new JsonObject();
                }

                FeatherRecipeManager.register(
                        new FeatherRecipe(item, effect, name, data));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
