package me.bristermitten.vouchers.data.voucher.persistence;

import me.bristermitten.mittenlib.util.Unit;
import me.bristermitten.vouchers.data.voucher.Voucher;
import me.bristermitten.vouchers.data.voucher.type.VoucherTypeRegistry;
import me.bristermitten.vouchers.database.Database;
import me.bristermitten.vouchers.database.RuntimePersistException;
import me.bristermitten.vouchers.persist.TableNameFactory;
import me.bristermitten.vouchers.util.CollectionUtil;
import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.CompletableFuture;

public class SQLVoucherPersistence implements VoucherPersistence {
    private final Database database;

    private final VoucherTypeRegistry voucherTypeRegistry;
    private final TableNameFactory tnf;

    @Inject
    public SQLVoucherPersistence(Database database, VoucherTypeRegistry voucherTypeRegistry, TableNameFactory tnf) {
        this.database = database;
        this.voucherTypeRegistry = voucherTypeRegistry;
        this.tnf = tnf;
    }


    private String tableName() {
        return tnf.getTableName("Vouchers");
    }

    @Override
    public @NotNull CompletableFuture<Unit> init() {
        return database.execute(
                "CREATE TABLE IF NOT EXISTS " + tableName() + "\n" +
                "(\n" +
                "    id           char(36)              not null " +
                "primary key,\n" +
                "    used         boolean default false not null,\n" +
                "    voucher_type varchar(1000)         null,\n" +
                "    data         varchar(1000)         null\n" +
                ");\n"
        );

    }

    private @NotNull List<Voucher> fromResultSet(@NotNull ResultSet resultSet) {
        List<Voucher> list = new ArrayList<>();
        try {
            while (resultSet.next()) {
                final String voucherTypeId = resultSet.getString("voucher_type");
                list.add(new Voucher(
                        UUID.fromString(resultSet.getString("id")),
                        resultSet.getString("data"),
                        voucherTypeRegistry.get(voucherTypeId)
                                .orElseThrow(() -> new IllegalStateException("Voucher type " + voucherTypeId + " does not exist!")),
                        resultSet.getBoolean("used"))
                );
            }
        } catch (SQLException e) {
            throw new RuntimePersistException(e);
        }
        return list;
    }

    @Override
    public @NotNull CompletableFuture<Optional<Voucher>> load(@NotNull UUID id) {
        return database.query(
                        "SELECT * FROM " + tableName() + " WHERE id = ?",
                        statement -> statement.setString(1, id.toString()),
                        this::fromResultSet
                )
                .thenApply(CollectionUtil::onlyOrNull)
                .thenApply(Optional::ofNullable);
    }

    @Override
    public @NotNull CompletableFuture<Unit> delete(@NotNull UUID id) {
        return database.execute("DELETE FROM " + tableName() + " WHERE id = ?",
                statement -> statement.setString(1, id.toString()));
    }

    @Override
    public @NotNull CompletableFuture<Collection<Voucher>> loadAll() {
        return database.query(
                        "SELECT * FROM " + tableName(),
                        s -> {
                        },
                        this::fromResultSet)
                .thenApply(Collections::unmodifiableList);
    }

    @Override
    public @NotNull CompletableFuture<Unit> saveAll(@NotNull Collection<Voucher> values) {
        return database.runTransactionally(db ->
                db.runWithStatement("INSERT INTO " + tableName() + " (id, used, voucher_type, data)\n" +
                                    "VALUES (?, ?, ?, ?)\n" +
                                    "ON DUPLICATE KEY UPDATE used         = VALUES(used),\n" +
                                    "                        voucher_type = VALUES(voucher_type),\n" +
                                    "                        data         = VALUES(data)"
                        , statement -> {
                            for (Voucher voucher : values) {
                                statement.setString(1, voucher.getId().toString());
                                statement.setBoolean(2, voucher.isUsed());
                                statement.setString(3, voucher.getType().getId());
                                statement.setString(4, voucher.getData());
                                statement.addBatch();
                            }
                            statement.executeBatch();
                        }).join());
    }
}
