package me.bristermitten.vouchers.hooks;

import com.google.inject.AbstractModule;
import org.bukkit.Bukkit;

public class HookModule extends AbstractModule {
    @Override
    protected void configure() {
        if (Bukkit.getPluginManager().isPluginEnabled("Vault")) {
            bind(PermissionChecker.class).to(VaultPermissionChecker.class);
        } else {
            bind(PermissionChecker.class).to(BukkitPermissionChecker.class);
        }
    }
}
