package net.daplow.daplowfletch.screen;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import net.daplow.daplowfletch.data.FletchingPotionData;
import net.daplow.daplowfletch.data.FletchingTableWorldData;
import net.daplow.daplowfletch.item.ModItems;
import net.daplow.daplowfletch.item.arrow.SpawnOpArrow;
import net.daplow.daplowfletch.other.recipes.FeatherRecipe;
import net.daplow.daplowfletch.other.recipes.FeatherRecipeManager;
import net.daplow.daplowfletch.other.recipes.StemRecipe;
import net.daplow.daplowfletch.other.recipes.StemRecipeManager;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.PotionItem;
import net.minecraft.nbt.NbtCompound;
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
import java.util.Map;
import java.util.Optional;

import static net.daplow.daplowfletch.other.network.ModPackets.*;
import static net.daplow.daplowfletch.other.recipes.StemRecipeManager.RECIPES;

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
                return StemRecipeManager.getOpt(stack.getItem()).isPresent();
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
        PotionLeft = PotionData.getPotionLeft();


        boolean Potion_Hanji = false;
        boolean Stem_Hanji = false;
        boolean Other_Hanji = false;

        int addon1 = 0;
        int addon2 = 0;
        int addon3 = 0;

        String string_stem = "";
        String string_potion = "";
        String string_feather = "";


        String potion_lore = "";
        String potion_duration_lore = "";
        String feather_lore = "";
        String stem_lore = "";

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

        String potionId = PotionID;

        if (!(PotionLeft > 0)) {
            potionId = "none";
        }
        ItemStack result = ItemStack.EMPTY;
        if (in0.getItem() == Items.ARROW) {
            result = SpawnOpArrow.getStack(potionId, in2, in3, 1);
        }

        if (in2.isEmpty() && in3.isEmpty() && PotionLeft <1) {
            result = ItemStack.EMPTY;
        }

        FeatherRecipe featherRecipe =
                FeatherRecipeManager.get(in3.getItem());
        if (featherRecipe == null && !in3.isEmpty()) {
            result = ItemStack.EMPTY;
        }


        if (!ItemStack.areEqual(current, result) && !ItemStack.areNbtEqual(current, result)) {
            fletchingInventory.setStack(4, result);

        }

        savePotionData();
        updatingOutput = false;
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
                } else if (StemRecipeManager.getOpt(stack.getItem()).isPresent()) {
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
