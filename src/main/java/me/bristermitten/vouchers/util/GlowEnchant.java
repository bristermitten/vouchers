package me.bristermitten.vouchers.util;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Field;


public class GlowEnchant extends Enchantment {

    public static final GlowEnchant GLOW = new GlowEnchant();
    private static final int ID = 31239;

    private GlowEnchant() {
        super(ID);
    }

    public static void registerGlow() throws NoSuchFieldException, IllegalAccessException {
        Field acceptingNew = Enchantment.class.getDeclaredField("acceptingNew");
        acceptingNew.setAccessible(true);
        acceptingNew.set(null, true);

        Enchantment.registerEnchantment(GLOW);
        acceptingNew.set(null, false);
    }

    @Override
    public String getName() {
        return "Glow";
    }

    @Override
    public int getMaxLevel() {
        return 1;
    }

    @Override
    public int getStartLevel() {
        return 1;
    }

    @Override
    public EnchantmentTarget getItemTarget() {
        return EnchantmentTarget.ALL;
    }

    @Override
    public boolean conflictsWith(Enchantment other) {
        return false;
    }

    @Override
    public boolean canEnchantItem(ItemStack item) {
        return true;
    }
}
