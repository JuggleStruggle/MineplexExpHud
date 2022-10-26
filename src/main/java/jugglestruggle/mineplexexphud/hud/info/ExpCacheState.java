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
