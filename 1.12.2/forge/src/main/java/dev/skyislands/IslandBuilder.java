package dev.skyislands;

import net.minecraft.block.BlockChest;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldServer;
import net.minecraft.block.state.IBlockState;

/**
 * Builds the starting island of a sky island world. Everything is placed
 * with plain setBlockState at fixed coordinates around the spawn chunk
 * origin (center x=8, z=8, grass surface at y=63, players stand at y=64),
 * so builds are deterministic and idempotent by construction; the
 * saved-data marker in IslandBootstrap additionally prevents rebuilding a
 * looted island. Direct translation of the modern IslandBuilder onto the
 * 1.12.2 block-state API.
 */
public final class IslandBuilder {

    private static final int CX = 8;
    private static final int CZ = 8;
    /** Y of the grass layer; the standing surface is SURFACE + 1. */
    private static final int SURFACE = 63;

    private IslandBuilder() {
    }

    public static void build(WorldServer level, IslandType type) {
        if (type == IslandType.CLASSIC) {
            mainIsland5x5(level);
            oakTree(level, 6, 10);
            chest(level, 10, 8, EnumFacing.WEST,
                    new ItemStack(Items.LAVA_BUCKET),
                    new ItemStack(Item.getItemFromBlock(Blocks.ICE)));
        } else if (type == IslandType.SMALL) {
            mainIsland3x3(level);
            oakTree(level, 7, 9);
            chest(level, 9, 7, EnumFacing.WEST,
                    new ItemStack(Items.LAVA_BUCKET),
                    new ItemStack(Item.getItemFromBlock(Blocks.ICE), 2),
                    new ItemStack(Items.REEDS),
                    new ItemStack(Items.PUMPKIN_SEEDS),
                    new ItemStack(Items.MELON_SEEDS),
                    new ItemStack(Items.BREAD),
                    new ItemStack(Item.getItemFromBlock(Blocks.CACTUS)));
        } else if (type == IslandType.SINGLE) {
            set(level, CX, SURFACE, CZ, Blocks.GRASS);
            set(level, CX, SURFACE + 1, CZ, Blocks.SAPLING);
        }
    }

    /** Classic 5x5 double-layer island with bedrock anchor in the center. */
    private static void mainIsland5x5(WorldServer level) {
        for (int x = CX - 2; x <= CX + 2; x++) {
            for (int z = CZ - 2; z <= CZ + 2; z++) {
                set(level, x, SURFACE, z, Blocks.GRASS);
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

    /** Small 3x3 island: one grass layer on one dirt layer, bedrock below. */
    private static void mainIsland3x3(WorldServer level) {
        for (int x = CX - 1; x <= CX + 1; x++) {
            for (int z = CZ - 1; z <= CZ + 1; z++) {
                set(level, x, SURFACE, z, Blocks.GRASS);
                set(level, x, SURFACE - 1, z, Blocks.DIRT);
            }
        }
        set(level, CX, SURFACE - 2, CZ, Blocks.BEDROCK);
    }

    /** A small oak: 4 logs plus the vanilla-style leaf blob, floating over void. */
    private static void oakTree(WorldServer level, int tx, int tz) {
        for (int y = SURFACE + 1; y <= SURFACE + 4; y++) {
            set(level, tx, y, tz, Blocks.LOG);
        }
        for (int y = SURFACE + 3; y <= SURFACE + 4; y++) {
            for (int dx = -2; dx <= 2; dx++) {
                for (int dz = -2; dz <= 2; dz++) {
                    if ((Math.abs(dx) == 2 && Math.abs(dz) == 2) || (dx == 0 && dz == 0)) {
                        continue;
                    }
                    setIfAir(level, tx + dx, y, tz + dz, Blocks.LEAVES);
                }
            }
        }
        for (int dx = -1; dx <= 1; dx++) {
            for (int dz = -1; dz <= 1; dz++) {
                setIfAir(level, tx + dx, SURFACE + 5, tz + dz, Blocks.LEAVES);
            }
        }
        setIfAir(level, tx + 1, SURFACE + 6, tz, Blocks.LEAVES);
        setIfAir(level, tx - 1, SURFACE + 6, tz, Blocks.LEAVES);
        setIfAir(level, tx, SURFACE + 6, tz, Blocks.LEAVES);
        setIfAir(level, tx, SURFACE + 6, tz + 1, Blocks.LEAVES);
        setIfAir(level, tx, SURFACE + 6, tz - 1, Blocks.LEAVES);
    }

    private static void chest(WorldServer level, int x, int z, EnumFacing facing, ItemStack... items) {
        BlockPos pos = new BlockPos(x, SURFACE + 1, z);
        IBlockState state = Blocks.CHEST.getDefaultState().withProperty(BlockChest.FACING, facing);
        level.setBlockState(pos, state, 3);
        TileEntity te = level.getTileEntity(pos);
        if (te instanceof TileEntityChest) {
            TileEntityChest chest = (TileEntityChest) te;
            for (int i = 0; i < items.length; i++) {
                chest.setInventorySlotContents(i, items[i]);
            }
            chest.markDirty();
        }
    }

    private static void set(WorldServer level, int x, int y, int z, net.minecraft.block.Block block) {
        level.setBlockState(new BlockPos(x, y, z), block.getDefaultState(), 3);
    }

    private static void setIfAir(WorldServer level, int x, int y, int z, net.minecraft.block.Block block) {
        if (level.isAirBlock(new BlockPos(x, y, z))) {
            set(level, x, y, z, block);
        }
    }
}
