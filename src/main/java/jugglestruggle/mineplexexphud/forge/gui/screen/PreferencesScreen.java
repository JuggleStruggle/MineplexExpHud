package jugglestruggle.mineplexexphud.forge.gui.screen;

import com.google.common.collect.ImmutableList;
import jugglestruggle.mineplexexphud.forge.MineplexExpHudClientForge;
import jugglestruggle.mineplexexphud.forge.gui.widget.ButtonWidget;
import jugglestruggle.mineplexexphud.forge.gui.widget.CyclingButtonWidget;
import jugglestruggle.mineplexexphud.forge.gui.widget.NumericWidget;
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

public class PreferencesScreen extends Screen
{
    WidgetRowListWidget<Widget> prefsList;
    CyclingButtonWidget<Boolean> showHudButton;
    ButtonWidget doneButton;
    
    String title;
    int titleWidth;
    
    public PreferencesScreen() {
        this(null);
    }
    
    public PreferencesScreen(GuiScreen parentScreen) {
        super(parentScreen);
    }
    
    @Override
    public void initGui()
    {
        if (!super.screenCreated)
        {
            List<Widget> prefs = new ArrayList<>(8);
    
            prefs.add(new ButtonWidget(150, 20, I18n.format(LANG_FORMAT+"displayFormatting"),
                    this::onModifyDisplayFormatClick));
            prefs.add(new ButtonWidget(150, 20, I18n.format(LANG_FORMAT+"coloring"),
                    this::onModifyColoringClick));
            
            prefs.add(new ButtonWidget(150, 20, I18n.format(LANG_FORMAT+"hudEditor"),
                    this::onModifyHudClick));
            CyclingButtonWidget<Boolean> cbw = CyclingButtonWidget.bool(150, 20,
                    I18n.format(LANG_FORMAT+"worksLocally"), true, (byte)1);
            cbw.setInteractable(false);
            cbw.setTooltipText(I18n.format(LANG_FORMAT+"worksLocally.notimplemented"));
            prefs.add(cbw);
    
            prefs.add(CyclingButtonWidget.bool(150, 20, I18n.format(LANG_FORMAT+"expUpdateEnabled"),
                 Preferences.expUpdateEnabled, (byte)1).setValueChangeListener(this::onExpUpdateEnabledUpdate));
            prefs.add(new CyclingButtonWidget<>(150, 20, I18n.format(LANG_FORMAT+"accuracy"),
                 Preferences.accuracy, ImmutableList.copyOf(AccuracyMode.values()), AccuracyMode::getFormattedText,
                    AccuracyMode::getFormattedTextDesc).setValueChangeListener(this::onAcurracyTypeUpdate));
            
            prefs.add(new CyclingButtonWidget<>(150, 20, I18n.format(LANG_FORMAT+"updateMethod"),
                 Preferences.updateMethod, ImmutableList.copyOf(UpdateMethod.values()), UpdateMethod::getFormattedText)
                    .setValueChangeListener(this::onExpUpdateMethodUpdate));
            prefs.add(CyclingButtonWidget.bool(150, 20, I18n.format(LANG_FORMAT+"worldChangeUseDelays"),
                 Preferences.worldChangeUseDelays, (byte)1).setValueChangeListener(this::onWorldChangeUseDelaysUpdate));
            
            prefs.add(CyclingButtonWidget.bool(150, 20, I18n.format(LANG_FORMAT+"postRender"),
                 Preferences.postRender, (byte)1).setValueChangeListener(this::onShowInPostRenderUpdate));
            prefs.add(CyclingButtonWidget.bool(150, 20, I18n.format(LANG_FORMAT+"showWhileDebugScreenActive"),
                 Preferences.showWhileDebugScreenActive, (byte)1).setValueChangeListener(this::onShowWhileDebugScreenActiveUpdate));
    
            prefs.add(new NumericWidget(this.mc.fontRendererObj, 150, 20, Preferences.millisUntilNextExpUpdate,
                    1000L, Long.MAX_VALUE, I18n.format(LANG_FORMAT+"millisUntilNextExpUpdate"))
                    .setValueChangeListener(this::onMillisUntilNextExpUpdateUpdated));
            prefs.add(CyclingButtonWidget.bool(150, 20, I18n.format(LANG_FORMAT+"ignoreEmptyExp"),
                    Preferences.ignoreEmptyExp, (byte)1).setValueChangeListener(this::onIgnoreEmptyExpUpdate));
    
            this.prefsList = new WidgetRowListWidget<>(
                this, 0, 0, 0, 0,
                22, prefs.toArray(new Widget[0])
            );
    
    
            super.listWidgets = new ArrayList<>(1);
            super.listWidgets.add(this.prefsList);
    
            this.showHudButton = CyclingButtonWidget.bool(150, 20, I18n.format(LANG_FORMAT+"showHud"),
                    Preferences.showHud, (byte)1).setValueChangeListener(this::onShowHudUpdate);
            this.doneButton = new ButtonWidget(150, 20, I18n.format("jugglestruggle.saveandreturn"), this::onDoneClick);
    
            super.widgets.add(this.showHudButton);
            super.widgets.add(this.doneButton);
    
            this.title = I18n.format(LANG_FORMAT + "preferences");
            this.titleWidth = this.mc.fontRendererObj.getStringWidth(this.title);
    
            super.screenCreated = true;
        }
    
        // Update the location and the size of the widgets and also add any buttons that were removed along the way
        this.prefsList.setDimensions(this.width, this.height, 32, this.height - 32);
        this.showHudButton.setPos(this.width / 2 - 151, this.height - 24);
        this.doneButton.setPos(this.width / 2 + 1, this.height - 24);
    }
    
