package net.daplow.daplowfletch.other;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import net.daplow.daplowfletch.item.ModItems;
import net.daplow.daplowfletch.item.arrow.SpawnOpArrow;
import net.daplow.daplowfletch.other.recipes.FeatherRecipe;
import net.daplow.daplowfletch.other.recipes.FeatherRecipeManager;
import net.daplow.daplowfletch.other.recipes.StemRecipe;
import net.daplow.daplowfletch.other.recipes.StemRecipeManager;
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
import java.util.Objects;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;
public class ModCommands {
    private static final String[] STEM_OPTIONS = {"none", "gold", "iron"};
    private static final String[] EXTRA_OPTIONS = {"none", "tnt", "prismarine", "fire", "enderpearl", "endereye"};

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {


        RequiredArgumentBuilder<ServerCommandSource, String> potionArg =
                argument("effect", StringArgumentType.word())
                        .suggests((context, builder) -> {
                            String typed = builder.getRemaining().toLowerCase();
                            if ("none".startsWith(typed)) {
                                builder.suggest("none");
                            }
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

                            if ("none".startsWith(typed)) {
                                builder.suggest("none");
                            }

                            for (StemRecipe recipe : StemRecipeManager.getRecipes()) {

                                Identifier id = Registry.ITEM.getId(recipe.getItem());

                                String name = id.getPath();

                                if (name.startsWith(typed)) {
                                    builder.suggest(name);
                                }
                            }

                            return builder.buildFuture();
                        });

        RequiredArgumentBuilder<ServerCommandSource, String> extraArg =
                argument("extra", StringArgumentType.word())
                        .suggests((context, builder) -> {
                            String typed = builder.getRemaining().toLowerCase();

                            if ("none".startsWith(typed)) {
                                builder.suggest("none");
                            }

                            for (FeatherRecipe recipe : FeatherRecipeManager.getRecipes()) {

                                Identifier id = Registry.ITEM.getId(recipe.getItem());

                                String name = id.getPath();

                                if (name.startsWith(typed)) {
                                    builder.suggest(name);
                                }
                            }

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


        Identifier potionIdentifier = Identifier.tryParse("minecraft:" + effectId);

        if (potionIdentifier == null && !Objects.equals(effectId, "none")) {
            source.sendFeedback(Text.literal("Invalid potion effect: " + effectId).formatted(Formatting.RED), false);
            return 0;
        }



        Identifier stemId = new Identifier("minecraft", stemArgValue);
        if (!Registry.ITEM.containsId(stemId) && !Objects.equals(stemArgValue, "none")) {
            source.sendFeedback(Text.literal("Invalid stem value: " + stemArgValue).formatted(Formatting.RED), false);
            return 0;
        }
        ItemStack stemItem = new ItemStack(Registry.ITEM.get(stemId));


        Identifier featherId = new Identifier("minecraft", extraArgValue);
        if (!Registry.ITEM.containsId(featherId) && !Objects.equals(extraArgValue, "none")) {
            source.sendFeedback(Text.literal("Invalid value: " + stemArgValue).formatted(Formatting.RED), false);
            return 0;
        }
        ItemStack featherItem = new ItemStack(Registry.ITEM.get(featherId));

        String potionId = "minecraft:" + effectId;

        ItemStack stack = SpawnOpArrow.getStack(potionId, stemItem, featherItem, count);

        source.getPlayer().getInventory().insertStack(stack);
        source.sendFeedback(Text.literal("Gave Op Arrow x" + count + "!").formatted(Formatting.GREEN), false);


        return 1;

    }


}
