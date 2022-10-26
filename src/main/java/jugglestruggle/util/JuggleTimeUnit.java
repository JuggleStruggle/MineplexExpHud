package jugglestruggle.util;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import jugglestruggle.mineplexexphud.pref.ElemFunction;

import java.util.Locale;
import java.util.concurrent.TimeUnit;

import static jugglestruggle.mineplexexphud.MineplexExpHudClient.getLang;

public enum JuggleTimeUnit implements ElemFunction<JuggleTimeUnit>
{
    MILLISECONDS {
        @Override public long millis  (long v) { return v; }
        @Override public long millisTo(long v) { return v; }
    },
    SECONDS {
        @Override public long millis  (long v) { return v * S; }
        @Override public long millisTo(long v) { return v / S; }
    },
    MINUTES {
        @Override public long millis  (long v) { return v * M; }
        @Override public long millisTo(long v) { return v / M; }
    },
    HOURS {
        @Override public long millis  (long v) { return v * H; }
        @Override public long millisTo(long v) { return v / H; }
    },
    DAYS {
        @Override public long millis  (long v) { return v * D; }
        @Override public long millisTo(long v) { return v / D; }
    },
    WEEKS {
        @Override public long millis  (long v) { return v * W; }
        @Override public long millisTo(long v) { return v / W; }
    },
    MONTHS {
        @Override public long millis  (long v) { return v * (D * 30L); }
        @Override public long millisTo(long v) { return v / (D * 30L); }
    },
    YEARS {
        @Override public long millis  (long v) { return v * (D * 365L); }
        @Override public long millisTo(long v) { return v / (D * 365L); }
    },
    DECADES {
        @Override public long millis  (long v) { return v * ((D * 365L) * 10L); }
        @Override public long millisTo(long v) { return v / ((D * 365L) * 10L); }
    },
    
    ;
    
    static final long S = 1000L;
    static final long M = S * 60L;
    static final long H = M * 60L;
    static final long D = H * 24L;
    static final long W = D * 7L;
    
    public static final String JTU = "jugglestruggle.timeunit.";
    
    public static String formatFromMillis(long v)
    {
        JuggleTimeUnit jtu;
        
             if (v < S) jtu = MILLISECONDS;
        else if (v < M) jtu = SECONDS;
        else if (v < H) jtu = MINUTES;
        else if (v < D) jtu = HOURS;
        else if (v < W) jtu = DAYS;
        else            jtu = MILLISECONDS;
        
        v = jtu.millisTo(v);
        
        return getLang().format(JTU + "format", v, jtu.getNameFormat(v == 1));
    }
    public static String formatUnknown() {
        return getLang().format(JTU + "unknown");
    }
    
    
    
    
    
    public String getDisplayName(boolean singular) {
        return getLang().format(JuggleTimeUnit.JTU + this.getNameFormat(singular));
    }
    public String getNameFormat(boolean singular)
    {
        String fn = this.name().toLowerCase(Locale.ROOT);
        
        if (singular && fn.endsWith("s")) {
            fn = fn.substring(0, fn.length() - 1);
        }
        
        return fn;
    }
    public String format(String number, boolean singular) {
        return getLang().format(JTU + "format", number, this.getDisplayName(singular));
    }
    
    public TimeUnit getCorrespondingTimeUnit()
    {
        switch (this)
        {
            case MILLISECONDS:
                return TimeUnit.MILLISECONDS;
            case SECONDS:
                return TimeUnit.SECONDS;
            case MINUTES:
                return TimeUnit.MINUTES;
            case HOURS:
                return TimeUnit.HOURS;
            case DAYS:
                return TimeUnit.DAYS;
            default:
                return null;
        }
    }
    
    /**
     * Gets the millis of a value.
     * @param v the value to convert into a millis.
     * @return a millis represented by the unit
     */
    public abstract long millis(long v);
    /**
     * Gets the corresponding time unit from millis.
     * @param v the millisecond to transform into
     * @return the transformed millis to a set one
     */
    public abstract long millisTo(long v);
    
    public static JuggleTimeUnit getByString(String s)
    {
        switch (s)
        {
            case "milliseconds":
                return JuggleTimeUnit.MILLISECONDS;
            case "seconds":
                return JuggleTimeUnit.SECONDS;
            case "minutes":
                return JuggleTimeUnit.MINUTES;
            case "hours":
                return JuggleTimeUnit.HOURS;
            case "days":
                return JuggleTimeUnit.DAYS;
            case "weeks":
                return JuggleTimeUnit.WEEKS;
            case "months":
                return JuggleTimeUnit.MONTHS;
            case "years":
                return JuggleTimeUnit.YEARS;
            case "decades":
                return JuggleTimeUnit.DECADES;
    
            default:
                return null;
        }
    }
    
    @Override
    public JsonElement write() {
        return new JsonPrimitive(this.name().toLowerCase(Locale.ROOT));
    }
    
    @Override
    public JuggleTimeUnit read(JsonElement elem)
    {
        if (elem.isJsonPrimitive())
        {
            JsonPrimitive prim = elem.getAsJsonPrimitive();
            
            if (prim.isString())
                return JuggleTimeUnit.getByString(prim.getAsString());
        }
        
        return null;
    }
}
