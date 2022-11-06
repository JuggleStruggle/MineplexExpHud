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

package jugglestruggle.mineplexexphud.pref;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

/**
 * Imported from another mod of mine, DaylightChangerStruggle, see at
 * https://github.com/JuggleStruggle/DaylightChangerStruggle
 *
 * <p> This does not contain all of the code used in that mod, just only some things
 * to help it get it done faster and also has new code to "generalize" things
 *
 * @author JuggleStruggle
 * @implNote Created on 31-Jan-2022, Monday
 */
public class Configuration
{
    public static <V> void checkOrCreateProp(JsonObject section, String propertyName, V defaultValue)
    {
        JsonElement propertyElement = section.get(propertyName);
        
        boolean expectingPrimitive = (defaultValue instanceof Number ||
                defaultValue instanceof String || defaultValue instanceof Boolean);
        
        if (propertyElement == null)
        {
            if (expectingPrimitive) {
                Configuration.addExpectedPrimitive(section, propertyName, defaultValue);
            }
        }
        else
        {
            if (expectingPrimitive)
            {
                if (propertyElement.isJsonPrimitive())
                {
                    JsonPrimitive primitive = propertyElement.getAsJsonPrimitive();
                    
                    boolean removeAndAdd = false;
                    
                    if (primitive.isBoolean()) {
                        removeAndAdd = (defaultValue instanceof Number || defaultValue instanceof String);
                    } else if (primitive.isNumber()) {
                        removeAndAdd = (defaultValue instanceof String || defaultValue instanceof Boolean);
                    } else if (primitive.isString()) {
                        removeAndAdd = (defaultValue instanceof Boolean || defaultValue instanceof Number);
                    }
                    
                    if (removeAndAdd)
                    {
                        section.remove(propertyName);
                        Configuration.addExpectedPrimitive(section, propertyName, defaultValue);
                    }
                }
            }
        }
    }
    
    private static <V> JsonPrimitive addExpectedPrimitive(JsonObject section, String propertyName, V value)
    {
        JsonPrimitive primitive;
        
        if (value instanceof Number)
            primitive = new JsonPrimitive((Number)value);
        else if (value instanceof Boolean)
            primitive = new JsonPrimitive((Boolean)value);
        else
            primitive = new JsonPrimitive((String)value);
        
        section.add(propertyName, primitive);
        
        return primitive;
    }
    
    // created on MineplexExpHud; not part of the time changer mod
    public static <V> V get(JsonObject data, String prop, V defValue) {
        return Configuration.get(data, prop, defValue, null, null);
    }
    public static <V> V get(JsonObject data, String prop, V defValue, V min, V max)
    {
        JsonElement elem = data == null ? null : data.get(prop);
        
        if (elem != null)
        {
            if (defValue instanceof ElemFunction)
            {
                if (elem.isJsonPrimitive() || elem.isJsonArray() || elem.isJsonObject())
                {
                    V val = ((ElemFunction<V>)defValue).read(elem);
    
                    if (val != null)
                        return val;
                }
            }
            else if (elem.isJsonPrimitive())
            {
                JsonPrimitive prim = elem.getAsJsonPrimitive();

                if (prim.isBoolean() && defValue instanceof Boolean)
                    return (V)(Object)prim.getAsBoolean();
                else if (prim.isString() && defValue instanceof String)
                    return (V)prim.getAsString();
//                if (prim.isNumber() && defValue instanceof Number)
                else if (defValue instanceof Number)
                {
                    try
                    {
                        Number n = prim.getAsNumber();
    
                        if (min != null && max != null)
                        {
                            // For now only handle integers and long types
                            if (defValue instanceof Integer)
                            {
                                     if (n.intValue() < (Integer)min) n = (Integer)min;
                                else if (n.intValue() > (Integer)max) n = (Integer)max;
                            }
                            else if (defValue instanceof Long)
                            {
                                     if (n.longValue() < (Long)min) n = (Long)min;
                                else if (n.longValue() > (Long)max) n = (Long)max;
                            }
                            else if (defValue instanceof Float)
                            {
                                     if (n.floatValue() < (Float)min) n = (Float)min;
                                else if (n.floatValue() > (Float)max) n = (Float)max;
                            }
                        }
                        
                        if (defValue instanceof Integer)
                            return (V)(Integer)n.intValue();
                        else if (defValue instanceof Long)
                            return (V)(Long)n.longValue();
                        else if (defValue instanceof Float)
                            return (V)(Float)n.floatValue();
                        else
                            return defValue;
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        
        return defValue;
    }
}
