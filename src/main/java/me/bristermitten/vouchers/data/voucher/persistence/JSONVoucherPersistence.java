package me.bristermitten.vouchers.data.voucher.persistence;

import com.google.gson.Gson;
import me.bristermitten.mittenlib.util.Futures;
import me.bristermitten.mittenlib.util.Unit;
import me.bristermitten.vouchers.data.voucher.Voucher;
import me.bristermitten.vouchers.database.RuntimePersistException;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class JSONVoucherPersistence implements VoucherPersistence {
    private static final String FILE_PATH = "Vouchers";

    private final Path filePath;

    private final Gson gson;

    @Inject
    public JSONVoucherPersistence(Plugin plugin, Gson gson) {
        this.filePath = plugin.getDataFolder().toPath().resolve(FILE_PATH);
        this.gson = gson;
    }

    @Override
    public @NotNull CompletableFuture<Unit> init() {
        return CompletableFuture.supplyAsync(() -> {
            try {
                Files.createDirectories(filePath);
            } catch (IOException e) {
                throw new RuntimePersistException(e);
            }
            return Unit.UNIT;
        });

    }

    private Path getVoucherFile(UUID voucherId) {
        return filePath.resolve(voucherId + ".json");
    }

    @Override
    public @NotNull CompletableFuture<Unit> save(@NotNull Voucher value) {
        Path path = getVoucherFile(value.getId());
        return init().thenCompose(x -> CompletableFuture.supplyAsync(() -> {
            try (Writer writer = Files.newBufferedWriter(path)) {
                gson.toJson(value, writer);
                writer.flush();
            } catch (IOException e) {
                throw new RuntimePersistException(e);
            }
            return Unit.UNIT;
        }));
    }

    @Override
    public @NotNull CompletableFuture<Optional<Voucher>> load(@NotNull UUID id) {
        Path path = getVoucherFile(id);
        return init().thenCompose(x -> load(path));
    }

    private CompletableFuture<Optional<Voucher>> load(Path path) {
        return CompletableFuture.supplyAsync(() -> {
            if (!Files.exists(path)) {
                return Optional.empty();
            }
            try (Reader reader = Files.newBufferedReader(path)) {
                return Optional.of(gson.fromJson(reader, Voucher.class));
            } catch (IOException e) {
                throw new RuntimePersistException(e);
            }
        });

    }

    @Override
    public @NotNull CompletableFuture<Unit> delete(@NotNull UUID id) {
        Path path = getVoucherFile(id);
        return CompletableFuture.supplyAsync(() -> {
            try {
                Files.deleteIfExists(path);
            } catch (IOException e) {
                throw new RuntimePersistException(e);
            }
            return Unit.UNIT;
        });
    }

    @Override
    public @NotNull CompletableFuture<Collection<Voucher>> loadAll() {
        try (Stream<Path> list = Files.list(filePath)) {
            return Futures.sequence(list.map(this::load).collect(Collectors.toList())).thenApply(optionals -> optionals.stream().filter(Optional::isPresent).map(Optional::get).collect(Collectors.toList()));
        } catch (IOException e) {
            throw new RuntimePersistException(e);
        }
    }

    @Override
    public @NotNull CompletableFuture<Unit> saveAll(@NotNull Collection<Voucher> values) {
        return Futures.sequence(values.stream().map(this::save).collect(Collectors.toList())).thenApply(x -> Unit.UNIT);
    }
}
