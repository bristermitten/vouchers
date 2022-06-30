package me.bristermitten.vouchers.data.voucher.type;

public abstract class VoucherType {
    private final String id;
    private final VoucherTypeSettings settings;

    protected VoucherType(String id, VoucherTypeSettings settings) {
        this.id = id;
        this.settings = settings;
    }

    public VoucherTypeSettings getSettings() {
        return settings;
    }

    public String getId() {
        return id;
    }
}
