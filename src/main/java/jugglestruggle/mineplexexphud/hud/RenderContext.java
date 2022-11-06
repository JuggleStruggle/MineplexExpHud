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

package jugglestruggle.mineplexexphud.hud;

public interface RenderContext
{
    void createScissor(int x, int y, int w, int h);
    void removeScissor();
    
    void fill(float left, float top, float right, float bottom, int color);
    void outerWireframe(float left, float top, float right, float bottom, int color);
    void drawString(String text, float x, float y, int color, boolean drawTextWithShadow);
    
    void enableBlend();
    void disableBlend();
    void enableTexture();
    void disableTexture();
    
    void defaultBlendFunc();
    
    void glColor(float r, float g, float b, float a);
    
    
    default void fillWithWireframe(float left, float top, float right, float bottom, int fillColor, int wireframeColor)
    {
        this.fill(left, top, right, bottom, fillColor);
        this.outerWireframe(left, top, right, bottom, wireframeColor);
    }
    
    /**
     * Gets the scaled resolution but in integers.
     *
     * @return
     * <ul>
     *     <li> 0 = Scaled X Size </li>
     *     <li> 1 = Scaled Y Size </li>
     *     <li> 2 = Scale Factor </li>
     *     <li> 3 = Original X Size </li>
     *     <li> 4 = Original Y Size </li>
     * </ul>
     */
    int[] getSCR();
}
