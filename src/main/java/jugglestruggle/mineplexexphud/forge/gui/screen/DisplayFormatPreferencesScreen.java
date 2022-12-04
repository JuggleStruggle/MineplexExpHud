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

import jugglestruggle.mineplexexphud.AbstractExpHud;
import jugglestruggle.mineplexexphud.MineplexExpHudClient;
import jugglestruggle.mineplexexphud.forge.ForgeRenderContext;
import jugglestruggle.mineplexexphud.forge.MineplexExpHudClientForge;
import jugglestruggle.mineplexexphud.forge.gui.widget.ButtonRowListWidget;
import jugglestruggle.mineplexexphud.forge.gui.widget.ButtonWidget;
import jugglestruggle.mineplexexphud.forge.gui.widget.CyclingButtonWidget;
import jugglestruggle.mineplexexphud.forge.gui.widget.NumericWidget;
import jugglestruggle.mineplexexphud.forge.gui.widget.TextWidget;
import jugglestruggle.mineplexexphud.forge.gui.widget.Widget;
import jugglestruggle.mineplexexphud.forge.gui.widget.WidgetRowListWidget;
import jugglestruggle.mineplexexphud.hud.info.LineInfoCache;
import jugglestruggle.mineplexexphud.pref.Preferences;
import jugglestruggle.util.TextUtility;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import org.lwjgl.input.Keyboard;

import java.math.BigDecimal;
import java.util.ArrayList;


import static jugglestruggle.mineplexexphud.MineplexExpHudClient.LANG_FORMAT;
import static jugglestruggle.mineplexexphud.MineplexExpHudClient.getLang;

public class DisplayFormatPreferencesScreen extends BaseEditScreen
{
    public static final AvailableFormat[] FORMATTERS_AVAILABLE;
    
    static
    {
        FORMATTERS_AVAILABLE = new AvailableFormat[]
        {
            of(1, "currentExp"),
            of(2, "expUntilNextLevel"),
            of(3, "currentLevel"),
            of(4, "nextLevel"),
            of(5, "expPercentageUntilNextLevel"),
            of(6, "updateType"),
            of(7, "expLevelsGainedSetTimeLeft"),
            of(8, "levelGainedInSession"),
            of(9, "expGainedInSession"),
            of(10, "levelGainedInSetTime"),
            of(11, "expGainedInSetTime"),
            of(12, "guessAccumulatedExp"),
            of(13, "guessAccumulatedExpPercentage")
        };
    }
    
    private static AvailableFormat of(int id, String desc)
    {
        return new AvailableFormat()
        {
            @Override public int matchId() { return id; }
            @Override public String descTranslation() { return desc; }
        };
    }
    
    
    
    
    DisplayStringList displayStringList;
    AvailableFormattersList availableFormattersList;
    CyclingButtonWidget<Boolean> togglePreviewMode;
    CyclingButtonWidget<Boolean> toggleFormattersListView;
    NumericWidget lineToShowProgress;
    
    float previewModeWidthPercent = 0.5f;
    float displayStringListPercentage = 0.8f;
    
    /**
     * Lines which are used and rendered once the user decides to toggle "Show Preview"
     * and is continually updated as the user makes changes to the lines they are working
     * on.
     *
     * <p> It is to be cleared once the user disables "Show preview".
     */
    LineInfoCache[] previewLines;
    
    private long lineToShowProgressValue;
    private long lineToShowProgressUserValue;
    private boolean lineToShowProgressApplyUserValue;
    
    public DisplayFormatPreferencesScreen(GuiScreen parentScreen) {
        super(parentScreen);
    }
    
