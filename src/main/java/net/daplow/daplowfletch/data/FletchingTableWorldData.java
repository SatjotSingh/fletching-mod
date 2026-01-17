package net.daplow.daplowfletch.data;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.PersistentState;

import java.util.HashMap;
import java.util.Map;

public class FletchingTableWorldData extends PersistentState {
    private final Map<BlockPos, FletchingPotionData> tableData = new HashMap<>();

    public FletchingTableWorldData() {
        super();
    }

    // Serialize all table data
    @Override
    public NbtCompound writeNbt(NbtCompound tag) {
        NbtList list = new NbtList();
        for (Map.Entry<BlockPos, FletchingPotionData> entry : tableData.entrySet()) {
            BlockPos pos = entry.getKey();
            FletchingPotionData data = entry.getValue();

            NbtCompound entryTag = new NbtCompound();
            entryTag.putInt("x", pos.getX());
            entryTag.putInt("y", pos.getY());
            entryTag.putInt("z", pos.getZ());
            entryTag.putString("potionID", data.getPotionID());
            entryTag.putInt("potionLeft", data.getPotionLeft());
            entryTag.putInt("potionColor", data.getPotionColor());

            list.add(entryTag);
        }
        tag.put("tables", list);
        return tag;
    }

    // Deserialize all table data
    public void readNbt(NbtCompound tag) {
        tableData.clear();
        NbtList list = tag.getList("tables", NbtCompound.COMPOUND_TYPE);
        for (int i = 0; i < list.size(); i++) {
            NbtCompound entryTag = list.getCompound(i);
            BlockPos pos = new BlockPos(entryTag.getInt("x"), entryTag.getInt("y"), entryTag.getInt("z"));
            FletchingPotionData data = new FletchingPotionData(
                    entryTag.getString("potionID"),
                    entryTag.getInt("potionLeft"),
                    entryTag.getInt("potionColor")

            );
            tableData.put(pos, data);
        }
    }

    // ✅ Correct 3-argument getOrCreate for 1.19.2
    public static FletchingTableWorldData get(ServerWorld world) {
        return world.getPersistentStateManager().getOrCreate(
                nbt -> {
                    FletchingTableWorldData data = new FletchingTableWorldData();
                    data.readNbt(nbt);
                    return data;
                },
                FletchingTableWorldData::new,
                "daplow_fletching_tables"
        );

    }

    // Get or create potion data for a table position
    public FletchingPotionData getOrCreate(BlockPos pos) {
        return tableData.computeIfAbsent(pos, p -> new FletchingPotionData("minecraft:water", 0, 0xFF385DC6));
    }

    // Optional: mark dirty to ensure save
    public void markDirtyForSave() {
        this.markDirty();
    }

    public void remove(BlockPos pos) {
        tableData.remove(pos);
    }
}
