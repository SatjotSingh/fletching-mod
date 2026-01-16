package net.daplow.daplowfletch.screen;

import io.netty.buffer.Unpooled;
import net.daplow.daplowfletch.block.FletchingTableBlockEntity;
import net.daplow.daplowfletch.data.FletchingPotionData;
import net.daplow.daplowfletch.data.FletchingTableWorldData;
import net.daplow.daplowfletch.item.ModItems;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.PotionItem;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.potion.PotionUtil;
import net.minecraft.screen.ArrayPropertyDelegate;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.slot.Slot;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import org.apache.logging.log4j.core.jmx.Server;

import static net.daplow.daplowfletch.network.ModPackets.*;

public class FletchingScreenHandler extends ScreenHandler {

    private final PlayerInventory playerInventory;
    private final SimpleInventory fletchingInventory; // 4 inputs + 1 output
    private String storedPotion = null;

    private FletchingPotionData PotionData;
    private final BlockPos tablePos;

    public int ClientColor = 0xFF385DC6;
    public int ClientHeight = 5;

    private int PotionLeft = 5;
    private int PotionColor = 0xFF385DC6;
    private String PotionID = "minecraft.water";


    public FletchingScreenHandler(int syncId, PlayerInventory playerInventory, BlockPos tablePos) {
        super(ModScreenHandler.CUSTOM_FLETCHING, syncId);
        this.playerInventory = playerInventory;
        this.tablePos = tablePos;
// After creating handler on server

        /*
        if (!playerInventory.player.getWorld().isClient()) {
            ServerWorld world = (ServerWorld) playerInventory.player.getWorld();
            FletchingTableWorldData worldData = FletchingTableWorldData.get(world);

            // Use getOrCreate so we never get null
            PotionData = FletchingTableWorldData.get(world).getOrCreate(tablePos);


            // Copy values to your handler fields
            PotionLeft = this.PotionData.getPotionLeft();
            PotionColor = this.PotionData.getPotionColor();
            PotionID = this.PotionData.getPotionID();
            System.out.println("Potion Saved");
            System.out.print(PotionLeft);
            System.out.print(PotionColor);
            System.out.print(PotionID);

            worldData.markDirtyForSave();
        } else {
            System.out.println("Yo we got client doing somthing");
        }
*/

        // Safe inventory: markDirty only triggers onContentChanged
        this.fletchingInventory = new SimpleInventory(5) {
            @Override
            public void markDirty() {
                super.markDirty();
                onContentChanged(this);
            }
        };

        // Input slots
        this.addSlot(new Slot(fletchingInventory, 0, 26, 43)); // arrow
        this.addSlot(new Slot(fletchingInventory, 1, 54, 24) { // potion
            @Override
            public boolean canInsert(ItemStack stack) {
                return stack.getItem() instanceof PotionItem;
            }

            @Override
            public int getMaxItemCount() {
                return 1; // Only store one potion
            }
        });
        this.addSlot(new Slot(fletchingInventory, 2, 80, 43));
        this.addSlot(new Slot(fletchingInventory, 3, 80, 62));

        // Output slot (read-only)
        this.addSlot(new Slot(fletchingInventory, 4, 134, 43) {
            @Override
            public boolean canInsert(ItemStack stack) {
                return false;
            }
        });

        // Player inventory
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



        System.out.println("Potion Loaded: " + PotionLeft + ", " + PotionColor + ", " + PotionID);

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

    public SimpleInventory getFletchingInventory() {
        return fletchingInventory;
    }

    private boolean updatingOutput = false;

    @Override
    public void onContentChanged(Inventory inventory) {
        super.onContentChanged(inventory);

        if (updatingOutput) return; // Prevent recursion
        if (inventory != fletchingInventory) return;
        if (playerInventory.player.getWorld().isClient) return;
        updatingOutput = true; // start updating
        ItemStack current = fletchingInventory.getStack(4); // output
        ItemStack newOutput = ItemStack.EMPTY;

        ItemStack in0 = fletchingInventory.getStack(0);
        ItemStack potionSlot = fletchingInventory.getStack(1);
        ItemStack in2 = fletchingInventory.getStack(2);
        ItemStack in3 = fletchingInventory.getStack(3);

        boolean Potion_Hanji = false;
        boolean Stem_Hanji = false;
        boolean Other_Hanji = false;

        System.out.println("Potion Checked: " + PotionLeft + ", " + PotionColor + ", " + PotionID);
        System.out.println("Potion Truth: " + PotionData.getPotionLeft() + ", " +PotionData.getPotionColor()  + ", " + PotionData.getPotionID() );
        PotionLeft = PotionData.getPotionLeft();


        // Get Potion
        if (!potionSlot.isEmpty() && potionSlot.getItem() instanceof PotionItem && storedPotion == null) {

            if (potionSlot.hasNbt() && potionSlot.getNbt().contains("Potion")) {
                PotionID = potionSlot.getNbt().getString("Potion");
            } else {
                // Some potion items (like Night Vision) might not have NBT until created
                // Get the potion id from PotionUtil
                PotionID = PotionUtil.getPotion(potionSlot).getClass().toString();
            }

            PotionLeft = 8;
            PotionColor = PotionUtil.getColor(potionSlot);

            // Consume potion
            potionSlot.decrement(1);


        }

        // Recipe logic: arrow + potion + gold nugget + feather
        if (in0.getItem() == Items.ARROW) {
            newOutput = new ItemStack(ModItems.OP_ARROW);



            //get Potion
            if (PotionLeft > 0) {
                String potionId = "minecraft:water"; // default fallback


                potionId = PotionID;

                Potion_Hanji = true;
                // Set the "Potion" tag as a STRING on the arrow
                newOutput.getOrCreateNbt().putString("Potion", potionId);
                newOutput.getOrCreateNbt().putBoolean("potion", true);
            }
            /*
            // in1 = the slot with potion
            if (in1.getItem() instanceof PotionItem) {
                String potionId = "minecraft:water"; // default fallback

                // Check if in1 has Potion NBT
                if (in1.hasNbt() && in1.getNbt().contains("Potion")) {
                    potionId = in1.getNbt().getString("Potion");
                } else {
                    // Some potion items (like Night Vision) might not have NBT until created
                    // Get the potion id from PotionUtil
                    potionId = PotionUtil.getPotion(in1).getClass().toString();
                }

                Potion_Hanji = true;
                // Set the "Potion" tag as a STRING on the arrow
                newOutput.getOrCreateNbt().putString("Potion", potionId);
                newOutput.getOrCreateNbt().putBoolean("potion", true);
            }
            */


            if (in2.getItem() == Items.GOLD_NUGGET) {
                newOutput.getOrCreateNbt().putString("stem_variant", "gold");
                newOutput.getOrCreateNbt().putBoolean("stem", true);
                Stem_Hanji = true;
            }
            if (in2.getItem() == Items.IRON_NUGGET) {
                newOutput.getOrCreateNbt().putString("stem_variant", "iron");
                newOutput.getOrCreateNbt().putBoolean("stem", true);
                Stem_Hanji = true;
            }

            if (in3.getItem() == Items.FEATHER) {
                newOutput.getOrCreateNbt().putBoolean("hasFeatherLayer", true);
                Other_Hanji = true;
            }
        }

        if (Potion_Hanji & Stem_Hanji) {
            newOutput.getOrCreateNbt().putBoolean("potion_stem", true);
        }

        if (!Potion_Hanji & !Stem_Hanji & !Other_Hanji) {
            newOutput = ItemStack.EMPTY;
        }
        // Only update output if item or NBT changed
        if (!ItemStack.areEqual(current, newOutput) && !ItemStack.areNbtEqual(current, newOutput)) {
            fletchingInventory.setStack(4, newOutput);

        }
        savePotionData();
        updatingOutput = false; // done updating
    }

    // Returns leftover items to player when GUI closes
    @Override
    public void close(PlayerEntity player) {
        super.close(player);

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

    @Override
    public ItemStack transferSlot(PlayerEntity player, int index) {
        return ItemStack.EMPTY; // disable shift-click for now
    }



    public int getPotionAmount() {
        return ClientHeight;
    }

    public int getPotionColor() {
        return ClientColor;
    }

    public Text getDisplayName() {
        return Text.translatable("container.fletching"); // vanilla title
    }

    public void savePotionData() {
        if (playerInventory.player.getWorld().isClient) return; // ❌ client does nothing

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

        System.out.println("Saved Potion Info Succefully");
    }
    public void syncToClient() {

        System.out.println("Synce Started 1");
        if (playerInventory.player.getWorld().isClient) return; // client does nothing
        System.out.println("Synce Started 2");
        // Ensure the player is a server player
        if (!(playerInventory.player instanceof ServerPlayerEntity serverPlayer)) return;
        System.out.println(ClientColor + ", " + ClientHeight);
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeInt(ClientHeight);
        buf.writeInt(ClientColor);

        ServerPlayNetworking.send(serverPlayer, POTION_INFO, buf);
        System.out.println("Synce Started 4");
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
