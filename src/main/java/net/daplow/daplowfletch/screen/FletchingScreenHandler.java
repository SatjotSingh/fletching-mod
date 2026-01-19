package net.daplow.daplowfletch.screen;

import net.daplow.daplowfletch.data.FletchingPotionData;
import net.daplow.daplowfletch.data.FletchingTableWorldData;
import net.daplow.daplowfletch.item.ModItems;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.PotionItem;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionUtil;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;

import java.util.ArrayList;
import java.util.List;

import static net.daplow.daplowfletch.network.ModPackets.*;

public class FletchingScreenHandler extends ScreenHandler {

    private final PlayerInventory playerInventory;
    private final SimpleInventory fletchingInventory;
    private String storedPotion = null;
    private FletchingPotionData PotionData;
    private final BlockPos tablePos;

    public int ClientColor = 0xFF385DC6;
    public int ClientHeight = 0;

    private int PotionLeft = 0;
    private int PotionColor = 0xFF385DC6;
    private String PotionID = "minecraft.water";


    public FletchingScreenHandler(int syncId, PlayerInventory playerInventory, BlockPos tablePos) {
        super(ModScreenHandler.CUSTOM_FLETCHING, syncId);
        this.playerInventory = playerInventory;
        this.tablePos = tablePos;

        this.fletchingInventory = new SimpleInventory(5) {
            @Override
            public void markDirty() {
                super.markDirty();
                onContentChanged(this);
            }
        };

        this.addSlot(new Slot(fletchingInventory, 0, 26, 43) {// arrow
            @Override
            public boolean canInsert(ItemStack stack) {
                return stack.getItem() == Items.ARROW;
            }

            @Override
            public int getMaxItemCount() {
                return 64;
            }
        });

        this.addSlot(new Slot(fletchingInventory, 1, 54, 24) { // potion
            @Override
            public boolean canInsert(ItemStack stack) {
                return stack.getItem() instanceof PotionItem;
            }

            @Override
            public int getMaxItemCount() {
                return 1; // max of 1 potion
            }
        });
        this.addSlot(new Slot(fletchingInventory, 2, 80, 43) {
            @Override
            public boolean canInsert(ItemStack stack) {
                return stack.isOf(Items.GOLD_NUGGET) || stack.isOf(Items.IRON_NUGGET);
            }
        });
        this.addSlot(new Slot(fletchingInventory, 3, 80, 62));

        // Output slot (read-only)
        this.addSlot(new Slot(fletchingInventory, 4, 134, 43) {
            @Override
            public boolean canInsert(ItemStack stack) {
                return false;
            }
            @Override
            public void onTakeItem(PlayerEntity player, ItemStack stack) {
                super.onTakeItem(player, stack);

                ItemStack item = fletchingInventory.getStack(4);

                fletchingInventory.removeStack(0, 1); // arrow
                fletchingInventory.removeStack(2, 1); // nugget
                fletchingInventory.removeStack(3, 1); // feather

                if (PotionLeft > 0) {
                    PotionLeft--;
                }


                savePotionData();
                onContentChanged(fletchingInventory);

            }
        });

        addPlayerInventory(playerInventory);
    }


    public void loadPotionDataServer() {
        if (playerInventory.player.getWorld().isClient) return; // skip client
        ServerWorld world = (ServerWorld) playerInventory.player.getWorld();
        FletchingTableWorldData worldData = FletchingTableWorldData.get(world);

        this.PotionData = worldData.getOrCreate(tablePos);
        if (PotionData == null) return;
        this.PotionLeft = PotionData.getPotionLeft();
        this.PotionColor = PotionData.getPotionColor();
        this.PotionID = PotionData.getPotionID();

        this.ClientColor = PotionData.getPotionColor();
        this.ClientHeight = PotionData.getPotionLeft();
    }


