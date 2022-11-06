package jugglestruggle.util;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import jugglestruggle.mineplexexphud.pref.ElemFunction;

public class TimeUnitInfo implements ElemFunction<TimeUnitInfo>
{
    protected JuggleTimeUnit unit;
    protected long value;
    
    public TimeUnitInfo(JuggleTimeUnit unit, long value) {
        this.unit = unit; this.value = value;
    }
    
    public JuggleTimeUnit getUnit() {
        return this.unit;
    }
    /**
     * Gets the value that this unit represents.
     * @return a long value
     */
    public long getValue() {
        return this.value;
    }
    /**
     * Gets the millis of the unit.
     * @return a long value which depends on what was picked
     */
    public long getResult() {
        return this.unit.millis(this.value);
    }
    
    
    public String getDisplayText() {
        return this.unit.format(""+this.value, this.value == 1);
    }
    
    public long getMin() {
        return this.unit.getMinMillis();
    }
    public long getMax() {
        return this.unit.getMaxMillis();
    }
    
    
    @Override
    public TimeUnitInfo read(JsonElement elem)
    {
        if (elem.isJsonObject())
        {
            JsonObject data = elem.getAsJsonObject();
            
            JsonElement unitElem = data.get("unit");
            JsonElement valueElem = data.get("value");
            
            if (unitElem != null && valueElem != null && valueElem.isJsonPrimitive())
            {
                JuggleTimeUnit jtu = JuggleTimeUnit.HOURS.read(unitElem);
                
                if (jtu != null)
                    return new TimeUnitInfo(jtu, valueElem.getAsJsonPrimitive().getAsLong());
            }
        }
        
        return null;
    }
    
    @Override
    public JsonElement write()
    {
        JsonObject data = new JsonObject();
        
        data.add("unit", this.getUnit().write());
        data.addProperty("value", this.getValue());
        
        return data;
    }
}
