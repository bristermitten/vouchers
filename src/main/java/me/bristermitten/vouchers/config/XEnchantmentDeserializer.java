package me.bristermitten.vouchers.config;

import com.cryptomorin.xseries.XEnchantment;
import com.cryptomorin.xseries.XMaterial;
import me.bristermitten.mittenlib.config.DeserializationContext;
import me.bristermitten.mittenlib.config.extension.CustomDeserializerFor;
import me.bristermitten.mittenlib.config.tree.DataTree;
import me.bristermitten.mittenlib.util.Result;

@CustomDeserializerFor(XEnchantment.class)
public class XEnchantmentDeserializer {

    public static Result<XEnchantment> deserialize(DeserializationContext context) {
        DataTree data = context.getData();
        if (data instanceof DataTree.DataTreeLiteral.DataTreeLiteralString) {
            String value = ((DataTree.DataTreeLiteral.DataTreeLiteralString) data).value();
            return Result.runCatching(() -> XEnchantment.of(value)
                    .orElseThrow(() -> new IllegalArgumentException("Invalid XMaterial: " + value)));
        }
        return Result.fail(new IllegalArgumentException("Invalid XMaterial: " + data));
    }
}
