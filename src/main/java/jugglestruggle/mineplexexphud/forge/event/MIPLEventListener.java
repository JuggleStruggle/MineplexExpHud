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

package jugglestruggle.mineplexexphud.forge.event;

import jugglestruggle.mineplexexphud.forge.ForgeRenderContext;
import jugglestruggle.mineplexexphud.forge.MineplexExpHudClientForge;
import jugglestruggle.mineplexexphud.forge.gui.screen.HudEditorScreen;
import jugglestruggle.mineplexexphud.forge.gui.screen.PreferencesScreen;
import jugglestruggle.mineplexexphud.pref.Preferences;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.ChatComponentText;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class MIPLEventListener
{
    @SubscribeEvent
    public void onPreGameHudRender(RenderGameOverlayEvent.Pre e)
    {
        if (!Preferences.postRender && e.type == RenderGameOverlayEvent.ElementType.ALL)
            renderSelfHud(e.resolution, e.partialTicks);
    }
    @SubscribeEvent
    public void onPostGameHudRender(RenderGameOverlayEvent.Post e)
    {
        if (Preferences.postRender && e.type == RenderGameOverlayEvent.ElementType.ALL)
            renderSelfHud(e.resolution, e.partialTicks);
    }
    
    @SubscribeEvent
    public void onChatReceived(ClientChatReceivedEvent e)
    {
        if (e.type != 2 && e.message instanceof ChatComponentText)
        {
            if (MineplexExpHudClientForge.getForgeInstance().getExpHud().onChatReceived
                (e.message, e.message.getFormattedText(), e.message.getUnformattedText()))
            {
                e.setCanceled(true);
            }
        }
    }
    
    @SubscribeEvent
    public void onWorldLoad(WorldEvent.Load e) {
        MineplexExpHudClientForge.getForgeInstance().getExpHud().onWorldLoad();
    }
    
    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent e)
    {
        if (e.phase == TickEvent.Phase.END)
        {
            Minecraft client = Minecraft.getMinecraft();
            
            if (MineplexExpHudClientForge.showHudBinding.isPressed())
            {
                Preferences.showHud = !Preferences.showHud;
                MineplexExpHudClientForge.getInstance().writeToFile();
            }
            else if (MineplexExpHudClientForge.showPrefsBinding.isPressed() && client.currentScreen == null) {
                client.displayGuiScreen(new PreferencesScreen());
            } else if (MineplexExpHudClientForge.showHudEditorBinding.isPressed() && client.currentScreen == null) {
                client.displayGuiScreen(new HudEditorScreen());
            }
    
            MineplexExpHudClientForge.getForgeExpHud().tick();
        }
    }
    
    private static int lastScaledWidth;
    private static int lastScaledHeight;
    
    private static void renderSelfHud(ScaledResolution sr, float delta)
    {
        if (Preferences.showHud && (Preferences.showWhileDebugScreenActive ||
                !Minecraft.getMinecraft().gameSettings.showDebugInfo))
        {
            renderHud(sr, delta);
        }
    }
    public static void renderHud(ScaledResolution sr, float delta)
    {
        ForgeRenderContext ctx = MineplexExpHudClientForge.getCtx();
        ctx.updateScaledResolution(sr);
    
        if (sr.getScaledWidth() != lastScaledWidth || sr.getScaledHeight() != lastScaledHeight)
        {
            lastScaledWidth = sr.getScaledWidth();
            lastScaledHeight = sr.getScaledHeight();
        
            MineplexExpHudClientForge.getForgeExpHud().setPositionByScreenAreaAndOffset();
        }
    
        MineplexExpHudClientForge.getForgeExpHud().render(ctx, delta);
    }
    
}
