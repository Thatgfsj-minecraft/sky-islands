package dev.skyislands;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ChestBlock;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.ChestTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;

/**
 * Builds the starting island of a sky island world. Everything is placed
 * with plain setBlock at fixed coordinates around the spawn chunk origin
 * (center x=8, z=8, grass surface at y=63, players stand at y=64), so builds
 * are deterministic and idempotent by construction; the saved-data marker in
 * {@link IslandBootstrap} additionally prevents rebuilding a looted island.
 * Direct translation of the modern IslandBuilder (Forge 1.16.5 naming).
 */
public final class IslandBuilder {

    private static final int CX = 8;
    private static final int CZ = 8;
    /** Y of the grass layer; the standing surface is SURFACE + 1. */
    private static final int SURFACE = 63;

    private IslandBuilder() {
    }

    public static void build(ServerWorld level, IslandType type) {
        if (type == IslandType.CLASSIC) {
            mainIsland5x5(level);
            oakTree(level, 6, 10);
            chest(level, 10, 8, Direction.WEST,
                    new ItemStack(net.minecraft.item.Items.LAVA_BUCKET),
                    new ItemStack(net.minecraft.block.Blocks.ICE.asItem()));
        } else if (type == IslandType.SMALL) {
            mainIsland3x3(level);
            oakTree(level, 7, 9);
            chest(level, 9, 7, Direction.WEST,
                    new ItemStack(net.minecraft.item.Items.LAVA_BUCKET),
                    new ItemStack(net.minecraft.block.Blocks.ICE.asItem(), 2),
                    new ItemStack(net.minecraft.item.Items.SUGAR_CANE),
                    new ItemStack(net.minecraft.item.Items.PUMPKIN_SEEDS),
                    new ItemStack(net.minecraft.item.Items.MELON_SEEDS),
                    new ItemStack(net.minecraft.item.Items.BREAD),
                    new ItemStack(net.minecraft.block.Blocks.CACTUS.asItem()));
        } else if (type == IslandType.SINGLE) {
            set(level, CX, SURFACE, CZ, net.minecraft.block.Blocks.GRASS_BLOCK);
            set(level, CX, SURFACE + 1, CZ, net.minecraft.block.Blocks.OAK_SAPLING);
        }
    }

    /** Classic 5x5 double-layer island with bedrock anchor in the center. */
    private static void mainIsland5x5(ServerWorld level) {
        for (int x = CX - 2; x <= CX + 2; x++) {
            for (int z = CZ - 2; z <= CZ + 2; z++) {
                set(level, x, SURFACE, z, net.minecraft.block.Blocks.GRASS_BLOCK);
                set(level, x, SURFACE - 1, z, net.minecraft.block.Blocks.DIRT);
            }
        }
        for (int x = CX - 1; x <= CX + 1; x++) {
            for (int z = CZ - 1; z <= CZ + 1; z++) {
                set(level, x, SURFACE - 2, z, net.minecraft.block.Blocks.DIRT);
            }
        }
        set(level, CX, SURFACE - 3, CZ, net.minecraft.block.Blocks.BEDROCK);
    }

    /** Small 3x3 island: one grass layer on one dirt layer, bedrock below. */
    private static void mainIsland3x3(ServerWorld level) {
        for (int x = CX - 1; x <= CX + 1; x++) {
            for (int z = CZ - 1; z <= CZ + 1; z++) {
                set(level, x, SURFACE, z, net.minecraft.block.Blocks.GRASS_BLOCK);
                set(level, x, SURFACE - 1, z, net.minecraft.block.Blocks.DIRT);
            }
        }
        set(level, CX, SURFACE - 2, CZ, net.minecraft.block.Blocks.BEDROCK);
    }

    /** A small oak: 4 logs plus the vanilla-style leaf blob, floating over void. */
    private static void oakTree(ServerWorld level, int tx, int tz) {
        for (int y = SURFACE + 1; y <= SURFACE + 4; y++) {
            set(level, tx, y, tz, net.minecraft.block.Blocks.OAK_LOG);
        }
        for (int y = SURFACE + 3; y <= SURFACE + 4; y++) {
            for (int dx = -2; dx <= 2; dx++) {
                for (int dz = -2; dz <= 2; dz++) {
                    if ((Math.abs(dx) == 2 && Math.abs(dz) == 2) || (dx == 0 && dz == 0)) {
                        continue;
                    }
                    setIfAir(level, tx + dx, y, tz + dz, net.minecraft.block.Blocks.OAK_LEAVES);
                }
            }
        }
        for (int dx = -1; dx <= 1; dx++) {
            for (int dz = -1; dz <= 1; dz++) {
                setIfAir(level, tx + dx, SURFACE + 5, tz + dz, net.minecraft.block.Blocks.OAK_LEAVES);
            }
        }
        setIfAir(level, tx + 1, SURFACE + 6, tz, net.minecraft.block.Blocks.OAK_LEAVES);
        setIfAir(level, tx - 1, SURFACE + 6, tz, net.minecraft.block.Blocks.OAK_LEAVES);
        setIfAir(level, tx, SURFACE + 6, tz, net.minecraft.block.Blocks.OAK_LEAVES);
        setIfAir(level, tx, SURFACE + 6, tz + 1, net.minecraft.block.Blocks.OAK_LEAVES);
        setIfAir(level, tx, SURFACE + 6, tz - 1, net.minecraft.block.Blocks.OAK_LEAVES);
    }

    private static void chest(ServerWorld level, int x, int z, Direction facing, ItemStack... items) {
        BlockPos pos = new BlockPos(x, SURFACE + 1, z);
        BlockState state = net.minecraft.block.Blocks.CHEST.defaultBlockState().setValue(ChestBlock.FACING, facing);
        level.setBlock(pos, state, 3);
        if (level.getBlockEntity(pos) instanceof ChestTileEntity) {
            ChestTileEntity chest = (ChestTileEntity) level.getBlockEntity(pos);
            for (int i = 0; i < items.length; i++) {
                chest.setItem(i, items[i]);
            }
            chest.setChanged();
        }
    }

    private static void set(ServerWorld level, int x, int y, int z, Block block) {
        level.setBlock(new BlockPos(x, y, z), block.defaultBlockState(), 3);
    }

    private static void setIfAir(ServerWorld level, int x, int y, int z, Block block) {
        if (level.getBlockState(new BlockPos(x, y, z)).isAir()) {
            set(level, x, y, z, block);
        }
    }
}
