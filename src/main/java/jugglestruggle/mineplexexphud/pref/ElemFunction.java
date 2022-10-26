package jugglestruggle.mineplexexphud.pref;

import com.google.gson.JsonElement;

import java.util.function.Function;
import java.util.function.Supplier;

/**
 * A Java-extended function which, according to {@link Configuration}, takes
 * in {@link com.google.gson.JsonArray}, {@link com.google.gson.JsonObject}
 * and {@link com.google.gson.JsonPrimitive}.
 *
 * @author JuggleStruggle
 * @param <V> the value to be represented from the provided JsonElement
 */
public interface ElemFunction<V> extends Function<JsonElement, V>, Supplier<JsonElement>
{
    @Override
    default V apply(JsonElement elem) {
        return this.read(elem);
    }
    @Override
    default JsonElement get() {
        return this.write();
    }
    
    V read(JsonElement elem);
    JsonElement write();
}
