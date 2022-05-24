package me.bristermitten.vouchers.database;

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
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

public class SQLDatabase implements Database {
    private final @NotNull HikariDataSource hikariDataSource;

    @Inject
    public SQLDatabase(@NotNull HikariDataSource hikariDataSource) {
        this.hikariDataSource = hikariDataSource;
    }

    protected <R> CompletableFuture<R> withConnection(SafeFunction<Connection, R> body) {
        return CompletableFuture.supplyAsync(() -> {
            try (final Connection connection = hikariDataSource.getConnection()) {
                return body.apply(connection);
            } catch (Throwable e) {
                throw new RuntimeSQLException(e);
            }
        });
    }
@Override
    public <T> CompletableFuture<T> runWithStatement(String query, SafeFunction<PreparedStatement, T> block) {
        return withConnection(connection -> {
            try (final PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                return block.apply(preparedStatement);
            } catch (SQLException e) {
                throw new RuntimeSQLException(e);
            }
        });
    }

    @Override
    @NotNull
    public <T> CompletableFuture<T> query(String query, SafeConsumer<PreparedStatement> initializer, SafeFunction<ResultSet, T> process) {
        return runWithStatement(query, statement -> {
            initializer.consume(statement);
            try (final ResultSet resultSet = statement.executeQuery()) {
                return process.apply(resultSet);
            }
        });
    }

    @Override
    @NotNull
    public CompletableFuture<Integer> update(String query, SafeConsumer<PreparedStatement> initializer) {
        return runWithStatement(query, statement -> {
            initializer.consume(statement);
            return statement.executeUpdate();
        });
    }

    @Override
    @NotNull
    public CompletableFuture<Unit> execute(String query, SafeConsumer<PreparedStatement> initializer) {
        return runWithStatement(query, statement -> {
            initializer.consume(statement);
            statement.execute();
            return Unit.UNIT;
        });
    }

    @Override
    @NotNull
    public CompletableFuture<Unit> runTransactionally(SafeConsumer<Database> initializer) {
        return withConnection(connection -> {
            connection.setAutoCommit(false);
            TransactionalDatabase trans = new TransactionalDatabase(hikariDataSource, connection);
            try {
                initializer.consume(trans);
                trans.withConnection(c -> {
                    c.commit();
                    return null;
                });
            } catch (SQLException e) {
                connection.rollback();
                throw new RuntimeSQLException(e);
            } finally {
                connection.setAutoCommit(true);
                trans.close();
            }
            return Unit.UNIT;
        });
    }


    private static class TransactionalDatabase extends SQLDatabase {
        private Connection connection;

        public TransactionalDatabase(@NotNull HikariDataSource hikariDataSource, Connection connection) {
            super(hikariDataSource);
            this.connection = connection;
        }

        @Override
        protected <R> CompletableFuture<R> withConnection(SafeFunction<Connection, R> body) {
            return CompletableFuture.supplyAsync(
                    () -> {
                        synchronized (this) {
                            try {
                                Objects.requireNonNull(connection, "Transaction closed");
                                return body.apply(connection);
                            } catch (Throwable e) {
                                throw new RuntimeSQLException(e);
                            }
                        }
                    }
            );
        }

        void close() {
            synchronized (this) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    throw new RuntimeSQLException(e);
                }
                connection = null;
            }
        }
    }
}
