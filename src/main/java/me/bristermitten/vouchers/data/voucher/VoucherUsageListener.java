package me.bristermitten.vouchers.data.voucher;

import de.tr7zw.changeme.nbtapi.NBTItem;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import javax.inject.Inject;
import java.util.Optional;
import java.util.UUID;
import java.util.logging.Logger;

public class VoucherUsageListener implements Listener {
    private final VoucherRegistry voucherRegistry;
    private final Logger logger;

    @Inject
    public VoucherUsageListener(VoucherRegistry voucherRegistry, Logger logger) {
        this.voucherRegistry = voucherRegistry;
        this.logger = logger;
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        ItemStack item = event.getItem();
        if (item == null) return;
        if (item.getType() == Material.AIR) return;
        NBTItem nbtItem = new NBTItem(item);
        if (Boolean.FALSE.equals(nbtItem.hasKey(Voucher.NBT_KEY))) return;
        UUID id = UUID.fromString(nbtItem.getString(Voucher.NBT_KEY));

        Optional<Voucher> voucherOptional = voucherRegistry.get(id);
        if (!voucherOptional.isPresent()) {
            logger.warning(() -> "Voucher with id " + id + " not found");
            return;
        }
        Voucher voucher = voucherOptional.get();
        if (voucher.isUsed()) {
            logger.warning(() -> "Voucher with id " + id + " has already been used");
            event.getPlayer().getInventory().remove(item);
            event.setCancelled(true);
            return;
        }
        voucher.use(event.getPlayer());
        event.setCancelled(true);
        event.getPlayer().getInventory().remove(item);
    }
}
