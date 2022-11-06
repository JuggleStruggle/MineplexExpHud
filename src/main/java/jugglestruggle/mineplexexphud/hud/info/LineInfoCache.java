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

public class LineInfoCache
{
    public String text;
    
    public float textX;
    public float textY;
    public int textWidth;
    
    public float left;
    public float top;
    public float right;
    public float bottom;
    
    public LineInfoCache() {
        // nothing to worry!
    }
    
    public LineInfoCache(LineInfoCache c)
    {
        this.text = c.text;
        this.textWidth = c.textWidth;
        
        this.textX = c.textX;
        this.textY = c.textY;
        
        this.left = c.left;
        this.top = c.top;
        this.right = c.right;
        this.bottom = c.bottom;
    }
    
    public LineInfoCache copy() {
        return new LineInfoCache(this);
    }
    
    /**
     * @param f a six-array float representing the text position and
     *          the positions of the rectangle that will be used on render
     */
    public void setPosAndBoxPos(float[] f)
    {
        this.textX = f[0];
        this.textY = f[1];
        
        this.left   = f[2];
        this.top    = f[3];
        this.right  = f[4];
        this.bottom = f[5];
    }
}