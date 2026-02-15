package net.daplow.daplowfletch.other;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import net.daplow.daplowfletch.item.ModItems;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.potion.Potion;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.potion.PotionUtil;
import net.minecraft.potion.Potions;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.ArrayList;
import java.util.List;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;
public class ModCommands {
    //private static final String[] POTION_OPTIONS = {"none", "swiftness", "healing", "night_vision"};
    private static final String[] STEM_OPTIONS = {"none", "gold", "iron"};
    private static final String[] EXTRA_OPTIONS = {"none", "tnt", "prismarine", "fire", "enderpearl", "endereye"};

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {


        RequiredArgumentBuilder<ServerCommandSource, String> potionArg =
                argument("effect", StringArgumentType.word())
                        .suggests((context, builder) -> {
                            String typed = builder.getRemaining().toLowerCase();
                            builder.suggest("none");
                            Registry.POTION.stream()
                                    .map(Registry.POTION::getId)
                                    .filter(id -> id != null)
                                    .map(Identifier::getPath)
                                    .filter(name -> name.startsWith(typed))
                                    .filter(name -> !name.equals("empty")) // ⭐ REMOVE EMPTY
                                    .forEach(builder::suggest);
                            return builder.buildFuture();
                        });


        RequiredArgumentBuilder<ServerCommandSource, String> stemArg =
                argument("stem", StringArgumentType.word())
                        .suggests((context, builder) -> {
                            String typed = builder.getRemaining().toLowerCase();
                            for (String s : STEM_OPTIONS) if (s.startsWith(typed)) builder.suggest(s);
                            return builder.buildFuture();
                        });

        RequiredArgumentBuilder<ServerCommandSource, String> extraArg =
                argument("extra", StringArgumentType.word())
                        .suggests((context, builder) -> {
                            String typed = builder.getRemaining().toLowerCase();
                            for (String s : EXTRA_OPTIONS) if (s.startsWith(typed)) builder.suggest(s);
                            return builder.buildFuture();
                        });

        RequiredArgumentBuilder<ServerCommandSource, Integer> countArg =
                argument("count", IntegerArgumentType.integer(1, 64));

        dispatcher.register(
                literal("daplow")
                        .then(
                                literal("give")
                                        .then(
                                                literal("op_arrow")
                                                        .then(potionArg
                                                                .then(stemArg
                                                                        .then(extraArg

                                                                                // ✅ default count = 1
                                                                                .executes(ctx -> giveArrow(ctx.getSource(),
                                                                                        StringArgumentType.getString(ctx, "effect"),
                                                                                        StringArgumentType.getString(ctx, "stem"),
                                                                                        StringArgumentType.getString(ctx, "extra"),
                                                                                        1))

                                                                                // ✅ optional count provided
                                                                                .then(countArg
                                                                                        .executes(ctx -> giveArrow(ctx.getSource(),
                                                                                                StringArgumentType.getString(ctx, "effect"),
                                                                                                StringArgumentType.getString(ctx, "stem"),
                                                                                                StringArgumentType.getString(ctx, "extra"),
                                                                                                IntegerArgumentType.getInteger(ctx, "count")
                                                                                        ))
                                                                                )
                                                                        )
                                                                )
                                                        )
                                        )
                        )
        );
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

    private static int giveArrow(ServerCommandSource source,
                                 String effectId,
                                 String stemArgValue,
                                 String extraArgValue,
                                 int count) {

        ItemStack arrow = new ItemStack(ModItems.OP_ARROW, count);

        // --- Potion logic ---
        boolean hasPotion = false;
        String potionName = "";
        java.util.List<String> potionLore = new java.util.ArrayList<>();

        if (!effectId.equalsIgnoreCase("none")) {

            Identifier potionIdentifier = Identifier.tryParse(effectId);

            if (potionIdentifier == null || !Registry.POTION.containsId(potionIdentifier)) {
                source.sendFeedback(Text.literal("Invalid potion effect: " + effectId).formatted(Formatting.RED), false);
                return 0;
            }

            Potion potion = Registry.POTION.get(potionIdentifier);

            arrow.getOrCreateNbt().putString("Potion", "minecraft:" + effectId);
            arrow.getOrCreateNbt().putBoolean("potion", true);
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

            if (hasPotion && !potionLore.isEmpty()) {
                potionName = " of " + tempPotionStack.getName().getString().replace("Potion of ", "");
            }
        }

        // ---- Stem ----
        boolean hasStem = false;
        String stemName = "";
        String stemLore = "";
        int addon1 = 0;

        switch (stemArgValue.toLowerCase()) {
            case "gold" -> {
                arrow.getOrCreateNbt().putString("stem_variant", "gold");
                arrow.getOrCreateNbt().putBoolean("stem", true);
                stemName = "Gilded ";
                stemLore = "Gold Stemmed";
                hasStem = true;
                addon1 = 1;
            }
            case "iron" -> {
                arrow.getOrCreateNbt().putString("stem_variant", "iron");
                arrow.getOrCreateNbt().putBoolean("stem", true);
                stemName = "Iron ";
                stemLore = "Iron Stemmed";
                hasStem = true;
                addon1 = 1;
            }
            case "none" -> addon1 = 0;
            default -> {
                source.sendFeedback(Text.literal("Invalid stem: " + stemArgValue).formatted(Formatting.RED), false);
                return 0;
            }
        }

        // ---- Extra ----
        boolean hasFeather = false;
        String featherName = "";
        String featherLore = "";
        int addon3 = 0;

        switch (extraArgValue.toLowerCase()) {
            case "tnt" -> {
                arrow.getOrCreateNbt().putBoolean("hasFeatherLayer", true);
                arrow.getOrCreateNbt().putString("feather", "tnt");
                featherName = "TNT ";
                featherLore = "Kaboom on impact";
                hasFeather = true;
                addon3 = 1;
            }
            case "prismarine" -> {
                arrow.getOrCreateNbt().putBoolean("hasFeatherLayer", true);
                arrow.getOrCreateNbt().putString("feather", "prismarine");
                featherName = "Water ";
                featherLore = "Can be used in Water";
                hasFeather = true;
                addon3 = 2;
            }
            case "fire" -> {
                arrow.getOrCreateNbt().putBoolean("hasFeatherLayer", true);
                arrow.getOrCreateNbt().putString("feather", "fire");
                featherName = "Flaming ";
                featherLore = "Increased Fire Damage";
                hasFeather = true;
                addon3 = 4;
            }
            case "enderpearl" -> {
                arrow.getOrCreateNbt().putBoolean("hasFeatherLayer", true);
                arrow.getOrCreateNbt().putString("feather", "enderpearl");
                featherName = "Warping ";
                featherLore = "Teleports player to location of arrow";
                hasFeather = true;
                addon3 = 5;
            }
            case "endereye" -> {
                arrow.getOrCreateNbt().putBoolean("hasFeatherLayer", true);
                arrow.getOrCreateNbt().putString("feather", "endereye");
                featherName = "Homing ";
                featherLore = "Homes into nearby targets";
                hasFeather = true;
                addon3 = 6;
            }
            case "none" -> addon3 = 0;
            default -> {
                source.sendFeedback(Text.literal("Invalid type: " + extraArgValue).formatted(Formatting.RED), false);
                return 0;
            }
        }

        // ---- Index ----
        int addon2 = hasPotion ? 1 : 0;
        int index = addon1 * 20 + addon2 * 10 + addon3;
        arrow.getOrCreateNbt().putInt("index", index);

        // ---- Name ----
        arrow.setCustomName(
                Text.literal(stemName + featherName + "Arrow" + potionName)
                        .styled(style -> style.withItalic(false))
        );

        // ---- Lore ----
        var displayTag = arrow.getOrCreateSubNbt("display");
        NbtList loreList = new NbtList();

        for (String loreLine : potionLore) {
            loreList.add(NbtString.of(Text.Serializer.toJson(
                    Text.literal(loreLine).styled(style -> style.withItalic(false))
            )));
        }

        if (!featherLore.isEmpty()) {
            loreList.add(NbtString.of(Text.Serializer.toJson(
                    Text.literal(featherLore).styled(style -> style.withItalic(false))
            )));
        }

        if (!stemLore.isEmpty()) {
            loreList.add(NbtString.of(Text.Serializer.toJson(
                    Text.literal(stemLore).styled(style -> style.withItalic(false))
            )));
        }

        displayTag.put("Lore", loreList);

        source.getPlayer().getInventory().insertStack(arrow);
        source.sendFeedback(Text.literal("Gave OP Arrow x" + count + "!").formatted(Formatting.GREEN), false);

        return 1;
    }

}
