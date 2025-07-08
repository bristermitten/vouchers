package me.bristermitten.vouchers.database;

import me.bristermitten.mittenlib.util.Unit;
import me.bristermitten.mittenlib.util.lambda.SafeConsumer;
import me.bristermitten.mittenlib.util.lambda.SafeFunction;
import org.intellij.lang.annotations.Language;
import org.jetbrains.annotations.NotNull;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.concurrent.CompletableFuture;

public interface Database {

    <T> CompletableFuture<T> runWithStatement(@Language("MariaDB") String query, SafeFunction<PreparedStatement, T> block);

    default CompletableFuture<Unit> runWithStatement(@Language("MariaDB") String query, SafeConsumer<PreparedStatement> initializer) {
        return runWithStatement(query, s -> {
            initializer.consume(s);
            return Unit.UNIT;
        });
    }

    <T> @NotNull CompletableFuture<T> query(@Language("MariaDB") String query, SafeConsumer<PreparedStatement> initializer, SafeFunction<ResultSet, T> process);

    @NotNull
    CompletableFuture<Integer> update(@Language("SQL") String query, SafeConsumer<PreparedStatement> initializer);

    @NotNull
    CompletableFuture<Unit> execute(@Language("SQL") String query, SafeConsumer<PreparedStatement> initializer);

    default @NotNull
    CompletableFuture<Unit> execute(@Language("SQL") String query) {
        return execute(query, unused -> {
        });
    }

    @NotNull
    CompletableFuture<Unit> runTransactionally(SafeConsumer<Database> initializer);

}
