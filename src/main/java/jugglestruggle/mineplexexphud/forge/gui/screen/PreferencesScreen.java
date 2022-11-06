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

import com.google.common.collect.ImmutableList;
import jugglestruggle.mineplexexphud.forge.MineplexExpHudClientForge;
import jugglestruggle.mineplexexphud.forge.gui.widget.ButtonWidget;
import jugglestruggle.mineplexexphud.forge.gui.widget.CyclingButtonWidget;
import jugglestruggle.mineplexexphud.forge.gui.widget.NumericWidget;
import jugglestruggle.mineplexexphud.forge.gui.widget.TimeUnitInfoWidget;
import jugglestruggle.mineplexexphud.forge.gui.widget.Widget;
import jugglestruggle.mineplexexphud.forge.gui.widget.WidgetRowListWidget;
import jugglestruggle.mineplexexphud.hud.enums.AccuracyMode;
import jugglestruggle.mineplexexphud.hud.enums.UpdateMethod;
import jugglestruggle.mineplexexphud.pref.Preferences;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;

import java.util.ArrayList;
import java.util.List;


import static jugglestruggle.mineplexexphud.MineplexExpHudClient.LANG_FORMAT;

public class PreferencesScreen extends BaseEditScreen
{
    WidgetRowListWidget<Widget> prefsList;
    CyclingButtonWidget<Boolean> showHudButton;
    
    public PreferencesScreen() {
        this(null);
    }
    
    public PreferencesScreen(GuiScreen parentScreen) {
        super(parentScreen);
    }
    
    @Override
    protected void createWidgets()
    {
        super.createTitles(1);
        super.updateTitleLine(0, I18n.format(LANG_FORMAT + "preferences"));
        
        List<Widget> prefs = new ArrayList<>(14);
        
        // Display Formatting & Visuals
        prefs.add(of("displayFormatting", this::onModifyDisplayFormatClick));
        prefs.add(of("visuals", this::onModifyVisualsClick));
        
        // Modify HUD Position & Modify HUD's Background Edge
        prefs.add(of("hudEditor", this::onModifyHudClick));
        prefs.add(of("hudEdges", this::onModifyHudEdgesClick));
    
        // EXP Update Enabled & Accuracy
        CyclingButtonWidget<Boolean> cbw = CyclingButtonWidget.bool(150, 20, I18n.format(LANG_FORMAT+"expUpdateEnabled"),
                Preferences.expUpdateEnabled, (byte)1).setValueChangeListener(this::onExpUpdateEnabledUpdate);
        cbw.setTooltipText(I18n.format(LANG_FORMAT+"expUpdateEnabled.description"));
        prefs.add(cbw);
        prefs.add(new CyclingButtonWidget<>(150, 20, I18n.format(LANG_FORMAT+"accuracy"),
                Preferences.accuracy, ImmutableList.copyOf(AccuracyMode.values()), AccuracyMode::getFormattedText,
                AccuracyMode::getFormattedTextDesc).setValueChangeListener(this::onAcurracyTypeUpdate));
    
        // Update Method & On World Change: Use Delays
        prefs.add(new CyclingButtonWidget<>(150, 20, I18n.format(LANG_FORMAT+"updateMethod"),
                Preferences.updateMethod, ImmutableList.copyOf(UpdateMethod.values()), UpdateMethod::getFormattedText)
                .setValueChangeListener(this::onExpUpdateMethodUpdate));
        prefs.add(CyclingButtonWidget.bool(150, 20, I18n.format(LANG_FORMAT+"worldChangeUseDelays"),
                Preferences.worldChangeUseDelays, (byte)1).setValueChangeListener(this::onWorldChangeUseDelaysUpdate));
    
        // Works Offline & Ignore Empty EXP
        cbw = CyclingButtonWidget.bool(150, 20, I18n.format(LANG_FORMAT+"worksLocally"), true, (byte)1);
        cbw.setInteractable(false);
        cbw.setTooltipText(I18n.format(LANG_FORMAT+"worksLocally.notimplemented"));
        prefs.add(cbw);
        cbw = CyclingButtonWidget.bool(150, 20, I18n.format(LANG_FORMAT+"ignoreEmptyExp"),
                Preferences.ignoreEmptyExp, (byte)1).setValueChangeListener(this::onIgnoreEmptyExpUpdate);
        cbw.setTooltipText(I18n.format(LANG_FORMAT+"ignoreEmptyExp.description"));
        prefs.add(cbw);
    
        // Post Render & Show While Debug Screen's Active
        prefs.add(CyclingButtonWidget.bool(150, 20, I18n.format(LANG_FORMAT+"postRender"),
                Preferences.postRender, (byte)1).setValueChangeListener(this::onShowInPostRenderUpdate));
        prefs.add(CyclingButtonWidget.bool(150, 20, I18n.format(LANG_FORMAT+"showWhileDebugScreenActive"),
                Preferences.showWhileDebugScreenActive, (byte)1).setValueChangeListener(this::onShowWhileDebugScreenActiveUpdate));
        
        // Millis Until Next EXP Update & EXP Gained in Set Time
        prefs.add(new NumericWidget(this.mc.fontRendererObj, 150, 20, Preferences.millisUntilNextExpUpdate,
                1000L, Long.MAX_VALUE, I18n.format(LANG_FORMAT+"millisUntilNextExpUpdate"))
                .setValueChangeListener(this::onMillisUntilNextExpUpdateUpdated));
        TimeUnitInfoWidget tui = new TimeUnitInfoWidget(this.mc.fontRendererObj, 150, 20,
                Preferences.expLevelGainedInSetTime, I18n.format(LANG_FORMAT+"expGainedInSetTime"));
        tui.setPostOnValueChanged(this::onExpGainedInSetTimeValueChanged);
        prefs.add(tui);
        
        
        this.prefsList = new WidgetRowListWidget<>(this, 0, 0, 0, 0,22, prefs.toArray(new Widget[0]));
        
        super.listWidgets = new ArrayList<>(1);
        super.listWidgets.add(this.prefsList);
        
        this.showHudButton = CyclingButtonWidget.bool(150, 20, I18n.format(LANG_FORMAT+"showHud"),
                Preferences.showHud, (byte)1).setValueChangeListener(this::onShowHudUpdate);
        
        super.widgets.add(this.showHudButton);
    }
    
