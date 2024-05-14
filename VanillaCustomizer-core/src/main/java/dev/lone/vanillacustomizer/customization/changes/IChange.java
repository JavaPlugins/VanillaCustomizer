package dev.lone.vanillacustomizer.customization.changes;

import dev.lone.vanillacustomizer.ChangeSession;
import dev.lone.vanillacustomizer.nms.items.ItemsNms;

public interface IChange
{
    void apply(ChangeSession session);

    static String replacePlaceholders(ChangeSession session, String str)
    {
        if(session.item.getType().isEdible())
        {
            str = str.replace("{food}", String.valueOf(ItemsNms.nms().getNutrition(session.item)));
            str = str.replace("{saturation}", String.valueOf(ItemsNms.nms().getSaturation(session.item)));
        }

        if(session.item.getType().isBlock())
            str = str.replace("{hardness}", String.valueOf(ItemsNms.nms().getDestroySpeed(session.item)));

        return str;
    }
}
