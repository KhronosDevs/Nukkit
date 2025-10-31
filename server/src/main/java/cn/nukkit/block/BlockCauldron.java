package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.blockentity.BlockEntityCauldron;
import cn.nukkit.event.player.PlayerBucketEmptyEvent;
import cn.nukkit.event.player.PlayerBucketFillEvent;
import cn.nukkit.event.player.PlayerGlassBottleFillEvent;
import cn.nukkit.item.*;
import cn.nukkit.level.sound.CauldronFillPotionSound;
import cn.nukkit.level.sound.ExplodeSound;
import cn.nukkit.level.sound.GraySplashSound;
import cn.nukkit.level.sound.SplashSound;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.Tag;
import cn.nukkit.potion.Effect;
import cn.nukkit.potion.Potion;
import cn.nukkit.utils.DyeColor;

import java.util.Iterator;
import java.util.Map;

/**
 * Updated port from PocketMine's Cauldron implementation.
 * Tries to mirror behavior in the provided PHP version: bucket handling, dyeing,
 * leather coloring, potions, glass bottles and sounds.
 *
 * NOTE: This is a best-effort port — depending on your Nukkit build some method
 * / class names (Color, Potion constants, BlockEntity IDs, event constructors)
 * may differ slightly. If the compiler complains, adjust the referenced API
 * names to your server version.
 */
public class BlockCauldron extends BlockSolid {

    public BlockCauldron() {
        super(0);
    }

    public BlockCauldron(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return CAULDRON_BLOCK;
    }

    public String getName() {
        return "Cauldron";
    }

    @Override
    public double getResistance() {
        return 10;
    }

    @Override
    public double getHardness() {
        return 2;
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_PICKAXE;
    }

    @Override
    public boolean canBeActivated() {
        return true;
    }

    public boolean isFull() {
        return this.meta == 0x06;
    }

    public boolean isEmpty() {
        return this.meta == 0x00;
    }

