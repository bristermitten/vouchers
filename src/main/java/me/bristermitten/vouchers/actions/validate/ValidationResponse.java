package me.bristermitten.vouchers.actions.validate;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

public class ValidationResponse<T> {
    private final boolean isOk;
    private final List<String> errors;
    private final T value;

    public ValidationResponse(boolean isOk, List<String> errors, T value) {
        this.isOk = isOk;
        this.errors = errors;
        this.value = value;
    }

    public static <T> ValidationResponse<T> ok(T value) {
        return new ValidationResponse<>(true, Collections.emptyList(), value);
    }

    public static <T> ValidationResponse<T> error(String error) {
        return new ValidationResponse<>(false, Collections.singletonList(error), null);
    }

    public boolean isOk() {
        return isOk;
    }

    public List<String> getErrors() {
        return errors;
    }

    public <R> ValidationResponse<R> then(ValidationResponse<R> other) {
        if (this.isOk && other.isOk()) {
            return other;
        } else {
            List<String> combinedErrors = new ArrayList<>(this.errors);
            combinedErrors.addAll(other.getErrors());
            return new ValidationResponse<>(false, combinedErrors, null);
        }
    }

    public <R> ValidationResponse<R> then(Function<T, ValidationResponse<R>> other) {
        if (!this.isOk) {
            //noinspection unchecked
            return (ValidationResponse<R>) this;
        }
        ValidationResponse<R> apply = other.apply(value);
        if (apply.isOk) {
            return apply;
        }
        List<String> combinedErrors = new ArrayList<>(this.errors);
        combinedErrors.addAll(apply.errors);
        return new ValidationResponse<>(false, combinedErrors, apply.value);
    }

    public @NotNull T getOrThrow() throws ValidationFailedException {
        if (isOk) {
            return value;
        } else {
            throw new ValidationFailedException("Validation failed: " + String.join(", ", errors));
        }
    }
}
