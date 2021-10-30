package me.bristermitten.claimboxes.command;

import co.aikar.commands.BukkitCommandManager;
import co.aikar.commands.PaperCommandManager;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.Plugin;

import javax.inject.Inject;
import javax.inject.Provider;

public class CommandManagerProvider implements Provider<BukkitCommandManager> {
    private final Plugin plugin;
    private final OfflinePlayerHandler offlinePlayerHandler;

    @Inject
    public CommandManagerProvider(Plugin plugin, OfflinePlayerHandler offlinePlayerHandler) {
        this.plugin = plugin;
        this.offlinePlayerHandler = offlinePlayerHandler;
    }

    @Override
    public BukkitCommandManager get() {
        final PaperCommandManager paperCommandManager = new PaperCommandManager(plugin);
        paperCommandManager.enableUnstableAPI("help");
        paperCommandManager.getCommandCompletions().registerAsyncCompletion("offlinePlayers", offlinePlayerHandler);
        paperCommandManager.getCommandContexts().registerContext(OfflinePlayer.class, offlinePlayerHandler);
        return paperCommandManager;
    }
}