    @Override
    public boolean onActivate(Item item, Player player) {
        BlockEntity be = this.level.getBlockEntity(this);

        if (!(be instanceof BlockEntityCauldron)) {
            return false;
        }

        BlockEntityCauldron cauldron = (BlockEntityCauldron) be;

        switch (item.getId()) {
            case Item.BUCKET:
                if (item.getDamage() == 0) { // empty bucket
                    if (!isFull() || cauldron.isCustomColor() || cauldron.hasPotion()) {
                        break;
                    }

                    ItemBucket bucket = (ItemBucket) item.clone();
                    bucket.setDamage(8); // water bucket

                    PlayerBucketFillEvent ev = new PlayerBucketFillEvent(player, this, 0, item, bucket);
                    this.level.getServer().getPluginManager().callEvent(ev);
                    if (!ev.isCancelled()) {
                        if (player.isSurvival()) {
                            player.getInventory().setItemInHand(ev.getItem());
                        }
                        this.meta = 0; // empty
                        this.level.setBlock(this, this, true);
                        cauldron.clearCustomColor();
                        this.level.addSound(new SplashSound(this.add(0.5, 1, 0.5)));
                    }
                } else if (item.getDamage() == 8) { // water bucket

                    if (isFull() && !cauldron.isCustomColor() && !cauldron.hasPotion()) {
                        break;
                    }

                    ItemBucket bucket = (ItemBucket) item.clone();
                    bucket.setDamage(0); // empty bucket

                    PlayerBucketEmptyEvent ev = new PlayerBucketEmptyEvent(player, this, 0, item, bucket);
                    this.level.getServer().getPluginManager().callEvent(ev);
                    if (!ev.isCancelled()) {
                        if (player.isSurvival()) {
                            player.getInventory().setItemInHand(ev.getItem());
                        }
                        if (cauldron.hasPotion()) { // if has potion
                            this.meta = 0; // empty
                            cauldron.setPotionId(0xffff); // reset potion
                            cauldron.setSplashPotion(false);
                            cauldron.clearCustomColor();
                            this.level.setBlock(this, this, true);
                            this.level.addSound(new ExplodeSound(this.add(0.5, 0, 0.5)));
                        } else {
                            this.meta = 6; // fill
                            cauldron.clearCustomColor();
                            this.level.setBlock(this, this, true);
                            this.level.addSound(new SplashSound(this.add(0.5, 1, 0.5)));
                        }

                        // this.update();
                    }
                }
                break;
            case Item.DYE:
                if (cauldron.hasPotion()) {
                    break;
                }

                DyeColor color = DyeColor.getByDyeData(item.getDamage());
                if (player.isSurvival()) {
                    item.setCount(item.getCount() - 1);
                }
                cauldron.setCustomColor(color.getColor().getRed(), color.getColor().getGreen(), color.getColor().getBlue());
                this.level.addSound(new SplashSound(this.add(0.5, 1, 0.5)));
                this.level.setBlock(this, this, true);
                break;
            case Item.LEATHER_CAP:
            case Item.LEATHER_TUNIC:
            case Item.LEATHER_PANTS:
            case Item.LEATHER_BOOTS:
                if (this.isEmpty()) break;
                if (cauldron.isCustomColor()) {
                    --this.meta;
                    this.level.setBlock(this, this, true);
                    ItemColorArmor newItem = (ItemColorArmor) item.clone();
                    newItem.setColor(cauldron.getCustomColor().getRed(), cauldron.getCustomColor().getGreen(), cauldron.getCustomColor().getBlue());
                    player.getInventory().setItemInHand(newItem);
                    this.level.addSound(new SplashSound(this.add(0.5, 1, 0.5)));
                    if (this.isEmpty()) {
                        cauldron.clearCustomColor();
                    }
                } else {
                    --this.meta;
                    this.level.setBlock(this, this, true);
                    ItemColorArmor newItem = (ItemColorArmor) Item.get(item.getId(), item.getDamage(), item.getCount());
                    player.getInventory().setItemInHand(newItem);
                    this.level.addSound(new SplashSound(this.add(0.5, 1, 0.5)));
                }
                break;
            case Item.POTION:
            case Item.SPLASH_POTION:
                int potionId = item.getDamage();
                boolean isSplash = item.getId() == Item.SPLASH_POTION;

                if (!this.isEmpty() && ((cauldron.getPotionId() != potionId && potionId != Potion.WATER)
                        || (item.getId() == Item.POTION && cauldron.isSplashPotion())
                        || (item.getId() == Item.SPLASH_POTION && !cauldron.isSplashPotion()) && potionId != 0
                        || (potionId == Potion.WATER && cauldron.hasPotion()))) {
                    // clear cauldron
                    this.meta = 0x00;
                    this.level.setBlock(this, this, true);
                    cauldron.setPotionId(0xffff);
                    cauldron.setSplashPotion(false);
                    cauldron.clearCustomColor();
                    if (player.isSurvival()) {
                        player.getInventory().setItemInHand(new ItemGlassBottle());
                    }
                    this.level.addSound(new ExplodeSound(this.add(0.5, 0, 0.5)));
                } else if (potionId == Potion.WATER) {
                    this.meta += 2;
                    if (this.meta > 0x06) this.meta = 0x06;
                    this.level.setBlock(this, this, true);
                    if (player.isSurvival()) {
                        player.getInventory().setItemInHand(new ItemGlassBottle());
                    }
                    cauldron.setPotionId(0xffff);
                    cauldron.setSplashPotion(false);
                    cauldron.clearCustomColor();
                    this.level.addSound(new SplashSound(this.add(0.5, 1, 0.5)));
                } else if (!this.isFull()) {
                    this.meta += 2;
                    if (this.meta > 0x06) this.meta = 0x06;
                    cauldron.setPotionId(potionId);
                    cauldron.setSplashPotion(isSplash);
                    cauldron.clearCustomColor();
                    this.level.setBlock(this, this, true);
                    if (player.isSurvival()) {
                        player.getInventory().setItemInHand(new ItemGlassBottle());
                    }
                    int[] col = Effect.getEffect(potionId).getColor();
                    this.level.addSound(new CauldronFillPotionSound(this.add(0.5, 1, 0.5), col[0], col[1], col[2]));
                }
                break;
            case Item.GLASS_BOTTLE:
                PlayerGlassBottleFillEvent ev = new PlayerGlassBottleFillEvent(player, this, item);
                this.level.getServer().getPluginManager().callEvent(ev);
                if (ev.isCancelled()) {
                    return false;
                }
                if (this.meta < 2) {
                    break;
                }
                if (cauldron.hasPotion()) {
                    this.meta -= 2;
                    Item result;
                    if (cauldron.isSplashPotion()) {
                        result = new ItemPotionSplash(cauldron.getPotionId());
                    } else {
                        result = new ItemPotion(cauldron.getPotionId());
                    }
                    if (this.isEmpty()) {
                        cauldron.setPotionId(0xffff);
                        cauldron.setSplashPotion(false);
                        cauldron.clearCustomColor();
                    }
                    this.level.setBlock(this, this, true);
                    this.addItem(item, player, result);
                    int[] color1 = Effect.getEffect(result.getDamage()).getColor();
                    this.level.addSound(new CauldronFillPotionSound(this.add(0.5, 1, 0.5), color1[0], color1[1], color1[2]));
                } else {
                    this.meta -= 2;
                    this.level.setBlock(this, this, true);
                    if (player.isSurvival()) {
                        Item result = new ItemPotion(Potion.WATER);
                        this.addItem(item, player, result);
                    }
                    this.level.addSound(new GraySplashSound(this.add(0.5, 1, 0.5)));
                }
                break;
        }
        return true;
    }

    public void addItem(Item item, Player player, Item result) {
        if (item.getCount() <= 1) {
            player.getInventory().setItemInHand(result);
        } else {
            item.setCount(item.getCount() - 1);
            if (player.getInventory().canAddItem(result)) {
                player.getInventory().addItem(result);
            } else {
                cn.nukkit.math.Vector3 motion = player.getDirectionVector().multiply(0.4);
                cn.nukkit.math.Vector3 position = player.getPosition();
                this.level.dropItem(position.add(0, 0.5, 0), result, motion, 40);
            }
        }
    }

    @Override
    public boolean place(Item item, Block block, Block target, int face, double fx, double fy, double fz, Player player) {
        CompoundTag nbt = new CompoundTag("")
                .putString("id", BlockEntityCauldron.class.getSimpleName())
                .putInt("x", (int) this.x)
                .putInt("y", (int) this.y)
                .putInt("z", (int) this.z)
                .putShort("PotionId", (short) 0xffff)
                .putByte("SplashPotion", (byte) 0);

        if (item.hasCustomBlockData()) {
            Map<String, Tag> customData = item.getCustomBlockData().getTags();
            Iterator iter = customData.entrySet().iterator();
            while (iter.hasNext()) {
                Map.Entry tag = (Map.Entry) iter.next();
                nbt.put((String) tag.getKey(), (Tag) tag.getValue());
            }
        }

        new BlockEntityCauldron(this.level.getChunk((int) this.x >> 4, (int) this.z >> 4), nbt);
        this.getLevel().setBlock(block, this, true, true);
        return true;
    }

    @Override
    public int[][] getDrops(Item item) {
        if (item.getTier() >= ItemTool.TIER_WOODEN) {
            return new int[][]{{Item.CAULDRON, 0, 1}};
        }

        return new int[0][0];
    }
}