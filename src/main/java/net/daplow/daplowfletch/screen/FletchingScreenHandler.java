package net.daplow.daplowfletch.screen;

import net.daplow.daplowfletch.block.FletchingTableBlockEntity;
import net.daplow.daplowfletch.item.ModItems;
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
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.slot.Slot;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import org.apache.logging.log4j.core.jmx.Server;

public class FletchingScreenHandler extends ScreenHandler {

    private final PlayerInventory playerInventory;
    private final SimpleInventory fletchingInventory; // 4 inputs + 1 output
    private final ScreenHandlerContext context;
    private String storedPotion = null;
    private int PotionLeft = 5;
    int PotionColor = 0xFF385DC6;
    //private final FletchingTableBlockEntity fletchingBE;
    private String PotionID = "minecraft.water";



    public FletchingScreenHandler(int syncId, PlayerInventory playerInventory, ScreenHandlerContext context) {
        super(ModScreenHandler.CUSTOM_FLETCHING, syncId);
        this.playerInventory = playerInventory;
        this.context = context;

        context.run((world, pos) -> {
            BlockEntity be = world.getBlockEntity(pos);
            if (be instanceof FletchingTableBlockEntity fletchingBE) {
                // use it safely
            }
        });
        System.out.println("Server FletchingScreenHandler OPEND");

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

        return PotionLeft;
    }

    public int getPotionColor() {
        return PotionColor;
    }

    public Text getDisplayName() {
        return Text.translatable("container.fletching"); // vanilla title
    }
}
