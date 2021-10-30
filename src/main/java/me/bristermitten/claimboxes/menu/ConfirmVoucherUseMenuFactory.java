package me.bristermitten.claimboxes.menu;

import dev.triumphteam.gui.components.GuiAction;
import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.GuiItem;
import me.bristermitten.claimboxes.config.ClaimBoxesConfig;
import me.bristermitten.mittenlib.lang.format.MessageFormatter;
import org.bukkit.OfflinePlayer;
import org.bukkit.event.inventory.InventoryClickEvent;

import javax.inject.Inject;
import javax.inject.Provider;

public class ConfirmVoucherUseMenuFactory {
    private final Provider<ClaimBoxesConfig> configProvider;
    private final MessageFormatter messageFormatter;
    private final MenuItems menuItems;

    @Inject
    public ConfirmVoucherUseMenuFactory(Provider<ClaimBoxesConfig> configProvider, MessageFormatter messageFormatter, MenuItems menuItems) {
        this.configProvider = configProvider;
        this.messageFormatter = messageFormatter;
        this.menuItems = menuItems;
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

        gui.getFiller().fill(new GuiItem(menuItems.toItem(config.confirmGui().background(), owner)));
        gui.setItem(2, new GuiItem(menuItems.toItem(config.confirmGui().cancel(), owner), onCancel));
        gui.setItem(6, new GuiItem(menuItems.toItem(config.confirmGui().confirm(), owner), onConfirm));
        return gui;
    }
}
