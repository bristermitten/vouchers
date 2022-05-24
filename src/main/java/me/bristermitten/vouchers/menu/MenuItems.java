package me.bristermitten.vouchers.menu;

import me.bristermitten.vouchers.config.ClaimBoxesConfig;
import me.bristermitten.mittenlib.lang.format.MessageFormatter;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import javax.inject.Inject;

public class MenuItems {
    private final MessageFormatter messageFormatter;

    @Inject
    public MenuItems(MessageFormatter messageFormatter) {
        this.messageFormatter = messageFormatter;
    }

    public ItemStack toItem(ClaimBoxesConfig.ItemConfig config, OfflinePlayer viewer) {
        final ItemStack itemStack = new ItemStack(config.type(), 1, config.data());
        final ItemMeta meta = itemStack.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(messageFormatter.preFormat(config.name(), viewer));
            itemStack.setItemMeta(meta);
        }
        return itemStack;
    }
}
