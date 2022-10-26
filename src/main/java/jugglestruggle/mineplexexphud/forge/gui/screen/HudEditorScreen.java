package jugglestruggle.mineplexexphud.forge.gui.screen;

import jugglestruggle.mineplexexphud.forge.ForgeExpHud;
import jugglestruggle.mineplexexphud.forge.MineplexExpHudClientForge;
import jugglestruggle.mineplexexphud.forge.event.MIPLEventListener;
import jugglestruggle.mineplexexphud.forge.gui.widget.AbstractMultiWidget;
import jugglestruggle.mineplexexphud.forge.gui.widget.ButtonWidget;
import jugglestruggle.mineplexexphud.forge.gui.widget.CyclingButtonWidget;
import jugglestruggle.mineplexexphud.forge.gui.widget.Widget;
import jugglestruggle.mineplexexphud.hud.enums.HudPositioning;
import jugglestruggle.mineplexexphud.pref.Preferences;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.resources.I18n;

import static jugglestruggle.mineplexexphud.MineplexExpHudClient.LANG_FORMAT;

public class HudEditorScreen extends Screen
{
    boolean draggingHud;
    private float lastXorSize;
    private float lastYorSize;
    private float xOffsetFromMouseStart;
    private float yOffsetFromMouseStart;
    
    DraggableMenuWidget menuWidget;
    
    public HudEditorScreen() {
        this(null);
    }
    
    public HudEditorScreen(GuiScreen parentScreen) {
        super(parentScreen);
    }
    
    @Override
    public void initGui()
    {
        if (!super.screenCreated)
        {
            this.menuWidget = new DraggableMenuWidget(this);
            super.widgets.add(this.menuWidget);
            
            super.screenCreated = true;
        }
        
        if (this.menuWidget.movedFromBasePos)
        {
            int lastX = this.menuWidget.getX();
            int lastY = this.menuWidget.getY();
            
            if (this.menuWidget.getXR() > this.width)
                this.menuWidget.setX(this.width - this.menuWidget.getW());
            if (this.menuWidget.getYB() > this.height)
                this.menuWidget.setY(this.height - this.menuWidget.getH());
            
            if (this.menuWidget.getX() != lastX || this.menuWidget.getY() != lastY) {
                this.menuWidget.updateWidgetPositions();
            }
        }
        else
        {
            this.menuWidget.moveToBasePos();
        }
    }
    
    @Override
    protected boolean onEscapeKeyLeaves() {
        return true;
    }
    
    @Override
    protected void mouseClicked(int mouseX, int mouseY, int button)
    {
        ForgeExpHud hud = MineplexExpHudClientForge.getForgeExpHud();
    
        if (this.menuWidget.onMouseDown(this.mc, mouseX, mouseY, button))
        {
            super.selectedWidget = this.menuWidget;
        }
        else if (this.draggingHud)
        {
            if (button == 1)
                this.endDrag(true);
        }
        else
        {
            if (button == 0 &&
                mouseX >= hud.getX() && mouseX < hud.getX() + hud.getWidth() &&
                mouseY >= hud.getY() && mouseY < hud.getY() + hud.getHeight())
            {
                this.beginDrag(mouseX, mouseY);
            }
        }
    }
    
    @Override
    protected void mouseReleased(int mouseX, int mouseY, int button)
    {
        if (this.draggingHud && button == 0)
            this.endDrag(false);
        
        
        super.mouseReleased(mouseX, mouseY, button);
    }
    
    @Override
    public void drawScreen(int mouseX, int mouseY, float delta)
    {
        if (this.mc.theWorld == null)
        {
            this.drawBackground(0);
            MIPLEventListener.renderHud(new ScaledResolution(this.mc), delta);
        }
    
        ForgeExpHud hud = MineplexExpHudClientForge.getForgeExpHud();
        
        MineplexExpHudClientForge.getCtx().fill
        (
            hud.getX(), hud.getY(),
            hud.getX() + hud.getWidth(),
            hud.getY() + hud.getHeight(),
            0xDDFFAA55
        );
        
        if (this.draggingHud)
        {
            float newX = (float)mouseX + this.xOffsetFromMouseStart;
            float newY = (float)mouseY + this.yOffsetFromMouseStart;
    
            if (newX < 0.0f) newX = 0.0f;
            if (newY < 0.0f) newY = 0.0f;
    
            if (newX + hud.getWidth() > this.width)
                newX = this.width - hud.getWidth();
            if (newY + hud.getHeight() > this.height)
                newY = this.height - hud.getHeight();
    
            if (hud.getX() != newX || hud.getY() != newY) {
                hud.setPos(newX, newY, true);
            }
        }
        
        super.drawScreen(mouseX, mouseY, delta);
        
    }
    
