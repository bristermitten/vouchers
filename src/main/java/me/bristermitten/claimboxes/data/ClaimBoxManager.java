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

import java.util.Arrays;
import java.util.Collection;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class ClaimBoxManager {
    private final LuckPerms luckPerms;
    private final ClaimBoxStorage claimBoxStorage;
    private final SQLClaimBoxPersistence persistence;

    @Inject
    public ClaimBoxManager(LuckPerms luckPerms, ClaimBoxStorage claimBoxStorage, SQLClaimBoxPersistence persistence) {
        this.luckPerms = luckPerms;
        this.claimBoxStorage = claimBoxStorage;
        this.persistence = persistence;
    }

    public void reset(ClaimBox claimBox) {
        claimBox.getMutableVoucherIds().clear();
        persistence.save(claimBox)
                .exceptionally(e -> {
                    e.printStackTrace();
                    return null;
                });
    }

    public CompletableFuture<ClaimBox> getBox(UUID owner) {
        return claimBoxStorage.getOrCreate(owner);
    }

    public void give(ClaimBox claimBox, String voucherId, @Nullable String arg) {
        final String voucherString = VoucherUtil.makeVoucherString(voucherId, arg);
        claimBox.getMutableVoucherIds().add(voucherString);
        persistence.addOne(claimBox.getOwner(), voucherString)
                .exceptionally(e -> {
                    e.printStackTrace();
                    return null;
                });
    }


    public void remove(ClaimBox claimBox, String voucherId, @Nullable String arg) {
        final String voucherString = VoucherUtil.makeVoucherString(voucherId, arg);
        claimBox.getMutableVoucherIds().remove(voucherString);
        persistence.removeOne(claimBox.getOwner(), voucherString)
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

    public void resetAll() {
        for (ClaimBox value : claimBoxStorage.lookupAll().values()) {
            reset(value);
        }
        claimBoxStorage.loadAll().thenAccept(c -> c.forEach(this::reset))
                .exceptionally(e -> {
                    e.printStackTrace();
                    return null;
                });
    }
}