    @Override
    protected void createWidgets()
    {
        super.createTitles(2);
        super.updateTitleLine(0, I18n.format(LANG_FORMAT + "displayFormatting.preferences"));
    
        this.displayStringList = new DisplayStringList(this);
        this.availableFormattersList = new AvailableFormattersList(this);
        this.availableFormattersList.visible = false;
    
        super.listWidgets = new ArrayList<>(2);
        super.listWidgets.add(this.displayStringList);
        super.listWidgets.add(this.availableFormattersList);
    
        this.togglePreviewMode = CyclingButtonWidget.bool(150, 20,
                I18n.format(LANG_FORMAT+"displayFormatting.preview"),
                false, (byte)0).setPostValueChangeListener(this::onPreviewUpdate);
        this.toggleFormattersListView = CyclingButtonWidget.bool(150, 20,
                I18n.format(LANG_FORMAT+"displayFormatting.showAvailableFormatters"),
                false, (byte)1).setValueChangeListener(this::onShowFormatterListViewUpdate);
    
        this.lineToShowProgressValue = Preferences.lineToShowProgress;
        this.lineToShowProgressUserValue = this.lineToShowProgressValue + 1L;
        this.lineToShowProgressApplyUserValue = true;
    
        this.lineToShowProgress = new NumericWidget(this.mc.fontRendererObj, 150, 20,
                this.lineToShowProgressUserValue,0L, (long)Integer.MAX_VALUE + 1L,
                I18n.format(LANG_FORMAT+"lineToShowProgress"));
    
        this.lineToShowProgress.setValueChangeListener(this::onLineToShowProgressValueChange);
        
        this.updateLineAdditionAndRemovals();
        
        super.widgets.add(this.togglePreviewMode);
        super.widgets.add(this.toggleFormattersListView);
        super.widgets.add(this.lineToShowProgress);
    }
    
    @Override
    protected void resize()
    {
        Keyboard.enableRepeatEvents(true);
    
        // Update the location and the size of the widgets and also add any buttons that were removed along the way
    
        int idealHeight = this.height - 54;
    
        int idealWidth = this.togglePreviewMode.getValue() ?
                (this.width - (int)((float)this.width * this.previewModeWidthPercent)) - 2: this.width;
        int listsSHeight = this.availableFormattersList.visible ?
                (int)((float)idealHeight * this.displayStringListPercentage) - 2: idealHeight;
    
        if (this.availableFormattersList.visible)
        {
            this.displayStringList.setDimensions(idealWidth, listsSHeight, 32, listsSHeight);
    
            this.availableFormattersList.setDimensions
            (
                idealWidth - 2, idealHeight - listsSHeight + 2,
                listsSHeight + 2, idealHeight
            );
        }
        else
            this.displayStringList.setDimensions(idealWidth, this.height, 32, listsSHeight);
    
    
        final int x = this.width / 2;
        final int y = this.height - 26;
    
        this.toggleFormattersListView.setPos(x - 151, y - 22);
        this.lineToShowProgress.setPos(x + 1, y - 22);
        this.togglePreviewMode.setPos(x - 151, y);
        super.doneButton.setPos(x + 1, y);
    
        this.updatePreviewLinesRenderingPos();
    }
    
    @Override
    protected boolean onEscapeKeyLeaves() {
        return true;
    }
    
    @Override
    public void onGuiClosed()
    {
        Keyboard.enableRepeatEvents(false);
        MineplexExpHudClientForge.getForgeInstance().writeToFile();
        
        super.onGuiClosed();
    }
    
    private void onPreviewUpdate(CyclingButtonWidget<Boolean> b) {
        this.initGui(); this.updatePreviewLines();
    }
    private boolean onShowFormatterListViewUpdate(CyclingButtonWidget<Boolean> b, Boolean v) {
        this.availableFormattersList.visible = v; this.initGui(); return true;
    }
    
    private boolean onLineToShowProgressValueChange(Double newValue, Double oldValue)
    {
        BigDecimal newValueD = BigDecimal.valueOf(newValue);
        
        this.lineToShowProgressValue = newValueD.subtract(BigDecimal.ONE).longValue();
        
        if (this.lineToShowProgressApplyUserValue)
            this.lineToShowProgressUserValue = newValueD.longValue();
        
        return true;
    }
    
    @Override
    protected boolean onDoneClick(ButtonWidget b)
    {
        this.applyValuesToPreferences();
        MineplexExpHudClientForge.getForgeExpHud().rebuildDisplayCacheInfo();
        
        return super.onDoneClick(b);
    }
    
    private String validateAndCreateSingleLineFormatting()
    {
        StringBuilder b = new StringBuilder();
        
        final DisplayStringEntry[] entries = this.displayStringList.getEntries();
        final int entrySize = entries.length;
        
        int acceptedEntries = 0;
        
        for (int i = 0; i < entrySize; ++i)
        {
            DisplayStringEntry dse = entries[i];
            
            if (dse.validFormat)
            {
                if (acceptedEntries > 0)
                    b.append("\\n");
                
                // b.append(dse.formatBox.getText());
                b.append(dse.getFormatBoxParsedText());
                
                ++acceptedEntries;
            }
        }
        
        return b.toString();
    }
    
 
    
