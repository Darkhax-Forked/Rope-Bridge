package com.mrtrollnugnug.ropebridge.block;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BridgeSlabTest extends Block {

    protected static final AxisAlignedBB AABB_BLOCK_1 = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.5D, 1.0D);

    protected static final AxisAlignedBB AABB_BLOCK_2 = new AxisAlignedBB(0.0D, 0.25D, 0.0D, 1.0D, 0.5D, 1.0D);

    protected static final AxisAlignedBB AABB_BLOCK_3 = new AxisAlignedBB(0.0D, 0.5D, 0.0D, 1.0D, 0.75D, 1.0D);

    protected static final AxisAlignedBB AABB_BLOCK_4 = new AxisAlignedBB(0.0D, 0.75D, 0.0D, 1.0D, 1.0D, 1.0D);

    protected static float slabHeight = 4.0F / 16.0F;

    public BridgeSlabTest (String unlocalizedName, float minX, float minY, float minZ, float maxX, float maxY, float maxZ) {
        super(Material.WOOD);
        this.setSoundType(SoundType.WOOD);
        this.setDefaultState(this.blockState.getBaseState().withProperty(SIDE, Side.BOTTOM).withProperty(WOOD_TYPE, WoodType.OAK));
        new AxisAlignedBB(minX, minY, minZ, maxX, maxY, maxZ);
    }

    @Override
    public boolean isFullCube (IBlockState state) {

        return false;
    }

    @Override
    public boolean isFullBlock (IBlockState state) {

        return false;
    }

    @Override
    public boolean isOpaqueCube (IBlockState state) {

        return false;
    }

    @Override
    public AxisAlignedBB getBoundingBox (IBlockState state, IBlockAccess source, BlockPos pos) {

        return state.getValue(SIDE) == Side.BOTTOM ? AABB_BLOCK_2 : AABB_BLOCK_4;
    }

    @Override
    public boolean canDropFromExplosion (Explosion explosionIn) {

        return true;
    }

    @Override
    public boolean canHarvestBlock (IBlockAccess world, BlockPos pos, EntityPlayer player) {

        return true;
    }

    @Override
    public boolean canSilkHarvest (World world, BlockPos pos, IBlockState state, EntityPlayer player) {

        return false;
    }

    @Override
    public List<ItemStack> getDrops (IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {

        final List<ItemStack> ret = new java.util.ArrayList<>();
        final int meta = this.getMetaFromState(state);
        final int slabMeta = (meta - meta % 2) / 2;
        ret.add(new ItemStack(Blocks.WOODEN_SLAB, 1, slabMeta));
        ret.add(new ItemStack(Items.STRING, RANDOM.nextInt(2)));
        return ret;
    }

    public ItemStack getPickBlock (RayTraceResult target, World world, BlockPos pos) {

        return null;
    }

    public boolean isOpaqueCube () {

        return false;
    }

    public boolean isFullCube () {

        return false;
    }

    @Override
    public int getFlammability (IBlockAccess world, BlockPos pos, EnumFacing face) {

        return 20;
    }

    @Override
    public boolean isFlammable (IBlockAccess world, BlockPos pos, EnumFacing face) {

        return true;
    }

    @Override
    public int getFireSpreadSpeed (IBlockAccess world, BlockPos pos, EnumFacing face) {

        return 5;
    }

    @Override
    public boolean isFireSource (World world, BlockPos pos, EnumFacing side) {

        return false;
    }

    @Override
    public boolean isPassable (IBlockAccess worldIn, BlockPos pos) {

        return false;
    }

    @Override
    protected BlockStateContainer createBlockState () {

        return new BlockStateContainer(this, new IProperty[] { SIDE, WOOD_TYPE });
    }

    @Override
    public IBlockState getStateFromMeta (int meta) {

        final int side = meta >> 3;
        final int type = meta & 0x7;
        return this.getDefaultState().withProperty(SIDE, side == 0 ? Side.TOP : Side.BOTTOM).withProperty(WOOD_TYPE, WoodType.get(type));
    }

    @Override
    public int getMetaFromState (IBlockState state) {

        return state.getValue(SIDE).i << 3 + state.getValue(WOOD_TYPE).i;
    }

    public static final IProperty<Side> SIDE = PropertyEnum.create("side", Side.class);

    public static final IProperty<WoodType> WOOD_TYPE = PropertyEnum.create("type", WoodType.class);

    public enum Side implements IStringSerializable {
        TOP(0),
        BOTTOM(1);

        public final int i;

        private Side (int i) {
            this.i = i;
        }

        @Override
        public String getName () {

            return this.name().toLowerCase();
        }
    }

    public enum WoodType implements IStringSerializable {
        OAK(0, "oak"),
        BIRCH(1, "birch"),
        SPRUCE(2, "spruce"),
        JUNGLE(3, "jungle"),
        DARK_OAK(4, "dark_oak"),
        ACACIA(5, "acacia");

        public final int i;

        public final String name;

        WoodType (int i, String name) {
            this.i = i;
            this.name = name;
        }

        public static WoodType get (int type) {

            for (final WoodType t : values())
                if (t.i == type)
                    return t;
            return OAK;
        }

        @Override
        public String getName () {

            return this.name;
        }

    }
}
