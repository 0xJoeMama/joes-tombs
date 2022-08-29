package io.github.joemama.tomb.joestombs.common.blockentity;

import io.github.joemama.tomb.joestombs.common.lib.PlayerDeath;
import io.github.joemama.tomb.joestombs.common.register.ModRegistry;
import org.jetbrains.annotations.Nullable;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;

public class TombBlockEntity extends BlockEntity {
    @Nullable
    private PlayerDeath death;

    public TombBlockEntity(BlockPos pos, BlockState state) {
        super(ModRegistry.TOMB_BLOCK_ENTITY, pos, state);
    }

    public void setDeath(@Nullable PlayerDeath death) {
        this.death = death;
    }

    public void acceptPlayer(ServerPlayerEntity player) {
        if (this.death == null) {
            player.world.removeBlock(this.pos, false);
        }

        if (this.death.id().equals(player.getUuid())) {
            PlayerInventory inventory = player.getInventory();

            for (PlayerDeath.ItemEntry entry : this.death.items()) {
                ItemStack stack = inventory.getStack(entry.slot());

                if (stack.isEmpty()) {
                    inventory.setStack(entry.slot(), entry.stack());
                } else {
                    inventory.offerOrDrop(entry.stack());
                }
            }

            this.death.experience().apply(player);
            player.world.removeBlock(this.pos, false);
        }
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        this.death = PlayerDeath.fromNbt(nbt.getCompound("Death"));
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        if (this.death != null) {
            nbt.put("Death", this.death.toNbt());
        }
    }
}
