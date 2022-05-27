package me.bristermitten.vouchers.config;

import me.bristermitten.mittenlib.config.Config;
import me.bristermitten.mittenlib.config.Source;
import me.bristermitten.mittenlib.config.names.NamingPattern;
import me.bristermitten.mittenlib.config.names.NamingPatterns;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;

@Config
@Source("vouchers.yml")
@NamingPattern(NamingPatterns.LOWER_KEBAB_CASE)
public class VoucherConfigDTO {
    Map<String, VoucherTypeDTO> voucherTypes;

    @Config
    static class VoucherTypeDTO {
        @Nullable String defaultValue;
        @Nullable ItemConfigDTO item;
        List<String> actions;
    }
}
