package me.bristermitten.vouchers.data.voucher.persistence;

import me.bristermitten.vouchers.data.voucher.Voucher;
import me.bristermitten.vouchers.persist.Persistence;

import java.util.UUID;

public interface VoucherPersistence extends Persistence<UUID, Voucher> {
}
