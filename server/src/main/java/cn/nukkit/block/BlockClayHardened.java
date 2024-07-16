package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.item.ItemTool;
import cn.nukkit.utils.BlockColor;
import cn.nukkit.utils.DyeColor;

/**
 * Created on 2015/11/24 by xtypr.
 * Package cn.nukkit.block in project Nukkit .
 */
public class BlockClayHardened extends BlockSolid {
    public BlockClayHardened() {
        this(0);
    }

    public BlockClayHardened(int meta) {
        super(0);
    }

    public BlockClayHardened(DyeColor dyeColor) {
        this(dyeColor.getWoolData());
    }

    @Override
    public int getId() {
        return HARDENED_CLAY;
    }

    @Override
    public String getName() {
        return "Hardened Clay";
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_PICKAXE;
    }

    @Override
    public double getHardness() {
        return 1.25;
    }

    @Override
    public double getResistance() {
        return 7;
    }

    @Override
    public int[][] getDrops(Item item) {
        if (item.isPickaxe() && item.getTier() >= ItemTool.TIER_WOODEN) {
            return new int[][]{
                    {Item.HARDENED_CLAY, 0, 1}
            };
        } else {
            return new int[0][0];
        }
    }

    @Override
    public BlockColor getColor() {
        return DyeColor.getByWoolData(meta).getColor();
    }

    public DyeColor getDyeColor() {
        return DyeColor.getByWoolData(meta);
    }
}
