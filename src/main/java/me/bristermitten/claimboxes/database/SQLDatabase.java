package me.bristermitten.claimboxes.database;

import com.google.inject.Inject;
import com.zaxxer.hikari.HikariDataSource;
import me.bristermitten.mittenlib.util.Unit;
import me.bristermitten.mittenlib.util.lambda.SafeConsumer;
import me.bristermitten.mittenlib.util.lambda.SafeFunction;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

public class SQLDatabase implements Database {
    private final @NotNull HikariDataSource hikariDataSource;

    @Inject
    public SQLDatabase(@NotNull HikariDataSource hikariDataSource) {
        this.hikariDataSource = hikariDataSource;
    }

    @Override
    public @NotNull <T> CompletableFuture<T> query(String query, SafeConsumer<PreparedStatement> initializer, SafeFunction<ResultSet, T> process) {
        return CompletableFuture.supplyAsync(() -> {
            try (final Connection connection = hikariDataSource.getConnection();
                 final PreparedStatement preparedStatement = connection.prepareStatement(query)) {

                initializer.consume(preparedStatement);
                final ResultSet resultSet = preparedStatement.executeQuery();
                return process.apply(resultSet);

            } catch (Throwable exception) {
                throw new CompletionException(exception);
            }
        });
    }

    @Override
    @NotNull
    public CompletableFuture<Integer> update(String query, SafeConsumer<PreparedStatement> initializer) {
        return CompletableFuture.supplyAsync(() -> {
            try (final Connection connection = hikariDataSource.getConnection();
                 final PreparedStatement preparedStatement = connection.prepareStatement(query)) {

                initializer.consume(preparedStatement);
                return preparedStatement.executeUpdate();
            } catch (Throwable exception) {
                throw new CompletionException(exception);
            }
        });
    }

    @Override
    @NotNull
    public CompletableFuture<Unit> execute(String query, SafeConsumer<PreparedStatement> initializer) {
        return CompletableFuture.supplyAsync(() -> {
            try (final Connection connection = hikariDataSource.getConnection();
                 final PreparedStatement preparedStatement = connection.prepareStatement(query)) {

                initializer.consume(preparedStatement);
                preparedStatement.execute();
                return Unit.UNIT;
            } catch (Throwable exception) {
                throw new CompletionException(exception);
            }
        });
    }

    @Override
    public @NotNull CompletableFuture<Unit> runTransactionally(String query, SafeConsumer<PreparedStatement> initializer) {
        return CompletableFuture.supplyAsync(() -> {
            try (final Connection connection = hikariDataSource.getConnection()) {
                try (final PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                    connection.setAutoCommit(false);
                    initializer.consume(preparedStatement);
                    connection.commit();
                    return Unit.UNIT;
                } catch (SQLException e) {
                    connection.rollback();
                    connection.setAutoCommit(true);
                    throw e;
                }
            } catch (Throwable exception) {
                throw new CompletionException(exception);
            }
        });
    }
}
