package jugglestruggle.mineplexexphud.forge.gui.screen;

import com.google.common.collect.ImmutableList;
import jugglestruggle.mineplexexphud.forge.gui.widget.Widget;
import jugglestruggle.mineplexexphud.forge.gui.widget.WidgetRowListWidget;
import net.minecraft.client.gui.GuiScreen;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public abstract class Screen extends GuiScreen
{
    protected GuiScreen parentScreen;
    protected boolean screenCreated;
    
    protected List<Widget> widgets;
    protected List<WidgetRowListWidget<?>> listWidgets;
    protected Widget selectedWidget;
    
    public Screen(GuiScreen parentScreen)
    {
        this.parentScreen = parentScreen;
        this.widgets = new ArrayList<>();
    }
    
    @Override
    public void drawScreen(int mouseX, int mouseY, float delta)
    {
        String[] tooltipLines = null;
        
        if (this.listWidgets != null)
            for (WidgetRowListWidget<?> wl : this.listWidgets)
            {
                wl.drawScreen(mouseX, mouseY, delta);
                
                if (tooltipLines == null)
                    tooltipLines = wl.getTooltipLines(mouseX, mouseY);
            }
    
        super.drawScreen(mouseX, mouseY, delta);
        
        for (Widget w : this.widgets)
        {
            w.render(this.mc, mouseX, mouseY, delta);
            
            if (tooltipLines == null && w.isVisibleAndMouseOver(mouseX, mouseY))
                tooltipLines = w.getTooltipLines(mouseX, mouseY);
        }
        
        if (tooltipLines != null) {
            super.drawHoveringText(ImmutableList.copyOf(tooltipLines), mouseX, mouseY, this.mc.fontRendererObj);
        }
    }
    
    @Override
    protected void keyTyped(char charCode, int keyCode)
    {
        if (keyCode == 1 && this.onEscapeKeyLeaves())
        {
            this.mc.displayGuiScreen(this.parentScreen);
        
            if (this.mc.currentScreen == null)
                this.mc.setIngameFocus();
        }
        else
        {
            for (Widget w : this.widgets)
                if (w.isActuallyFocused())
                    w.onKeyTyped(charCode, keyCode);
    
    
            if (this.listWidgets != null)
                for (WidgetRowListWidget<?> wl : this.listWidgets)
                    if (wl.visible && wl.isFocused() && wl.onKeyTyped(charCode, keyCode))
                        return;
        }
    }
    
    @Override
    public void handleMouseInput() throws IOException
    {
        super.handleMouseInput();
    
        if (this.listWidgets != null)
            for (WidgetRowListWidget<?> wl : this.listWidgets)
                wl.handleMouseInput();
    }
    
    @Override
    protected void mouseClicked(int mouseX, int mouseY, int button) throws IOException
    {
        if (this.listWidgets != null)
            for (WidgetRowListWidget<?> wl : this.listWidgets)
                wl.mouseClicked(mouseX, mouseY, button);
        
        super.mouseClicked(mouseX, mouseY, button);
        
        for (Widget w : this.widgets)
        {
            boolean mouseDown = w.onMouseDown(this.mc, mouseX, mouseY, button);
            w.setAttentionGiven(mouseDown);
            
            if (mouseDown) {
                w.emitSoundOnClick(this.mc.getSoundHandler()); this.selectedWidget = w;
            }
        }
    }
    
    @Override
    protected void mouseReleased(int mouseX, int mouseY, int button)
    {
        if (this.listWidgets != null)
            for (WidgetRowListWidget<?> wl : this.listWidgets)
                wl.mouseReleased(mouseX, mouseY, button);
        
        super.mouseReleased(mouseX, mouseY, button);
        
        if (this.selectedWidget != null)
        {
            this.selectedWidget.onMouseUp(mouseX, mouseY, button);
            this.selectedWidget = null;
        }
    }
    
    @Override
    public void updateScreen()
    {
        if (this.listWidgets != null)
            for (WidgetRowListWidget<?> wl : this.listWidgets)
                wl.tick();
        
        for (Widget w : this.widgets)
            w.tick();
    }
    
    protected abstract boolean onEscapeKeyLeaves();
}