    private boolean onMillisUntilNextExpUpdateUpdated(double newValue, double oldValue)
    {
        Preferences.millisUntilNextExpUpdate = (long)newValue;
        MineplexExpHudClientForge.getForgeExpHud().delayActiveMillisUntilNextExpUpdate();
        
        return true;
    }
    
    @Override
    protected boolean onEscapeKeyLeaves() {
        return true;
    }
    
    @Override
    public void onGuiClosed()
    {
        MineplexExpHudClientForge.getForgeInstance().writeToFile();
        super.onGuiClosed();
    }
    
    private boolean onExpUpdateEnabledUpdate(CyclingButtonWidget<Boolean> b, Boolean v) {
        Preferences.expUpdateEnabled = v; return true;
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
    private boolean onExpUpdateMethodUpdate(CyclingButtonWidget<UpdateMethod> b, UpdateMethod v) {
        Preferences.updateMethod = v; MineplexExpHudClientForge.getForgeExpHud().rebuildDisplayCacheInfo();
        return true;
    }
    private boolean onIgnoreEmptyExpUpdate(CyclingButtonWidget<Boolean> b, Boolean v) {
        Preferences.ignoreEmptyExp = v; return true;
    }
//    private boolean onUpdate(CyclingButtonWidget<> b,  v) {
//        Preferences. = v; return true;
//    }
    private boolean onModifyDisplayFormatClick(ButtonWidget b)
    {
        this.mc.displayGuiScreen(new DisplayFormatPreferencesScreen(this));
        return true;
    }
    private boolean onModifyColoringClick(ButtonWidget b)
    {
        this.mc.displayGuiScreen(new ColorPreferencesScreen(this));
        return true;
    }
    private boolean onModifyHudClick(ButtonWidget buttonWidget)
    {
        this.mc.displayGuiScreen(new HudEditorScreen(this));
        return true;
    }
    
    private boolean onDoneClick(ButtonWidget b)
    {
        this.mc.displayGuiScreen(this.parentScreen);
        return true;
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
    
        this.mc.fontRendererObj.drawString(this.title, (this.width / 2) - (this.titleWidth / 2),
                10, 0xFFFFFF);
    
        super.drawScreen(mouseX, mouseY, delta);
    
    }
}
