package me.bristermitten.vouchers.data;

import com.google.inject.AbstractModule;

public class ClaimBoxDataModule  extends AbstractModule {
    @Override
    protected void configure() {
        bind(ClaimBoxPersistence.class).to(ClaimBoxStorage.class);
    }
}
