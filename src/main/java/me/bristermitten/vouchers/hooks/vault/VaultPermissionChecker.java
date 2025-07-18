package me.bristermitten.vouchers.hooks.vault;

import me.bristermitten.mittenlib.util.Cached;
import me.bristermitten.vouchers.hooks.PermissionChecker;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.RegisteredServiceProvider;

public class VaultPermissionChecker implements PermissionChecker {
    private final Cached<Permission> permissionCached = new Cached<>(() -> {
        RegisteredServiceProvider<Permission> registration = Bukkit.getServicesManager().getRegistration(Permission.class);
        if (registration == null) {
            throw new IllegalStateException("Vault & a Permissions Plugin is not installed");
        }
        return registration.getProvider();
    });

    @Override
    public boolean has(OfflinePlayer player, String permission) {
        return permissionCached.get().playerHas(null, player, permission);
    }
}
