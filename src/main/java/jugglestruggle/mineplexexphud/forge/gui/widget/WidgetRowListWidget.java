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

import com.google.common.collect.ImmutableList;
import jugglestruggle.mineplexexphud.forge.MineplexExpHudClientForge;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiListExtended;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.Tessellator;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiPredicate;

public class WidgetRowListWidget<W extends Widget> extends GuiListExtended
{
    protected final List<WidgetEntry<W>> entries;
    public final GuiScreen owningScreen;
    
    public int idealRowWidth = 320; // 155
    public boolean repositionWidgetsX = true;
    
    public boolean visible = true;
    public boolean focused = false;
    
    public WidgetRowListWidget(GuiScreen owningScreen, int width, int height, int top, int bottom,
                               int rowHeight, W[] widgets)
    {
        this(owningScreen, width, height, top, bottom, rowHeight, widgets, (l, r) -> true);
    }
    public WidgetRowListWidget(GuiScreen owningScreen, int width, int height, int top, int bottom,
                               int rowHeight, W[] widgets, BiPredicate<W, W> multiWidgetsRowPredicate)
    {
        super(owningScreen.mc, width, height, top, bottom, rowHeight);
    
        this.owningScreen = owningScreen;
        this.field_148163_i = false;
        
        this.entries = new ArrayList<>();
    
        if (widgets != null)
            this.addEntries(widgets, multiWidgetsRowPredicate);
    }
    
    protected WidgetEntry<W> create(W[] widgets) {
        return new WidgetEntry<>(this, widgets);
    }
    protected WidgetEntry<W> create(W left, W right) {
        return new WidgetEntry<>(this, left, right);
    }
    
    @Override
    public void setDimensions(int width, int height, int top, int bottom)
    {
        super.setDimensions(width, height, top, bottom);
        this.updateWidgetLocations();
    }
    
    public void updateWidgetLocations()
    {
        for (WidgetEntry<W> r : this.entries)
            r.updateWidgetLocations();
    }
    
    @Override
    public int getSize() {
        return this.entries.size();
    }
    
    @Override
    public int getListWidth() {
        return 400;
    }
    
    @Override
    protected int getScrollBarX() {
        return super.getScrollBarX() + 32;
    }
    
    @Override
    public IGuiListEntry getListEntry(int index) {
        return this.entries.get(index);
    }
    
    @Override
    public void handleMouseInput()
    {
        if (this.visible)
            super.handleMouseInput();
    }
    @Override
    public boolean mouseClicked(int mouseX, int mouseY, int button)
    {
        boolean isInside = mouseX >= this.left && mouseX < this.right &&
                           mouseY >= this.top  && mouseY < this.bottom;
    
        this.focused = this.visible && isInside;
    
        if (this.focused)
        {
            for (WidgetEntry<W> w : this.entries) {
                w.unfocus();
            }
    
            return super.mouseClicked(mouseX, mouseY, button);
        }
        
        return false;
    }
    @Override
    public boolean mouseReleased(int mouseX, int mouseY, int button) {
        return this.visible && super.mouseReleased(mouseX, mouseY, button);
    }
    
    @Override
    public void drawScreen(int mouseX, int mouseY, float delta)
    {
        if (this.visible)
            super.drawScreen(mouseX, mouseY, delta);
    }
    
    @Override
    protected void drawContainerBackground(Tessellator tessellator) {
        MineplexExpHudClientForge.getCtx().createScissor(this.left, this.top, this.width, this.bottom - this.top);
    }
    
    @Override
    protected void func_148142_b(int p_148142_1_, int p_148142_2_) {
        MineplexExpHudClientForge.getCtx().removeScissor();
    }
    
    @Override
    protected void overlayBackground(int startY, int endY, int startAlpha, int endAlpha)
    {
    }
    
    @Override
    protected void drawSlot(int slotIndex, int x, int y, int slotHeight, int mouseX, int mouseY)
    {
        if (y + slotHeight >= this.top || y < this.bottom)
            super.drawSlot(slotIndex, x, y, slotHeight, mouseX, mouseY);
    }
    
    public void tick()
    {
        for (WidgetEntry<W> w : this.entries)
            w.tick();
    }
    
    public boolean onKeyTyped(char charCode, int keyCode)
    {
        for (WidgetEntry<W> w : this.entries)
        {
            if (w.focused && w.onKeyTyped(charCode, keyCode))
                return true;
        }
    
        return false;
    }
    
    protected void addEntries(W[] widgets, BiPredicate<W, W> multiWidgetsRowPredicate)
    {
        final int bSize = widgets.length;
        
        for (int i = 0; i < bSize; ++i)
//        for (int i = 0; i < bSize; i += 2)
        {
            W l = widgets[i];
            W r = (i < bSize - 1) ? widgets[i + 1] : null;
            
            if (multiWidgetsRowPredicate.test(l, r))
            {
                this.entries.add(this.create(l, r));
                ++i;
            }
            else
                this.entries.add(this.create(l, null));
        }
//        for (int i = 0; i < bSize; i += 2) {
//            this.rows.add(new AbstractWidgetRow<>(this, widgets[i], (i < bSize - 1) ? widgets[i + 1] : null));
//        }
        
        this.updateWidgetLocations();
    }
    
