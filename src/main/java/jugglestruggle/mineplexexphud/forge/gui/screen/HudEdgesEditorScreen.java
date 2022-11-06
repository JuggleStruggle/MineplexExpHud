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
import jugglestruggle.mineplexexphud.AbstractExpHud;
import jugglestruggle.mineplexexphud.MineplexExpHudClient;
import jugglestruggle.mineplexexphud.forge.ForgeRenderContext;
import jugglestruggle.mineplexexphud.forge.MineplexExpHudClientForge;
import jugglestruggle.mineplexexphud.forge.gui.widget.ButtonWidget;
import jugglestruggle.mineplexexphud.forge.gui.widget.CyclingButtonWidget;
import jugglestruggle.mineplexexphud.forge.gui.widget.NumericWidget;
import jugglestruggle.mineplexexphud.forge.gui.widget.Widget;
import jugglestruggle.mineplexexphud.forge.gui.widget.WidgetRowListWidget;
import jugglestruggle.mineplexexphud.hud.enums.HudPositioning;
import jugglestruggle.mineplexexphud.hud.info.LineInfoCache;
import jugglestruggle.mineplexexphud.pref.Preferences;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;


import static jugglestruggle.mineplexexphud.MineplexExpHudClient.LANG_FORMAT;
import static jugglestruggle.mineplexexphud.MineplexExpHudClient.getLang;

public class HudEdgesEditorScreen extends BaseEditScreen
{
    static final HudPositioning[] DIRECTIONS = new HudPositioning[]
    {
        HudPositioning.Left, HudPositioning.Right,
        HudPositioning.Top, HudPositioning.Bottom
    };
    
    WidgetRowListWidget<Widget> prefsList;
    CyclingButtonWidget<Boolean> togglePreviewMode;
    
    NumericWidget leftEdge;
    NumericWidget rightEdge;
    NumericWidget topEdge;
    NumericWidget bottomEdge;
    CyclingButtonWidget<MoveByWhat> moveByWhatWidget;
    ButtonWidget resetToDefaults;
    CyclingButtonWidget<Boolean> showBorders;
    
    boolean skipChecks;
    
    float previewModeWidthPercent;
    
    /**
     * Lines which are used and rendered once the user decides to toggle "Show Preview"
     * and is continually updated as the user makes changes to the lines they are working
     * on.
     *
     * <p> It is to be cleared once the user disables "Show preview".
     */
    LineInfoCache[] previewLines;
    
    public HudEdgesEditorScreen(GuiScreen parentScreen) {
        super(parentScreen); this.skipChecks = false; this.previewModeWidthPercent = 0.2f;
    }
    
    @Override
    protected void createWidgets()
    {
        super.createTitles(1);
        super.updateTitleLine(0, I18n.format(LANG_FORMAT + "hudEdges.preferences"));
        
        
        List<Widget> prefs = new ArrayList<>(8);
    
        this.leftEdge = this.of(Preferences.leftBackgroundEdge, "left", this::onLeftEdgeValueChanged);
        this.rightEdge = this.of(Preferences.rightBackgroundEdge, "right", this::onRightEdgeValueChanged);
        this.topEdge = this.of(Preferences.topBackgroundEdge, "top", this::onTopEdgeValueChanged);
        this.bottomEdge = this.of(Preferences.bottomBackgroundEdge, "bottom", this::onBottomEdgeValueChanged);
    
        this.moveByWhatWidget = new CyclingButtonWidget<>(150, 20,
                I18n.format(LANG_FORMAT + "hudEdges.moveBy"), MoveByWhat.INDIVIDUAL,
                ImmutableList.copyOf(MoveByWhat.values()), MoveByWhat::getFormattedText,
                MoveByWhat::getFormattedTextDescription);
        this.resetToDefaults = of("hudEdges.resetToDefaults", this::onResetToDefaultsClick);
        this.showBorders = of("showBorders", Preferences.showBorders, this::onShowBordersUpdate);
        
        prefs.add(this.leftEdge);
        prefs.add(this.rightEdge);
        
        prefs.add(this.topEdge);
        prefs.add(this.bottomEdge);
        
        prefs.add(this.showBorders);
        prefs.add(this.moveByWhatWidget);
        
        prefs.add(this.resetToDefaults);
    
        this.prefsList = new WidgetRowListWidget<>(this, 0, 0, 0, 0, 22, prefs.toArray(new Widget[0]));
    
        this.togglePreviewMode = of("hudEdges.preview", true, this::onPreviewUpdate);
        super.widgets.add(this.togglePreviewMode);
        
        super.listWidgets = new ArrayList<>(1);
        super.listWidgets.add(this.prefsList);
        
        this.updatePreviewLines();
    }
    
