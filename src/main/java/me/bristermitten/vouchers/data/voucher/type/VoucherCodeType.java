package me.bristermitten.vouchers.data.voucher.type;

public class VoucherCodeType extends VoucherType {
    public VoucherCodeType(String id, VoucherTypeSettings settings) {
        super(id, settings);
        if (settings.getItemDescriptor().isPresent()) {
            throw new IllegalArgumentException("Item descriptor is not null for a voucher code type");
        }
    }
}
