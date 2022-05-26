package me.bristermitten.vouchers.util;

import me.bristermitten.vouchers.database.RuntimePersistException;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class ResultSetUtil {

    private ResultSetUtil() {

    }

    public static Stream<ResultSet> getRows(ResultSet resultSet) {
        Iterator<ResultSet> iterator = new Iterator<ResultSet>() {
            @Override
            public boolean hasNext() {
                try {
                    return resultSet.next();
                } catch (SQLException e) {
                    throw new RuntimePersistException(e);
                }
            }

            @Override
            public ResultSet next() {
                try {
                    if (resultSet.isAfterLast() || resultSet.isClosed()) {
                        throw new NoSuchElementException();
                    }
                } catch (SQLException e) {
                    throw new RuntimePersistException(e);
                }
                return resultSet;
            }
        };
        Spliterator<ResultSet> resultSetSpliterator = Spliterators.spliteratorUnknownSize(iterator, Spliterator.IMMUTABLE | Spliterator.NONNULL);
        return StreamSupport.stream(resultSetSpliterator, false);
    }
}
