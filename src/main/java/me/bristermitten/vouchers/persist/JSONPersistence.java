package me.bristermitten.vouchers.persist;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import me.bristermitten.mittenlib.util.Futures;
import me.bristermitten.mittenlib.util.Unit;
import me.bristermitten.vouchers.database.RuntimePersistException;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * JSON-based implementation of the Persistence interface.
 * Stores each entity in its own JSON file.
 *
 * @param <ID> The ID type
 * @param <T> The entity type
 */
public abstract class JSONPersistence<ID, T> implements Persistence<ID, T> {

    protected final Path filePath;

    private final Gson gson;
    private final TypeToken<T> token;

    protected JSONPersistence(Path filePath, Gson gson, TypeToken<T> token) {
        this.filePath = filePath;
        this.gson = gson;
        this.token = token;
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

    protected abstract Path getFilePath(T value);

    protected abstract Path getFilePathFromId(ID id);

    @Override
    @NotNull
    public CompletableFuture<Unit> save(@NotNull T value) {
        Path path = getFilePath(value);
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
    public @NotNull CompletableFuture<Optional<T>> load(@NotNull ID id) {
        Path path = getFilePathFromId(id);
        return init().thenCompose(x -> loadFrom(path));
    }

    private CompletableFuture<Optional<T>> loadFrom(Path path) {
        return CompletableFuture.supplyAsync(() -> {
            if (!Files.exists(path)) {
                return Optional.empty();
            }
            try (Reader reader = Files.newBufferedReader(path)) {
                return Optional.of(gson.fromJson(reader, token.getType()));
            } catch (IOException e) {
                throw new RuntimePersistException(e);
            }
        });

    }

    @Override
    public @NotNull CompletableFuture<Unit> delete(@NotNull ID id) {
        Path path = getFilePathFromId(id);
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
    public @NotNull CompletableFuture<Collection<T>> loadAll() {
        try (Stream<Path> list = Files.list(filePath)) {
            return Futures.sequence(list.map(this::loadFrom).collect(Collectors.toList()))
                    .thenApply(optionals -> optionals.stream()
                            .filter(Optional::isPresent)
                            .map(Optional::get)
                            .collect(Collectors.toList()));
        } catch (IOException e) {
            throw new RuntimePersistException(e);
        }
    }

    @Override
    public @NotNull CompletableFuture<Unit> saveAll(@NotNull Collection<T> values) {
        return Futures.sequence(values.stream().map(this::save).collect(Collectors.toList()))
                .thenApply(x -> Unit.UNIT);
    }

    @Override
    public @NotNull CompletableFuture<Unit> flush() {
        return Unit.unitFuture();
    }
}
