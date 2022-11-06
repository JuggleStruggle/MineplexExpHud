package jugglestruggle.util;

import com.google.common.collect.ImmutableList;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import jugglestruggle.mineplexexphud.pref.ElemFunction;

import java.util.List;
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
        @Override public long millis  (long v) { return v * MO; }
        @Override public long millisTo(long v) { return v / MO; }
    },
    YEARS {
        @Override public long millis  (long v) { return v * Y; }
        @Override public long millisTo(long v) { return v / Y; }
    },
    DECADES {
        @Override public long millis  (long v) { return v * DE; }
        @Override public long millisTo(long v) { return v / DE; }
    },
    
    ;
    
    public static final long S = 1000L;
    public static final long M = S * 60L;
    public static final long H = M * 60L;
    public static final long D = H * 24L;
    public static final long W = D * 7L;
    
    public static final long MO = D * 30L;
    public static final long Y  = D * 365L;
    public static final long DE  = Y * 10L;
    
    
    public static final String JTU = "jugglestruggle.timeunit.";
    public static final List<JuggleTimeUnit> TIME_UNIT_LIST;
    
    static {
        TIME_UNIT_LIST = ImmutableList.copyOf(JuggleTimeUnit.values());
    }
    
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
//    public String formatByNumber
    
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
     * Gets the millisecond of a value.
     * @param v the value to convert into a millisecond.
     * @return a millis represented by the unit
     */
    public abstract long millis(long v);
    /**
     * Gets the corresponding time unit from millisecond.
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
    
    public long getMinMillis() {
        return 1;
    }
    
    // For now limit it based on the time unit as the current goal is to avoid crashes
    public long getMaxMillis()
    {
        switch (this)
        {
            case MILLISECONDS:
                return Long.MAX_VALUE;
            case SECONDS:
                return Long.MAX_VALUE / S;
            case MINUTES:
                return Long.MAX_VALUE / M;
            case HOURS:
                return Long.MAX_VALUE / H;
            case DAYS:
                return Long.MAX_VALUE / D;
            case WEEKS:
                return Long.MAX_VALUE / W;
            case MONTHS:
                return Long.MAX_VALUE / MO;
            case YEARS:
                return Long.MAX_VALUE / Y;
            case DECADES:
                return Long.MAX_VALUE / DE;
        }
    
        return 10;
    }
}
