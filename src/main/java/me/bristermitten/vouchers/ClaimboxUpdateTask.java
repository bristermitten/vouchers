package me.bristermitten.vouchers;

import me.bristermitten.vouchers.config.ClaimBoxesConfig;
import me.bristermitten.vouchers.data.ClaimBoxStorage;
import me.bristermitten.mittenlib.util.Unit;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import javax.inject.Inject;
import javax.inject.Provider;
import java.util.concurrent.CompletableFuture;

public class ClaimboxUpdateTask {
    private final Provider<ClaimBoxesConfig> configProvider;
    private final ClaimBoxStorage persistence;
    private final Plugin plugin;

    @Inject
    public ClaimboxUpdateTask(Provider<ClaimBoxesConfig> configProvider, ClaimBoxStorage persistence, Plugin plugin) {
        this.configProvider = configProvider;
        this.persistence = persistence;
        this.plugin = plugin;
    }

    public CompletableFuture<Unit> run() {
        return persistence.loadAll().exceptionally(t -> {
            t.printStackTrace();
            return null;
        }).thenApply(e -> Unit.UNIT);
    }

    public void schedule() {
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            run().thenRun(() ->
                    plugin.getLogger().info(() ->
                            String.format("Updated %d Claimboxes from database", persistence.lookupAll().size())));

            schedule();
        }, configProvider.get().updateInterval() * 20L);
    }
}