    void updateLineAdditionAndRemovals()
    {
        final int rowSize = this.displayStringList.getSize();
        float val = (float)this.displayStringList.rowsWithValidFormats / (float)rowSize;
        
        if (Float.isNaN(val))
            val = 0.0f;
        
        super.updateTitleLine
        (
            1, I18n.format
            (
                LANG_FORMAT + "displayFormatting.validFormats",
                this.displayStringList.rowsWithValidFormats, rowSize,
                MineplexExpHudClientForge.getForgeInstance().getExpHud()
                    .progressPercentageFormat.format(val * 100.0f)
            )
        );
        
        this.updatePreviewLines();
        this.updateLineToShowProgressWidgetMaximum();
    }
    void updateLineToShowProgressWidgetMaximum()
    {
        if (this.lineToShowProgress == null)
            return;
    
        if (this.displayStringList.rowsWithValidFormats <= 0) {
            this.lineToShowProgress.setMax(Integer.MAX_VALUE + 1L);
        } else {
            this.lineToShowProgress.setMax(this.displayStringList.rowsWithValidFormats);
        }
    
        this.lineToShowProgressApplyUserValue = false;
    
        final long currentMax = this.lineToShowProgress.getMaxLong();
    
        this.lineToShowProgress.setValue(this.lineToShowProgressUserValue <= currentMax ?
                this.lineToShowProgressUserValue : currentMax);
    
        this.lineToShowProgressApplyUserValue = true;
    }
    void updatePreviewLines()
    {
        // Just how many failsafes do I have to make in order for it not to crash
        // whenever initializing the menu?
        if (this.togglePreviewMode == null)
            return;
        
        if (this.togglePreviewMode.getValue())
        {
            AbstractExpHud hud = MineplexExpHudClient.getInstance().getExpHud();
            
            LineInfoCache[] lines = hud.attemptDisplayFormattingS
                    (this.validateAndCreateSingleLineFormatting(), false);
            
            if (lines == null)
            {
                lines = hud.attemptDisplayFormattingS(getLang().translate
                        (LANG_FORMAT+"displayFormatting.preview.nothingToShow"), false);
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
    
        final int idealWidth = (this.width - (int)((float)this.width * this.previewModeWidthPercent));
        final int idealBottom = this.availableFormattersList.visible ?
                this.availableFormattersList.bottom : this.displayStringList.bottom;
        
        MineplexExpHudClient.getInstance().getExpHud().updateLineRenderPosBox
        (
            this.previewLines,
            idealWidth + 6, this.displayStringList.top + 4,
            this.width - (idealWidth + 10),
            idealBottom - (this.displayStringList.top + 8)
        );
    }
    
    
    private void applyValuesToPreferences()
    {
        Preferences.displayFormatting = this.validateAndCreateSingleLineFormatting();
        Preferences.lineToShowProgress = (int)this.lineToShowProgressValue;
    }
    
    
    @Override
    public void drawScreen(int mouseX, int mouseY, float delta)
    {
        final int idealWidth = this.togglePreviewMode.getValue() ?
                (this.width - (int)((float)this.width * this.previewModeWidthPercent)) - 2 : this.width;
        final int idealBottom = this.availableFormattersList.visible ?
                this.availableFormattersList.bottom : this.displayStringList.bottom;
        
        if (this.mc.theWorld == null)
        {
            this.drawBackground(0);
            
            Gui.drawRect(0, this.displayStringList.top, this.displayStringList.right,
                    this.displayStringList.bottom, 0x88000000);
    
            if (this.availableFormattersList.visible)
                Gui.drawRect(0, this.availableFormattersList.top, idealWidth, this.availableFormattersList.bottom, 0x88000000);
            
            if (this.togglePreviewMode.getValue())
                Gui.drawRect(idealWidth + 4, this.displayStringList.top, this.width, idealBottom, 0x88000000);
        }
        else
        {
            Gui.drawRect(0, 0, this.width, this.displayStringList.top, 0x88000000);
    
            if (this.availableFormattersList.visible)
            {
                Gui.drawRect(0, this.displayStringList.bottom, idealWidth, this.availableFormattersList.top, 0x88000000);
                Gui.drawRect(0, this.availableFormattersList.top, idealWidth, this.availableFormattersList.bottom, 0xDD111111);
            }
    
            Gui.drawRect(0, idealBottom, this.width, this.height, 0x88000000);
    
            if (this.togglePreviewMode.getValue())
                Gui.drawRect(idealWidth, this.displayStringList.top, idealWidth + 4, idealBottom, 0x88000000);
        }
        
        if (this.togglePreviewMode.getValue() && this.previewLines != null)
        {
            ForgeRenderContext ctx = MineplexExpHudClientForge.getCtx();
    
            ctx.createScissor
            (
                idealWidth + 4, this.displayStringList.top,
                this.width - (idealWidth + 4), idealBottom - this.displayStringList.top
            );
            
            int previousLineToShowProgress = Preferences.lineToShowProgress;
            Preferences.lineToShowProgress = (int)this.lineToShowProgressValue;
            
            // Why not use the same method of rendering as it relies more on the line and text
            // positioning more than anything else?
            MineplexExpHudClientForge.getForgeExpHud().render(this.previewLines, ctx, delta);
            
            Preferences.lineToShowProgress = previousLineToShowProgress;
            
            ctx.removeScissor();
        }
        
        super.drawScreen(mouseX, mouseY, delta);
    }
    
    interface AvailableFormat
    {
        int matchId();
        String descTranslation();
        
        
        default String getFormat() {
            return "%" + this.matchId() + "$s";
            // return Matcher.quoteReplacement("%" + this.matchId() + "$s");
        }
        default String getDesc() {
            return I18n.format(LANG_FORMAT + "displayFormatting." + this.descTranslation());
        }
        default String getExampleDesc() {
            return I18n.format(LANG_FORMAT + "displayFormatting." + this.descTranslation() + ".example");
        }
    }
    
    static class DisplayStringList extends WidgetRowListWidget<Widget>
    {
        int rowsWithValidFormats;
        
        public DisplayStringList(DisplayFormatPreferencesScreen owningScreen)
        {
            super(owningScreen, 0, 0, 0, 0, 20, null, null);
            this.repositionWidgetsX = false;

            this.rowsWithValidFormats = 0;
            
            if (Preferences.displayFormatting.trim().isEmpty())
            {
                super.entries.add(DisplayStringEntry.of(this, null, false));
            }
            else
            {
                String[] lines = Preferences.displayFormatting.split("\\\\n");

                for (String line : lines)
                {
                    if (line.isEmpty())
                        continue;

                    super.entries.add(DisplayStringEntry.of(this, line, false));
                }
            }
            
            for (WidgetEntry<Widget> dse : super.entries)
                if (((DisplayStringEntry)dse).validFormat)
                    ++this.rowsWithValidFormats;
        }
        
        void add(DisplayStringEntry reference, String startingFormat, boolean fromDuplicate)
        {
            int index = super.entries.indexOf(reference);
            
            DisplayStringEntry dse = DisplayStringEntry.of(this, startingFormat, fromDuplicate);
            
            if (index == -1 || super.entries.isEmpty())
                super.entries.add(dse);
            else
            {
                // Adds it above the referenced entry
                if (GuiScreen.isShiftKeyDown() && index > 0)
                    super.entries.add(index, dse);
                // Adds it below the referenced entry
                else if (index + 1 < super.entries.size())
                    super.entries.add(index + 1, dse);
                // Just add it into the end if both checks failed
                else
                    super.entries.add(dse);
            }
            
            if (dse.validFormat)
                ++this.rowsWithValidFormats;
            
            this.updateValidLines();
            this.updateWidgetLocations();
        }

        void remove(DisplayStringEntry entry)
        {
            int index = super.entries.indexOf(entry);
            
            if (index >= 0)
            {
                if (entry.validFormat)
                    --this.rowsWithValidFormats;
                
                super.entries.remove(index);
            }
            
            if (super.entries.isEmpty())
                this.add(null, null, false);
            else
            {
                this.updateValidLines();
                this.updateWidgetLocations();
            }
        }
        
        public void updateValidLines() {
            ((DisplayFormatPreferencesScreen)super.owningScreen).updateLineAdditionAndRemovals();
        }
        public void updatePreviewLines() {
            ((DisplayFormatPreferencesScreen)super.owningScreen).updatePreviewLines();
        }
        
        public DisplayStringEntry getActiveEntry()
        {
            for (WidgetEntry<Widget> entry : super.entries)
                if (entry.isFocused())
                    return (DisplayStringEntry)entry;
            
            return null;
        }
    
        @Override
        public int getListWidth() {
            return this.width;
        }
    
        @Override
        protected int getScrollBarX() {
            return this.width - 6;
        }
    
        public DisplayStringEntry[] getEntries() {
            return super.entries.toArray(new DisplayStringEntry[0]);
        }
    }
    static class DisplayStringEntry extends WidgetRowListWidget.WidgetEntry<Widget>
    {
        public static DisplayStringEntry of(DisplayStringList owner, String startingFormat, boolean fromDuplicate)
        {
            TextWidget formatText = new TextWidget(owner.getClient().fontRendererObj, 144, 12);
            ButtonWidget add       = new ButtonWidget(18, 18, "+", null);
            ButtonWidget duplicate = new ButtonWidget(18, 18, "++", null);
            ButtonWidget reset     = new ButtonWidget(18, 18, "\u21BA", null);
            ButtonWidget remove    = new ButtonWidget(18, 18, "X", null);
            
            DisplayStringEntry dse = new DisplayStringEntry(owner, formatText, add, duplicate, reset, remove);
    
            dse.setInitialFormatBoxText(startingFormat, fromDuplicate);
    
            formatText.setTextChangedListener(dse::onFormatTextUpdate);
            formatText.setCursorPositionZero();
            
            dse.performValidFormattingCheck(formatText.getText(), false);
            
            return dse;
        }
        
        final TextWidget formatBox;
        final ButtonWidget add;
        final ButtonWidget duplicate;
        final ButtonWidget reset;
        final ButtonWidget remove;
        
        String startingText;
        
        boolean validFormat;
        byte invalidFormatTimer;
        
        private DisplayStringEntry(DisplayStringList owner, TextWidget formatBox, ButtonWidget add,
                                   ButtonWidget duplicate, ButtonWidget reset, ButtonWidget remove)
        {
            super(owner, new Widget[]{formatBox, add, duplicate, reset, remove});
    
            this.formatBox = formatBox;
    
            this.add = add; this.duplicate = duplicate;
            this.reset = reset; this.remove = remove;
    
            this.add.setOnClick(this::onAddClick);
            this.duplicate.setOnClick(this::onDuplicateClick);
            this.reset.setOnClick(this::onResetClick);
            this.remove.setOnClick(this::onRemoveClick);
    
            this.add.setTooltipText(I18n.format(LANG_FORMAT + "displayFormatting.add"));
            this.duplicate.setTooltipText(I18n.format(LANG_FORMAT + "displayFormatting.duplicate"));
            this.reset.setTooltipText(I18n.format(LANG_FORMAT + "displayFormatting.reset"));
            this.remove.setTooltipText(I18n.format(LANG_FORMAT + "displayFormatting.remove"));
    
            this.add.renderWithTextures = this.duplicate.renderWithTextures =
            this.reset.renderWithTextures = this.remove.renderWithTextures = false;
            
            this.formatBox.setMaxStringLength(32000);
            
            this.formatBox.setPosOffset(0, 3);
            this.formatBox.setEnableBackgroundDrawing(false);
        }
        
        public boolean onAddClick(ButtonWidget b)
        {
            ((DisplayStringList)super.rowList).add(this, null, false);
            return true;
        }
        
        public boolean onDuplicateClick(ButtonWidget b)
        {
            String selText = this.formatBox.getSelectedText();
            
            ((DisplayStringList)super.rowList).add(this,
                    selText.trim().isEmpty() ? this.formatBox.getText() : selText, true);
            
            return true;
        }
        
        public boolean onResetClick(ButtonWidget b)
        {
            this.formatBox.setText(this.startingText);
            this.formatBox.setCursorPositionZero();
            
            return true;
        }
        
        public boolean onRemoveClick(ButtonWidget b)
        {
            ((DisplayStringList)super.rowList).remove(this);
            return true;
        }
    
    
    
    
        public String getFormatBoxText() {
            return this.formatBox.getText();
        }
        
        private void setInitialFormatBoxText(String text, boolean avoidUnapplyingFormats)
        {
            if (text == null)
            {
                this.startingText = "";
            }
            else
            {
                this.startingText = avoidUnapplyingFormats ? text : unapplyExtraFormats(text);
                this.formatBox.setText(this.startingText);
            }
        }
        public String getFormatBoxParsedText() {
            return applyExtraFormats(this.getFormatBoxText());
        }
    
        public static String applyExtraFormats(String text)
        {
            for (char c : TextUtility.CHAT_COLORS_AND_FORMATS)
            {
                text = text.replaceAll("(?<!&)&"+c, "§"+c);
                text = text.replaceAll("&&"+c, "&"+c);
            }
        
            return text;
        }
        public static String unapplyExtraFormats(String text)
        {
            for (char c : TextUtility.CHAT_COLORS_AND_FORMATS)
            {
                text = text.replaceAll("&"+c, "&&"+c);
                text = text.replaceAll("§"+c, "&"+c);
            }
            
            return text;
        }
        
        
        
        public void onFormatTextUpdate(String newText, String oldText) {
            this.performValidFormattingCheck(newText, true);
        }
        public void performValidFormattingCheck(String newText, boolean doNotSkipValidationChange)
        {
            LineInfoCache[] linesMade = MineplexExpHudClient.getInstance().getExpHud().attemptDisplayFormattingS(newText, false);
            boolean isValidFormatting = linesMade != null && linesMade.length > 0;
    
            this.formatBox.setTextColor(isValidFormatting ? 14737632 : 0xFF8888);
            
            int textColor = isValidFormatting ? 0xFFFFFF : 0xFF8888;
            
            this.add.textColor = textColor;
            this.reset.textColor = textColor;
            this.duplicate.textColor = textColor;
            this.remove.textColor = textColor;
            
            boolean validationChanged = isValidFormatting != this.validFormat;
            
            this.validFormat = isValidFormatting;
    
            DisplayStringList dsl = ((DisplayStringList)super.rowList);

            if (validationChanged && doNotSkipValidationChange)
            {
                if (this.validFormat)
                    ++dsl.rowsWithValidFormats;
                else
                    --dsl.rowsWithValidFormats;
                
                dsl.updateValidLines();
            }
            else
            {
                dsl.updatePreviewLines();
            }
        }
        
        
        @Override
        public void drawEntry(int slotIndex, int x, int y, int listWidth, int slotHeight, int mouseX, int mouseY, boolean isSelected)
        {
            int fillColor;
            int lineColor;
            
            if (this.validFormat)
            {
                fillColor = -16777216;
                lineColor = -6250336;
            }
            else
            {
                final int ab = (this.invalidFormatTimer % 20);
                
                Gui.drawRect(x - 1, y - 1, x + listWidth - 5, y + slotHeight + 3,
                        ((180 - ab * 8) << 24) | 0xFF0000);
                
                fillColor = ((200 - ab * 4) << 24) | 0x440000;
                lineColor = ((220 - ab * 6) << 24) | 0xDD4444;
            }
            
            MineplexExpHudClientForge.getCtx().fillWithWireframe
            (
                this.formatBox.getX() - 2, this.formatBox.getY() - 2,
                this.formatBox.getXR() + 2 , this.formatBox.getYB() + 2,
                
                fillColor, lineColor
            );
            
            super.drawEntry(slotIndex, x, y, listWidth, slotHeight, mouseX, mouseY, isSelected);
        }
        
        
        @Override
        public void updateWidgetLocations()
        {
            int r = super.rowList.width - 90;
            
            if (super.rowList.func_148135_f() > 0)
                r -= 8;
            
            this.formatBox.setX(8);
            this.formatBox.setW(r - 8);
            
            this.add.setX(r + 6);
            this.duplicate.setX(r + 26);
            this.reset.setX(r + 46);
            this.remove.setX(r + 66);
        }
        
        @Override
        public void tick()
        {
            super.tick();
            
            if (this.validFormat)
                this.invalidFormatTimer = 0;
            else if (this.invalidFormatTimer < 100)
                ++this.invalidFormatTimer;
        }
    }
    
    
    static class AvailableFormattersList extends ButtonRowListWidget<ButtonWidget>
    {
        public AvailableFormattersList(DisplayFormatPreferencesScreen screen)
        {
            super(screen, 0, 0, 0, 0, 24, null, null);
    
            for (AvailableFormat af : DisplayFormatPreferencesScreen.FORMATTERS_AVAILABLE) {
                super.entries.add(AvailableFormattersEntry.of(this, af));
            }
        }
    
        @Override
        public int getListWidth() {
            return this.width;
        }
    
        @Override
        protected int getScrollBarX() {
            return this.width - 6;
        }
    }
    static class AvailableFormattersEntry extends WidgetRowListWidget.WidgetEntry<ButtonWidget>
    {
        public static AvailableFormattersEntry of(AvailableFormattersList owner, AvailableFormat af)
        {
            ButtonWidget useFormatter = new ButtonWidget(60, 20, ">> "+ af.matchId() + " <<", null);
            CyclingButtonWidget<Boolean> toggleExampleView = CyclingButtonWidget.bool
                    (20, 20, "", false, "<", ">");
            
            useFormatter.setTooltipText(I18n.format(LANG_FORMAT + "displayFormatting.useFormat", af.getDesc()));
            toggleExampleView.setTooltipText(I18n.format(LANG_FORMAT + "displayFormatting.examples.show"));
            toggleExampleView.setDisplayFormat("%2$s");
            
            AvailableFormattersEntry afe = new AvailableFormattersEntry(owner, useFormatter, toggleExampleView, af);
            useFormatter.setOnClick(afe::onFormatterUseClick);
            toggleExampleView.setValueChangeListener(afe::onExampleViewToggle);
            
            return afe;
        }
        
        
        final AvailableFormat af;
        
        String fullText;
        String displayedText;
        
        final ButtonWidget useFormatter;
        final CyclingButtonWidget<Boolean> toggleExampleView;
        
        private AvailableFormattersEntry(AvailableFormattersList owner,  ButtonWidget useFormatter,
                                         CyclingButtonWidget<Boolean> toggleExampleView, AvailableFormat af)
        {
            super(owner, useFormatter, toggleExampleView);
            this.af = af;
            
            this.useFormatter = useFormatter;
            this.toggleExampleView = toggleExampleView;
            
            this.fullText = this.displayedText = af.getDesc();
        }
        
        @Override
        public void updateWidgetLocations()
        {
            int listWidth = super.rowList.width - 98;
            
            if (super.rowList.func_148135_f() > 0)
                listWidth -= 8;
            
            this.useFormatter.setX(2);
            this.toggleExampleView.setX(this.useFormatter.getW() + 4);
            
            
            final FontRenderer fr = super.rowList.owningScreen.mc.fontRendererObj;
            
            int textWidth = fr.getStringWidth(this.fullText);
            
            if (textWidth > listWidth) {
                this.displayedText = fr.trimStringToWidth(this.fullText, listWidth);
            } else {
                this.displayedText = this.fullText;
            }
        }
        
        public boolean onFormatterUseClick(ButtonWidget b)
        {
            DisplayStringEntry activeEntry =
            ((DisplayFormatPreferencesScreen)super.rowList.owningScreen).displayStringList.getActiveEntry();
            
            if (activeEntry != null && activeEntry.formatBox.isActuallyFocused())
                activeEntry.formatBox.writeText(this.af.getFormat());
            
            return true;
        }
        
        public boolean onExampleViewToggle(CyclingButtonWidget<Boolean> b, Boolean v)
        {
            this.fullText = v ? this.af.getExampleDesc() : this.af.getDesc();
            
            b.setTooltipText(I18n.format(LANG_FORMAT + "displayFormatting.examples."+(v ? "hide" : "show")));
            this.updateWidgetLocations();
            
            return true;
        }
        
        @Override
        public void drawEntry(int slotIndex, int x, int y, int listWidth, int slotHeight, int mouseX, int mouseY, boolean isSelected)
        {
            super.drawEntry(slotIndex, x, y, listWidth, slotHeight, mouseX, mouseY, isSelected);
            
            super.rowList.owningScreen.mc.fontRendererObj.drawString
                    (this.displayedText, this.toggleExampleView.getXR() + 4, y + 6, 0xFFFFFFFF);
        }
    }
}
