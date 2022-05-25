package me.bristermitten.vouchers.data.voucher;

import com.google.inject.AbstractModule;
import me.bristermitten.vouchers.data.voucher.persistence.VoucherPersistence;

public class VoucherModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(VoucherPersistence.class).to(VoucherRegistry.class);
    }
}
