package jugglestruggle.mineplexexphud.forge.gui.widget;

import net.minecraft.client.Minecraft;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractMultiWidget implements Widget
{
    protected int x;
    protected int y;
    protected int width;
    protected int height;
    
    protected int xOffset;
    protected int yOffset;
    
    protected boolean visible;
    protected boolean enabled;
    protected boolean focused;
    
    protected List<Widget> childWidgets;
    
    protected AbstractMultiWidget(int w, int h, int widgetsSize)
    {
        this.width = w; this.height = h;
        this.visible = true; this.enabled = true;
    
        this.childWidgets = new ArrayList<>(widgetsSize);
        
    }
    
    protected abstract void updateWidgetPositions();
    protected void updateWidgetPositionsFromRender() {
        this.updateWidgetPositions();
    }
    
    protected boolean allowMultiWidgetsKeyInteraction() {
        return false;
    }
    
    @Override
    public int getX() {
        return this.x;
    }
    @Override
    public int getY() {
        return this.y;
    }
    
    @Override
    public int getW() {
        return this.width;
    }
    @Override
    public int getH() {
        return this.height;
    }
    
    @Override
    public int getXOffset() {
        return this.xOffset;
    }
    @Override
    public int getYOffset() {
        return this.yOffset;
    }
    
    @Override
    public void setX(int x) {
        this.x = x;
    }
    @Override
    public void setY(int y) {
        this.y = y;
    }
    
    @Override
    public void setW(int w) {
        this.width = w;
    }
    @Override
    public void setH(int h) {
        this.height = h;
    }
    
    @Override
    public void setXOffset(int x) {
        this.xOffset = x;
    }
    @Override
    public void setYOffset(int y) {
        this.yOffset = y;
    }
    
    @Override
    public void tick() {
        for (Widget w : this.childWidgets)
            w.tick();
    }
    
    @Override
    public boolean isVisibleToUser() {
        return this.visible;
    }
    @Override
    public boolean isInteractable() {
        return this.enabled;
    }
    @Override
    public boolean isAttentionGiven() {
        return this.focused;
    }
    
    @Override
    public void setVisibleToUser(boolean visible) {
        this.visible = visible;
    }
    @Override
    public void setInteractable(boolean enabled) {
        this.enabled = enabled;
    }
    @Override
    public void setAttentionGiven(boolean focused)
    {
        this.focused = focused;
        
        if (!this.focused)
            for (Widget w : this.childWidgets)
                w.setAttentionGiven(false);
    }
    
    @Override
    public void render(Minecraft client, int mouseX, int mouseY, float delta)
    {
        if (this.isVisibleToUser())
        {
            this.updateWidgetPositionsFromRender();
    
            for (Widget w : this.childWidgets)
            {
                if (w.isVisibleToUser())
                    w.render(client, mouseX, mouseY, delta);
            }
        }
    }
    
    @Override
    public boolean onMouseDown(Minecraft client, int mouseX, int mouseY, int button)
    {
        if (this.isInteractableAndMouseOver(mouseX, mouseY))
        {
            for (Widget w : this.childWidgets)
            {
                boolean mouseDown = w.onMouseDown(client, mouseX, mouseY, button);
    
                w.setAttentionGiven(mouseDown);
                
                if (mouseDown) {
                    w.emitSoundOnClick(client.getSoundHandler());
                }
            }
            
            return true;
        }
        else
        {
            for (Widget w : this.childWidgets)
                w.setAttentionGiven(false);
        }
        
        return false;
    }
    
    @Override
    public boolean onMouseUp(int mouseX, int mouseY, int button)
    {
        boolean s = false;
    
        for (Widget w : this.childWidgets) {
            s |= w.onMouseUp(mouseX, mouseY, button);
        }
    
        return s;
    }
    
    @Override
    public boolean onKeyTyped(char charCode, int keyCode)
    {
        boolean s = false;
        
        for (Widget w : this.childWidgets)
        {
            boolean typed = w.isActuallyFocused() && w.onKeyTyped(charCode, keyCode);
            s |= typed;
            
            if (typed && !this.allowMultiWidgetsKeyInteraction())
                return true;
        }
        
        return s;
    }
    
    @Override
    public String[] getTooltipLines(int mouseX, int mouseY)
    {
        if (this.isInteractableAndMouseOver(mouseX, mouseY))
        {
            for (Widget w : this.childWidgets)
                if (w.isVisibleAndMouseOver(mouseX, mouseY))
                {
                    String[] lines = w.getTooltipLines(mouseX, mouseY);
    
                    if (lines != null)
                        return lines;
                }
        }
        
        return null;
    }
}
