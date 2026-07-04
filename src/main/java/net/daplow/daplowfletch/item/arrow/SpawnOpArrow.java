package net.daplow.daplowfletch.item.arrow;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import net.daplow.daplowfletch.item.ModItems;
import net.daplow.daplowfletch.other.recipes.FeatherRecipe;
import net.daplow.daplowfletch.other.recipes.FeatherRecipeManager;
import net.daplow.daplowfletch.other.recipes.StemRecipe;
import net.daplow.daplowfletch.other.recipes.StemRecipeManager;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionUtil;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.Map;

public class SpawnOpArrow {


    public static ItemStack getStack(String potionID, ItemStack stemStack, ItemStack featherStack, int count) {
        ItemStack arrow = new ItemStack(ModItems.OP_ARROW);

        //
        // Note: potionID should be "minecraft:fire_resistance"
        //

        // --- Potion logic ---
        boolean hasPotion = false;
        String potionName = "";
        java.util.List<String> potionLore = new java.util.ArrayList<>();

        if (!potionID.equalsIgnoreCase("minecraft:none") && !potionID.equalsIgnoreCase("none")) {

            Identifier potionIdentifier = Identifier.tryParse(potionID);


            Potion potion = Registry.POTION.get(potionIdentifier);

            arrow.getOrCreateNbt().putString("Potion", potionID);
            hasPotion = true;

            ItemStack tempPotionStack = new ItemStack(Items.POTION);
            PotionUtil.setPotion(tempPotionStack, potion);

            for (StatusEffectInstance statusEffect : PotionUtil.getPotionEffects(tempPotionStack)) {

                String loreLine = Text.translatable(
                        statusEffect.getEffectType().getTranslationKey()
                ).getString();

                if (statusEffect.getAmplifier() > 0) {
                    loreLine += " " + toRoman(statusEffect.getAmplifier() + 1);
                }

                if (!statusEffect.getEffectType().isInstant()) {
                    int totalSeconds = Math.max(statusEffect.getDuration() / 8, 20) / 20;
                    int minutes = totalSeconds / 60;
                    int seconds = totalSeconds % 60;
                    loreLine += String.format(" (%d:%02d)", minutes, seconds);
                }

                potionLore.add(loreLine);
            }

            if (hasPotion) {
                potionName = " of " + tempPotionStack.getName().getString().replace("Potion of ", "");
            }
        }

        // --- Stem Logic ---
        String string_stem = "";
        String stem_lore = "";
        int addon1 = 0;
        StemRecipe stemRecipe =
                StemRecipeManager.get(stemStack.getItem());

        if (stemRecipe != null) {
            addon1 = 1;
            string_stem = stemRecipe.getName().get("Name").getAsString() + " ";
            stem_lore = stemRecipe.getName().get("Lore").getAsString();

            arrow.getOrCreateNbt().putString("Stem", stemRecipe.getStemId().toString());
            //arrow.getOrCreateNbt().putBoolean("stem", true);
            arrow.getOrCreateNbt().putInt("color", stemRecipe.getColor());
            arrow.getOrCreateNbt().put("StemData", jsonToNbt(stemRecipe.getData()));
        }


        // --- Feather Logic ---
        String string_feather = "";
        String feather_lore = "";
        int addon3 = 0;

        FeatherRecipe featherRecipe =
                FeatherRecipeManager.get(featherStack.getItem());
        if (featherRecipe != null) {
            addon3 = featherRecipe.getData().get("variant").getAsInt();
            if (addon3 == 0) addon3 = 1; // fix from bugging

            string_feather =  featherRecipe.getName().get("Name").getAsString() + " ";
            feather_lore =  featherRecipe.getName().get("Lore").getAsString();

            //arrow.getOrCreateNbt().putBoolean("hasFeatherLayer", true);
            arrow.getOrCreateNbt().putString("Feather", featherRecipe.getFeatherId().toString());
            arrow.getOrCreateNbt().put("FeatherData", jsonToNbt(featherRecipe.getData()));
        }


        // --- All logic ---
        int addon2 = hasPotion ? 1 : 0;

        int index = addon1 * 20 + addon2 * 10 + addon3; // 0..39

        arrow.getOrCreateNbt().putInt("index", index);
        ItemStack result = new ItemStack(ModItems.OP_ARROW, count);

        if (arrow.hasNbt()) {
            result.setNbt(arrow.getNbt().copy());
        }
        result.getOrCreateNbt().putInt("index", index);
        result.setCustomName(Text.literal(string_stem + string_feather + "Arrow" + potionName).styled(style -> style.withItalic(false)));

        var displayTag = result.getOrCreateSubNbt("display");
        NbtList loreList = new NbtList();

        for (String loreLine : potionLore) {
            loreList.add(NbtString.of(Text.Serializer.toJson(
                    Text.literal(loreLine).styled(style -> style.withItalic(false))
            )));
        }

        if (!(feather_lore=="")) {
            loreList.add(NbtString.of(Text.Serializer.toJson(Text.literal(feather_lore).styled(style -> style.withItalic(false)))));
        }
        if (!(stem_lore=="")) {
            loreList.add(NbtString.of(Text.Serializer.toJson(Text.literal(stem_lore).styled(style -> style.withItalic(false)))));
        }
        displayTag.put("Lore", loreList);


        if (index==0) {
            result = Items.ARROW.getDefaultStack();
        }


        return result;

    }
    public static ItemStack getStackfromItem(String potionID, Item stemItem, Item featherItem, int count) {
        ItemStack arrow = new ItemStack(ModItems.OP_ARROW);

        ItemStack stemStack = stemItem.getDefaultStack();
        ItemStack featherStack = featherItem.getDefaultStack();

        return getStack(potionID,stemStack,featherStack,count);

    }


    private static String toRoman(int number) {
        return switch (number) {
            case 1 -> "I";
            case 2 -> "II";
            case 3 -> "III";
            case 4 -> "IV";
            case 5 -> "V";
            case 6 -> "VI";
            case 7 -> "VII";
            case 8 -> "VIII";
            case 9 -> "IX";
            case 10 -> "X";
            default -> Integer.toString(number);
        };
    }

    private static NbtCompound jsonToNbt(JsonObject json) {

        NbtCompound nbt = new NbtCompound();

        for (Map.Entry<String, JsonElement> entry : json.entrySet()) {

            String key = entry.getKey();
            JsonElement value = entry.getValue();

            if (value.isJsonPrimitive()) {
                JsonPrimitive prim = value.getAsJsonPrimitive();

                if (prim.isString()) {
                    nbt.putString(key, prim.getAsString());
                } else if (prim.isBoolean()) {
                    nbt.putBoolean(key, prim.getAsBoolean());
                } else if (prim.isNumber()) {
                    // choose int vs double
                    Number num = prim.getAsNumber();
                    if (num.doubleValue() % 1 == 0) {
                        nbt.putInt(key, num.intValue());
                    } else {
                        nbt.putDouble(key, num.doubleValue());
                    }
                }

            } else if (value.isJsonObject()) {
                nbt.put(key, jsonToNbt(value.getAsJsonObject()));

            } else if (value.isJsonArray()) {
                // optional: implement later if you need lists
            }
        }

        return nbt;
    }
}



