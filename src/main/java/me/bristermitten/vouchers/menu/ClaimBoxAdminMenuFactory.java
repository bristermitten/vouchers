package me.bristermitten.vouchers.menu;

import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.GuiItem;
import dev.triumphteam.gui.guis.PaginatedGui;
import me.bristermitten.mittenlib.lang.format.MessageFormatter;
import me.bristermitten.vouchers.config.ClaimBoxesConfig;
import me.bristermitten.vouchers.data.claimbox.ClaimBox;
import me.bristermitten.vouchers.data.claimbox.ClaimBoxManager;
import me.bristermitten.vouchers.data.voucher.Voucher;
import me.bristermitten.vouchers.data.voucher.VoucherRegistry;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemStack;

import javax.inject.Inject;
import javax.inject.Provider;
import java.util.Optional;

public class ClaimBoxAdminMenuFactory {
    private final Provider<ClaimBoxesConfig> configProvider;
    private final MessageFormatter messageFormatter;
    private final MenuItems menuItems;
    private final ClaimBoxManager claimBoxManager;

    private final VoucherRegistry voucherRegistry;

    @Inject
    public ClaimBoxAdminMenuFactory(Provider<ClaimBoxesConfig> configProvider, MessageFormatter messageFormatter, MenuItems menuItems, ClaimBoxManager claimBoxManager, VoucherRegistry voucherRegistry) {
        this.configProvider = configProvider;
        this.messageFormatter = messageFormatter;
        this.menuItems = menuItems;
        this.claimBoxManager = claimBoxManager;
        this.voucherRegistry = voucherRegistry;
    }

    public GuiItem createItem(ClaimBox box, me.bristermitten.vouchers.data.voucher.Voucher voucher) {
        final ItemStack item = voucherRegistry.createVoucherItem(voucher, null);
        return new GuiItem(item, event -> claimBoxManager.remove(box, voucher));
    }


    public PaginatedGui create(ClaimBox claimBox, OfflinePlayer owner) {
        final ClaimBoxesConfig config = configProvider.get();
        final PaginatedGui gui = Gui.paginated()
                .rows(6)
                .disableAllInteractions()
                .title(messageFormatter.format(
                        config.gui().title(),
                        owner
                ))
                .enableItemTake()
                .enableItemPlace()
                .create();

        gui.setItem(6, 3, new GuiItem(menuItems.toItem(config.gui().prevPage(), owner), e -> gui.previous()));
        gui.setItem(6, 7, new GuiItem(menuItems.toItem(config.gui().nextPage(), owner), e -> gui.next()));

        gui.setDefaultClickAction(event -> {
            if (event.getCurrentItem() != null && event.getCurrentItem().getType() != Material.AIR) {
                return;
            }
            final ItemStack cursor = event.getCursor();
            if (cursor == null || cursor.getType() == Material.AIR) {
                return;
            }
            final Optional<Voucher> voucherFromItemOpt = voucherRegistry.lookupFromItem(cursor);
            if (!voucherFromItemOpt.isPresent()) {
                return;
            }
            final Voucher voucherFromItem = voucherFromItemOpt.get();
            if (event.getClickedInventory() == event.getView().getTopInventory()) {
                claimBoxManager.give(claimBox, voucherFromItem);
            } else {
                claimBoxManager.remove(claimBox, voucherFromItem);
            }
        });


        for (Voucher voucher : claimBox.getVouchers()) {
            gui.addItem(createItem(claimBox, voucher));
        }

        return gui;
    }
}
