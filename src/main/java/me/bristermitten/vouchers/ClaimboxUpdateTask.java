package me.bristermitten.vouchers;

import me.bristermitten.mittenlib.util.Unit;
import me.bristermitten.vouchers.config.ClaimBoxesConfig;
import me.bristermitten.vouchers.data.claimbox.ClaimBoxStorage;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;
import javax.inject.Provider;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;

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

    public @NotNull CompletableFuture<Unit> run() {
        return persistence.flush();
    }

    public void schedule() {
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            run().thenRun(() ->
                            plugin.getLogger().info(() ->
                                    String.format("Updated %d Claimboxes from database", persistence.lookupAll().size())))
                    .exceptionally(e -> {
                        plugin.getLogger().log(Level.SEVERE, e, () -> String.format("Failed to update Claimboxes from database!: %s", e.getMessage()));
                        return null;
                    });

            schedule();
        }, configProvider.get().updateInterval() * 20L);
    }
}
