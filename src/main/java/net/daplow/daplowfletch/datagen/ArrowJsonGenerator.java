package net.daplow.daplowfletch.datagen;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ArrowJsonGenerator {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final String BASE_PATH = "src/main/resources/assets/daplow-fletch/models/item/";

    public static void main(String[] args) throws IOException {
        new File(BASE_PATH).mkdirs(); // make folder if not exists

        for (int a1 = 0; a1 <= 1; a1++) {
            for (int a2 = 0; a2 <= 1; a2++) {
                for (int a3 = 0; a3 < 10; a3++) {
                    int index = a1 * 20 + a2 * 10 + a3;
                    String fileName = "op_arrow_" + index + ".json";

                    Map<String, String> textures = new HashMap<>();
                    textures.put("layer0", "daplow-fletch:item/arrow_base");
                    textures.put("layer1", a1 == 1 ? "daplow-fletch:item/op_arrow_stem" : "daplow-fletch:item/no_addon");
                    textures.put("layer3", a2 == 1 ? "daplow-fletch:item/tipped_arrow_head" : "daplow-fletch:item/no_addon");
                    textures.put("layer2", "daplow-fletch:item/op_arrow_addon_" + a3);

                    Map<String, Object> json = new HashMap<>();
                    json.put("parent", "item/generated");
                    json.put("textures", textures);

                    try (FileWriter writer = new FileWriter(BASE_PATH + fileName)) {
                        GSON.toJson(json, writer);
                    }
                }
            }
        }

        System.out.println("All arrow JSONs generated!");
    }
}