package io.github.joemama.tomb.joestombs.common.lib;

import java.util.Arrays;
import java.util.UUID;

import net.fabricmc.fabric.api.util.NbtType;
import org.jetbrains.annotations.Nullable;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.server.network.ServerPlayerEntity;

public record PlayerDeath(UUID id, String name, ExperienceEntry experience, ItemEntry[] items) {
    public static PlayerDeath fromPlayer(ServerPlayerEntity serverPlayer) {
        var experience = new ExperienceEntry(serverPlayer.experienceLevel, serverPlayer.experienceProgress);
        String name = serverPlayer.getGameProfile().getName();

        PlayerInventory inventory = serverPlayer.getInventory();
        var items = new ItemEntry[inventory.size()];
        for (int i = 0; i < inventory.size(); i++) {
            var stackAtI = inventory.getStack(i);
            items[i] = new ItemEntry(stackAtI, i);
        }

        return new PlayerDeath(serverPlayer.getUuid(), name, experience, items);
    }

    public static @Nullable PlayerDeath fromNbt(NbtCompound nbt) {
        if (nbt.isEmpty()) {
            return null;
        }

        var id = nbt.getUuid("Id");
        var name = nbt.getString("Name");
        var experience = ExperienceEntry.fromNbt(nbt.getCompound("Experience"));
        var items = nbt.getList("Items", NbtType.COMPOUND).stream()
                       .map(it -> ((NbtCompound) it))
                       .map(ItemEntry::fromNbt)
                       .toArray(ItemEntry[]::new);

        return new PlayerDeath(id, name, experience, items);
    }

    public NbtCompound toNbt() {
        var nbt = new NbtCompound();

        nbt.putUuid("Id", this.id);
        nbt.putString("Name", this.name);
        nbt.put("Experience", this.experience.toNbt());
        nbt.put("Items", Arrays.stream(this.items)
                               .map(ItemEntry::toNbt)
                               .collect(NbtList::new, NbtList::add, NbtList::addAll));

        return nbt;
    }

    public record ExperienceEntry(int levels, float progress) {
        public static ExperienceEntry fromNbt(NbtCompound nbt) {
            return new ExperienceEntry(nbt.getInt("Levels"), nbt.getFloat("Progress"));
        }

        public NbtCompound toNbt() {
            var nbt = new NbtCompound();
            nbt.putInt("Levels", this.levels);
            nbt.putFloat("Progress", this.progress);
            return nbt;
        }

        public void apply(ServerPlayerEntity player) {
            player.setExperienceLevel(this.levels);
            player.setExperiencePoints((int) (this.progress * player.getNextLevelExperience()));
        }
    }

    public record ItemEntry(ItemStack stack, int slot) {
        public static ItemEntry fromNbt(NbtCompound nbt) {
            var stack = ItemStack.fromNbt(nbt.getCompound("Stack"));
            var slot = nbt.getInt("Slot");
            return new ItemEntry(stack, slot);
        }

        public NbtCompound toNbt() {
            var nbt = new NbtCompound();
            nbt.put("Stack", stack.writeNbt(new NbtCompound()));
            nbt.putInt("Slot", slot);
            return nbt;
        }
    }
}
