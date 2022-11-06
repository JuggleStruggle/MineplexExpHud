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

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import jugglestruggle.mineplexexphud.pref.ElemFunction;
import net.minecraft.client.resources.I18n;

import java.util.Locale;


import static jugglestruggle.mineplexexphud.MineplexExpHudClient.LANG_FORMAT;

/**
 * A way to know what mode it should use when attempting to seek out for what it wants. The EXP
 * chat logs contain 9 lines in Mineplex (as of October 06, 2022) and only have 2 lines worth
 * of useful content, one line only containing the progress bar, 2 lines only used for green dash
 * decoration and 4 lines that are empty with... color formatting for some odd reason.
 *
 * @author JuggleStruggle
 */
public enum AccuracyMode implements ElemFunction<AccuracyMode>
{
    /**
     * Do not show any new chat logs that contain the matching entries; provided that the current
     * line is going for has an specific match to that line. If it fails to get a match during
     * the current line being higher than 0, this is assumed to be a reset and spews out the
     * cached chat lines to avoid clearing out misunderstood lines.
     */
    ACCOUNT_FOR_ALL_LINES,
    /**
     * Relies on not caring on other data besides what matters like Current Level, Player EXP and
     * the nitty-gritty of the data needed to build this HUD. But, this will not make any efforts
     * to cache any known EXP formatting and shows the EXP chat logs which might be undesired.
     */
    ONLY_ON_WHAT_MATTERS;
    
    
    public static String getFormattedText(AccuracyMode a)
    {
        switch (a)
        {
            case ACCOUNT_FOR_ALL_LINES:
                return I18n.format(LANG_FORMAT + "accuracy.account_for_all_lines");
            case ONLY_ON_WHAT_MATTERS:
                return I18n.format(LANG_FORMAT + "accuracy.only_on_what_matters");
        }
        
        return "";
    }
    public static String getFormattedTextDesc(AccuracyMode a)
    {
        switch (a)
        {
            case ACCOUNT_FOR_ALL_LINES:
                return I18n.format(LANG_FORMAT + "accuracy.account_for_all_lines.description");
            case ONLY_ON_WHAT_MATTERS:
                return I18n.format(LANG_FORMAT + "accuracy.only_on_what_matters.description");
        }
        
        return "";
    }
    
    public static AccuracyMode getByString(String s)
    {
        switch (s)
        {
            case "account_for_all_lines":
                return AccuracyMode.ACCOUNT_FOR_ALL_LINES;
            case "only_on_what_matters":
                return AccuracyMode.ONLY_ON_WHAT_MATTERS;
            
            default:
                return null;
        }
    }
    
    @Override
    public JsonElement write() {
        return new JsonPrimitive(this.name().toLowerCase(Locale.ROOT));
    }
    
    @Override
    public AccuracyMode read(JsonElement elem)
    {
        if (elem.isJsonPrimitive())
        {
            JsonPrimitive prim = elem.getAsJsonPrimitive();
            
            if (prim.isString())
                return AccuracyMode.getByString(prim.getAsString());
        }
        
        return null;
    }
}