    public boolean isFocused() {
        return this.focused;
    }
    public void setFocused(boolean focused) {
        this.focused = focused;
    }
    
    public String[] getTooltipLines(int mouseX, int mouseY, boolean fromRender)
    {
        if (fromRender && !this.visible)
            return null;
        
        int i = super.getSlotIndexFromScreenCoords(mouseX, mouseY);
    
        if (i >= 0)
            return this.entries.get(i).getTooltipLines(mouseX, mouseY);
    
        return null;
    }
    
    public Minecraft getClient() {
        return super.mc;
    }
    
    public static class WidgetEntry<W extends Widget> implements IGuiListEntry
    {
        protected final WidgetRowListWidget<W> rowList;
        protected final List<W> widgets;
        protected final boolean withTwo;
        
        boolean focused;
        
        public WidgetEntry(final WidgetRowListWidget<W> owner, W[] widgets)
        {
            this.rowList = owner;
            this.widgets = ImmutableList.copyOf(widgets);
            this.withTwo = false;
        }
        public WidgetEntry(final WidgetRowListWidget<W> owner, W left, W rght)
        {
            this.rowList = owner;
            
            boolean leftEmpty = left == null;
            boolean rghtEmpty = rght == null;
    
            if (leftEmpty && rghtEmpty)
                this.widgets = null;
            else if (leftEmpty || rghtEmpty)
                this.widgets = ImmutableList.of(!leftEmpty ? left : rght);
            else
                this.widgets = ImmutableList.of(left, rght);
            
            this.withTwo = true;
        }
    
        @Override
        public void drawEntry(int slotIndex, int x, int y, int listWidth, int slotHeight, int mouseX, int mouseY, boolean isSelected)
        {
            if (this.widgets != null)
            {
                for (W widget : this.widgets)
                {
                    widget.setY(y + widget.getYOffset());
                    widget.render(this.rowList.mc, mouseX, mouseY, 0.0f);
                }
            }
        }
    
        @Override
        public boolean mousePressed(int slotIndex, int mouseX, int mouseY, int button, int p_148278_5_, int p_148278_6_)
        {
            if (this.widgets != null)
                for (W w : this.widgets)
                {
                    boolean mouseDown = w.onMouseDown(this.rowList.mc, mouseX, mouseY, button);
    
                    w.setAttentionGiven(mouseDown);
    
                    if (mouseDown)
                    {
                        w.emitSoundOnClick(this.rowList.mc.getSoundHandler());
                        this.focused = true;
    
                        return true;
                    }
                }
            
            return false;
        }
    
        @Override
        public void mouseReleased(int slotIndex, int x, int y, int button, int relativeX, int relativeY)
        {
            if (this.widgets != null)
                for (W widget : this.widgets)
                    widget.onMouseUp(x, y, button);
        }
    
        public boolean onKeyTyped(char charCode, int keyCode)
        {
            if (this.widgets != null)
                for (Widget w : this.widgets)
                    if (w.isActuallyFocused() && w.onKeyTyped(charCode, keyCode))
                        return true;
        
            return false;
        }
    
        @Override
        public void setSelected(int p_178011_1_, int p_178011_2_, int p_178011_3_)
        {
        }
    
        public void updateWidgetLocations()
        {
            int rowWidth = this.rowList.idealRowWidth / 2;
            final int bX = (this.rowList.width / 2) - rowWidth;
        
            if (this.widgets != null)
            {
                if (this.withTwo)
                {
                    Widget w = this.widgets.get(0);
//                    int initialXPos = bX - (rowWidth / 4);
                    int initialXPos = bX + 8;
                    
                    w.setX(initialXPos + w.getXOffset());
    
                    if (this.widgets.size() == 2)
                    {
                        w = this.widgets.get(1);
    
                        if (w != null) {
                            if (this.rowList.repositionWidgetsX)
                                w.setX(bX + (rowWidth) + w.getXOffset());
                            else
                                w.setX(initialXPos + w.getXOffset());
                        }
                    }
                }
                else
                {
                    final int widgetSize = this.widgets.size();
                    final int additive = this.rowList.repositionWidgetsX ? (rowWidth / widgetSize) : 0;
    
                    for (int i = 0; i < widgetSize; ++i)
                    {
                        Widget w = this.widgets.get(i);
                        w.setX(bX + (additive * i) + w.getXOffset());
                    }
                }
            }
        }
    
        public void tick()
        {
            if (this.widgets != null)
                for (Widget w : this.widgets)
                    w.tick();
        }
    
        public boolean isFocused() {
            return this.focused;
        }
    
        public void unfocus()
        {
            this.focused = false;
            
            if (this.widgets != null)
                for (Widget w : this.widgets)
                    w.setAttentionGiven(false);
        }
    
        public String[] getTooltipLines(int mouseX, int mouseY)
        {
            if (this.widgets != null)
                for (Widget w : this.widgets)
                {
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
}
