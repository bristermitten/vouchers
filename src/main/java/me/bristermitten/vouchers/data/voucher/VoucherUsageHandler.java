package me.bristermitten.vouchers.data.voucher;

import me.bristermitten.mittenlib.collections.Maps;
import me.bristermitten.mittenlib.util.lambda.Functions;
import me.bristermitten.vouchers.actions.Action;
import me.bristermitten.vouchers.lang.VouchersLangService;
import org.bukkit.entity.Player;

import javax.inject.Inject;
import java.util.Optional;

public class VoucherUsageHandler {

    private final VoucherRegistry voucherRegistry;
    private final VouchersLangService langService;
    private final VoucherUsageExceptionHandler exceptionHandler;

    @Inject
    public VoucherUsageHandler(VoucherRegistry voucherRegistry, VouchersLangService langService, VoucherUsageExceptionHandler exceptionHandler) {
        this.voucherRegistry = voucherRegistry;
        this.langService = langService;
        this.exceptionHandler = exceptionHandler;
    }

    /**
     * Uses a voucher
     *
     * @param voucher The voucher to use
     * @param user    The user to use the voucher for
     * @return If the voucher could be used or not. False is returned when {@link VoucherUsageException} is thrown
     */
    public boolean use(Voucher voucher, Player user) {
        try {
            useThrowing(voucher, user);
            return true;
        } catch (VoucherUsageException e) {
            exceptionHandler.handle(user, voucher, e);
            return false;
        }
    }

    private void useThrowing(Voucher voucher, Player user) throws VoucherUsageException {
        if (voucher.isUsed()) {
            throw new IllegalStateException("Voucher has already been used!");
        }
        Optional<String> permission = voucher.getType().getSettings().getPermission();
        if (permission.isPresent() && !user.hasPermission(permission.get())) {
            throw new VoucherUsageException.NoPermission(permission.get());
        }
        voucher.setUsed();
        voucherRegistry.save(voucher);
        voucher.getType().getSettings().getRedeemMessage().ifPresent(redeemMessage ->
                langService.send(user, Functions.constant(redeemMessage),
                        Maps.of(Action.DATA_PLACEHOLDER, String.valueOf(voucher.getData()), Action.PLAYER_PLACEHOLDER, user.getName())));

        for (Action action : voucher.getType().getSettings().getActions()) {
            action.runWith(user, voucher.getData());
        }
    }
}
