package me.bristermitten.vouchers.data.voucher.type;

import me.bristermitten.vouchers.actions.Action;
import me.bristermitten.vouchers.data.voucher.ItemDescriptor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

public class NormalVoucherType extends VoucherType {

    public NormalVoucherType(String id, Collection<Action> actions, @Nullable String defaultData, @NotNull ItemDescriptor itemDescriptor) {
        super(id, actions, defaultData, itemDescriptor);
    }
}
