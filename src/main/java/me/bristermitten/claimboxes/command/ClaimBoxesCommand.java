package me.bristermitten.claimboxes.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import me.badbones69.vouchers.api.Vouchers;
import me.bristermitten.claimboxes.data.ClaimBoxManager;
import me.bristermitten.claimboxes.lang.ClaimBoxesLangConfig;
import me.bristermitten.claimboxes.lang.ClaimBoxesLangService;
import me.bristermitten.claimboxes.menu.ClaimBoxAdminMenuFactory;
import me.bristermitten.claimboxes.menu.ClaimBoxMenuFactory;
import me.bristermitten.mittenlib.collections.Maps;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import javax.inject.Inject;


@CommandAlias("claimbox|cb|cr")
public class ClaimBoxesCommand extends BaseCommand {
    public static final String PLAYER = "{player}";
    private final ClaimBoxManager claimBoxManager;
    private final ClaimBoxMenuFactory menuFactory;
    private final ClaimBoxesLangService langService;
    private final ClaimBoxAdminMenuFactory adminMenuFactory;

    @Inject
    public ClaimBoxesCommand(ClaimBoxManager claimBoxManager, ClaimBoxMenuFactory menuFactory, ClaimBoxesLangService langService, ClaimBoxAdminMenuFactory adminMenuFactory) {
        this.claimBoxManager = claimBoxManager;
        this.menuFactory = menuFactory;
        this.langService = langService;
        this.adminMenuFactory = adminMenuFactory;
    }

    @Default
    public void onSelfOpen(Player player) {
        claimBoxManager.getBox(player.getUniqueId())
                .thenAccept(box -> {
                    if (box.getVoucherIds().isEmpty()) {
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
                    if (box.getVoucherIds().isEmpty()) {
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
                .thenAccept(box -> {
                    claimBoxManager.reset(box);
                    langService.send(player, ClaimBoxesLangConfig::claimboxReset);
                }).exceptionally(e -> {
                    e.printStackTrace();
                    return null;
                });
    }

    @Subcommand("reset")
    @CommandPermission("claimbox.reset.others")
    @CommandCompletion("@offlinePlayers")
    public void resetOther(Player player, OfflinePlayer target) {
        claimBoxManager.getBox(target.getUniqueId())
                .thenAccept(box -> {
                    claimBoxManager.reset(box);
                    langService.send(player, ClaimBoxesLangConfig::claimboxReset, Maps.of(PLAYER, target.getName()));
                }).exceptionally(e -> {
                    e.printStackTrace();
                    return null;
                });
    }

    @Subcommand("give")
    @CommandPermission("claimbox.give")
    @CommandCompletion("@offlinePlayers")
    public void give(CommandSender sender, OfflinePlayer target, String voucherId, @Optional @Nullable String arg) {
        if (Vouchers.getVoucher(voucherId) == null) {
            langService.send(sender, conf -> conf.errors().unknownVoucher(), Maps.of("{id}", voucherId));
            return;
        }
        claimBoxManager.getBox(target.getUniqueId())
                .thenAccept(box -> {
                    claimBoxManager.give(box, voucherId, arg);
                    langService.send(sender, ClaimBoxesLangConfig::voucherGiven, Maps.of(PLAYER, target.getName(), "{id}", voucherId));
                }).exceptionally(e -> {
                    e.printStackTrace();
                    return null;
                });

    }

    @Subcommand("giveall")
    @CommandPermission("claimbox.giveall")
    public void giveAll(CommandSender sender, String targetGroup, boolean online, String voucherId, @Optional @Nullable String arg) {
        claimBoxManager.giveAll(targetGroup, online, voucherId, arg)
                .thenAccept(box ->
                        langService.send(sender, ClaimBoxesLangConfig::voucherGivenAll,
                                Maps.of("{group}", targetGroup, "{id}", voucherId)))
                .exceptionally(e -> {
                    e.printStackTrace();
                    return null;
                });
    }

}
