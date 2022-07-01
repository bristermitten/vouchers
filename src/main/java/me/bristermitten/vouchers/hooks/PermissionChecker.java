package me.bristermitten.vouchers.hooks;

import org.bukkit.OfflinePlayer;

public interface PermissionChecker {
    boolean has(OfflinePlayer player, String permission);
}
