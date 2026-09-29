package dev.skyislands;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Builds the starting island(s) of a sky island world. Everything is placed
 * with plain setBlock at fixed coordinates around the spawn chunk origin
 * (center x=8, z=8, grass surface at y=63, players stand at y=64), so builds
 * are deterministic and idempotent by construction; the saved-data marker in
 * {@link IslandBootstrap} additionally prevents rebuilding a looted island.
 */
public final class IslandBuilder {

    private static final int CX = 8;
    private static final int CZ = 8;
    /** Y of the grass layer; the standing surface is SURFACE + 1. */
    private static final int SURFACE = 63;

    private IslandBuilder() {
    }

    public static void build(ServerLevel level, IslandType type) {
        switch (type) {
            case CLASSIC -> {
                mainIsland5x5(level);
                oakTree(level, 6, 10);
                chest(level, 10, 8, Direction.WEST,
                        new ItemStack(Items.LAVA_BUCKET),
                        new ItemStack(Items.ICE));
            }
            case OLDSCHOOL -> {
                mainIsland3x3(level);
                oakTree(level, 7, 9);
                chest(level, 9, 7, Direction.WEST,
                        new ItemStack(Items.LAVA_BUCKET),
                        new ItemStack(Items.ICE, 2),
                        new ItemStack(Items.SUGAR_CANE),
                        new ItemStack(Items.PUMPKIN_SEEDS),
                        new ItemStack(Items.MELON_SEEDS),
                        new ItemStack(Items.BREAD),
                        new ItemStack(Items.CACTUS));
            }
            case ARCHIPELAGO -> {
                mainIsland5x5(level);
                oakTree(level, 6, 10);
                chest(level, 10, 8, Direction.WEST,
                        new ItemStack(Items.LAVA_BUCKET),
                        new ItemStack(Items.ICE));
                satellite(level, 30, 8, Blocks.SAND, Blocks.SAND);
                level.setBlock(new BlockPos(30, SURFACE + 1, 8), Blocks.CACTUS.defaultBlockState(), 3);
                level.setBlock(new BlockPos(30, SURFACE + 2, 8), Blocks.CACTUS.defaultBlockState(), 3);
                satellite(level, 8, 30, Blocks.SNOW_BLOCK, Blocks.SNOW_BLOCK);
                satellite(level, -14, 8, Blocks.NETHERRACK, Blocks.SOUL_SAND);
                satellite(level, 8, -14, Blocks.END_STONE, Blocks.END_STONE);
            }
            case SINGLE -> {
                set(level, CX, SURFACE, CZ, Blocks.GRASS_BLOCK);
                set(level, CX, SURFACE + 1, CZ, Blocks.OAK_SAPLING);
            }
        }
    }

    /** Classic 5x5 double-layer island with bedrock anchor in the center. */
    private static void mainIsland5x5(ServerLevel level) {
        for (int x = CX - 2; x <= CX + 2; x++) {
            for (int z = CZ - 2; z <= CZ + 2; z++) {
                set(level, x, SURFACE, z, Blocks.GRASS_BLOCK);
                set(level, x, SURFACE - 1, z, Blocks.DIRT);
            }
        }
        for (int x = CX - 1; x <= CX + 1; x++) {
            for (int z = CZ - 1; z <= CZ + 1; z++) {
                set(level, x, SURFACE - 2, z, Blocks.DIRT);
            }
        }
        set(level, CX, SURFACE - 3, CZ, Blocks.BEDROCK);
    }

    /** Old-school 3x3 island: one grass layer on one dirt layer, bedrock below. */
    private static void mainIsland3x3(ServerLevel level) {
        for (int x = CX - 1; x <= CX + 1; x++) {
            for (int z = CZ - 1; z <= CZ + 1; z++) {
                set(level, x, SURFACE, z, Blocks.GRASS_BLOCK);
                set(level, x, SURFACE - 1, z, Blocks.DIRT);
            }
        }
        set(level, CX, SURFACE - 2, CZ, Blocks.BEDROCK);
    }

    /** A small 3x3 themed satellite island two blocks thick. */
    private static void satellite(ServerLevel level, int cx, int cz, Block base, Block topCenter) {
        for (int x = cx - 1; x <= cx + 1; x++) {
            for (int z = cz - 1; z <= cz + 1; z++) {
                set(level, x, SURFACE, z, base);
                set(level, x, SURFACE - 1, z, base);
            }
        }
        set(level, cx, SURFACE, cz, topCenter);
    }

    /** A small oak: 4 logs plus the vanilla-style leaf blob, floating over void. */
    private static void oakTree(ServerLevel level, int tx, int tz) {
        for (int y = SURFACE + 1; y <= SURFACE + 4; y++) {
            set(level, tx, y, tz, Blocks.OAK_LOG);
        }
        for (int y = SURFACE + 3; y <= SURFACE + 4; y++) {
            for (int dx = -2; dx <= 2; dx++) {
                for (int dz = -2; dz <= 2; dz++) {
                    if ((Math.abs(dx) == 2 && Math.abs(dz) == 2) || (dx == 0 && dz == 0)) {
                        continue;
                    }
                    setIfAir(level, tx + dx, y, tz + dz, Blocks.OAK_LEAVES);
                }
            }
        }
        for (int dx = -1; dx <= 1; dx++) {
            for (int dz = -1; dz <= 1; dz++) {
                setIfAir(level, tx + dx, SURFACE + 5, tz + dz, Blocks.OAK_LEAVES);
            }
        }
        setIfAir(level, tx + 1, SURFACE + 6, tz, Blocks.OAK_LEAVES);
        setIfAir(level, tx - 1, SURFACE + 6, tz, Blocks.OAK_LEAVES);
        setIfAir(level, tx, SURFACE + 6, tz, Blocks.OAK_LEAVES);
        setIfAir(level, tx, SURFACE + 6, tz + 1, Blocks.OAK_LEAVES);
        setIfAir(level, tx, SURFACE + 6, tz - 1, Blocks.OAK_LEAVES);
    }

    private static void chest(ServerLevel level, int x, int z, Direction facing, ItemStack... items) {
        BlockPos pos = new BlockPos(x, SURFACE + 1, z);
        BlockState state = Blocks.CHEST.defaultBlockState().setValue(ChestBlock.FACING, facing);
        level.setBlock(pos, state, 3);
        if (level.getBlockEntity(pos) instanceof ChestBlockEntity chest) {
            for (int i = 0; i < items.length; i++) {
                chest.setItem(i, items[i]);
            }
            chest.setChanged();
        }
    }

    private static void set(ServerLevel level, int x, int y, int z, Block block) {
        level.setBlock(new BlockPos(x, y, z), block.defaultBlockState(), 3);
    }

    private static void setIfAir(ServerLevel level, int x, int y, int z, Block block) {
        if (level.getBlockState(new BlockPos(x, y, z)).isAir()) {
            set(level, x, y, z, block);
        }
    }
}
