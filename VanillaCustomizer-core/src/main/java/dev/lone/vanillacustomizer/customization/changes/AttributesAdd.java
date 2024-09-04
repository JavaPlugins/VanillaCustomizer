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

public class AttributesAdd implements IChange
{
    Multimap<Attribute, AttributeModifier> modifiers;

    public AttributesAdd(Multimap<Attribute, AttributeModifier> modifiers)
    {
        this.modifiers = modifiers;
    }

    public static AttributeModifier generateModifier(@Nullable String uuid,
                                                     String name,
                                                     Attribute attribute,
                                                     double value,
                                                     @NotNull String operation,
                                                     EquipmentSlot slot)
    {
        AttributeModifier.Operation op;
        if (operation.equals("multiply_base"))
            op = AttributeModifier.Operation.ADD_SCALAR;
        else if (operation.equals("multiply"))
            op = AttributeModifier.Operation.MULTIPLY_SCALAR_1;
        else
            op = AttributeModifier.Operation.ADD_NUMBER;

        return new AttributeModifier(
                uuid != null ? UUID.fromString(uuid) : UUID.nameUUIDFromBytes((attribute + op.name() + value + slot).getBytes()),
                name,
                value,
                op,
                slot
        );
    }

    @Override
    public void apply(ChangeSession session)
    {
        session.refreshMeta();

        Multimap<Attribute, AttributeModifier> finalModifiers = ArrayListMultimap.create();
        if(session.meta.hasAttributeModifiers())
        {
            Multimap<Attribute, AttributeModifier> originalAttributes = session.meta.getAttributeModifiers();
            assert originalAttributes != null;
            originalAttributes.forEach((attribute, attributeModifier) -> {
                modifiers.forEach((newAttr, newMod) -> {
                    // If the item already has the same attribute with same UUID I have to replace it with the
                    // new attribute provided in the configuration.
                    if(attributeModifier.getUniqueId().equals(newMod.getUniqueId()))
                    {
                        finalModifiers.put(newAttr, newMod);
                    }
                    // Otherwise I have to put both as they don't interfere.
                    else
                    {
                        finalModifiers.put(attribute, attributeModifier);
                        finalModifiers.put(newAttr, newMod);
                    }
                });
            });
            modifiers.forEach(finalModifiers::put);
            session.meta.setAttributeModifiers(finalModifiers);
        }
        else
        {
            session.meta.setAttributeModifiers(modifiers);
        }

        session.applyMeta();
    }
}
