package dev.lone.vanillacustomizer.nms.items;

import dev.lone.vanillacustomizer.nms.NMS;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.CookingRecipe;

import java.util.ArrayList;
import java.util.List;

public class ItemsNms
{
    public IItemsNms nms;
    static ItemsNms instance;

    public List<Material> SMELTED_MATERIALS = new ArrayList<>();

    ItemsNms()
    {
        nms = NMS.findImplementation(IItemsNms.class, this, false);
    }

    public static ItemsNms inst()
    {
        if(instance == null)
            instance = new ItemsNms();
        return instance;
    }

    public static IItemsNms nms()
    {
        return inst().nms;
    }

    public void initialize()
    {
        Bukkit.recipeIterator().forEachRemaining(recipe -> {
            if(recipe instanceof CookingRecipe)
            {
                Material type = recipe.getResult().getType();
                if(!type.isEdible())
                    SMELTED_MATERIALS.add(type);
            }
        });
    }
}
