package me.bristermitten.claimboxes.lang;

import me.bristermitten.mittenlib.lang.LangMessage;
import me.bristermitten.mittenlib.lang.LangService;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.inject.Inject;
import javax.inject.Provider;
import java.util.Map;
import java.util.function.Function;

public class ClaimBoxesLangService {
    private final LangService langService;
    private final Provider<ClaimBoxesLangConfig> configProvider;

    @Inject
    public ClaimBoxesLangService(LangService langService, Provider<ClaimBoxesLangConfig> configProvider) {
        this.langService = langService;
        this.configProvider = configProvider;
    }

    public void send(@NotNull CommandSender receiver, @NotNull Function<ClaimBoxesLangConfig, LangMessage> langMessage) {
        langService.send(receiver, langMessage.apply(configProvider.get()));
    }

    public void send(@NotNull CommandSender receiver, @NotNull Function<ClaimBoxesLangConfig, LangMessage> langMessage, @NotNull Map<String, Object> placeholders) {
        langService.send(receiver, langMessage.apply(configProvider.get()), placeholders);
    }

    public void send(@NotNull CommandSender receiver, @NotNull Function<ClaimBoxesLangConfig, LangMessage> langMessage, @Nullable String messagePrefix) {
        langService.send(receiver, langMessage.apply(configProvider.get()), messagePrefix);
    }

    public void send(@NotNull CommandSender receiver, @NotNull Function<ClaimBoxesLangConfig, LangMessage> langMessage, @NotNull Map<String, Object> placeholders, @Nullable String messagePrefix) {
        langService.send(receiver, langMessage.apply(configProvider.get()), placeholders, messagePrefix);
    }
}
