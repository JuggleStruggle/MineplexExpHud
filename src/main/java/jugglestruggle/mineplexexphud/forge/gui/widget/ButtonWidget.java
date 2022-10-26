package jugglestruggle.mineplexexphud.forge.gui.widget;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;

import java.util.function.Predicate;

public class ButtonWidget extends GuiButton implements Widget
{
    private Predicate<ButtonWidget> onClick;
    
    protected int xOffset;
    protected int yOffset;
    
    public int xOffsetText;
    public int yOffsetText;
    
    /**
     * <li>0 = Center</li>
     * <li>1 = Left</li>
     * <li>2 = Right</li>
     */
    public byte positioning;
    
    String[] tooltipText;
    
    protected ButtonWidget(int w, int h, String buttonText) {
        this(w, h, buttonText, null);
    }
    public ButtonWidget(int w, int h, String buttonText, Predicate<ButtonWidget> onClick)
    {
        super(0, 0, 0, w, h, buttonText);
        this.onClick = onClick; this.positioning = 0;
    }
    
    @Override
    public boolean mousePressed(Minecraft mc, int mouseX, int mouseY)
    {
        if (super.mousePressed(mc, mouseX, mouseY))
            return this.onClick == null || this.onClick.test(this);
        else
            return false;
    }
    
    protected final boolean superMousePressed(int mouseX, int mouseY) {
        return super.mousePressed(null, mouseX, mouseY);
    }
    
    public void setOnClick(Predicate<ButtonWidget> onClick) {
        this.onClick = onClick;
    }
    
    public void setTooltipText(String tooltipText) {
        this.tooltipText = tooltipText.split("\\\\n");
    }
    
    @Override
    public int getX() {
        return super.xPosition;
    }
    @Override
    public int getY() {
        return super.yPosition;
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
    public int getW() {
        return super.width;
    }
    @Override
    public int getH() {
        return super.height;
    }
    
    @Override
    public void setX(int x) {
        super.xPosition = x;
    }
    @Override
    public void setY(int y) {
        super.yPosition = y;
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
    public void setW(int w) {
        super.width = w;
    }
    @Override
    public void setH(int h) {
        super.height = h;
    }
    
    
    @Override
    public boolean isInteractable() {
        return super.enabled;
    }
    @Override
    public boolean isAttentionGiven() {
        return false;
    }
    @Override
    public boolean isVisibleToUser() {
        return super.visible;
    }
    
    @Override
    public void setInteractable(boolean enabled) {
        super.enabled = enabled;
    }
    
    @Override
    public void setVisibleToUser(boolean visible) {
        super.visible = visible;
    }
    
    @Override
    public void setAttentionGiven(boolean focused) {}
    
    @Override
    public void render(Minecraft client, int mouseX, int mouseY, float delta) {
        this.drawButton(client, mouseX, mouseY);
    }
    
    @Override
    public boolean onMouseDown(Minecraft client, int mouseX, int mouseY, int button) {
        return this.mousePressed(client, mouseX, mouseY);
    }
    
    @Override
    public boolean onMouseUp(int mouseX, int mouseY, int button)
    {
        super.mouseReleased(mouseX, mouseY);
        return true;
    }
    
    @Override
    public void drawButton(Minecraft mc, int mouseX, int mouseY)
    {
        if (!this.visible)
            return;
    
        mc.getTextureManager().bindTexture(buttonTextures);
    
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
    
        this.hovered = mouseX >= this.xPosition && mouseY >= this.yPosition &&
                mouseX < this.xPosition + this.width && mouseY < this.yPosition + this.height;
    
        int i = this.getHoverState(this.hovered);
    
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GlStateManager.blendFunc(770, 771);
        
        this.drawTexturedModalRect(this.xPosition, this.yPosition, 0, 46 + i * 20, this.width / 2, this.height);
        this.drawTexturedModalRect(this.xPosition + this.width / 2, this.yPosition, 200 - this.width / 2, 46 + i * 20, this.width / 2, this.height);
    
        this.mouseDragged(mc, mouseX, mouseY);
        
        this.drawBeforeText(mc, mouseX, mouseY);
    
        if (this.displayString.isEmpty())
            return;
        
        int textColor;
    
        if (this.packedFGColour != 0)
            textColor = this.packedFGColour;
        else if (!this.enabled)
            textColor = 10526880;
        else if (this.hovered)
            textColor = 16777120;
        else
            textColor = 14737632;
    
        final int textWidth = mc.fontRendererObj.getStringWidth(this.displayString);
        final int startingX = this.xPosition + this.xOffsetText;
        final int startingY = this.yPosition + this.yOffsetText +
                (this.height / 2) - (mc.fontRendererObj.FONT_HEIGHT / 2);
        
        switch (this.positioning)
        {
            // Center
            default:
            case 0:
            {
                mc.fontRendererObj.drawString
                (
                    this.displayString,
                    startingX + (this.width / 2) - (textWidth / 2),
                    startingY, textColor, true
                );
    
                break;
            }
            // Left
            case 1:
            {
                mc.fontRendererObj.drawString
                (
                    this.displayString,
                    startingX + 4, startingY,
                    textColor, true
                );
    
                break;
            }
            // Right
            case 2:
            {
                mc.fontRendererObj.drawString
                (
                    this.displayString,
                    startingX + this.width - (textWidth + 4),
                    startingY, textColor, true
                );
    
                break;
            }
        }
    }
    
    protected void drawBeforeText(Minecraft client, int mouseX, int mouseY) {}
    
    @Override
    public void tick() {
    
    }
    
    @Override
    public void emitSoundOnClick(SoundHandler handler) {
        super.playPressSound(handler);
    }
    
    @Override
    public String[] getTooltipLines(int mouseX, int mouseY) {
        return this.tooltipText;
    }
}
