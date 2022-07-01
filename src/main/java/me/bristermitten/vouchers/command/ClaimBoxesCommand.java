package me.bristermitten.vouchers.command;

import co.aikar.commands.annotation.*;
import me.bristermitten.mittenlib.collections.Maps;
import me.bristermitten.mittenlib.commands.Command;
import me.bristermitten.vouchers.data.claimbox.ClaimBoxManager;
import me.bristermitten.vouchers.lang.ClaimBoxesLangConfig;
import me.bristermitten.vouchers.lang.VouchersLangService;
import me.bristermitten.vouchers.menu.ClaimBoxAdminMenuFactory;
import me.bristermitten.vouchers.menu.ClaimBoxMenuFactory;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import javax.inject.Inject;

import static me.bristermitten.vouchers.command.CommandPlaceholders.PLAYER;


@CommandAlias("claimbox|cb|cr")
public class ClaimBoxesCommand extends Command {
    private final ClaimBoxManager claimBoxManager;
    private final ClaimBoxMenuFactory menuFactory;
    private final VouchersLangService langService;
    private final ClaimBoxAdminMenuFactory adminMenuFactory;

    private final CommonVoucherMethods commonVoucherMethods;

    @Inject
    public ClaimBoxesCommand(ClaimBoxManager claimBoxManager, ClaimBoxMenuFactory menuFactory, VouchersLangService langService, ClaimBoxAdminMenuFactory adminMenuFactory, CommonVoucherMethods commonVoucherMethods) {
        this.claimBoxManager = claimBoxManager;
        this.menuFactory = menuFactory;
        this.langService = langService;
        this.adminMenuFactory = adminMenuFactory;
        this.commonVoucherMethods = commonVoucherMethods;
    }

    @Default
    public void onSelfOpen(Player player) {
        claimBoxManager.getBox(player.getUniqueId())
                .thenAccept(box -> {
                    if (box.getVouchers().isEmpty()) {
                        langService.send(player, conf -> conf.errors().claimboxEmpty());
                        return;
                    }
                    menuFactory.create(box, player).open(player);
                }).exceptionally(e -> {
                    e.printStackTrace();
                    return null;
                });
    }

    @Default
    @CommandPermission("claimbox.view.others")
    @CommandCompletion("@offlinePlayers")
    public void openOther(Player player, OfflinePlayer target) {
        claimBoxManager.getBox(target.getUniqueId())
                .thenAccept(box -> {
                    if (box.getVouchers().isEmpty()) {
                        langService.send(player, conf -> conf.errors().claimboxEmptyOther(), Maps.of(PLAYER, target.getName()));
                        return;
                    }
                    if (player.hasPermission("claimbox.edit.others")) {
                        adminMenuFactory.create(box, target).open(player);
                    } else {
                        menuFactory.create(box, target).open(player);
                    }
                }).exceptionally(e -> {
                    e.printStackTrace();
                    return null;
                });
    }

    @Subcommand("help")
    public void help() {
        //noinspection deprecation
        showCommandHelp();
    }

    @Subcommand("reset")
    @CommandPermission("claimbox.reset")
    public void resetSelf(Player player) {
        claimBoxManager.getBox(player.getUniqueId())
                .thenCompose(claimBoxManager::reset)
                .thenAccept(box -> langService.send(player, ClaimBoxesLangConfig::claimboxReset))
                .exceptionally(e -> {
                    e.printStackTrace();
                    return null;
                });
    }

    @Subcommand("resetall")
    @CommandPermission("resetall")
    public void resetAll(CommandSender sender) {
        claimBoxManager.resetAll()
                .thenRun(() ->
                        langService.send(sender, ClaimBoxesLangConfig::claimboxResetAll));
    }

    @Subcommand("reset")
    @CommandPermission("claimbox.reset.others")
    @CommandCompletion("@offlinePlayers")
    public void resetOther(Player player, OfflinePlayer target) {
        claimBoxManager.getBox(target.getUniqueId())
                .thenCompose(claimBoxManager::reset)
                .thenAccept(box -> langService.send(player, ClaimBoxesLangConfig::claimboxReset, Maps.of(PLAYER, target.getName())))
                .exceptionally(e -> {
                    e.printStackTrace();
                    return null;
                });
    }

    @Subcommand("give")
    @CommandPermission("claimbox.give")
    @CommandCompletion("@offlinePlayers @voucherIds")
    public void give(CommandSender sender, OfflinePlayer target, String voucherId, @Optional @Nullable String arg) {
        commonVoucherMethods.giveVoucher(sender, target, voucherId, arg, true);
    }
}
