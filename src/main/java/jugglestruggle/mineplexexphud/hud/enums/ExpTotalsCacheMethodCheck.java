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

/**
 * The way to check and update {@link jugglestruggle.mineplexexphud.AbstractExpHud#totalExpGainedInSession}
 * (and related fields) as each method provided has their own advantages and disadvantages.
 *
 * @author JuggleStruggle
 * @implNote Tuesday 11-10-2022 13:13:20
 */
public enum ExpTotalsCacheMethodCheck implements ElemFunction<ExpTotalsCacheMethodCheck>
{
    /**
     * Only updates the totals if the EXP update is done.
     */
    ONLY_ON_EXP_UPDATE_END,
    /**
     * Listens to chat messages and finds the expecting EXP pattern and uses these values to help add
     * the totals. This is more accurate than {@link #ONLY_ON_EXP_UPDATE_END} but requires constant
     * listening and delays things a bit further than they are supposed to be.
     *
     * <p> Only useful if either the user wants more accuracy at the expense of speed or is on early
     * levels and are playing modes that give a load of EXP and breaks the the former type.
     */
    LISTEN_TO_CHAT_MESSAGE;
    
    
    public static String getFormattedText(ExpTotalsCacheMethodCheck a)
    {
        switch (a)
        {
            case ONLY_ON_EXP_UPDATE_END:
                return I18n.format("jugglestruggle.miplexp.etcmc.only_on_exp_update_end");
            case LISTEN_TO_CHAT_MESSAGE:
                return I18n.format("jugglestruggle.miplexp.etcmc.listen_to_chat_message");
        }
        
        return "";
    }
    
    public static ExpTotalsCacheMethodCheck getByString(String s)
    {
        switch (s)
        {
            case "only_on_exp_update_end":
                return ExpTotalsCacheMethodCheck.ONLY_ON_EXP_UPDATE_END;
            case "listen_to_chat_message":
                return ExpTotalsCacheMethodCheck.LISTEN_TO_CHAT_MESSAGE;
            
            
            default:
                return null;
        }
    }
    
    @Override
    public JsonElement write() {
        return new JsonPrimitive(this.name().toLowerCase(Locale.ROOT));
    }
    
    @Override
    public ExpTotalsCacheMethodCheck read(JsonElement elem)
    {
        if (elem.isJsonPrimitive())
        {
            JsonPrimitive prim = elem.getAsJsonPrimitive();
            
            if (prim.isString())
                return ExpTotalsCacheMethodCheck.getByString(prim.getAsString());
        }
        
        return null;
    }
}