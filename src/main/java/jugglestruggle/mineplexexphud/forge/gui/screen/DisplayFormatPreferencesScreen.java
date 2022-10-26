package jugglestruggle.mineplexexphud.forge.gui.screen;

import jugglestruggle.mineplexexphud.MineplexExpHudClient;
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
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import org.lwjgl.input.Keyboard;

import java.util.ArrayList;


import static jugglestruggle.mineplexexphud.MineplexExpHudClient.LANG_FORMAT;

public class DisplayFormatPreferencesScreen extends Screen
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
    ButtonWidget doneButton;
    
    float previewModeWidthPercent = 0.5f;
    float displayStringListPercentage = 0.8f;
    
    boolean valid;
    
    String[] titles;
    int[] titlesWidth;
    
    public DisplayFormatPreferencesScreen(GuiScreen parentScreen) {
        super(parentScreen); this.valid = true;
    }
    
    @Override
    public void initGui()
    {
        if (!super.screenCreated)
        {
            this.titles = new String[2];
            this.titlesWidth = new int[2];
            this.updateTitleLine(0, I18n.format(LANG_FORMAT + "displayFormatting.preferences"));
    
            this.displayStringList = new DisplayStringList(this);
            this.availableFormattersList = new AvailableFormattersList(this);
            this.availableFormattersList.visible = false;
    
            super.listWidgets = new ArrayList<>(2);
            super.listWidgets.add(this.displayStringList);
            super.listWidgets.add(this.availableFormattersList);
            
            this.togglePreviewMode = CyclingButtonWidget.bool(150, 20,
                    I18n.format(LANG_FORMAT+"displayFormatting.preview"),
                    false, (byte)0).setValueChangeListener(this::onPreviewUpdate);
            this.toggleFormattersListView = CyclingButtonWidget.bool(150, 20,
                    I18n.format(LANG_FORMAT+"displayFormatting.showAvailableFormatters"),
                    false, (byte)1).setValueChangeListener(this::onShowFormatterListViewUpdate);
   
            this.togglePreviewMode.setTooltipText(I18n.format(LANG_FORMAT +
                    "displayFormatting.preview.notimplemented"));
            
            this.togglePreviewMode.enabled = false;
//            this.toggleFormattersListView.enabled = false;
            
            this.lineToShowProgress = new NumericWidget(this.mc.fontRendererObj, 150, 20,
                    Preferences.lineToShowProgress + 1,0, Integer.MAX_VALUE - 1,
                    I18n.format(LANG_FORMAT+"lineToShowProgress"));
            
            this.lineToShowProgress.setOnValueChange((newValue, o) -> {
                Preferences.lineToShowProgress = (int)(double)newValue - 1; return true;
            });
    
            this.doneButton = new ButtonWidget(150, 20, I18n.format("jugglestruggle.saveandreturn"), this::onDoneClick);
            
            super.widgets.add(this.togglePreviewMode);
            super.widgets.add(this.toggleFormattersListView);
            super.widgets.add(this.doneButton);
            super.widgets.add(this.lineToShowProgress);
    
            super.screenCreated = true;
        }
    
        Keyboard.enableRepeatEvents(true);
    
        // Update the location and the size of the widgets and also add any buttons that were removed along the way
    
        int idealHeight = this.height - 54;
    
        int listsWidth = this.togglePreviewMode.getValue() ?
                (int)((float)this.width * this.previewModeWidthPercent) : this.width;
        int listsSHeight = this.availableFormattersList.visible ?
                (int)((float)idealHeight * this.displayStringListPercentage) : idealHeight;
    
        if (this.availableFormattersList.visible)
            this.displayStringList.setDimensions(listsWidth, listsSHeight, 32, listsSHeight - 2);
        else
            this.displayStringList.setDimensions(listsWidth, this.height, 32, listsSHeight);
            
    
        if (this.availableFormattersList.visible)
        {
            int sHeight = idealHeight - listsSHeight;
            
            this.availableFormattersList.setDimensions
            (
                listsWidth, sHeight,
                listsSHeight + 2, idealHeight
            );
        }
    
        int x = this.width / 2;
        int y = this.height - 24;
    
        this.toggleFormattersListView.setPos(x - 151, y - 22);
        this.lineToShowProgress.setPos(x + 1, y - 22);
        this.togglePreviewMode.setPos(x - 151, y);
        this.doneButton.setPos(x + 1, y);
        
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
    
    private boolean onPreviewUpdate(CyclingButtonWidget<Boolean> b, Boolean v) {
        return false;
    }
    private boolean onShowFormatterListViewUpdate(CyclingButtonWidget<Boolean> b, Boolean v) {
        this.availableFormattersList.visible = v; this.initGui(); return true;
    }
    
    private boolean onDoneClick(ButtonWidget b)
    {
        this.validateAndSaveFormatting();
        MineplexExpHudClientForge.getForgeExpHud().rebuildDisplayCacheInfo();
        
        this.mc.displayGuiScreen(this.parentScreen);
        
        return true;
    }
    
    private void validateAndSaveFormatting()
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
                
                b.append(dse.formatBox.getText());
                
                ++acceptedEntries;
            }
        }
     
        
        
        Preferences.displayFormatting = b.toString();
    }
    
    
    
    
    @Override
    public void drawScreen(int mouseX, int mouseY, float delta)
    {
        if (this.mc.theWorld == null)
        {
            this.drawBackground(0);
            
            Gui.drawRect(0, this.displayStringList.top, this.width, this.displayStringList.bottom, 0x88000000);
    
            if (this.availableFormattersList.visible)
                Gui.drawRect(0, this.availableFormattersList.top, this.width, this.availableFormattersList.bottom, 0x88000000);
        }
        else
        {
            Gui.drawRect(0, 0, this.width, this.displayStringList.top, 0x88000000);
    
            if (this.availableFormattersList.visible)
            {
                Gui.drawRect(0, this.displayStringList.bottom, this.width, this.availableFormattersList.top, 0x88000000);
                Gui.drawRect(0, this.availableFormattersList.top, this.width, this.availableFormattersList.bottom, 0xDD111111);
                Gui.drawRect(0, this.availableFormattersList.bottom, this.width, this.height, 0x88000000);
            }
            else
            {
                Gui.drawRect(0, this.displayStringList.bottom, this.width, this.height, 0x88000000);
            }
        }
        
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
    
    private void updateTitleLine(int i, String text)
    {
        this.titles[i] = text;
        this.titlesWidth[i] = this.mc.fontRendererObj.getStringWidth(text);
    }
    
    interface AvailableFormat
    {
        int matchId();
        String descTranslation();
        
        
        default String getFormat() {
            return "%" + this.matchId() + "$s";
//            return Matcher.quoteReplacement("%" + this.matchId() + "$s");
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
            super(owningScreen, 0, 0, 0, 0, 24, null, null);
            this.repositionWidgetsX = false;

            this.rowsWithValidFormats = 0;
            
            if (Preferences.displayFormatting.trim().isEmpty())
            {
                super.entries.add(DisplayStringEntry.of(this, null));
            }
            else
            {
                String[] lines = Preferences.displayFormatting.split("\\\\n");

                for (String line : lines)
                {
                    if (line.isEmpty())
                        continue;

                    super.entries.add(DisplayStringEntry.of(this, line));
                }
            }
            
            for (WidgetEntry<Widget> dse : super.entries)
                if (((DisplayStringEntry)dse).validFormat)
                    ++this.rowsWithValidFormats;
            
            this.updateValidLines();
        }
        
        void add(DisplayStringEntry reference, String startingFormat)
        {
            int index = super.entries.indexOf(reference);
            
            DisplayStringEntry dse = DisplayStringEntry.of(this, startingFormat);
            
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
            
            this.updateValidLines();
            this.updateWidgetLocations();
        }
        
        public void updateValidLines()
        {
            final int rowSize = super.getSize();

            ((DisplayFormatPreferencesScreen)super.owningScreen).updateTitleLine
            (
                1, I18n.format
                (
                    LANG_FORMAT + "displayFormatting.validFormats",
                    this.rowsWithValidFormats, rowSize,
                    MineplexExpHudClientForge.getForgeInstance().getExpHud().progressPercentageFormat.format
                            (((float)this.rowsWithValidFormats / (float)rowSize) * 100.0f)
                )
            );
        }
        
        public DisplayStringEntry getActiveEntry()
        {
            for (WidgetEntry<Widget> entry : super.entries)
            {
                if (entry.isFocused()) {
                    return (DisplayStringEntry)entry;
                }
            }
            
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
        public static DisplayStringEntry of(DisplayStringList owner, String startingFormat)
        {
            TextWidget formatText = new TextWidget(owner.getClient().fontRendererObj, 144, 12);
            ButtonWidget add = new ButtonWidget(20, 20, "+", null);
            ButtonWidget duplicate = new ButtonWidget(20, 20, "++", null);
            ButtonWidget reset = new ButtonWidget(20, 20, "\u21BA", null);
            ButtonWidget remove = new ButtonWidget(20, 20, "X", null);
            
            DisplayStringEntry dse = new DisplayStringEntry(owner, formatText, add, duplicate, reset, remove);
            
            add.setOnClick(dse::onAddClick);
            duplicate.setOnClick(dse::onDuplicateClick);
            reset.setOnClick(dse::onResetClick);
            remove.setOnClick(dse::onRemoveClick);
    
            add.setTooltipText(I18n.format(LANG_FORMAT + "displayFormatting.add"));
            duplicate.setTooltipText(I18n.format(LANG_FORMAT + "displayFormatting.duplicate"));
            reset.setTooltipText(I18n.format(LANG_FORMAT + "displayFormatting.reset"));
            remove.setTooltipText(I18n.format(LANG_FORMAT + "displayFormatting.remove"));
            
            formatText.setMaxStringLength(32000);
            
            if (startingFormat != null)
                formatText.setText(startingFormat);
            
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
            
            this.startingText = formatBox.getText();
            
            this.formatBox.setPosOffset(0, 4);
            this.formatBox.setEnableBackgroundDrawing(false);
        }
        
        public boolean onAddClick(ButtonWidget b)
        {
            ((DisplayStringList)super.rowList).add(this, null);
            return true;
        }
        
        public boolean onDuplicateClick(ButtonWidget b)
        {
            String selText = this.formatBox.getSelectedText();
            
            ((DisplayStringList)super.rowList).add(this,
                    selText.trim().isEmpty() ? this.formatBox.getText() : selText);
            
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
        
        
        
        public void onFormatTextUpdate(String newText, String oldText) {
            this.performValidFormattingCheck(newText, true);
        }
        public void performValidFormattingCheck(String newText, boolean doNotSkipValidationChange)
        {
            LineInfoCache[] linesMade = MineplexExpHudClient.getInstance().getExpHud().attemptDisplayFormattingS(newText, false);
            boolean isValidFormatting = linesMade != null && linesMade.length > 0;
            
            this.formatBox.setTextColor(isValidFormatting ? 14737632 : 0xFF8888);
            
            int textColor = isValidFormatting ? 0x00000000 : 0xFF8888;
            
            this.add.packedFGColour = textColor;
            this.reset.packedFGColour = textColor;
            this.duplicate.packedFGColour = textColor;
            this.remove.packedFGColour = textColor;
            
            boolean validationChanged = isValidFormatting != this.validFormat;
            
            this.validFormat = isValidFormatting;
            
            if (validationChanged && doNotSkipValidationChange)
            {
                DisplayStringList dsl = ((DisplayStringList)super.rowList);
                
                if (this.validFormat)
                    ++dsl.rowsWithValidFormats;
                else
                    --dsl.rowsWithValidFormats;
                
                dsl.updateValidLines();
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
                
                Gui.drawRect(x, y - 1, x + listWidth - 6, y + slotHeight + 1,
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
            int r = super.rowList.width - 98;
            
            if (super.rowList.func_148135_f() > 0)
                r -= 8;
            
            this.formatBox.setX(8);
            this.formatBox.setW(r - 8);
            
            this.add.setX(r + 6);
            this.duplicate.setX(r + 28);
            this.reset.setX(r + 50);
            this.remove.setX(r + 72);
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
