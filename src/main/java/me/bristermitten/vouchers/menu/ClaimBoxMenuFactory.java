package me.bristermitten.vouchers.menu;

import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.GuiItem;
import dev.triumphteam.gui.guis.PaginatedGui;
import me.badbones69.vouchers.api.Vouchers;
import me.badbones69.vouchers.api.objects.Voucher;
import me.bristermitten.vouchers.VoucherUtil;
import me.bristermitten.vouchers.config.ClaimBoxesConfig;
import me.bristermitten.vouchers.data.ClaimBox;
import me.bristermitten.vouchers.data.ClaimBoxManager;
import me.bristermitten.vouchers.lang.ClaimBoxesLangService;
import me.bristermitten.mittenlib.lang.format.MessageFormatter;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import javax.inject.Inject;
import javax.inject.Provider;
import java.util.List;

public class ClaimBoxMenuFactory {
    private final Provider<ClaimBoxesConfig> configProvider;
    private final MessageFormatter messageFormatter;
    private final ConfirmVoucherUseMenuFactory confirmVoucherUseMenuFactory;
    private final MenuItems menuItems;
    private final ClaimBoxManager claimBoxManager;
    private final ClaimBoxesLangService langService;

    @Inject
    public ClaimBoxMenuFactory(Provider<ClaimBoxesConfig> configProvider, MessageFormatter messageFormatter, ConfirmVoucherUseMenuFactory confirmVoucherUseMenuFactory, MenuItems menuItems, ClaimBoxManager claimBoxManager, ClaimBoxesLangService langService) {
        this.configProvider = configProvider;
        this.messageFormatter = messageFormatter;
        this.confirmVoucherUseMenuFactory = confirmVoucherUseMenuFactory;
        this.menuItems = menuItems;
        this.claimBoxManager = claimBoxManager;
        this.langService = langService;
    }

    public GuiItem createItem(ClaimBox box, PaginatedGui gui, String voucherId, @Nullable String args) {
        final Voucher voucher = Vouchers.getVoucher(voucherId);
        if (voucher == null) {
            throw new IllegalArgumentException("Unknown voucher " + voucherId);
        }

        final ItemStack item = args == null ? voucher.buildItem() : voucher.buildItem(args);
        return new GuiItem(item, event -> {
            final Player whoClicked = (Player) event.getWhoClicked();
            confirmVoucherUseMenuFactory.create(
                    e -> gui.open(whoClicked),
                    e -> {
                        if (whoClicked.getInventory().firstEmpty() == -1) {
                            langService.send(whoClicked, conf -> conf.errors().inventoryFull());
                            return;
                        }
                        if (!box.getVoucherIds().contains(VoucherUtil.makeVoucherString(voucherId, args))) {
                            create(box, whoClicked).open(whoClicked, gui.getCurrentPageNum());
                            return;
                        }
                        whoClicked.getInventory().addItem(item);
                        claimBoxManager.remove(box, voucherId, args);
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

        final List<String> voucherIds = claimBox.getVoucherIds();
        for (String voucherData : voucherIds) {
            String[] parts = voucherData.split(" ");
            String voucherId = parts[0];
            final String arg = parts.length == 1 ? null : parts[1];
            gui.addItem(createItem(claimBox, gui, voucherId, arg));
        }

        return gui;
    }
}
