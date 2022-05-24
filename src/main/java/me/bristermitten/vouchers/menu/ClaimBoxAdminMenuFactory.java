package me.bristermitten.vouchers.menu;

import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.GuiItem;
import dev.triumphteam.gui.guis.PaginatedGui;
import me.badbones69.vouchers.api.Vouchers;
import me.badbones69.vouchers.api.objects.Voucher;
import me.bristermitten.vouchers.config.ClaimBoxesConfig;
import me.bristermitten.vouchers.config.data.ClaimBox;
import me.bristermitten.vouchers.config.data.ClaimBoxManager;
import me.bristermitten.mittenlib.lang.format.MessageFormatter;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import javax.inject.Inject;
import javax.inject.Provider;
import java.util.List;

public class ClaimBoxAdminMenuFactory {
    private final Provider<ClaimBoxesConfig> configProvider;
    private final MessageFormatter messageFormatter;
    private final MenuItems menuItems;
    private final ClaimBoxManager claimBoxManager;

    @Inject
    public ClaimBoxAdminMenuFactory(Provider<ClaimBoxesConfig> configProvider, MessageFormatter messageFormatter, MenuItems menuItems, ClaimBoxManager claimBoxManager) {
        this.configProvider = configProvider;
        this.messageFormatter = messageFormatter;
        this.menuItems = menuItems;
        this.claimBoxManager = claimBoxManager;
    }

    public GuiItem createItem(ClaimBox box, String voucherId, @Nullable String args) {
        final Voucher voucher = Vouchers.getVoucher(voucherId);
        if (voucher == null) {
            throw new IllegalArgumentException("Unknown voucher " + voucherId);
        }

        final ItemStack item = args == null ? voucher.buildItem() : voucher.buildItem(args);
        return new GuiItem(item, event -> claimBoxManager.remove(box, voucherId, args));
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
            final Voucher voucherFromItem = Vouchers.getVoucherFromItem(cursor);
            if (voucherFromItem == null) {
                return;
            }
            final String argument = Vouchers.getArgument(cursor, voucherFromItem);
            if (event.getClickedInventory() == event.getView().getTopInventory()) {
                claimBoxManager.give(claimBox, voucherFromItem.getName(), argument);
            } else {
                claimBoxManager.remove(claimBox, voucherFromItem.getName(), argument);
            }
        });


        final List<String> voucherIds = claimBox.getVoucherIds();
        for (String voucherData : voucherIds) {
            String[] parts = voucherData.split(" ");
            String voucherId = parts[0];
            final String arg = parts.length == 1 ? null : parts[1];
            gui.addItem(createItem(claimBox, voucherId, arg));
        }

        return gui;
    }
}
