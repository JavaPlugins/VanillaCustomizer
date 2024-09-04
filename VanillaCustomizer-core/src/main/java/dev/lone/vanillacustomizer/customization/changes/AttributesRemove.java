package dev.lone.vanillacustomizer.customization.changes;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import dev.lone.vanillacustomizer.ChangeSession;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.inventory.EquipmentSlot;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

@Deprecated //TODO implement this shit
public class AttributesRemove implements IChange
{
    Multimap<Attribute, AttributeModifier> modifiers;

    public AttributesRemove(Multimap<Attribute, AttributeModifier> modifiers)
    {
        this.modifiers = modifiers;
    }

    @Override
    public void apply(ChangeSession session)
    {
        session.refreshMeta();

        if (!session.meta.hasAttributeModifiers())
            return;

        Multimap<Attribute, AttributeModifier> finalModifiers = ArrayListMultimap.create();
        Multimap<Attribute, AttributeModifier> originalAttributes = session.meta.getAttributeModifiers();
        assert originalAttributes != null;
//        modifiers.forEach((attrRemove, modRemove) -> {
//
//            if(originalAttributes.containsKey(attrRemove))
//            {
//                if()
//            }
//
//            // If the item already has the same attribute with same UUID I have to replace it with the
//            // new attribute provided in the configuration.
//            if(attributeModifier.getUniqueId().equals(modRemove.getUniqueId()))
//            {
//                finalModifiers.put(attrRemove, modRemove);
//            }
//            // Otherwise I have to put both as they don't interfere.
//            else
//            {
//                finalModifiers.put(attribute, attributeModifier);
//                finalModifiers.put(attrRemove, modRemove);
//            }
//        });
//
//        originalAttributes.forEach((attribute, attributeModifier) -> {
//
//        });
//        modifiers.forEach(finalModifiers::put);
//        session.meta.setAttributeModifiers(finalModifiers);
//
//        session.applyMeta();
    }
}
