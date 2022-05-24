package me.bristermitten.vouchers.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.BukkitCommandManager;
import co.aikar.commands.PaperCommandManager;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.Plugin;

import javax.inject.Inject;
import javax.inject.Provider;
import java.util.Set;

public class CommandManagerProvider implements Provider<BukkitCommandManager> {
    private final Plugin plugin;
    private final OfflinePlayerHandler offlinePlayerHandler;

    private final Set<BaseCommand> commands;

    @Inject
    public CommandManagerProvider(Plugin plugin, OfflinePlayerHandler offlinePlayerHandler, Set<BaseCommand> commands) {
        this.plugin = plugin;
        this.offlinePlayerHandler = offlinePlayerHandler;
        this.commands = commands;
    }

    @Override
    public BukkitCommandManager get() {
        final PaperCommandManager paperCommandManager = new PaperCommandManager(plugin);
        paperCommandManager.enableUnstableAPI("help");
        paperCommandManager.getCommandCompletions().registerAsyncCompletion("offlinePlayers", offlinePlayerHandler);
        paperCommandManager.getCommandContexts().registerContext(OfflinePlayer.class, offlinePlayerHandler);

        commands.forEach(paperCommandManager::registerCommand);
        return paperCommandManager;
    }
}
