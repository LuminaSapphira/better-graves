package bettergraves.block;

import bettergraves.BetterGraves;
import com.google.common.collect.ImmutableMap;
import javax.annotation.Nullable;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.nbt.Tag;

import java.util.*;
import java.util.function.BiFunction;

public class BetterGraveBE extends BlockEntity {

    private Tag storedInventory = null;
    private Map<String, ImmutableMap<Integer, ItemStack>> customInventories = new HashMap<>();
    private UUID player = null;

    public BetterGraveBE() {
        super(BetterGraves.BETTER_GRAVE_BE_TYPE);

    }

    @Override
    public void fromTag(CompoundTag tag) {
        super.fromTag(tag);
        if (!tag.contains("Player")) return;
        player = NbtHelper.toUuid(tag.getCompound("Player"));
        if (tag.contains("PlayerInventory"))
            storedInventory = tag.get("PlayerInventory").copy();
        if (tag.contains("CustomInventoryCount")) {
            int cCount = tag.getInt("CustomInventoryCount");
            ListTag cListTag = tag.getList("CustomInventories", 10);
            for (int i = 0; i < cCount; ++i) {
                CompoundTag customTag = cListTag.getCompound(i);
                String key = customTag.getString("Key");
                CompoundTag items = customTag.getCompound("Items");
                ImmutableMap<Integer, ItemStack> itemStackMap = deserializeMap(items);
                this.customInventories.put(key, itemStackMap);
            }
        }
    }

    @Override
    public CompoundTag toTag(CompoundTag tag) {
        if (storedInventory != null) {
            tag.put("PlayerInventory", storedInventory);
            tag.put("Player", NbtHelper.fromUuid(player));
        }
        if (customInventories.size() > 0) {
            tag.putInt("CustomInventoryCount", customInventories.size());
            ListTag cListTag = new ListTag();
            customInventories.forEach((key, map) -> {
                CompoundTag customTag = new CompoundTag();
                customTag.putString("Key", key);
                CompoundTag items = new CompoundTag();
                serializeMap(map, items);
                customTag.put("Items", items);
                cListTag.add(customTag);
            });
            tag.put("CustomInventories", cListTag);
        }
        return super.toTag(tag);
    }

    private static void serializeMap(Map<Integer, ItemStack> map, CompoundTag tag) {
        tag.putInt("Count", map.size());
        int[] slots = new int[map.size()];
        ListTag items = new ListTag();
        int i = 0;
        for (int slot : map.keySet()) {
            slots[i] = slot;
            CompoundTag item = map.get(slot).toTag(new CompoundTag());
            items.add(i, item);
            ++i;
        }
        tag.putIntArray("Slots", slots);
        tag.put("Items", items);
    }

    private static ImmutableMap<Integer, ItemStack> deserializeMap(CompoundTag tag) {
        int count = tag.getInt("Count");
        int[] slots = tag.getIntArray("Slots");
        ListTag items = tag.getList("Items", 10);

        ImmutableMap.Builder<Integer, ItemStack> map = ImmutableMap.builder();
        for (int i = 0; i < count; ++i) {
            ItemStack stack = ItemStack.fromTag(items.getCompound(i));
            map.put(slots[i], stack);
        }
        return map.build();
    }

    public Tag getStoredPlayerInventory() {
        return storedInventory;
    }

    public void storeInventory(PlayerInventory playerInventory) {
        this.storedInventory = playerInventory.serialize(new ListTag());
        this.player = playerInventory.player.getGameProfile().getId();
    }

    public void storeInventory(String key, Map<Integer, ItemStack> inventory) {
        ImmutableMap.Builder<Integer, ItemStack> nMap = ImmutableMap.builder();
        inventory.forEach((slot, stack) -> nMap.put(slot, stack.copy()));
        this.customInventories.put(key, nMap.build());
    }

    public void restoreInventory(PlayerEntity player) {
        BetterGraves.restoreHandlers.forEach((key, handler) -> {
            handler.restoreItems(getStoredCustomInventory(key));
        });
        PlayerInventory old = new PlayerInventory(player);
        old.clone(player.inventory);
        player.inventory.deserialize((ListTag)getStoredPlayerInventory());
        for (int i = 0; i < old.getInvSize(); ++i) {
            player.inventory.offerOrDrop(player.world, old.getInvStack(i));
        }
    }

    @Nullable
    public Map<Integer, ItemStack> getStoredCustomInventory(String key) {
        return this.customInventories.getOrDefault(key, null);
    }

    public boolean doesPlayerMatch(PlayerEntity player) {
        return player.getGameProfile().getId().equals(this.player);
    }

}