    private void beginDrag(float mouseX, float mouseY)
    {
        ForgeExpHud hud = MineplexExpHudClientForge.getForgeExpHud();
        
        this.draggingHud = true;
    
        this.lastXorSize = hud.getX();
        this.lastYorSize = hud.getY();
    
        this.xOffsetFromMouseStart = hud.getX() - mouseX;
        this.yOffsetFromMouseStart = hud.getY() - mouseY;
    
        this.menuWidget.setVisibleToUser(false);
    }
    private void endDrag(boolean setLastPos)
    {
        ForgeExpHud hud = MineplexExpHudClientForge.getForgeExpHud();
    
        this.draggingHud = false;
    
        if (setLastPos)
            hud.setPos(this.lastXorSize, this.lastYorSize, true);
    
        this.lastXorSize = this.lastYorSize = 0;
        this.xOffsetFromMouseStart = this.yOffsetFromMouseStart = 0;
        
        this.menuWidget.setVisibleToUser(true);
        
        // X
        switch (Preferences.hudScreenPositioning)
        {
            case None:
            {
                Preferences.xOffsetScreenPercentage = hud.getX() / (float)this.width;
                break;
            }
            case Left:
            case TopLeft:
            case BottomLeft:
            {
                Preferences.xOffsetScreenPercentage = (hud.getX() - 4) / (float)this.width;
                break;
            }
            case Center:
            case Top:
            case Bottom:
            {
                Preferences.xOffsetScreenPercentage = (hud.getX() - (float)((this.width / 2) - ((int)hud.getWidth() / 2))) / (float)this.width;
                break;
            }
    
            case Right:
            case TopRight:
            case BottomRight:
            {
                Preferences.xOffsetScreenPercentage = (hud.getX() - (this.width - (4 + (int)hud.getWidth()))) / (float)this.width;
                break;
            }
        }
        
        // Y
        switch (Preferences.hudScreenPositioning)
        {
            case None:
            {
                Preferences.yOffsetScreenPercentage = hud.getY() / (float)this.height;
                break;
            }
            case Top:
            case TopLeft:
            case TopRight:
            {
                Preferences.yOffsetScreenPercentage = (hud.getY() - 4) / (float)this.height;
                break;
            }
            case Left:
            case Center:
            case Right:
            {
                Preferences.yOffsetScreenPercentage = (hud.getY() - (float)((this.height / 2) - ((int)hud.getHeight() / 2))) / (float)this.height;
                break;
            }
            case BottomLeft:
            case Bottom:
            case BottomRight:
            {
                Preferences.yOffsetScreenPercentage = (hud.getY() - (this.height - (4 + (int)hud.getHeight()))) / (float)this.height;
                break;
            }
        }
        
        // Check that both values aren't over -1 or +1
        if (Preferences.xOffsetScreenPercentage < -1.0f)
            Preferences.xOffsetScreenPercentage = -1.0f;
        else if (Preferences.xOffsetScreenPercentage > 1.0f)
            Preferences.xOffsetScreenPercentage = 1.0f;
        
        if (Preferences.yOffsetScreenPercentage < -1.0f)
            Preferences.yOffsetScreenPercentage = -1.0f;
        else if (Preferences.yOffsetScreenPercentage > 1.0f)
            Preferences.yOffsetScreenPercentage = 1.0f;
//        System.out.printf("xoffset: %1$s | yoffset: %2$s\n",
//                Preferences.xOffsetScreenPercentage, Preferences.yOffsetScreenPercentage);
    }
    
    static class DraggableMenuWidget extends AbstractMultiWidget
    {
        final HudEditorScreen screen;
    
        public CyclingButtonWidget<HudPositioning> textPositioning;
        public CyclingButtonWidget<HudPositioning> screenPositioning;
        public ButtonWidget done;
    
        public boolean dragged;
    
        private int lastX;
        private int lastY;
        private int xOffsetFromMouseStart;
        private int yOffsetFromMouseStart;
        private boolean lastMovedFromBasePos;
        
        public boolean movedFromBasePos;
        
        public DraggableMenuWidget(HudEditorScreen owningScreen)
        {
            super(306, 46, 3);
            this.screen = owningScreen;
            
            this.textPositioning = new CyclingButtonWidget<>(150, 20,
                    I18n.format(LANG_FORMAT + "hudPositioning"),
                    Preferences.hudTextPositioning, HudPositioning.HUD_POSITIONS_WITHOUT_NONE, HudPositioning::getTranslation);
            this.screenPositioning = new CyclingButtonWidget<>(150, 20,
                    I18n.format(LANG_FORMAT + "screenPositioning"),
                    Preferences.hudScreenPositioning, HudPositioning.HUD_POSITIONS, HudPositioning::getTranslation);
            
            this.textPositioning.setValueChangeListener(this::onTextPositioningUpdate);
            this.screenPositioning.setValueChangeListener(this::onScreenPositioningUpdate);
            
            this.done = new ButtonWidget(150, 20, I18n.format("jugglestruggle.saveandreturn"), this::onDoneButtonClick);
            
            super.childWidgets.add(this.textPositioning);
            super.childWidgets.add(this.screenPositioning);
            super.childWidgets.add(this.done);
        }
    
