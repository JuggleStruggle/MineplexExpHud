package jugglestruggle.mineplexexphud.forge.gui.screen;

import jugglestruggle.mineplexexphud.forge.MineplexExpHudClientForge;
import jugglestruggle.mineplexexphud.forge.gui.widget.ButtonWidget;
import jugglestruggle.mineplexexphud.forge.gui.widget.ColorWidget;
import jugglestruggle.mineplexexphud.forge.gui.widget.CyclingButtonWidget;
import jugglestruggle.mineplexexphud.forge.gui.widget.Widget;
import jugglestruggle.mineplexexphud.forge.gui.widget.WidgetRowListWidget;
import jugglestruggle.mineplexexphud.pref.Preferences;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import org.lwjgl.input.Keyboard;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;


import static jugglestruggle.mineplexexphud.MineplexExpHudClient.LANG_FORMAT;

public class ColorPreferencesScreen extends Screen
{
    WidgetRowListWidget<Widget> prefsList;
    CyclingButtonWidget<Boolean> showHudButton;
    ButtonWidget doneButton;
    
    String title;
    int titleWidth;
    
    public ColorPreferencesScreen(GuiScreen parentScreen) {
        super(parentScreen);
    }
    
    @Override
    public void initGui()
    {
        if (!super.screenCreated)
        {
            List<Widget> prefs = new ArrayList<>(8);
    
            prefs.add(CyclingButtonWidget.bool(150, 20, I18n.format(LANG_FORMAT+"drawTextWithShadow"),
                 Preferences.drawTextWithShadow, (byte)1).setValueChangeListener(this::onDrawTextWithShadowUpdate));
            prefs.add(this.of(Preferences.textColor, I18n.format(LANG_FORMAT+"textColor"),
                 (cw, c) -> Preferences.textColor = c));
            prefs.add(this.of(Preferences.backgroundColor, I18n.format(LANG_FORMAT+"backgroundColor"),
                 (cw, c) -> Preferences.backgroundColor = c));
            prefs.add(this.of(Preferences.borderColor, I18n.format(LANG_FORMAT+"borderColor"),
                 (cw, c) -> Preferences.borderColor = c));
            prefs.add(this.of(Preferences.progressBackgroundColor, I18n.format(LANG_FORMAT+"progressBackgroundColor"),
                 (cw, c) -> Preferences.progressBackgroundColor = c));
            prefs.add(this.of(Preferences.progressBorderColor, I18n.format(LANG_FORMAT+"progressBorderColor"),
                 (cw, c) -> Preferences.progressBorderColor = c));
    
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
    
            this.title = I18n.format(LANG_FORMAT + "coloring.preferences");
            this.titleWidth = this.mc.fontRendererObj.getStringWidth(this.title);
    
            super.screenCreated = true;
        }
    
        Keyboard.enableRepeatEvents(true);
    
        // Update the location and the size of the widgets and also add any buttons that were removed along the way
        this.prefsList.setDimensions(this.width, this.height, 32, this.height - 32);
        this.showHudButton.setPos(this.width / 2 - 151, this.height - 24);
        this.doneButton.setPos(this.width / 2 + 1, this.height - 24);
    }
    
    private ColorWidget of(int startColor, String text, BiConsumer<ColorWidget, Integer> onSet) {
        return new ColorWidget(this.mc.fontRendererObj, 150, startColor, text).setOnColorApply(onSet);
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
    
    private boolean onDrawTextWithShadowUpdate(CyclingButtonWidget<Boolean> b, Boolean v) {
        Preferences.drawTextWithShadow = v; return true;
    }
    private boolean onShowHudUpdate(CyclingButtonWidget<Boolean> b, Boolean v) {
        Preferences.showHud = v; return true;
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
        
//        this.prefsList.drawScreen(mouseX, mouseY, delta);
    
        this.mc.fontRendererObj.drawString(this.title, (this.width / 2) - (this.titleWidth / 2),
                10, 0xFFFFFF);
    
        super.drawScreen(mouseX, mouseY, delta);
    }
}
