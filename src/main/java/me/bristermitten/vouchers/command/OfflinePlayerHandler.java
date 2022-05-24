package me.bristermitten.vouchers.command;

import co.aikar.commands.BukkitCommandCompletionContext;
import co.aikar.commands.BukkitCommandExecutionContext;
import co.aikar.commands.CommandCompletions;
import co.aikar.commands.InvalidCommandArgument;
import co.aikar.commands.contexts.ContextResolver;
import me.bristermitten.vouchers.lang.ClaimBoxesLangService;
import me.bristermitten.mittenlib.collections.Maps;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import javax.inject.Inject;
import java.util.Arrays;
import java.util.Collection;
import java.util.UUID;
import java.util.stream.Collectors;

public class OfflinePlayerHandler implements CommandCompletions.AsyncCommandCompletionHandler<BukkitCommandCompletionContext>,
        ContextResolver<OfflinePlayer, BukkitCommandExecutionContext> {
    private final ClaimBoxesLangService langService;

    @Inject
    public OfflinePlayerHandler(ClaimBoxesLangService langService) {
        this.langService = langService;
    }

    @Override
    public Collection<String> getCompletions(BukkitCommandCompletionContext context) throws InvalidCommandArgument {
        return Arrays.stream(Bukkit.getOfflinePlayers())
                .map(p -> p.getName() == null ? p.getUniqueId().toString() : p.getName())
                .collect(Collectors.toList());
    }

    @Override
    public OfflinePlayer getContext(BukkitCommandExecutionContext bukkitCommandExecutionContext) throws InvalidCommandArgument {
        final String target = bukkitCommandExecutionContext.popFirstArg();
        if (target.length() == 36) {
            // try parse it as a UUID
            try {
                return Bukkit.getOfflinePlayer(UUID.fromString(target));
            } catch (IllegalArgumentException ignored) {
                // ignore the exception, treat it as a normal player name (even though it won't be, just DRY)
            }
        }
        //noinspection deprecation
        final OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(target);
        if (offlinePlayer == null) {
            langService.send(bukkitCommandExecutionContext.getSender(), conf -> conf.errors().unknownPlayer(), Maps.of("{text}", target));
            throw new InvalidCommandArgument(false);
        }
        return offlinePlayer;
    }
}
