package cn.nukkit.item.enchantment.damage;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class EnchantmentDamageAll extends EnchantmentDamage {

    public EnchantmentDamageAll() {
        super(ID_DAMAGE_ALL, "all", 10, TYPE.ALL);
    }

    @Override
    public int getMinEnchantAbility(int level) {
        return 1 + (level - 1) * 11;
    }

    @Override
    public int getMaxEnchantAbility(int level) {
        return this.getMinEnchantAbility(level) + 20;
    }

    @Override
    public int getMaxEnchantableLevel() {
        return 4;
    }
}
