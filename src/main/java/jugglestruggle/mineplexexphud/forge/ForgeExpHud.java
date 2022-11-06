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

package jugglestruggle.mineplexexphud.forge;

import jugglestruggle.mineplexexphud.AbstractExpHud;
import net.minecraft.client.Minecraft;
import net.minecraft.util.IChatComponent;

public class ForgeExpHud extends AbstractExpHud
{
    public ForgeExpHud() {
        super.updateFromPreferences();
    }
    
    @Override
    protected boolean avoidExecuting()
    {
        Minecraft client = Minecraft.getMinecraft();
        return client.thePlayer == null || client.theWorld == null;
    }
    
    @Override
    protected int getTextRendererHeight() {
        return Minecraft.getMinecraft().fontRendererObj.FONT_HEIGHT;
    }
    
    @Override
    protected int getTextRendererStringWidth(String text) {
        return Minecraft.getMinecraft().fontRendererObj.getStringWidth(text);
    }
    
    @Override
    protected void sendChatMessage(String text)
    {
        if (!this.avoidExecuting())
            Minecraft.getMinecraft().thePlayer.sendChatMessage(text);
    }
    
    @Override
    protected void printMessage(Object text)
    {
        if (!this.avoidExecuting() && text instanceof IChatComponent) {
            Minecraft.getMinecraft().ingameGUI.getChatGUI().printChatMessage((IChatComponent)text);
        }
    }
}