    @Override
    protected void resize()
    {
        final int x = this.width / 2;
        final int y = this.height - 26;
        
        // For now, since the option of resizing the preview area is not possible yet by dragging,
        // force it to stick to a particular position so that the user can see both the properties
        // they can modify and the preview as well
        final int idealWidth;
        
        if (this.togglePreviewMode.getValue()) {
            idealWidth = this.width > 310 ? 310 : this.width;
        } else {
            idealWidth = this.width;
        }
        
//        final int idealWidth = this.togglePreviewMode.getValue() ?
//                (this.width - (int)((float)this.width * this.previewModeWidthPercent)) - 2 : this.width;
    
        
        this.prefsList.setDimensions(idealWidth, this.height, 32, this.height - 32);
        
        this.togglePreviewMode.setPos(x - 151, y);
        super.doneButton.setPos(x + 1, y);
        
        this.updatePreviewLinesRenderingPos();
    }
    
    private NumericWidget of(float value, String format, BiConsumer<Double, Double> onValueChange) {
        return new NumericWidget(this.mc.fontRendererObj, 150, 20, value, 0.0f, 100.0f,
                I18n.format(LANG_FORMAT+"hudEdges."+format)).setPostValueChangeListener(onValueChange);
    }
    
    private void onLeftEdgeValueChanged(Double newValue, Double oldValue)
    {
        // Preferences.leftBackgroundEdge = BigDecimal.valueOf(newValue).floatValue();
        this.onEdgeValueChange(newValue, oldValue, HudPositioning.Left);
    }
    private void onRightEdgeValueChanged(Double newValue, Double oldValue)
    {
        // Preferences.rightBackgroundEdge = BigDecimal.valueOf(newValue).floatValue();
        this.onEdgeValueChange(newValue, oldValue, HudPositioning.Right);
    }
    private void onTopEdgeValueChanged(Double newValue, Double oldValue)
    {
        // Preferences.topBackgroundEdge = BigDecimal.valueOf(newValue).floatValue();
        this.onEdgeValueChange(newValue, oldValue, HudPositioning.Top);
    }
    private void onBottomEdgeValueChanged(Double newValue, Double oldValue)
    {
        // Preferences.bottomBackgroundEdge = BigDecimal.valueOf(newValue).floatValue();
        this.onEdgeValueChange(newValue, oldValue, HudPositioning.Bottom);
    }
    
    private boolean onResetToDefaultsClick(ButtonWidget b)
    {
        this.skipChecks = true;
    
        this.leftEdge.setValue(3.0);
        this.rightEdge.setValue(3.0);
        this.topEdge.setValue(2.0);
        this.bottomEdge.setValue(1.0);
    
        this.updatePreviewLinesRenderingPos();
        
        this.skipChecks = false;
        
        return true;
    }
    
    private void onPreviewUpdate(CyclingButtonWidget<Boolean> b)
    {
        this.initGui();
        this.updatePreviewLines();
    }
    
    private void onShowBordersUpdate(CyclingButtonWidget<Boolean> b)
    {
        // Preferences.showBorders = b.getValue();
        this.updatePreviewLinesRenderingPos();
        // MineplexExpHudClientForge.getForgeExpHud().updateLineRenderPosBox();
    }
    
    
    private void onEdgeValueChange(double newValue, double oldValue, HudPositioning from)
    {
        if (this.skipChecks)
            return;
        
        double diff = newValue - oldValue;
        
        switch (this.moveByWhatWidget.getValue())
        {
            case ALL_AT_ONCE: {
                this.onAllEdgesValueChanged(diff, from); break;
            } case AXIS: {
                this.onAxisEdgeValueChanged(diff, from); break;
            }
        }
        
        this.updatePreviewLinesRenderingPos();
    }
    
    private void onAllEdgesValueChanged(double diff, HudPositioning exclude)
    {
        this.skipChecks = true;
    
        for (HudPositioning direction : DIRECTIONS)
        {
            if (direction == exclude)
                continue;
    
            NumericWidget nw = this.getEdgeByDirection(direction);
    
            if (nw == null)
                continue;
    
            nw.setValue(nw.getValue() + diff);
        }
        
        this.skipChecks = false;
    }
    private void onAxisEdgeValueChanged(double diff, HudPositioning current)
    {
        this.skipChecks = true;
        
        HudPositioning opposite = current.getOpposite();
        
        if (opposite != current)
        {
            NumericWidget nw = this.getEdgeByDirection(opposite);
    
            if (nw != null)
                nw.setValue(nw.getValue() + diff);
        }
        
        this.skipChecks = false;
    }
    private NumericWidget getEdgeByDirection(HudPositioning direction)
    {
        switch (direction)
        {
            case Top: return this.topEdge;
            case Left: return this.leftEdge;
            case Right: return this.rightEdge;
            case Bottom: return this.bottomEdge;
            
            default: return null;
        }
    }
    
