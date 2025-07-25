package me.bristermitten.vouchers.persist;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import me.bristermitten.mittenlib.util.Unit;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.function.Function;

import static java.util.Optional.of;
import static java.util.concurrent.CompletableFuture.completedFuture;

public class CachingPersistence<I, T, P extends Persistence<I, T>> implements Persistence<I, T> {
    protected final Cache<I, T> cache = createCache();
    protected final P delegate;
    private final Function<T, I> idFunction;

    public CachingPersistence(P delegate, Function<T, I> idFunction) {
        this.delegate = delegate;
        this.idFunction = idFunction;
    }

    protected Cache<I, T> createCache() {
        return CacheBuilder.newBuilder().build();
    }

    protected @Nullable T addToCache(I id, T data) {
        T previous = cache.getIfPresent(id);
        cache.put(id, data);
        return previous;
    }

    protected void addToCache(T data) {
        addToCache(idFunction.apply(data), data);
    }

    @Override
    public @NotNull CompletableFuture<Unit> init() {
        return delegate.init().thenCompose(unit ->
                delegate.loadAll()
                        .thenApply(elements -> {
                            for (T element : elements) {
                                addToCache(idFunction.apply(element), element);
                            }
                            return Unit.UNIT;
                        }));
    }

    @Override
    public @NotNull CompletableFuture<Unit> cleanup() {
        return flush();
    }

    @Override
    public @NotNull CompletableFuture<Unit> flush() {
        return saveAll(cache.asMap().values()); // Flush the cache
    }

    @Override
    public @NotNull CompletableFuture<Unit> save(@NotNull T value) {
        addToCache(value);
        return delegate.save(value);
    }

    @Override
    public @NotNull CompletableFuture<Optional<T>> load(@NotNull I id) {
        final T ifPresent = cache.getIfPresent(id);
        if (ifPresent != null) {
            return completedFuture(of((ifPresent)));
        }
        return findInDatabase(id);
    }

    public @NotNull Optional<T> lookup(@NotNull I id) {
        return Optional.ofNullable(
                cache.getIfPresent(id)
        );
    }

    private CompletableFuture<Optional<T>> findInDatabase(@NotNull I id) {
        return delegate.load(id)
                .whenComplete((o, t) -> {
                    if (t != null) {
                        throw new CompletionException(t);
                    }
                    o.ifPresent(this::addToCache);
                });
    }

    @Override
    public @NotNull CompletableFuture<Unit> delete(@NotNull I id) {
        cache.invalidate(id);
        return delegate.delete(id);
    }

    @Override
    public @NotNull CompletableFuture<Collection<T>> loadAll() {
        return delegate.loadAll()
                .whenComplete((o, t) -> {
                    if (t != null) {
                        throw new CompletionException(t);
                    }
                    o.forEach(this::addToCache);
                });
    }

    /**
     * Return all elements already in the cache
     * This will not perform any blocking operations
     *
     * @return all elements already in the cache
     */
    public @UnmodifiableView Map<I, T> lookupAll() {
        return Collections.unmodifiableMap(cache.asMap());
    }

    @Override
    public @NotNull CompletableFuture<Unit> saveAll(@NotNull Collection<T> values) {
        values.forEach(this::addToCache);
        return delegate.saveAll(values);
    }
}
