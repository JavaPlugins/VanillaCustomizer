package dev.lone.vanillacustomizer.exception;

public class InvalidCustomizationPropertyException extends IllegalArgumentException
{
    public InvalidCustomizationPropertyException(String text)
    {
        super(text);
    }
}
