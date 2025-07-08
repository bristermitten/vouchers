package me.bristermitten.vouchers.database;

import com.google.inject.Inject;
import com.zaxxer.hikari.HikariDataSource;
import me.bristermitten.mittenlib.util.Unit;
import me.bristermitten.mittenlib.util.lambda.SafeConsumer;
import me.bristermitten.mittenlib.util.lambda.SafeFunction;
import org.intellij.lang.annotations.Language;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Untainted;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * SQL implementation of the Database interface that uses HikariCP for connection pooling.
 * This class provides methods for executing SQL queries and transactions.
 */
public class SQLDatabase implements Database, AutoCloseable {
    private static final Logger LOGGER = Logger.getLogger(SQLDatabase.class.getName());
    private final @NotNull HikariDataSource hikariDataSource;
    private final @NotNull ExecutorService databaseExecutor;

    @Inject
    public SQLDatabase(@NotNull HikariDataSource hikariDataSource) {
        this.hikariDataSource = Objects.requireNonNull(hikariDataSource, "HikariDataSource cannot be null");
        // Create a dedicated thread pool for database operations to avoid blocking the main thread
        this.databaseExecutor = Executors.newFixedThreadPool(
                Math.max(2, Runtime.getRuntime().availableProcessors() / 2),
                r -> {
                    Thread thread = new Thread(r, "Database-Worker");
                    thread.setDaemon(true);
                    return thread;
                }
        );
    }

    /**
     * Closes the database resources, including the connection pool and executor service.
     * This method should be called when the database is no longer needed.
     */
    @Override
    public void close() {
        // Shutdown the executor service
        if (!databaseExecutor.isShutdown()) {
            databaseExecutor.shutdown();
            try {
                if (!databaseExecutor.awaitTermination(10, TimeUnit.SECONDS)) {
                    databaseExecutor.shutdownNow();
                }
            } catch (InterruptedException e) {
                LOGGER.log(Level.WARNING, "Interrupted while shutting down database executor", e);
                Thread.currentThread().interrupt();
            }
        }

        // Close the connection pool
        if (!hikariDataSource.isClosed()) {
            hikariDataSource.close();
        }
    }

    /**
     * Executes a function with a database connection and handles resource cleanup.
     *
     * @param body The function to execute with the connection
     * @param <R>  The return type of the function
     * @return A CompletableFuture that will complete with the result of the function
     */
    protected <R> CompletableFuture<R> withConnection(@NotNull SafeFunction<Connection, R> body) {

        return CompletableFuture.supplyAsync(() -> {
            Connection connection = null;
            try {
                connection = hikariDataSource.getConnection();
                return body.apply(connection);
            } catch (Exception e) {
                throw new RuntimePersistException("Error executing database operation", e);
            } finally {
                if (connection != null) {
                    try {
                        if (!connection.isClosed()) {
                            connection.close();
                        }
                    } catch (SQLException e) {
                        // Just log the exception, don't rethrow as the main operation might have succeeded
                        LOGGER.log(Level.WARNING, "Error closing database connection", e);
                    }
                }
            }
        }, databaseExecutor);
    }

    /**
     * Executes a SQL statement with a prepared statement and returns the result.
     *
     * @param query The SQL query to execute
     * @param block The function to apply to the prepared statement
     * @param <T>   The return type of the function
     * @return A CompletableFuture that will complete with the result of the function
     */
    @Override
    public <T> CompletableFuture<T> runWithStatement(@Language("SQL") @NotNull @Untainted String query, @NotNull SafeFunction<PreparedStatement, T> block) {

        return withConnection(connection -> {
            try (final PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                return block.apply(preparedStatement);
            } catch (SQLException e) {
                throw new RuntimePersistException("Error executing SQL statement: " + query, e);
            }
        });
    }

    /**
     * Executes a SQL query and processes the result set.
     *
     * @param query       The SQL query to execute
     * @param initializer The consumer to initialize the prepared statement
     * @param process     The function to process the result set
     * @param <T>         The return type of the process function
     * @return A CompletableFuture that will complete with the result of the process function
     */
    @Override
    @NotNull
    public <T> CompletableFuture<T> query(@Language("SQL") @NotNull @Untainted String query,
                                          @NotNull SafeConsumer<PreparedStatement> initializer,
                                          @NotNull SafeFunction<ResultSet, T> process) {

        return runWithStatement(query, statement -> {
            try {
                initializer.consume(statement);
                try (final ResultSet resultSet = statement.executeQuery()) {
                    return process.apply(resultSet);
                } catch (SQLException e) {
                    throw new RuntimePersistException("Error processing query results: " + query, e);
                }
            } catch (Exception e) {
                throw new RuntimePersistException("Error initializing statement for query: " + query, e);
            }
        });
    }

    /**
     * Executes a SQL update statement.
     *
     * @param query       The SQL update statement to execute
     * @param initializer The consumer to initialize the prepared statement
     * @return A CompletableFuture that will complete with the number of rows affected
     */
    @Override
    @NotNull
    public CompletableFuture<Integer> update(@Language("SQL") @NotNull String query, @NotNull SafeConsumer<PreparedStatement> initializer) {

        return runWithStatement(query, statement -> {
            try {
                initializer.consume(statement);
                return statement.executeUpdate();
            } catch (SQLException e) {
                throw new RuntimePersistException("Error executing update: " + query, e);
            } catch (Exception e) {
                throw new RuntimePersistException("Error initializing statement for update: " + query, e);
            }
        });
    }

