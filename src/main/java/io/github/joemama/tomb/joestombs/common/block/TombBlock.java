package io.github.joemama.tomb.joestombs.common.block;

import io.github.joemama.tomb.joestombs.common.register.ModRegistry;
import org.jetbrains.annotations.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class TombBlock extends Block implements BlockEntityProvider {
    public static final VoxelShape SHAPE = Util.make(() -> {
        VoxelShape shape = VoxelShapes.empty();
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.0625, 0, 0.5625, 0.9375, 0.0625, 0.9375));
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.125, 0.0625, 0.625, 0.875, 0.75, 0.875));
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.1875, 0.75, 0.625, 0.8125, 0.8125, 0.875));
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.25, 0.8125, 0.625, 0.75, 0.875, 0.875));
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.3125, 0.875, 0.625, 0.6875, 0.9375, 0.875));
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.0625, 0, 0.5625, 0.9375, 0.9375, 0.9375));

        return shape;
    });

    public TombBlock(Settings settings) {
        super(settings);
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return ModRegistry.TOMB_BLOCK_ENTITY.instantiate(pos, state);
    }

    @SuppressWarnings("deprecation")
    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPE;
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity) {
        if (world.isClient) return;

        if (entity.getType() == EntityType.PLAYER && entity.isSneaking()) {
            world.getBlockEntity(pos, ModRegistry.TOMB_BLOCK_ENTITY)
                 .ifPresent(tomb -> tomb.acceptPlayer(((ServerPlayerEntity) entity)));
        }
    }
}
