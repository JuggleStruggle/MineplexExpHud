/*
 * Copyright (c) 2022.
 */

package jugglestruggle.mineplexexphud.hud.enums;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import jugglestruggle.mineplexexphud.AbstractExpHud;
import jugglestruggle.mineplexexphud.pref.ElemFunction;
import jugglestruggle.mineplexexphud.pref.Preferences;
import jugglestruggle.util.JuggleTimeUnit;

import java.util.Locale;

import static jugglestruggle.mineplexexphud.MineplexExpHudClient.LANG_FORMAT;
import static jugglestruggle.mineplexexphud.MineplexExpHudClient.getLang;

/**
 * Attempts to update the EXP depending on the selected enumerator.
 *
 * @author JuggleStruggle
 */
public enum UpdateMethod implements ElemFunction<UpdateMethod>
{
    /**
     * Whenever the player changes world, perform an EXP check. If there's delays,
     * it will also perform delays as well.
     */
    ON_WORLD_CHANGE,
    /**
     * Checks for EXP on a set millisecond interval, Do keep in mind that
     * the millis are heavily reliant on the tick when updating.
     */
    UNTIL_NEXT_MS_UPDATE;
    
    public String getUpdateTypeText(AbstractExpHud ov)
    {
        switch (this)
        {
            case ON_WORLD_CHANGE:
            {
                if (!Preferences.worldChangeUseDelays || !ov.worldChangeInitiatedDelay)
                    return getLang().format(LANG_FORMAT + "updateMethod.on_world_change");
            }
            case UNTIL_NEXT_MS_UPDATE:
            {
                long off = ov.activeMillisUntilNextExpUpdate - System.currentTimeMillis();
                return JuggleTimeUnit.SECONDS.format(ov.secondsFormat.format((double)off / 1000.0), false);
            }
            
            default:
                return "";
        }
    }
    
    public boolean requiresConstantTextCacheUpdate(AbstractExpHud ov)
    {
        switch (this)
        {
            default:
                return false;
            
            case ON_WORLD_CHANGE: {
                return Preferences.worldChangeUseDelays && ov.worldChangeInitiatedDelay;
            }
            case UNTIL_NEXT_MS_UPDATE: {
                return true;
            }
        }
    }
    
    public static String getFormattedText(UpdateMethod a)
    {
        switch (a)
        {
            case ON_WORLD_CHANGE:
                return getLang().format(LANG_FORMAT+"updateMethod.on_world_change");
            case UNTIL_NEXT_MS_UPDATE:
                return getLang().format(LANG_FORMAT+"updateMethod.until_next_ms_update");
        }
        
        return "";
    }
    
    public static UpdateMethod getByString(String s)
    {
        switch (s)
        {
            case "on_world_change":
                return UpdateMethod.ON_WORLD_CHANGE;
            case "until_next_ms_update":
                return UpdateMethod.UNTIL_NEXT_MS_UPDATE;
            
                
            default:
                return null;
        }
    }
    
    @Override
    public JsonElement write() {
        return new JsonPrimitive(this.name().toLowerCase(Locale.ROOT));
    }
    
    @Override
    public UpdateMethod read(JsonElement elem)
    {
        if (elem.isJsonPrimitive())
        {
            JsonPrimitive prim = elem.getAsJsonPrimitive();
            
            if (prim.isString())
                return UpdateMethod.getByString(prim.getAsString());
        }
        
        return null;
    }
}