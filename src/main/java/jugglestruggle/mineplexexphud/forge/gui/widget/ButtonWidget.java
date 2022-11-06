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

package jugglestruggle.mineplexexphud.forge.gui.widget;

import jugglestruggle.mineplexexphud.forge.MineplexExpHudClientForge;
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
    
    public int textColor = 0xFFFFFF;
    public int hoveredTextColor = 16777120;
    public int disabledTextColor = 10526880;
    public boolean drawTextWithShadow = true;
    
    public int backColor = 0xFF707070;
    public int hoveredBackColor = 0xFF7E88BF;
    public int disabledBackColor = 0xFF2C2C2C;
    public boolean renderWithTextures = true;
    
    public int borderColor = 0xFF000000;
    public int hoveredBorderColor = 0xFFFFFFFF;
    public int disabledBorderColor = 0xFF000000;
    
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
    
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
    
        this.hovered = mouseX >= this.xPosition && mouseY >= this.yPosition &&
                mouseX < this.xPosition + this.width && mouseY < this.yPosition + this.height;
    
        int hoverState = this.getHoverState(this.hovered);
    
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GlStateManager.blendFunc(770, 771);
    
        if (this.renderWithTextures)
        {
            mc.getTextureManager().bindTexture(buttonTextures);
            
            super.drawTexturedModalRect(this.xPosition, this.yPosition, 0, 46 + hoverState * 20, this.width / 2, this.height);
            super.drawTexturedModalRect(this.xPosition + this.width / 2, this.yPosition, 200 - this.width / 2, 46 + hoverState * 20, this.width / 2, this.height);
        }
        else
        {
            int backColor;
            int borderColor;
            
            if (this.enabled)
            {
                backColor = this.hovered ? this.hoveredBackColor : this.backColor;
                borderColor = this.hovered ? this.hoveredBorderColor : this.borderColor;
            }
            else
            {
                backColor = this.disabledBackColor;
                borderColor = this.disabledBorderColor;
            }
            
            MineplexExpHudClientForge.getCtx().fillWithWireframe
            (
                this.xPosition + 1, this.yPosition + 1,
                this.xPosition + this.width - 1,
                this.yPosition + this.height - 1,
                
                backColor, borderColor
            );
        }
    
        super.mouseDragged(mc, mouseX, mouseY);
        
        this.drawBeforeText(mc, mouseX, mouseY);
    
        if (this.displayString.isEmpty())
            return;
        
        int textColor;
    
        if (super.packedFGColour == 0)
        {
            if (this.enabled)
                textColor = this.hovered ? this.hoveredTextColor : this.textColor;
            else
                textColor = this.disabledTextColor;
        }
        else
            textColor = this.packedFGColour;
    
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
                    startingY, textColor, this.drawTextWithShadow
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
                    textColor, this.drawTextWithShadow
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
                    startingY, textColor, this.drawTextWithShadow
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
