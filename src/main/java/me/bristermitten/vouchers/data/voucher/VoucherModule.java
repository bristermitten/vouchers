package me.bristermitten.vouchers.data.voucher;

import com.google.inject.AbstractModule;
import me.bristermitten.vouchers.data.voucher.persistence.VoucherPersistence;
import me.bristermitten.vouchers.data.voucher.type.VoucherTypeCache;
import me.bristermitten.vouchers.data.voucher.type.VoucherTypeRegistry;

public class VoucherModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(VoucherPersistence.class).to(VoucherRegistry.class);
        bind(VoucherTypeRegistry.class).to(VoucherTypeCache.class);
    }
}
