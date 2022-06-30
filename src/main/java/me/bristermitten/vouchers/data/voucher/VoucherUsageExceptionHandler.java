package me.bristermitten.vouchers.data.voucher;

import me.bristermitten.mittenlib.collections.Maps;
import me.bristermitten.vouchers.lang.VouchersLangService;
import org.bukkit.entity.Player;

import javax.inject.Inject;

public class VoucherUsageExceptionHandler {
    private final VouchersLangService langService;

    @Inject
    public VoucherUsageExceptionHandler(VouchersLangService langService) {
        this.langService = langService;
    }

    public void handle(Player user, Voucher voucher, VoucherUsageException exception) {
        if (exception instanceof VoucherUsageException.NoPermission) {
            VoucherUsageException.NoPermission cast = (VoucherUsageException.NoPermission) exception;
            langService.send(user,
                    config -> config.errors().voucherNoPermission(),
                    Maps.of("{permission}", cast.getRequiredPermission()));
            return;
        }
        throw new UnsupportedOperationException("Don't know how to handle " + exception.getClass().getName() + " for " + voucher, exception);
    }
}
