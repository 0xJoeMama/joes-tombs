package io.github.joemama.tomb.joestombs.common.register;

import io.github.joemama.tomb.joestombs.JoesTombs;
import io.github.joemama.tomb.joestombs.common.block.TombBlock;
import io.github.joemama.tomb.joestombs.common.blockentity.TombBlockEntity;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.util.registry.Registry;

public final class ModRegistry {
    public static final Block TOMB = new TombBlock(FabricBlockSettings.copyOf(Blocks.STONE).noCollision());
    public static final BlockEntityType<TombBlockEntity> TOMB_BLOCK_ENTITY =
            FabricBlockEntityTypeBuilder.create(TombBlockEntity::new, TOMB).build();

    private ModRegistry() { }

    public static void init() {
        Registry.register(Registry.BLOCK, JoesTombs.id("tomb"), TOMB);
        Registry.register(Registry.ITEM, JoesTombs.id("tomb"), new BlockItem(TOMB, new Item.Settings()));
        Registry.register(Registry.BLOCK_ENTITY_TYPE, JoesTombs.id("tomb"), TOMB_BLOCK_ENTITY);
    }
}
