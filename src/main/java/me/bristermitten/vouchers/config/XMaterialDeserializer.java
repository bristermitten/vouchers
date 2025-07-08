package me.bristermitten.vouchers.config;

import com.cryptomorin.xseries.XMaterial;
import me.bristermitten.mittenlib.config.DeserializationContext;
import me.bristermitten.mittenlib.config.extension.CustomDeserializerFor;
import me.bristermitten.mittenlib.config.tree.DataTree;
import me.bristermitten.mittenlib.util.Result;

@CustomDeserializerFor(XMaterial.class)
public class XMaterialDeserializer {

    public static Result<XMaterial> deserialize(DeserializationContext context) {
        DataTree data = context.getData();
        if (data instanceof DataTree.DataTreeLiteral.DataTreeLiteralString) {
            String value = ((DataTree.DataTreeLiteral.DataTreeLiteralString) data).value();
            return Result.runCatching(() -> XMaterial.matchXMaterial(value)
                    .orElseThrow(() -> new IllegalArgumentException("Invalid XMaterial: " + value)));
        }
        return Result.fail(new IllegalArgumentException("Invalid XMaterial: " + data));
    }
}
