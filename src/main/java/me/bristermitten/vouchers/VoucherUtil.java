package me.bristermitten.vouchers;

import org.jetbrains.annotations.Nullable;

public class VoucherUtil {
    private VoucherUtil() {

    }
    public static String makeVoucherString(String id, @Nullable String arg) {
        if (arg == null) {
            return id;
        }
        return id + " " + arg;
    }
}
