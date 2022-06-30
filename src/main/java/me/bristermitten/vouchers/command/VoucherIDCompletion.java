package me.bristermitten.vouchers.command;

import co.aikar.commands.BukkitCommandCompletionContext;
import co.aikar.commands.InvalidCommandArgument;
import me.bristermitten.mittenlib.commands.handlers.TabCompleter;
import me.bristermitten.vouchers.data.voucher.type.VoucherType;
import me.bristermitten.vouchers.data.voucher.type.VoucherTypeRegistry;
import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;
import java.util.Collection;
import java.util.stream.Collectors;

public class VoucherIDCompletion implements TabCompleter {
    private final VoucherTypeRegistry registry;

    @Inject
    public VoucherIDCompletion(VoucherTypeRegistry registry) {
        this.registry = registry;
    }

    @Override
    public Collection<String> getCompletions(BukkitCommandCompletionContext context) throws InvalidCommandArgument {
        return registry.getAll()
                .stream()
                .map(VoucherType::getId)
                .collect(Collectors.toList());
    }

    @Override
    public @NotNull String id() {
        return "voucherIds";
    }
}
