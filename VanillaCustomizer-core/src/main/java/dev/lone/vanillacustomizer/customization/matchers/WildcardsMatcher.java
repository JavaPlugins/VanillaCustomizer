package dev.lone.vanillacustomizer.customization.matchers;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOCase;

import java.util.ArrayList;
import java.util.List;

@Deprecated // TODO: implement that shit
public class WildcardsMatcher implements ITextMatcher
{
//    private List<String> wildcardStart;
//    private List<String> wildcardEnd;
//    private List<String> wildcardContains;
    private List<String> wildcards;

    public void addWildcard(String wildcard)
    {
//        String cleanMatcher = wildcard.replace("*", "");
//        if(wildcard.startsWith("*") && wildcard.endsWith("*"))
//        {
//            if (wildcardContains == null)
//                wildcardContains = new ArrayList<>();
//            wildcardContains.add(cleanMatcher);
//        }
//        else
//        {
//            if (wildcard.startsWith("*"))
//            {
//                if (wildcardStart == null)
//                    wildcardStart = new ArrayList<>();
//                wildcardStart.add(cleanMatcher);
//            }
//            else
//            {
//                if (wildcardEnd == null)
//                    wildcardEnd = new ArrayList<>();
//                wildcardEnd.add(cleanMatcher);
//            }
//        }
        if (wildcards == null)
            wildcards = new ArrayList<>();
        wildcards.add(wildcard);
    }

    @Override
    public boolean matches(String text)
    {
        for (String wildcard : wildcards)
        {
            if(FilenameUtils.wildcardMatch(text, wildcard, IOCase.INSENSITIVE))
                return true;
        }
//        if(wildcardContains != null)
//        {
//            for (String wildcard : wildcardContains)
//            {
//                if(text.contains(wildcard))
//                    return true;
//            }
//        }
//
//        if(wildcardStart != null)
//        {
//            for (String wildcard : wildcardStart)
//            {
//                if(text.endsWith(wildcard))
//                    return true;
//            }
//        }
//
//        if(wildcardEnd != null)
//        {
//            for (String wildcard : wildcardEnd)
//            {
//                if(text.startsWith(wildcard))
//                    return true;
//            }
//        }

        return false;
    }
}
