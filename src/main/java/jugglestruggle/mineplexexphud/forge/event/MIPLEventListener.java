package jugglestruggle.mineplexexphud.forge.event;

import jugglestruggle.mineplexexphud.MineplexExpHudClient;
import jugglestruggle.mineplexexphud.forge.ForgeExpHud;
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
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class MIPLEventListener
{
    @SubscribeEvent
    public void onPreGameHudRender(RenderGameOverlayEvent.Pre e)
    {
        if (e.type == RenderGameOverlayEvent.ElementType.ALL && !Preferences.postRender) {
            renderSelfHud(e.resolution, e.partialTicks);
        }
    }
    @SubscribeEvent
    public void onPostGameHudRender(RenderGameOverlayEvent.Post e)
    {
        if (e.type == RenderGameOverlayEvent.ElementType.ALL && Preferences.postRender) {
            renderSelfHud(e.resolution, e.partialTicks);
        }
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
    public void onKeyInputEvent(InputEvent.KeyInputEvent e)
    {
    
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
