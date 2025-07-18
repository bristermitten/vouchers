package me.bristermitten.vouchers.hooks;

import com.google.inject.AbstractModule;
import me.bristermitten.vouchers.hooks.vault.BukkitPermissionChecker;

public class DefaultHookModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(PermissionChecker.class).to(BukkitPermissionChecker.class);
    }
}
