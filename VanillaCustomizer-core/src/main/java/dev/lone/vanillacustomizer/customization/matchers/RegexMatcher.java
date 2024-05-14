package dev.lone.vanillacustomizer.customization.matchers;

import java.util.regex.Pattern;

public class RegexMatcher implements ITextMatcher
{
    final Pattern regex;

    public RegexMatcher(String regexStr)
    {
        regex = Pattern.compile(regexStr);
    }

    @Override
    public boolean matches(String text)
    {
        return regex.matcher(text).matches();
    }
}
