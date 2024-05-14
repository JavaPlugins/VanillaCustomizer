package dev.lone.vanillacustomizer.customization;

import dev.lone.vanillacustomizer.ChangeSession;
import dev.lone.vanillacustomizer.customization.changes.IChange;
import dev.lone.vanillacustomizer.customization.rules.IRule;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class Customization
{
    public List<IRule> rules = new ArrayList<>();
    public List<IChange> changes = new ArrayList<>();

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

    public boolean matchesAll(ChangeSession session)
    {
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
