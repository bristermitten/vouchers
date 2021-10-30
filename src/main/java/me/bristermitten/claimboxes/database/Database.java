package me.bristermitten.claimboxes.database;

import me.bristermitten.mittenlib.util.Unit;
import me.bristermitten.mittenlib.util.lambda.SafeConsumer;
import me.bristermitten.mittenlib.util.lambda.SafeFunction;
import org.intellij.lang.annotations.Language;
import org.jetbrains.annotations.NotNull;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.concurrent.CompletableFuture;

public interface Database {
    <T> @NotNull CompletableFuture<T> query(@Language("MariaDB") String query, SafeConsumer<PreparedStatement> initializer, SafeFunction<ResultSet, T> process);

    @NotNull
    CompletableFuture<Integer> update(@Language("MariaDB") String query, SafeConsumer<PreparedStatement> initializer);

    @NotNull
    CompletableFuture<Unit> execute(@Language("MariaDB") String query, SafeConsumer<PreparedStatement> initializer);

    default @NotNull
    CompletableFuture<Unit> execute(@Language("MariaDB") String query) {
        return execute(query, unused -> {
        });
    }

    @NotNull
    CompletableFuture<Unit> runTransactionally(@Language("MariaDB") String query, SafeConsumer<PreparedStatement> initializer);

}
