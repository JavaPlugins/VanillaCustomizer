package dev.lone.vanillacustomizer;

public class InvalidCustomizationPropertyExtension extends IllegalArgumentException
{
    public InvalidCustomizationPropertyExtension(String text)
    {
        super(text);
    }
}
