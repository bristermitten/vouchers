package me.bristermitten.vouchers.database;

public class RuntimeSQLException extends RuntimeException{
    public RuntimeSQLException() {
        super();
    }

    public RuntimeSQLException(String message) {
        super(message);
    }

    public RuntimeSQLException(String message, Throwable cause) {
        super(message, cause);
    }

    public RuntimeSQLException(Throwable cause) {
        super(cause);
    }

    protected RuntimeSQLException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
