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
import me.bristermitten.vouchers.lang.ClaimBoxesLangService;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import javax.inject.Inject;
import javax.inject.Provider;
import java.util.Set;

public class ClaimBoxMenuFactory {
    private final Provider<ClaimBoxesConfig> configProvider;
    private final MessageFormatter messageFormatter;
    private final ConfirmVoucherUseMenuFactory confirmVoucherUseMenuFactory;
    private final MenuItems menuItems;
    private final ClaimBoxManager claimBoxManager;
    private final ClaimBoxesLangService langService;

    private final VoucherRegistry voucherRegistry;

    @Inject
    public ClaimBoxMenuFactory(Provider<ClaimBoxesConfig> configProvider, MessageFormatter messageFormatter, ConfirmVoucherUseMenuFactory confirmVoucherUseMenuFactory, MenuItems menuItems, ClaimBoxManager claimBoxManager, ClaimBoxesLangService langService, VoucherRegistry voucherRegistry) {
        this.configProvider = configProvider;
        this.messageFormatter = messageFormatter;
        this.confirmVoucherUseMenuFactory = confirmVoucherUseMenuFactory;
        this.menuItems = menuItems;
        this.claimBoxManager = claimBoxManager;
        this.langService = langService;
        this.voucherRegistry = voucherRegistry;
    }

    public GuiItem createItem(ClaimBox box, PaginatedGui gui, Voucher voucher) {
        ItemStack item = voucherRegistry.createVoucherItem(voucher, null);
        return new GuiItem(item, event -> {
            final Player whoClicked = (Player) event.getWhoClicked();
            confirmVoucherUseMenuFactory.create(
                    e -> gui.open(whoClicked),
                    e -> {
                        if (whoClicked.getInventory().firstEmpty() == -1) {
                            langService.send(whoClicked, conf -> conf.errors().inventoryFull());
                            return;
                        }
                        if (!box.getVouchers().contains(voucher)) {
                            create(box, whoClicked).open(whoClicked, gui.getCurrentPageNum());
                            return;
                        }
                        whoClicked.getInventory().addItem(item);
                        claimBoxManager.remove(box, voucher);
                        create(box, whoClicked).open(whoClicked, gui.getCurrentPageNum());
                    }, whoClicked
            ).open(whoClicked);
        });

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
                .create();


        gui.setItem(6, 3, new GuiItem(menuItems.toItem(config.gui().prevPage(), owner), e -> gui.previous()));
        gui.setItem(6, 7, new GuiItem(menuItems.toItem(config.gui().nextPage(), owner), e -> gui.next()));

        final Set<Voucher> vouchers = claimBox.getVouchers();
        for (Voucher voucher : vouchers) {
            gui.addItem(createItem(claimBox, gui, voucher));
        }

        return gui;
    }
}
