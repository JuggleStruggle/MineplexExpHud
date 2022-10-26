package jugglestruggle.mineplexexphud.forge;

import jugglestruggle.mineplexexphud.Lang;
import jugglestruggle.mineplexexphud.forge.util.ReflectionUtils;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.resources.Locale;

import java.lang.reflect.Field;
import java.util.Map;

public class ForgeLang extends Lang
{
    static final Field LOCALE;
    static final Field PROPERTIES;
    
    static
    {
        LOCALE = ReflectionUtils.getAndAccessField(I18n.class, true, "i18nLocale", "field_135054_a");
        PROPERTIES = ReflectionUtils.getAndAccessField(Locale.class, true, "properties", "field_135032_a");
    }
    
    @Override
    public String translate(String key)
    {
        try
        {
            Locale locale = (Locale)LOCALE.get(null);
            Map<String, String> properties = (Map<String, String>)PROPERTIES.get(locale);
            
            // Do a similar approach where the Locale checks if the key returns nothing
            String v = properties.get(key);
            return v == null ? key : v;
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    
        // Fallback to this last one if failed to get the locale and properties
        return I18n.format(key);
    }
    
    @Override
    public String format(String key, Object... args) {
        return I18n.format(key, args);
    }
    
    
    
}