    @Override
    protected void resize()
    {
        this.prefsList.setDimensions(this.width, this.height, 32, this.height - 32);
        this.showHudButton.setPos(this.width / 2 - 151, this.height - 26);
        super.doneButton.setPos(this.width / 2 + 1, this.height - 26);
    }
    
    private boolean onMillisUntilNextExpUpdateUpdated(double newValue, double oldValue)
    {
        Preferences.millisUntilNextExpUpdate = (long)newValue;
        MineplexExpHudClientForge.getForgeExpHud().delayActiveMillisUntilNextExpUpdate();
        
        return true;
    }
    
    @Override
    public void onGuiClosed()
    {
        MineplexExpHudClientForge.getForgeInstance().writeToFile();
        super.onGuiClosed();
    }
    
    private boolean onExpUpdateEnabledUpdate(CyclingButtonWidget<Boolean> b, Boolean v)
    {
        Preferences.expUpdateEnabled = v; MineplexExpHudClientForge.getForgeExpHud().rebuildDisplayCacheInfo();
        return true;
    }
    private boolean onWorldChangeUseDelaysUpdate(CyclingButtonWidget<Boolean> b, Boolean v) {
        Preferences.worldChangeUseDelays = v; return true;
    }
    private boolean onShowHudUpdate(CyclingButtonWidget<Boolean> b, Boolean v) {
        Preferences.showHud = v; return true;
    }
    private boolean onShowInPostRenderUpdate(CyclingButtonWidget<Boolean> b, Boolean v) {
        Preferences.postRender = v; return true;
    }
    private boolean onShowWhileDebugScreenActiveUpdate(CyclingButtonWidget<Boolean> b, Boolean v) {
        Preferences.showWhileDebugScreenActive = v; return true;
    }
    private boolean onAcurracyTypeUpdate(CyclingButtonWidget<AccuracyMode> b, AccuracyMode v) {
        Preferences.accuracy = v; return true;
    }
    private boolean onExpUpdateMethodUpdate(CyclingButtonWidget<UpdateMethod> b, UpdateMethod v)
    {
        Preferences.updateMethod = v; MineplexExpHudClientForge.getForgeExpHud().rebuildDisplayCacheInfo();
        return true;
    }
    private boolean onIgnoreEmptyExpUpdate(CyclingButtonWidget<Boolean> b, Boolean v) {
        Preferences.ignoreEmptyExp = v; return true;
    }
    private boolean onModifyDisplayFormatClick(ButtonWidget b) {
        this.mc.displayGuiScreen(new DisplayFormatPreferencesScreen(this)); return true;
    }
    private boolean onModifyVisualsClick(ButtonWidget b) {
        this.mc.displayGuiScreen(new VisualPreferencesScreen(this)); return true;
    }
    private boolean onModifyHudClick(ButtonWidget b) {
        this.mc.displayGuiScreen(new HudEditorScreen(this)); return true;
    }
     private boolean onModifyHudEdgesClick(ButtonWidget b) {
          this.mc.displayGuiScreen(new HudEdgesEditorScreen(this)); return true;
     }
    
    private void onExpGainedInSetTimeValueChanged(TimeUnitInfoWidget w) {
        Preferences.expLevelGainedInSetTime = w.getUnitInfo();
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
