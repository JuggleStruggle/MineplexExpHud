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

package jugglestruggle.mineplexexphud.hud.info;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import jugglestruggle.mineplexexphud.ExpStatics;
import jugglestruggle.mineplexexphud.pref.ElemFunction;

public class ExpCacheState implements ElemFunction<ExpCacheState>
{
    public long currentExp;
    public long expUntilNextLevel;
    public int currentLevel;
    public int nextLevel;
    
    public ExpCacheState() { }
    
    public ExpCacheState(ExpCacheState c)
    {
        this.nextLevel = c.nextLevel;
        this.currentExp = c.currentExp;
        this.currentLevel = c.currentLevel;
        this.expUntilNextLevel = c.expUntilNextLevel;
    }
    
    public boolean isEmpty() {
        return this.currentExp == 0 && this.expUntilNextLevel == 0 && this.currentLevel == 0 && this.nextLevel == 0;
    }
    public boolean isInitialExp()
    {
        return this.currentExp == 0 && this.expUntilNextLevel == ExpStatics.EXP_UNTIL_NEXT_LEVEL.get(0) &&
              this.currentLevel == 0 && this.nextLevel == 1;
    }
    
    public ExpCacheState copy() {
        return new ExpCacheState(this);
    }
    
    public static ExpCacheState readFromElem(JsonElement elem)
    {
        if (elem.isJsonObject())
        {
            JsonObject data = elem.getAsJsonObject();
        
            ExpCacheState ecs = new ExpCacheState();
        
            ecs.currentExp        = data.getAsJsonPrimitive("currentExp").getAsLong();
            ecs.expUntilNextLevel = data.getAsJsonPrimitive("expUntilNextLevel").getAsLong();
            ecs.currentLevel      = data.getAsJsonPrimitive("currentLevel").getAsInt();
            ecs.nextLevel         = data.getAsJsonPrimitive("nextLevel").getAsInt();
        
            return ecs;
        }
    
        return null;
    }
    
    @Override
    public ExpCacheState read(JsonElement elem)
    {
        return readFromElem(elem);
    }
    
    @Override
    public JsonElement write()
    {
        JsonObject data = new JsonObject();
        
        data.addProperty("currentExp", this.currentExp);
        data.addProperty("expUntilNextLevel", this.expUntilNextLevel);
        data.addProperty("currentLevel", this.currentLevel);
        data.addProperty("nextLevel", this.nextLevel);
        
        return data;
    }
}
