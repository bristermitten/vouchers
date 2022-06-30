package me.bristermitten.vouchers.data.voucher.type;

public class NormalVoucherType extends VoucherType {

    public NormalVoucherType(String id, VoucherTypeSettings settings) {
        super(id, settings);
        if (!settings.getItemDescriptor().isPresent()) {
            throw new IllegalArgumentException("Item descriptor is null for a normal voucher type");
        }
    }
}
