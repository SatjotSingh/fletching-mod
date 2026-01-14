package net.daplow.daplowfletch.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;

public class FletchingTableBlockEntity extends BlockEntity implements Inventory {

    private final DefaultedList<ItemStack> inventory =
            DefaultedList.ofSize(4, ItemStack.EMPTY);

    private String storedPotion = null;

    public FletchingTableBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.FLETCHING_TABLE_BE, pos, state);
    }

    /* ---------- PERSISTENCE ---------- */

    @Override
    protected void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        Inventories.writeNbt(nbt, inventory);

        if (storedPotion != null) {
            nbt.putString("StoredPotion", storedPotion);
        }
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        Inventories.readNbt(nbt, inventory);

        if (nbt.contains("StoredPotion")) {
            storedPotion = nbt.getString("StoredPotion");
        }
    }

    /* ---------- POTION LOGIC ---------- */

    public boolean hasPotion() {
        return storedPotion != null;
    }

    public void setPotion(String potionId) {
        storedPotion = potionId;
        markDirty();
    }

    public String getPotion() {
        return storedPotion;
    }

    /* ---------- Inventory boilerplate ---------- */
    @Override public int size() { return inventory.size(); }
    @Override public boolean isEmpty() { return inventory.stream().allMatch(ItemStack::isEmpty); }
    @Override public ItemStack getStack(int slot) { return inventory.get(slot); }
    @Override public ItemStack removeStack(int slot, int amount) { return Inventories.splitStack(inventory, slot, amount); }
    @Override public ItemStack removeStack(int slot) { return Inventories.removeStack(inventory, slot); }
    @Override public void setStack(int slot, ItemStack stack) { inventory.set(slot, stack); }


    @Override public boolean canPlayerUse(PlayerEntity player) { return true; }
    @Override public void clear() { inventory.clear(); }
}
