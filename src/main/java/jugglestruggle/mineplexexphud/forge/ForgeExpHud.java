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
