package me.bristermitten.vouchers.database;

public class RuntimePersistException extends RuntimeException{
    public RuntimePersistException() {
        super();
    }

    public RuntimePersistException(String message) {
        super(message);
    }

    public RuntimePersistException(String message, Throwable cause) {
        super(message, cause);
    }

    public RuntimePersistException(Throwable cause) {
        super(cause);
    }

    protected RuntimePersistException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
