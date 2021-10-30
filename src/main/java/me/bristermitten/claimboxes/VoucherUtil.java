package me.bristermitten.claimboxes;

import me.badbones69.vouchers.api.objects.Voucher;
import me.badbones69.vouchers.controllers.VoucherClick;
import me.bristermitten.mittenlib.util.Cached;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.RegisteredListener;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class VoucherUtil {
    private static final Cached<VoucherClick> voucherClick = new Cached<>(() -> {
        for (RegisteredListener registeredListener : PlayerInteractEvent.getHandlerList().getRegisteredListeners()) {
            if (registeredListener.getListener() instanceof VoucherClick) {
                return (VoucherClick) registeredListener.getListener(); // hmm today i will make a well written premium plugin
            }
        }
        throw new IllegalStateException("VoucherClick not found");
    });
    private static final Cached<Method> handleClick = new Cached<>(() -> {
        try {
            final Method useVoucher = VoucherClick.class.getDeclaredMethod("useVoucher", Player.class, Voucher.class, ItemStack.class);
            useVoucher.setAccessible(true); //NOSONAR shut it
            return useVoucher;
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    });

    private VoucherUtil() {
    }

    public static void redeemVoucher(Voucher voucher, Player player, ItemStack itemStack) {
        try {
            handleClick.get().invoke(voucherClick.get(), player, voucher, itemStack);
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    public static String makeVoucherString(String id, @Nullable String arg) {
        if (arg == null) {
            return id;
        }
        return id + " " + arg;
    }
}
