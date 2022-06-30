package me.bristermitten.vouchers.data.voucher.type;

import me.bristermitten.vouchers.actions.Action;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

public class VoucherCodeType extends VoucherType {
    public VoucherCodeType(String id, Collection<Action> actions, @Nullable String defaultData, @Nullable String permission) {
        super(id, actions, defaultData, null, permission);
    }
}