    /**
     * Executes a SQL statement.
     *
     * @param query       The SQL statement to execute
     * @param initializer The consumer to initialize the prepared statement
     * @return A CompletableFuture that will complete with Unit
     */
    @Override
    @NotNull
    public CompletableFuture<Unit> execute(@Language("SQL") @NotNull String query, @NotNull SafeConsumer<PreparedStatement> initializer) {

        return runWithStatement(query, statement -> {
            try {
                initializer.consume(statement);
                statement.execute();
                return Unit.UNIT;
            } catch (SQLException e) {
                throw new RuntimePersistException("Error executing statement: " + query, e);
            } catch (Exception e) {
                throw new RuntimePersistException("Error initializing statement: " + query, e);
            }
        });
    }

    /**
     * Runs a series of database operations within a transaction.
     * If any operation fails, the entire transaction is rolled back.
     *
     * @param initializer The consumer to initialize the transaction
     * @return A CompletableFuture that will complete with Unit when the transaction is committed
     */
    @Override
    @NotNull
    public CompletableFuture<Unit> runTransactionally(@NotNull SafeConsumer<Database> initializer) {
        return withConnection(connection -> {
            boolean originalAutoCommit = connection.getAutoCommit();
            connection.setAutoCommit(false);

            try (TransactionalDatabase trans = new TransactionalDatabase(hikariDataSource, connection, databaseExecutor)) {
                // Execute the transaction operations
                initializer.consume(trans);

                // Explicitly commit the transaction
                trans.withConnection(c -> {
                    c.commit();
                    return Unit.UNIT;
                }).join();

                return Unit.UNIT;
            } catch (Throwable e) {
                // Roll back on any exception
                try {
                    if (!connection.isClosed()) {
                        connection.rollback();
                    }
                } catch (SQLException rollbackEx) {
                    // Combine the original exception with the rollback exception
                    RuntimePersistException combined = new RuntimePersistException(
                            "Transaction failed and rollback also failed", e);
                    combined.addSuppressed(rollbackEx);
                    throw combined;
                }

                throw new RuntimePersistException("Transaction failed and was rolled back", e);
            } finally {
                try {
                    // Restore original auto-commit setting
                    if (!connection.isClosed()) {
                        connection.setAutoCommit(originalAutoCommit);
                    }
                } catch (SQLException e) {
                    LOGGER.log(Level.WARNING, "Failed to restore auto-commit setting", e);
                }
            }
        });
    }

    /**
     * A specialized database implementation for handling transactions.
     * It reuses the same connection for all operations within the transaction.
     */
    private static class TransactionalDatabase extends SQLDatabase {
        private final Object connectionLock = new Object();
        private final ExecutorService executor;
        private volatile Connection connection;

        /**
         * Creates a new transactional database with the given connection.
         *
         * @param hikariDataSource The data source
         * @param connection       The connection to use for the transaction
         * @param executor         The executor to use for async operations
         */
        public TransactionalDatabase(@NotNull HikariDataSource hikariDataSource,
                                     @NotNull Connection connection,
                                     @NotNull ExecutorService executor) {
            super(hikariDataSource);
            this.connection = Objects.requireNonNull(connection, "Transaction connection cannot be null");
            this.executor = Objects.requireNonNull(executor, "Executor cannot be null");
        }

        /**
         * Executes a function with the transaction's connection.
         * This method is thread-safe and ensures the connection is valid.
         *
         * @param body The function to execute
         * @param <R>  The return type of the function
         * @return A CompletableFuture that will complete with the result of the function
         */
        @Override
        protected <R> CompletableFuture<R> withConnection(@NotNull SafeFunction<Connection, R> body) {
            Objects.requireNonNull(body, "Body function cannot be null");

            return CompletableFuture.supplyAsync(() -> {
                synchronized (connectionLock) {
                    Connection conn = this.connection;
                    if (conn == null) {
                        throw new RuntimePersistException("Transaction closed");
                    }

                    try {
                        if (conn.isClosed()) {
                            throw new RuntimePersistException("Transaction connection is closed");
                        }
                        return body.apply(conn);
                    } catch (RuntimePersistException e) {
                        throw e;
                    } catch (SQLException e) {
                        throw new RuntimePersistException("SQL error in transaction", e);
                    } catch (Throwable e) {
                        throw new RuntimePersistException("Error in transaction", e);
                    }
                }
            }, executor);
        }

        /**
         * Closes the transaction's connection.
         * This method is thread-safe and idempotent.
         */
        @Override
        public void close() {
            synchronized (connectionLock) {
                if (connection != null) {
                    try {
                        if (!connection.isClosed()) {
                            connection.close();
                        }
                    } catch (SQLException e) {
                        LOGGER.log(Level.SEVERE, "Error closing transaction connection", e);
                        throw new RuntimePersistException("Error closing transaction connection", e);
                    } finally {
                        connection = null;
                    }
                }
            }
        }
    }
}
