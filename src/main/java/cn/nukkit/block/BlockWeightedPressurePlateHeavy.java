package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.item.ItemTool;

/**
 * @author Nukkit Project Team
 */
public class BlockWeightedPressurePlateHeavy extends BlockWeightedPressurePlate {

    public BlockWeightedPressurePlateHeavy(int meta) {
        super(meta);
    }

    public BlockWeightedPressurePlateHeavy() {
        this(0);
    }

    @Override
    public String getName() {
        return "Heavy Weighted Pressure Plate";
    }

    @Override
    public int getId() {
        return HEAVY_WEIGHTED_PRESSURE_PLATE;
    }

    @Override
    public double getHardness() {
        return 0.5D;
    }

    @Override
    public double getResistance() {
        return 2.5D;
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_PICKAXE;
    }

    @Override
    public int[][] getDrops(Item item) {
        if (item.isPickaxe() && item.getTier() >= ItemTool.TIER_WOODEN) {
            return new int[][]{
                    {Item.HEAVY_WEIGHTED_PRESSURE_PLATE, 0, 1}
            };
        } else {
            return new int[0][0];
        }
    }

}
