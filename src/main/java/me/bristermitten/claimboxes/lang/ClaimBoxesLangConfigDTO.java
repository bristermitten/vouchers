package me.bristermitten.claimboxes.lang;

import me.bristermitten.mittenlib.config.Config;
import me.bristermitten.mittenlib.config.Source;
import me.bristermitten.mittenlib.config.names.NamingPattern;
import me.bristermitten.mittenlib.config.names.NamingPatterns;
import me.bristermitten.mittenlib.lang.LangMessage;

@Config
@Source("lang.yml")
@NamingPattern(NamingPatterns.LOWER_KEBAB_CASE)
public class ClaimBoxesLangConfigDTO {
    ErrorConfigDTO errors;
    LangMessage claimboxReset;
    LangMessage claimboxResetOther;
    LangMessage claimboxResetAll;
    LangMessage voucherGiven;
    LangMessage voucherGivenAll;

    @Config
    static class ErrorConfigDTO {
        LangMessage notPlayer;
        LangMessage claimboxEmpty;
        LangMessage claimboxEmptyOther;
        LangMessage noPermission;
        LangMessage unknownPlayer;
        LangMessage invalidBoolean;
        LangMessage unknownVoucher;
        LangMessage unknownCommand;
        LangMessage inventoryFull;
    }
}
