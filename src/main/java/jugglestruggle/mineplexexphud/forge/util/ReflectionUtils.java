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
