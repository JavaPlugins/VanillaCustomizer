package dev.lone.vanillacustomizer.customization;

import dev.lone.vanillacustomizer.ChangeSession;
import dev.lone.vanillacustomizer.customization.changes.IChange;
import dev.lone.vanillacustomizer.customization.rules.IRule;
import dev.lone.vanillacustomizer.utils.Utils;
import org.apache.commons.io.FilenameUtils;
import org.bukkit.inventory.InventoryView;

import java.util.ArrayList;
import java.util.List;

public class Customization
{
    public final boolean ignoreInventoriesEnabled;
    public final boolean ignoreAnyCustomInventory;
    public final List<String> ignoreInventoriesByTitle;

    public List<IRule> rules = new ArrayList<>();
    public List<IChange> changes = new ArrayList<>();

    public Customization(boolean ignoreInventoriesEnabled, boolean ignoreAnyCustomInventory, List<String> ignoreInventoriesByTitle)
    {
        this.ignoreAnyCustomInventory = ignoreAnyCustomInventory;
        this.ignoreInventoriesByTitle = ignoreInventoriesByTitle;
        this.ignoreInventoriesEnabled = ignoreInventoriesEnabled;
    }

    public boolean isEmpty()
    {
        return rules.isEmpty() && changes.isEmpty();
    }

    public void addRule(IRule rule)
    {
        rules.add(rule);
    }

    public void addChange(IChange change)
    {
        changes.add(change);
    }

    boolean matchesAll(ChangeSession session)
    {
        InventoryView openInventory = session.getPlayer().getOpenInventory();
        if(ignoreInventoriesEnabled)
        {
            if(ignoreAnyCustomInventory)
            {
                if(Utils.isCustomInventory(openInventory))
                    return false;
            }

            for (String wildcard : ignoreInventoriesByTitle)
            {
                if (FilenameUtils.wildcardMatch(openInventory.getTitle(), wildcard))
                    return false;
            }
        }

        for (IRule rule : rules)
        {
            if(!rule.matches(session))
                return false;
        }
        return true;
    }

    //TODO: maybe implement also matchAny?
    public void handle(ChangeSession session)
    {
        if(matchesAll(session))
        {
            for (IChange change : changes)
            {
                change.apply(session);
            }
        }
    }
}
