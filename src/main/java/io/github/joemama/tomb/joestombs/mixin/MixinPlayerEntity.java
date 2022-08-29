package io.github.joemama.tomb.joestombs.mixin;

import io.github.joemama.tomb.joestombs.common.lib.PlayerDeath;
import io.github.joemama.tomb.joestombs.common.register.ModRegistry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

@Mixin(PlayerEntity.class)
public abstract class MixinPlayerEntity extends LivingEntity {
    protected MixinPlayerEntity(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    @Redirect(
            method = "dropInventory",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerInventory;dropAll()V")
    )
    private void createTomb(PlayerInventory instance) {
        if (!(((PlayerEntity) (Object) this) instanceof ServerPlayerEntity)) instance.dropAll();

        BlockPos.Mutable pos = this.getBlockPos().mutableCopy();

        while (!world.getBlockState(pos).isAir() && !world.getFluidState(pos).isEmpty()) {
            if (pos.getY() > world.getHeight()) {
                pos = this.getBlockPos().mutableCopy();
                break;
            }

            pos.move(Direction.UP);
        }

        world.setBlockState(pos, ModRegistry.TOMB.getDefaultState());
        world.getBlockEntity(pos, ModRegistry.TOMB_BLOCK_ENTITY)
             .ifPresent(tomb -> tomb.setDeath(PlayerDeath.fromPlayer((ServerPlayerEntity) (Object) this)));
    }

    @ModifyConstant(method = "getXpToDrop", constant = @Constant(intValue = 7))
    private int modifyXpDrops(int seven) {
        return 0;
    }
}
