package me.bristermitten.vouchers.util;

import org.jetbrains.annotations.Nullable;

import java.util.Collection;

public class CollectionUtil {
    private CollectionUtil() {

    }

    public static <T> @Nullable T onlyOrNull(Collection<T> collection) {
        if (collection.isEmpty()) {
            return null;
        }
        if (collection.size() > 1) {
            throw new IllegalArgumentException("Collection has more than one element");
        }
        return collection.iterator().next();
    }
}