    void updatePreviewLines()
    {
        if (this.togglePreviewMode.getValue())
        {
            AbstractExpHud hud = MineplexExpHudClient.getInstance().getExpHud();
            
            LineInfoCache[] lines = hud.attemptDisplayFormattingS
                    (hud.getCachedFormatToUse(), false);
            
            if (lines == null)
            {
                lines = hud.attemptDisplayFormattingS(getLang().translate
                        (LANG_FORMAT+"hudEdges.preview.nothingToShow"), false);
            }
            
            this.previewLines = lines;
            
            this.updatePreviewLinesRenderingPos();
        }
        else
        {
            this.previewLines = null;
        }
    }
    void updatePreviewLinesRenderingPos()
    {
        if (this.previewLines == null)
            return;
        
        boolean lastBorderState = Preferences.showBorders;
        float lastLeftEdge = Preferences.leftBackgroundEdge;
        float lastRightEdge = Preferences.rightBackgroundEdge;
        float lastTopEdge = Preferences.topBackgroundEdge;
        float lastBottomEdge = Preferences.bottomBackgroundEdge;
    
        this.applyValuesToPreferences();

        MineplexExpHudClient.getInstance().getExpHud().updateLineRenderPosBox
        (
            this.previewLines,
        
            this.prefsList.right + 8, this.prefsList.top + 4,
            this.width - (this.prefsList.right + 12),
            this.prefsList.bottom - (this.prefsList.top + 8)
        );
    
        Preferences.showBorders = lastBorderState;
        Preferences.leftBackgroundEdge = lastLeftEdge;
        Preferences.rightBackgroundEdge = lastRightEdge;
        Preferences.topBackgroundEdge = lastTopEdge;
        Preferences.bottomBackgroundEdge = lastBottomEdge;
    }
    
    @Override
    protected boolean onDoneClick(ButtonWidget b)
    {
        this.applyValuesToPreferences();
        MineplexExpHudClient.getInstance().getExpHud().updateLineRenderPosBox();
        
        return super.onDoneClick(b);
    }
    
    private void applyValuesToPreferences()
    {
        Preferences.showBorders = this.showBorders.getValue();
        Preferences.leftBackgroundEdge = this.leftEdge.getValueFloat();
        Preferences.rightBackgroundEdge = this.rightEdge.getValueFloat();
        Preferences.topBackgroundEdge = this.topEdge.getValueFloat();
        Preferences.bottomBackgroundEdge = this.bottomEdge.getValueFloat();
    }
    
    @Override
    public void drawScreen(int mouseX, int mouseY, float delta)
    {
        if (this.mc.theWorld == null)
        {
            this.drawBackground(0);
            
            Gui.drawRect(0, this.prefsList.top, this.prefsList.right,
                    this.prefsList.bottom, 0x88000000);
    
            if (this.togglePreviewMode.getValue())
            {
                Gui.drawRect(this.prefsList.right + 4, this.prefsList.top,
                        this.width, this.prefsList.bottom, 0x88000000);
            }
        }
        else
        {
            Gui.drawRect(0, 0, this.width, this.prefsList.top, 0x88000000);
            Gui.drawRect(0, this.prefsList.bottom, this.width, this.height, 0x88000000);
    
            if (this.togglePreviewMode.getValue())
            {
                Gui.drawRect(this.prefsList.right, this.prefsList.top,
                        this.prefsList.right + 4, this.prefsList.bottom, 0x88000000);
            }
        }
    
        // Copied from DisplayFormatPreferencesScreen with some changes
        if (this.togglePreviewMode.getValue() && this.previewLines != null)
        {
            ForgeRenderContext ctx = MineplexExpHudClientForge.getCtx();
        
            ctx.createScissor
            (
                this.prefsList.right + 4, this.prefsList.top,
                this.width - (this.prefsList.right + 4),
                this.prefsList.bottom - this.prefsList.top
            );
    
            boolean lastBorderState = Preferences.showBorders;
    
            Preferences.showBorders = this.showBorders.getValue();
            
            // Why not use the same method of rendering as it relies more on the line and text
            // positioning more than anything else?
            MineplexExpHudClientForge.getForgeExpHud().render(this.previewLines, ctx, delta);
    
            Preferences.showBorders = lastBorderState;
            
            ctx.removeScissor();
        }
        
        super.drawScreen(mouseX, mouseY, delta);
    }
    
    enum MoveByWhat
    {
        ALL_AT_ONCE,
        AXIS,
        INDIVIDUAL;
    
        public static String getFormattedText(MoveByWhat a)
        {
            switch (a)
            {
                case ALL_AT_ONCE:
                    return I18n.format(LANG_FORMAT + "hudEdges.all_at_once");
                case AXIS:
                    return I18n.format(LANG_FORMAT + "hudEdges.axis");
                case INDIVIDUAL:
                    return I18n.format(LANG_FORMAT + "hudEdges.individual");
            }
        
            return "";
        }
        public static String getFormattedTextDescription(MoveByWhat a)
        {
            switch (a)
            {
                case ALL_AT_ONCE:
                    return I18n.format(LANG_FORMAT + "hudEdges.all_at_once.description");
                case AXIS:
                    return I18n.format(LANG_FORMAT + "hudEdges.axis.description");
                case INDIVIDUAL:
                    return I18n.format(LANG_FORMAT + "hudEdges.individual.description");
            }
        
            return "";
        }
    }
}
