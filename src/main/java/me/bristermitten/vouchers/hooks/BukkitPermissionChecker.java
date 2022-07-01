package me.bristermitten.vouchers.hooks;

import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.Plugin;

import javax.inject.Inject;

public class BukkitPermissionChecker implements PermissionChecker {
    @Inject
    public BukkitPermissionChecker(Plugin plugin) {
        plugin.getLogger().warning("You do not have a Permissions Plugin / Vault installed and so some features will not fully work");
    }

    @Override
    public boolean has(OfflinePlayer player, String permission) {
        if (player.isOnline()) {
            return player.getPlayer().hasPermission(permission);
        } else {
            return false;
        }
    }
}
