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

package jugglestruggle.mineplexexphud.hud.enums;


import com.google.common.collect.ImmutableList;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import jugglestruggle.mineplexexphud.MineplexExpHudClient;
import jugglestruggle.mineplexexphud.pref.ElemFunction;

import java.util.List;
import java.util.Locale;


public enum HudPositioning implements ElemFunction<HudPositioning>
{
    /* 00 */        None,
    /* 01 */     TopLeft,
    /* 02 */         Top,
    /* 03 */    TopRight,
    /* 04 */        Left,
    /* 05 */      Center,
    /* 06 */       Right,
    /* 07 */  BottomLeft,
    /* 08 */      Bottom,
    /* 09 */ BottomRight;
    
    public static final List<HudPositioning> HUD_POSITIONS;
    public static final List<HudPositioning> HUD_POSITIONS_WITHOUT_NONE;
    
    static
    {
        HudPositioning[] enums = values();
    
        ImmutableList.Builder<HudPositioning> b = ImmutableList.builder();
        for (int i = 1; i < enums.length; ++i)
            b.add(enums[i]);
    
        HUD_POSITIONS = ImmutableList.copyOf(enums);
        HUD_POSITIONS_WITHOUT_NONE = b.build();
    }
    
    public String getTranslation() { return MineplexExpHudClient.getLang().format(this.getTranslationKey()); }
    public String getTranslationKey() { return "jugglestruggle.hudpos." + this.key(); }
    
    public String key() {
        return this.name().toLowerCase(Locale.ROOT);
    }
    
    public static HudPositioning getByString(String s)
    {
        switch (s)
        {
            case "none":        return HudPositioning.None;
            case "topleft":     return HudPositioning.TopLeft;
            case "top":         return HudPositioning.Top;
            case "topright":    return HudPositioning.TopRight;
            case "left":        return HudPositioning.Left;
            case "center":      return HudPositioning.Center;
            case "right":       return HudPositioning.Right;
            case "bottomleft":  return HudPositioning.BottomLeft;
            case "bottom":      return HudPositioning.Bottom;
            case "bottomright": return HudPositioning.BottomRight;
            
            default: return null;
        }
    }
    
    public HudPositioning getOpposite()
    {
        switch (this)
        {
            case TopLeft:      return HudPositioning.BottomRight;
            case Top:          return HudPositioning.Bottom;
            case TopRight:     return HudPositioning.BottomLeft;
            case Left:         return HudPositioning.Right;
            case Right:        return HudPositioning.Left;
            case BottomLeft:   return HudPositioning.TopRight;
            case Bottom:       return HudPositioning.Top;
            case BottomRight:  return HudPositioning.TopLeft;
            
            default: return this;
        }
    }
    
    @Override
    public JsonElement write() {
        return new JsonPrimitive(this.key());
    }
    
    @Override
    public HudPositioning read(JsonElement elem)
    {
        if (elem.isJsonPrimitive())
        {
            JsonPrimitive prim = elem.getAsJsonPrimitive();
            
            if (prim.isString())
                return HudPositioning.getByString(prim.getAsString());
        }
        
        return null;
    }
}