        @Override
        public void render(Minecraft client, int mouseX, int mouseY, float delta)
        {
            if (this.dragged)
            {
                int newX = mouseX + this.xOffsetFromMouseStart;
                int newY = mouseY + this.yOffsetFromMouseStart;
                
                if (newX < 0) newX = 0;
                if (newY < 0) newY = 0;
                
                if (newX + this.getW() > this.screen.width)
                    newX = this.screen.width - this.getW();
                if (newY + this.getH() > this.screen.height)
                    newY = this.screen.height - this.getH();
    
    
                if (this.getX() != newX || this.getY() != newY)
                {
                    this.setX(newX); this.setY(newY);
                    this.updateWidgetPositions();
                    
                    if (!this.movedFromBasePos)
                        this.movedFromBasePos = true;
                }
            }
    
            if (this.isVisibleToUser())
                Gui.drawRect(this.getX(), this.getY(), this.getXR(), this.getYB(), 0x77777777);
    
            super.render(client, mouseX, mouseY, delta);
        }
    
        @Override
        protected void updateWidgetPositionsFromRender() { }
    
        @Override
        public boolean onMouseDown(Minecraft client, int mouseX, int mouseY, int button)
        {
            if (this.dragged)
            {
                if (button == 1) { // Resets to last position
                    this.endDrag(true);
                }
                
                return true;
            }
            else if (this.isInteractableAndMouseOver(mouseX, mouseY))
            {
                boolean widgetMouseDowned = false;
    
                for (Widget w : this.childWidgets)
                {
                    widgetMouseDowned = w.onMouseDown(client, mouseX, mouseY, button);
        
                    if (widgetMouseDowned) {
                        w.emitSoundOnClick(client.getSoundHandler()); break;
                    }
                }
    
                if (!widgetMouseDowned)
                {
                    if (button == 0) { // Begin dragging
                        this.beginDrag(mouseX, mouseY);
                    } else if (button == 1) { // Reset to base position
                        this.movedFromBasePos = false; this.moveToBasePos();
                    }
                }
    
                return true;
            }
            
            return false;
        }
    
        @Override
        public boolean onMouseUp(int mouseX, int mouseY, int button)
        {
            if (this.dragged && button == 0) {
                this.endDrag(false);
            }
            
            return super.onMouseUp(mouseX, mouseY, button);
        }
    
        @Override
        protected void updateWidgetPositions()
        {
            this.textPositioning.setPos(this.getX() + 2, this.getY() + 2);
            this.screenPositioning.setPos(this.getX() + 154, this.getY() + 2);
            this.done.setPos(this.getX() + 75, this.getY() + 24);
        }
    
        private boolean onScreenPositioningUpdate(CyclingButtonWidget<HudPositioning> b, HudPositioning v)
        {
            Preferences.hudScreenPositioning = v;
            Preferences.xOffsetScreenPercentage = 0.0f;
            Preferences.yOffsetScreenPercentage = 0.0f;
            MineplexExpHudClientForge.getForgeExpHud().setPositionByScreenArea();
            
            return true;
        }
    
        private boolean onTextPositioningUpdate(CyclingButtonWidget<HudPositioning> b, HudPositioning v)
        {
            Preferences.hudTextPositioning = v;
            MineplexExpHudClientForge.getForgeExpHud().updateLineRenderPosBox();
            
            return true;
        }
    
        private boolean onDoneButtonClick(ButtonWidget b)
        {
            MineplexExpHudClientForge.getInstance().writeToFile();
            this.screen.mc.displayGuiScreen(this.screen.parentScreen);
            
            return true;
        }
        
        public void moveToBasePos()
        {
            this.setX((this.screen.width / 2) - (this.getW() / 2));
            this.setY(this.screen.height - (this.getH() + 20));
            
            this.updateWidgetPositions();
        }
    
    
        private void beginDrag(int mouseX, int mouseY)
        {
            this.dragged = true;
    
            this.lastX = this.getX();
            this.lastY = this.getY();
            
            this.lastMovedFromBasePos = this.movedFromBasePos;
    
            this.xOffsetFromMouseStart = this.getX() - mouseX;
            this.yOffsetFromMouseStart = this.getY() - mouseY;
        }
        private void endDrag(boolean setLastPos)
        {
            this.dragged = false;
    
            if (setLastPos)
            {
                this.setPos(this.lastX, this.lastY);
                this.movedFromBasePos = this.lastMovedFromBasePos;
            }
    
            this.lastX = this.lastY = 0;
            this.lastMovedFromBasePos = false;
    
            this.xOffsetFromMouseStart =
            this.yOffsetFromMouseStart = 0;
    
            this.updateWidgetPositions();
        }
    }
}
