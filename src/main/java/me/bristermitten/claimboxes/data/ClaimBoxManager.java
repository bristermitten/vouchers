package me.bristermitten.claimboxes.data;

import com.google.inject.Inject;
import me.bristermitten.claimboxes.VoucherUtil;
import me.bristermitten.claimboxes.data.persistence.SQLClaimBoxPersistence;
import me.bristermitten.mittenlib.util.Futures;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.model.user.User;
import net.luckperms.api.model.user.UserManager;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class ClaimBoxManager {
    private final Logger logger = Logger.getLogger("ClaimBoxManager");
    private final LuckPerms luckPerms;
    private final ClaimBoxStorage claimBoxStorage;
    private final SQLClaimBoxPersistence persistence;

    @Inject
    public ClaimBoxManager(LuckPerms luckPerms, ClaimBoxStorage claimBoxStorage, SQLClaimBoxPersistence persistence) {
        this.luckPerms = luckPerms;
        this.claimBoxStorage = claimBoxStorage;
        this.persistence = persistence;
    }

    public CompletableFuture<Void> reset(ClaimBox claimBox) {
        logger.info(() -> "Resetting ClaimBox " + claimBox.getOwner());
        claimBox.editVoucherIds(List::clear);
        return persistence.delete(claimBox.getOwner())
                .thenRun(() -> logger.info(() -> "Saved ClaimBox from reset() " + claimBox.getOwner()))
                .exceptionally(e -> {
                    e.printStackTrace();
                    return null;
                });
    }

    public CompletableFuture<ClaimBox> getBox(UUID owner) {
        logger.info(() -> "Getting ClaimBox for " + owner);
        return claimBoxStorage.getOrCreate(owner);
    }

    public CompletableFuture<Void> give(ClaimBox claimBox, String voucherId, @Nullable String arg) {
        final String voucherString = VoucherUtil.makeVoucherString(voucherId, arg);
        logger.info(() -> "Giving voucher " + voucherString + " to " + claimBox.getOwner());
        claimBox.editVoucherIds(v -> v.add(voucherString));
        logger.info(() -> "Edited voucher list for " + claimBox.getOwner() + " to " + claimBox.getVoucherIds());
        return persistence.addOne(claimBox.getOwner(), voucherString)
                .thenRun(() -> logger.info(() -> "Saved ClaimBox from give() " + claimBox.getOwner()))
                .exceptionally(e -> {
                    e.printStackTrace();
                    return null;
                });
    }


    public void remove(ClaimBox claimBox, String voucherId, @Nullable String arg) {
        final String voucherString = VoucherUtil.makeVoucherString(voucherId, arg);
        logger.info(() -> "Removing voucher " + voucherString + " from " + claimBox.getOwner());
        claimBox.editVoucherIds(v -> v.remove(voucherString));
        logger.info(() -> "Edited voucher list for " + claimBox.getOwner() + " to " + claimBox.getVoucherIds());
        persistence.removeOne(claimBox.getOwner(), voucherString)
                .thenRun(() -> logger.info(() -> "Saved ClaimBox from remove() " + claimBox.getOwner()))
                .exceptionally(e -> {
                    e.printStackTrace();
                    return null;
                });
    }

    public CompletableFuture<Set<UUID>> giveAll(String group, boolean online, String voucherId, @Nullable String arg) {
        final UserManager userManager = luckPerms.getUserManager();
        //noinspection unchecked
        final CompletableFuture<User>[] futures = Arrays.stream(Bukkit.getOfflinePlayers())
                .filter(p -> !online || p.isOnline())
                .map(OfflinePlayer::getUniqueId)
                .map(userManager::loadUser)
                .toArray(CompletableFuture[]::new);

        final CompletableFuture<Collection<User>> userListFuture = Futures.sequence(futures);

        return userListFuture
                .thenApplyAsync(list -> {
                    final Set<ClaimBox> boxesToAdd = list.stream()
                            .filter(user -> user.getInheritedGroups(user.getQueryOptions())
                                    .stream().anyMatch(g -> g.getName().equals(group)))
                            .map(user -> claimBoxStorage.getOrCreate(user.getUniqueId()).join())
                            .collect(Collectors.toSet());
                    boxesToAdd.forEach(box -> give(box, voucherId, arg));
                    return boxesToAdd
                            .stream()
                            .map(ClaimBox::getOwner)
                            .collect(Collectors.toSet());
                });
    }

    public CompletableFuture<Void> resetAll() {
        return claimBoxStorage.loadAll()
                .thenRun(() -> {
                    for (ClaimBox value : claimBoxStorage.lookupAll().values()) {
                        reset(value);
                    }
                }).exceptionally(e -> {
                    e.printStackTrace();
                    return null;
                });
    }
}
