package jugglestruggle.mineplexexphud.forge.util;

import jugglestruggle.mineplexexphud.MineplexExpHudClient;

import java.lang.reflect.Field;

public final class ReflectionUtils
{
    public static Field getAndAccessField(Class<?> c, boolean supressErrors, String... fieldsToUse)
    {
        Field f = null;
    
        for (String fieldName : fieldsToUse)
        {
            try
            {
                f = c.getDeclaredField(fieldName);
                f.setAccessible(true);
                
                break;
            }
            catch (NoSuchFieldException | SecurityException e)
            {
                if (!supressErrors)
                    MineplexExpHudClient.LOGGER.error("Failed to find "+c.getCanonicalName()+"'s field: "+fieldName, e);
                
                f = null;
            }
        }
        
        return f;
    }
    
    private ReflectionUtils() {}
}
