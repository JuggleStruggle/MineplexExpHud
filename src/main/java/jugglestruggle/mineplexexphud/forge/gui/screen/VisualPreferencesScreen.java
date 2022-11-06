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

import jugglestruggle.mineplexexphud.forge.MineplexExpHudClientForge;
import jugglestruggle.mineplexexphud.forge.gui.widget.ColorWidget;
import jugglestruggle.mineplexexphud.forge.gui.widget.CyclingButtonWidget;
import jugglestruggle.mineplexexphud.forge.gui.widget.Widget;
import jugglestruggle.mineplexexphud.forge.gui.widget.WidgetRowListWidget;
import jugglestruggle.mineplexexphud.pref.Preferences;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import org.lwjgl.input.Keyboard;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;


import static jugglestruggle.mineplexexphud.MineplexExpHudClient.LANG_FORMAT;

public class VisualPreferencesScreen extends BaseEditScreen
{
    WidgetRowListWidget<Widget> prefsList;
    CyclingButtonWidget<Boolean> showHudButton;
    
    public VisualPreferencesScreen(GuiScreen parentScreen) {
        super(parentScreen);
    }
    
    @Override
    protected void createWidgets()
    {
        super.createTitles(1);
        super.updateTitleLine(0, I18n.format(LANG_FORMAT + "visuals.preferences"));
        
        List<Widget> prefs = new ArrayList<>(8);
        
        prefs.add(of("drawTextWithShadow", Preferences.drawTextWithShadow, this::onDrawTextWithShadowUpdate));
        prefs.add(this.of(Preferences.textColor, "textColor", (cw, c) -> Preferences.textColor = c));
    
        prefs.add(this.of(Preferences.backgroundColor, "backgroundColor", (cw, c) -> Preferences.backgroundColor = c));
        prefs.add(this.of(Preferences.borderColor, "borderColor", (cw, c) -> Preferences.borderColor = c));
    
        prefs.add(this.of(Preferences.progressBackgroundColor, "progressBackgroundColor",(cw, c) -> Preferences.progressBackgroundColor = c));
        prefs.add(this.of(Preferences.progressBorderColor, "progressBorderColor", (cw, c) -> Preferences.progressBorderColor = c));
    
        prefs.add(of("showBorders", Preferences.showBorders, this::onShowBordersUpdate));
        
        this.prefsList = new WidgetRowListWidget<>(
                this, 0, 0, 0, 0,
                22, prefs.toArray(new Widget[0])
        );
        
        super.listWidgets = new ArrayList<>(1);
        super.listWidgets.add(this.prefsList);
        
        this.showHudButton = of("showHud", Preferences.showHud, this::onShowHudUpdate);
        
        super.widgets.add(this.showHudButton);
    }
    
    @Override
    protected void resize()
    {
        Keyboard.enableRepeatEvents(true);
        
        // Update the location and the size of the widgets and also add any buttons that were removed along the way
        this.prefsList.setDimensions(this.width, this.height, 32, this.height - 32);
        this.showHudButton.setPos(this.width / 2 - 151, this.height - 26);
        this.doneButton.setPos(this.width / 2 + 1, this.height - 26);
    }
    
    private ColorWidget of(int startColor, String format, BiConsumer<ColorWidget, Integer> onSet)
    {
        return new ColorWidget(this.mc.fontRendererObj, 150, startColor,
                I18n.format(LANG_FORMAT+format)).setOnColorApply(onSet);
    }
    
    @Override
    public void onGuiClosed()
    {
        Keyboard.enableRepeatEvents(false);
        MineplexExpHudClientForge.getForgeInstance().writeToFile();
        
        super.onGuiClosed();
    }
    
    private void onDrawTextWithShadowUpdate(CyclingButtonWidget<Boolean> b) {
        Preferences.drawTextWithShadow = b.getValue();
    }
    private void onShowBordersUpdate(CyclingButtonWidget<Boolean> b) {
        Preferences.showBorders = b.getValue(); MineplexExpHudClientForge.getForgeExpHud().updateLineRenderPosBox();
    }
    private void onShowHudUpdate(CyclingButtonWidget<Boolean> b) {
        Preferences.showHud = b.getValue();
    }
    
    @Override
    public void drawScreen(int mouseX, int mouseY, float delta)
    {
        if (this.mc.theWorld == null)
        {
            this.drawBackground(0);
            Gui.drawRect(0, this.prefsList.top, this.width, this.prefsList.bottom, 0x88000000);
        }
        else
        {
            Gui.drawRect(0, 0, this.width, this.prefsList.top, 0x88000000);
            Gui.drawRect(0, this.prefsList.bottom, this.width, this.height, 0x88000000);
        }
        
        super.drawScreen(mouseX, mouseY, delta);
    }
}
