package me.bristermitten.vouchers.persist;

import me.bristermitten.mittenlib.util.Unit;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

public abstract class SQLPersistence<ID, T> implements Persistence<ID, T> {
    @Override
    public @NotNull CompletableFuture<Unit> flush() {
        return Unit.unitFuture(); // do nothing
    }
}
