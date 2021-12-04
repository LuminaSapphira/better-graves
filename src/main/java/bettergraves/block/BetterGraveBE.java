package bettergraves.block;

import bettergraves.BetterGraves;
import bettergraves.api.BetterGravesAPI;
import com.google.common.collect.ImmutableMap;
import com.mojang.authlib.GameProfile;
import net.fabricmc.fabric.api.block.entity.BlockEntityClientSerializable;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.nbt.NbtList;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

public class BetterGraveBE extends BlockEntity implements BlockEntityClientSerializable {

    private NbtElement storedInventory = null;
    private Map<String, ImmutableMap<Integer, ItemStack>> customInventories = new HashMap<>();
    private GameProfile player = null;
    private boolean restored = false;

    public BetterGraveBE() {
        super(BetterGraves.BETTER_GRAVE_BE_TYPE);

    }

    @Override
    public void readNbt(BlockState state, NbtCompound tag) {
        super.readNbt(state, tag);
        if (!tag.contains("Player")) return;
        player = NbtHelper.toGameProfile(tag.getCompound("Player"));
        if (tag.contains("PlayerInventory"))
            storedInventory = tag.get("PlayerInventory").copy();
        if (tag.contains("CustomInventoryCount")) {
            int cCount = tag.getInt("CustomInventoryCount");
            NbtList cListTag = tag.getList("CustomInventories", 10);
            for (int i = 0; i < cCount; ++i) {
                NbtCompound customTag = cListTag.getCompound(i);
                String key = customTag.getString("Key");
                NbtCompound items = customTag.getCompound("Items");
                ImmutableMap<Integer, ItemStack> itemStackMap = deserializeMap(items);
                this.customInventories.put(key, itemStackMap);
            }
        }
    }

    @Override
    public NbtCompound writeNbt(NbtCompound tag) {
        if (storedInventory != null) {
            tag.put("PlayerInventory", storedInventory);
            tag.put("Player", NbtHelper.writeGameProfile(new NbtCompound(), player));
        }
        if (customInventories.size() > 0) {
            tag.putInt("CustomInventoryCount", customInventories.size());
            NbtList cListTag = new NbtList();
            customInventories.forEach((key, map) -> {
                NbtCompound customTag = new NbtCompound();
                customTag.putString("Key", key);
                NbtCompound items = new NbtCompound();
                serializeMap(map, items);
                customTag.put("Items", items);
                cListTag.add(customTag);
            });
            tag.put("CustomInventories", cListTag);
        }
        return super.writeNbt(tag);
    }

    private static void serializeMap(Map<Integer, ItemStack> map, NbtCompound tag) {
        tag.putInt("Count", map.size());
        int[] slots = new int[map.size()];
        NbtList items = new NbtList();
        int i = 0;
        for (int slot : map.keySet()) {
            slots[i] = slot;
            NbtCompound item = map.get(slot).writeNbt(new NbtCompound());
            items.add(i, item);
            ++i;
        }
        tag.putIntArray("Slots", slots);
        tag.put("Items", items);
    }

    private static ImmutableMap<Integer, ItemStack> deserializeMap(NbtCompound tag) {
        int count = tag.getInt("Count");
        int[] slots = tag.getIntArray("Slots");
        NbtList items = tag.getList("Items", 10);

        ImmutableMap.Builder<Integer, ItemStack> map = ImmutableMap.builder();
        for (int i = 0; i < count; ++i) {
            ItemStack stack = ItemStack.fromNbt(items.getCompound(i));
            map.put(slots[i], stack);
        }
        return map.build();
    }

    public NbtElement getStoredPlayerInventory() {
        return storedInventory;
    }

    public void storeInventory(PlayerInventory playerInventory) {
        this.storedInventory = playerInventory.writeNbt(new NbtList());
        // Copies the GameProfile
        this.player = NbtHelper.toGameProfile(NbtHelper.writeGameProfile(new NbtCompound(), playerInventory.player.getGameProfile()));
    }

    @Override
    public void setLocation(World world, BlockPos pos) {
        super.setLocation(world, pos);
        if (!world.isClient)
            sync();
    }

    public void storeInventory(String key, Map<Integer, ItemStack> inventory) {
        ImmutableMap.Builder<Integer, ItemStack> nMap = ImmutableMap.builder();
        inventory.forEach((slot, stack) -> nMap.put(slot, stack.copy()));
        this.customInventories.put(key, nMap.build());
    }

    public void restoreInventory(ServerPlayerEntity player) {
        BetterGravesAPI.restoreHandlers.forEach((key, handler) -> handler.restoreItems(player, getStoredCustomInventory(key)));
        PlayerInventory old = new PlayerInventory(player);
        old.clone(player.inventory);
        player.inventory.readNbt((NbtList)getStoredPlayerInventory());
        for (int i = 0; i < old.size(); ++i) {
            player.inventory.offerOrDrop(player.world, old.getStack(i));
        }
        restored = true;
    }

    @Nullable
    public Map<Integer, ItemStack> getStoredCustomInventory(String key) {
        return this.customInventories.getOrDefault(key, null);
    }

    public boolean doesPlayerMatch(PlayerEntity player) {
        return player.getGameProfile().getId().equals(this.player.getId());
    }

    public GameProfile getOwner() {
        return this.player;
    }

    @Override
    public void fromClientTag(NbtCompound compoundTag) {
        this.player = NbtHelper.toGameProfile(compoundTag.getCompound("Player"));
    }

    @Override
    public NbtCompound toClientTag(NbtCompound compoundTag) {
        compoundTag.put("Player", NbtHelper.writeGameProfile(new NbtCompound(), this.player));
        return compoundTag;
    }

    public boolean isRestored() { return restored; }
}
