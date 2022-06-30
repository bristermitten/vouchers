package me.bristermitten.vouchers.util;

public class NumberUtil {
    private NumberUtil() {

    }

    public static Integer parseHex(String hex) {
        if (hex == null) {
            return null;
        }
        if (hex.startsWith("0x")) {
            hex = hex.substring(2);
        }
        if (hex.startsWith("#")) {
            hex = hex.substring(1);
        }
        return Integer.parseInt(hex, 16);
    }
}
