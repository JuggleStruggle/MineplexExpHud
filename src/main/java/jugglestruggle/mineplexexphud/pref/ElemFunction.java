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
