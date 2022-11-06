/*
 * MineplexExpHud: A mod which tracks the current
 * EXP the user has on the Mineplex server.
 * Copyright (C) 2022  JuggleStruggle
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See
 * the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program.  If not, see
 *  <https://www.gnu.org/licenses/>.
 */

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