    private void addPlayerInventory(PlayerInventory inventory) {
        int startX = 8;
        int startY = 84;

        // Main inventory
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                this.addSlot(new Slot(inventory, col + row * 9 + 9, startX + col * 18, startY + row * 18));
            }
        }

        // Hotbar
        for (int col = 0; col < 9; col++) {
            this.addSlot(new Slot(inventory, col, startX + col * 18, startY + 58));
        }
    }


    private boolean updatingOutput = false;

    @Override
    public void onContentChanged(Inventory inventory) {
        super.onContentChanged(inventory);

        if (updatingOutput) return;
        if (inventory != fletchingInventory) return;
        if (playerInventory.player.getWorld().isClient) return;
        updatingOutput = true;
        ItemStack current = fletchingInventory.getStack(4);
        ItemStack newOutput = ItemStack.EMPTY;

        ItemStack in0 = fletchingInventory.getStack(0);
        ItemStack potionSlot = fletchingInventory.getStack(1);
        ItemStack in2 = fletchingInventory.getStack(2);
        ItemStack in3 = fletchingInventory.getStack(3);

        boolean Potion_Hanji = false;
        boolean Stem_Hanji = false;
        boolean Other_Hanji = false;
        PotionLeft = PotionData.getPotionLeft();

        int addon1 = 0;
        int addon2 = 0;
        int addon3 = 0;

        String string_stem = "";
        String string_potion = "";
        String string_feather = "";


        String potion_lore = "";
        String potion_duration_lore = "";
        String feather_lore = "";

        List<String> all_potion_lore = new ArrayList<>();

        if (!potionSlot.isEmpty() && potionSlot.getItem() instanceof PotionItem && storedPotion == null) {

            if (PotionLeft == 0) {
                if (potionSlot.hasNbt() && potionSlot.getNbt().contains("Potion")) {
                    PotionID = potionSlot.getNbt().getString("Potion");
                } else {
                    PotionID = PotionUtil.getPotion(potionSlot).getClass().toString();
                }

                PotionLeft = 8;
                PotionColor = PotionUtil.getColor(potionSlot);

                potionSlot.decrement(1);
                fletchingInventory.setStack(1, Items.GLASS_BOTTLE.getDefaultStack());
            }
        }

        if (in0.getItem() == Items.ARROW) {
            newOutput = new ItemStack(ModItems.OP_ARROW[0]);
            if (PotionLeft > 0) {
                String potionId = "minecraft:water";
                potionId = PotionID;
                Potion_Hanji = true;
                newOutput.getOrCreateNbt().putString("Potion", potionId);
                newOutput.getOrCreateNbt().putBoolean("potion", true);
                addon2 = 1;

                ItemStack tempPotionStack = new ItemStack(Items.POTION);

                Potion potion = Registry.POTION.get(new Identifier(potionId));
                PotionUtil.setPotion(tempPotionStack, potion);

                string_potion =
                        " of " + tempPotionStack.getName().getString().replace("Potion of ", "");


                for (StatusEffectInstance effect : PotionUtil.getPotionEffects(tempPotionStack)) {
                    potion_lore =
                            Text.translatable(effect.getEffectType().getTranslationKey()).getString();
                    int amplifier = effect.getAmplifier();
                    if (amplifier > 0) {
                        potion_lore += " " + toRoman(amplifier + 1);
                    }
                    int potionTime = Math.max(effect.getDuration() / 8, 20);
                    int totalSeconds = potionTime / 20;
                    int minutes = totalSeconds / 60;
                    int seconds = totalSeconds % 60;

                    potion_duration_lore =
                            String.format("%d:%02d", minutes, seconds);
                    String finalLoreLine = "of Unknown Potion";
                    if (effect.getEffectType().isInstant()) {
                        finalLoreLine = potion_lore;
                    } else {
                        finalLoreLine = potion_lore + " (" + potion_duration_lore + ")";
                    }
                    all_potion_lore.add(finalLoreLine);
                }




            }

            if (in2.getItem() == Items.GOLD_NUGGET) {
                newOutput.getOrCreateNbt().putString("stem_variant", "gold");
                newOutput.getOrCreateNbt().putBoolean("stem", true);
                Stem_Hanji = true;
                addon1 = 1;
                string_stem = "Gilded ";
            }
            if (in2.getItem() == Items.IRON_NUGGET) {
                newOutput.getOrCreateNbt().putString("stem_variant", "iron");
                newOutput.getOrCreateNbt().putBoolean("stem", true);
                Stem_Hanji = true;
                addon1 = 1;
                string_stem = "Iron ";

            }

            if (in3.getItem() == Items.TNT) {
                newOutput.getOrCreateNbt().putBoolean("hasFeatherLayer", true);
                newOutput.getOrCreateNbt().putString("feather", "tnt");
                Other_Hanji = true;
                addon3 = 1;
                string_feather = "TNT ";
                feather_lore = "Kaboom on impact";

            } else if (in3.getItem() == Items.PRISMARINE_SHARD) {
                newOutput.getOrCreateNbt().putBoolean("hasFeatherLayer", true);
                newOutput.getOrCreateNbt().putString("feather", "prismarine");
                Other_Hanji = true;
                addon3 = 2;
                string_feather = "Water ";
                feather_lore = "Can be used in Water";
            }
            else if (in3.getItem() == Items.FIRE_CHARGE) {
                newOutput.getOrCreateNbt().putBoolean("hasFeatherLayer", true);
                newOutput.getOrCreateNbt().putString("feather", "fire");
                Other_Hanji = true;
                addon3 = 4;
                string_feather = "Flaming ";
                feather_lore = "Increased Fire Damage";
            }
            else if (in3.getItem() == Items.ENDER_PEARL) {
                newOutput.getOrCreateNbt().putBoolean("hasFeatherLayer", true);
                newOutput.getOrCreateNbt().putString("feather", "enderpearl");
                Other_Hanji = true;
                addon3 = 5;
                string_feather = "Warping ";
                feather_lore = "Teleports player to location of arrow";
            }
            else if (in3.getItem() == Items.ENDER_EYE) {
                newOutput.getOrCreateNbt().putBoolean("hasFeatherLayer", true);
                newOutput.getOrCreateNbt().putString("feather", "endereye");
                Other_Hanji = true;
                addon3 = 6;
                string_feather = "Homing ";
                feather_lore = "Homes into nearby targets";
            }

            else {
                if (!in3.isEmpty()) {
                Potion_Hanji = false;
                Stem_Hanji = false;
                Other_Hanji = false;
            }
            }

        }


        int index = addon1 * 20 + addon2 * 10 + addon3; // 0..39
        ItemStack result = new ItemStack(ModItems.OP_ARROW[index]);

        if (newOutput.hasNbt()) {
            result.setNbt(newOutput.getNbt().copy());
        }
        result.getOrCreateNbt().putInt("index", index);
        result.setCustomName(Text.literal(string_stem + string_feather + "Arrow" + string_potion).styled(style -> style.withItalic(false)));

        var displayTag = result.getOrCreateSubNbt("display");
        NbtList loreList = new NbtList();
        if (!(potion_lore=="")) {

            for (String loreLine : all_potion_lore) {
                loreList.add(
                        NbtString.of(
                                Text.Serializer.toJson(
                                        Text.literal(loreLine)
                                                .styled(style -> style.withItalic(false))
                                )
                        )
                );
            }        }
        if (!(feather_lore=="")) {
            loreList.add(NbtString.of(Text.Serializer.toJson(Text.literal(feather_lore).styled(style -> style.withItalic(false)))));
        }
        displayTag.put("Lore", loreList);

        if (!Potion_Hanji & !Stem_Hanji & !Other_Hanji) {
            result = ItemStack.EMPTY;
        }
        if (index==0) {
            result = ItemStack.EMPTY;
        }

        if (!ItemStack.areEqual(current, result) && !ItemStack.areNbtEqual(current, result)) {
            fletchingInventory.setStack(4, result);

        }

        savePotionData();
        updatingOutput = false;
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

    @Override
    public ItemStack transferSlot(PlayerEntity player, int index) {
        // For shift clicking
        ItemStack result = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);

        if (slot != null && slot.hasStack()) {
            ItemStack stack = slot.getStack();
            result = stack.copy();

            if (index >= 0 && index <= 3) {
                if (!this.insertItem(stack, 5, 41, false)) { // 5 = player inventory start, 41 = end
                    return ItemStack.EMPTY;
                }
                slot.onTakeItem(player, stack);
            }
            if (index == 4) {
                if (!this.insertItem(stack, 5, 41, true)) { // 5 = player inventory start, 41 = end
                    return ItemStack.EMPTY;
                }
                slot.onTakeItem(player, stack);
            }

            else if (index >= 5) {
                boolean moved = false;

                if (stack.getItem() == Items.ARROW) {
                    moved = this.insertItem(stack, 0, 1, false);
                } else if (stack.getItem() instanceof PotionItem) {
                    moved = this.insertItem(stack, 1, 2, false);
                } else if (stack.getItem()==Items.IRON_NUGGET||stack.getItem()==Items.GOLD_NUGGET) {
                    moved = this.insertItem(stack, 2, 3, false);
                } else {
                    moved = this.insertItem(stack, 3, 4, false);
                }

                if (!moved) return ItemStack.EMPTY;
            }

            if (stack.isEmpty()) {
                slot.setStack(ItemStack.EMPTY);
            } else {
                slot.markDirty();
            }
        }
        return result;

    }

    @Override
    public void close(PlayerEntity player) {
        super.close(player);
        // Give back items when closed
        for (int i = 0; i < 4; i++) { // input slots only
            ItemStack stack = fletchingInventory.getStack(i);
            if (!stack.isEmpty()) {
                player.getInventory().offerOrDrop(stack);
                fletchingInventory.setStack(i, ItemStack.EMPTY);
            }
        }
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return true;
    }


    public void savePotionData() {
        if (playerInventory.player.getWorld().isClient) return;

        PotionData.setPotionLeft(PotionLeft);
        PotionData.setPotionColor(PotionColor);
        ServerWorld world = (ServerWorld) playerInventory.player.getWorld();
        if (PotionData == null) return;

        // Update the stored objectB
        PotionData.setPotionID(PotionID);


        this.ClientColor = PotionData.getPotionColor();
        this.ClientHeight = PotionData.getPotionLeft();

        syncToClient();

        FletchingTableWorldData.get(world).markDirtyForSave();

    }
    public void syncToClient() {

        if (playerInventory.player.getWorld().isClient) return;
        if (!(playerInventory.player instanceof ServerPlayerEntity serverPlayer)) return;
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeInt(ClientHeight);
        buf.writeInt(ClientColor);

        ServerPlayNetworking.send(serverPlayer, POTION_INFO, buf);
    }

    @Override
    public void sendContentUpdates() {
        super.sendContentUpdates();
        if (playerInventory.player instanceof ServerPlayerEntity serverPlayer) {
            PacketByteBuf buf = PacketByteBufs.create();
            buf.writeInt(ClientHeight);
            buf.writeInt(ClientColor);
            ServerPlayNetworking.send(serverPlayer, POTION_INFO, buf);
        }
    }
}
