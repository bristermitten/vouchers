package me.bristermitten.vouchers.menu;

import dev.triumphteam.gui.components.GuiAction;
import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.GuiItem;
import me.bristermitten.vouchers.config.ClaimBoxesConfig;
import me.bristermitten.mittenlib.lang.format.MessageFormatter;
import me.bristermitten.vouchers.config.ItemCreator;
import org.bukkit.OfflinePlayer;
import org.bukkit.event.inventory.InventoryClickEvent;

import javax.inject.Inject;
import javax.inject.Provider;

public class ConfirmVoucherUseMenuFactory {
    private final Provider<ClaimBoxesConfig> configProvider;
    private final MessageFormatter messageFormatter;
    private final ItemCreator creator;

    @Inject
    public ConfirmVoucherUseMenuFactory(Provider<ClaimBoxesConfig> configProvider, MessageFormatter messageFormatter, ItemCreator creator) {
        this.configProvider = configProvider;
        this.messageFormatter = messageFormatter;
        this.creator = creator;
    }


    public Gui create(GuiAction<InventoryClickEvent> onCancel, GuiAction<InventoryClickEvent> onConfirm, OfflinePlayer owner) {
        final ClaimBoxesConfig config = configProvider.get();
        final Gui gui = Gui.gui()
                .rows(1)
                .disableAllInteractions()
                .title(messageFormatter.format(
                        config.confirmGui().title(),
                        owner
                ))
                .create();

        gui.getFiller().fill(new GuiItem(creator.toItem(config.confirmGui().background(), owner)));
        gui.setItem(2, new GuiItem(creator.toItem(config.confirmGui().cancel(), owner), onCancel));
        gui.setItem(6, new GuiItem(creator.toItem(config.confirmGui().confirm(), owner), onConfirm));
        return gui;
    }
}
