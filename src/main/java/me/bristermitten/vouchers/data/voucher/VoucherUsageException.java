package me.bristermitten.vouchers.data.voucher;

/**
 * Exception used for <b>friendly errors</b> when using a voucher.
 * This type should not be used when something internal goes wrong.
 * See {@link VoucherUsageExceptionHandler} which handles these errors in a pretty way
 */
public class VoucherUsageException extends Exception {

    public static class NoPermission extends VoucherUsageException {
        private final String requiredPermission;

        public NoPermission(String requiredPermission) {
            this.requiredPermission = requiredPermission;
        }

        public String getRequiredPermission() {
            return requiredPermission;
        }
    }
}
