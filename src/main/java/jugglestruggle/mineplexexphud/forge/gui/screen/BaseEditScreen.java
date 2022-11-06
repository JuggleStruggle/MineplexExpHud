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

package jugglestruggle.mineplexexphud.forge.gui.screen;

import jugglestruggle.mineplexexphud.forge.gui.widget.ButtonWidget;
import jugglestruggle.mineplexexphud.forge.gui.widget.CyclingButtonWidget;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;

import java.util.function.Consumer;
import java.util.function.Predicate;


import static jugglestruggle.mineplexexphud.MineplexExpHudClient.LANG_FORMAT;

public abstract class BaseEditScreen extends Screen
{
    protected String[] titles;
    protected int[] titlesWidth;
    ButtonWidget doneButton;
    
    protected BaseEditScreen(GuiScreen parentScreen) {
        super(parentScreen);
    }
    
    @Override
    public void initGui()
    {
        if (!super.screenCreated)
        {
            this.createWidgets();
    
            this.doneButton = new ButtonWidget(150, 20, I18n.format("jugglestruggle.saveandreturn"), this::onDoneClick);
            super.widgets.add(this.doneButton);
    
            super.screenCreated = true;
        }
        
        this.resize();
    }
    
    @Override
    public void drawScreen(int mouseX, int mouseY, float delta)
    {
        for (int i = 0; i < this.titles.length; ++i)
        {
            this.mc.fontRendererObj.drawString
            (
                this.titles[i],
                (this.width / 2) - (this.titlesWidth[i] / 2),
                10 + ((this.mc.fontRendererObj.FONT_HEIGHT + 2) * i), 0xFFFFFF
            );
        }
        
        super.drawScreen(mouseX, mouseY, delta);
    }
    
    protected boolean onDoneClick(ButtonWidget b) {
        this.mc.displayGuiScreen(this.parentScreen); return true;
    }
    
    protected void createTitles(int size)
    {
        if (size < 0)
            throw new ArrayIndexOutOfBoundsException();
            
        this.titles = new String[size];
        this.titlesWidth = new int[size];
    }
    
    protected void updateTitleLine(int i, String text)
    {
        this.titles[i] = text;
        this.titlesWidth[i] = this.mc.fontRendererObj.getStringWidth(text);
    }
    
    protected abstract void createWidgets();
    protected abstract void resize();
    
    protected static ButtonWidget of(String format, Predicate<ButtonWidget> onClick) {
        return new ButtonWidget(150, 20, I18n.format(LANG_FORMAT+format), onClick);
    }
    protected static CyclingButtonWidget<Boolean> of(String format, boolean value,
                                                   Consumer<CyclingButtonWidget<Boolean>> onChanged)
    {
        return CyclingButtonWidget.bool(150, 20, I18n.format(LANG_FORMAT+format), value, (byte)1)
                .setPostValueChangeListener(onChanged);
    }
}
